package com.drp.service;

import com.drp.dto.request.ResourceUpdateRequest;
import com.drp.dto.response.PageResponse;
import com.drp.dto.response.ResourceResponse;
import com.drp.entity.ActivityAction;
import com.drp.entity.Category;
import com.drp.entity.Resource;
import com.drp.entity.Role;
import com.drp.entity.User;
import com.drp.exception.BadRequestException;
import com.drp.exception.ResourceNotFoundException;
import com.drp.exception.UnauthorizedException;
import com.drp.repository.ResourceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final CategoryService categoryService;
    private final FileStorageService fileStorageService;
    private final ActivityLogService activityLogService;

    public ResourceService(
            ResourceRepository resourceRepository,
            CategoryService categoryService,
            FileStorageService fileStorageService,
            ActivityLogService activityLogService) {
        this.resourceRepository = resourceRepository;
        this.categoryService = categoryService;
        this.fileStorageService = fileStorageService;
        this.activityLogService = activityLogService;
    }

    @Transactional(readOnly = true)
    public PageResponse<ResourceResponse> search(
            String keyword,
            Long categoryId,
            String fileType,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Resource> results = resourceRepository.search(keyword, categoryId, fileType, pageable);
        Page<ResourceResponse> mapped = results.map(ResourceResponse::from);
        return PageResponse.from(mapped);
    }

    @Transactional(readOnly = true)
    public ResourceResponse getById(Long id) {
        return ResourceResponse.from(findResourceById(id));
    }

    @Transactional(readOnly = true)
    public Resource findResourceById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
    }

    @Transactional
    public ResourceResponse upload(
            String title,
            String description,
            String tags,
            Long categoryId,
            MultipartFile file,
            User uploader) {

        if (title == null || title.isBlank()) {
            throw new BadRequestException("Title is required");
        }

        Category category = null;
        if (categoryId != null) {
            category = categoryService.findCategoryById(categoryId);
        }

        FileStorageService.StoredFile storedFile = fileStorageService.store(file);

        Resource resource = new Resource();
        resource.setTitle(title.trim());
        resource.setDescription(description);
        resource.setTags(tags);
        resource.setFileName(storedFile.originalFileName());
        resource.setFilePath(storedFile.storedFileName());
        resource.setFileType(storedFile.contentType());
        resource.setFileSize(storedFile.size());
        resource.setUploadedBy(uploader);
        resource.setCategory(category);

        Resource saved = resourceRepository.save(resource);
        activityLogService.log(uploader, ActivityAction.UPLOAD, "Resource", saved.getId(),
                "Uploaded resource: " + saved.getTitle());

        return ResourceResponse.from(saved);
    }

    @Transactional
    public ResourceResponse update(Long id, ResourceUpdateRequest request, User actor) {
        Resource resource = findResourceById(id);
        assertCanModify(resource, actor);

        resource.setTitle(request.getTitle().trim());
        resource.setDescription(request.getDescription());
        resource.setTags(request.getTags());

        if (request.getCategoryId() != null) {
            resource.setCategory(categoryService.findCategoryById(request.getCategoryId()));
        } else {
            resource.setCategory(null);
        }

        Resource saved = resourceRepository.save(resource);
        activityLogService.log(actor, ActivityAction.UPDATE, "Resource", saved.getId(),
                "Updated resource: " + saved.getTitle());

        return ResourceResponse.from(saved);
    }

    @Transactional
    public void delete(Long id, User actor) {
        Resource resource = findResourceById(id);
        assertCanModify(resource, actor);

        fileStorageService.delete(resource.getFilePath());
        resourceRepository.delete(resource);

        activityLogService.log(actor, ActivityAction.DELETE, "Resource", id,
                "Deleted resource: " + resource.getTitle());
    }

    @Transactional(readOnly = true)
    public org.springframework.core.io.Resource loadFileAsResource(Long id) {
        Resource resource = findResourceById(id);
        return fileStorageService.loadAsResource(resource.getFilePath());
    }

    @Transactional(readOnly = true)
    public String getOriginalFileName(Long id) {
        return findResourceById(id).getFileName();
    }

    private void assertCanModify(Resource resource, User actor) {
        boolean isOwner = resource.getUploadedBy().getId().equals(actor.getId());
        boolean isAdmin = actor.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException("You are not allowed to modify this resource");
        }
    }
}
