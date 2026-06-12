package com.drp.controller;

import com.drp.dto.request.ResourceUpdateRequest;
import com.drp.dto.response.ApiResponse;
import com.drp.dto.response.PageResponse;
import com.drp.dto.response.ResourceResponse;
import com.drp.entity.User;
import com.drp.security.SecurityUtils;
import com.drp.service.ResourceService;
import com.drp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {

    private final ResourceService resourceService;
    private final UserService userService;

    public ResourceController(ResourceService resourceService, UserService userService) {
        this.resourceService = resourceService;
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<PageResponse<ResourceResponse>> searchResources(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String fileType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        return ApiResponse.success(resourceService.search(keyword, categoryId, fileType, page, size, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ApiResponse<ResourceResponse> getResource(@PathVariable Long id) {
        return ApiResponse.success(resourceService.getById(id));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadResource(@PathVariable Long id) {
        Resource file = resourceService.loadFileAsResource(id);
        String fileName = resourceService.getOriginalFileName(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(file);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ResourceResponse> uploadResource(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Long categoryId,
            @RequestParam("file") MultipartFile file) {

        User uploader = userService.findUserByUsername(SecurityUtils.getCurrentUsername());
        ResourceResponse response = resourceService.upload(title, description, tags, categoryId, file, uploader);
        return ApiResponse.success("Resource uploaded", response);
    }

    @PutMapping("/{id}")
    public ApiResponse<ResourceResponse> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceUpdateRequest request) {

        User actor = userService.findUserByUsername(SecurityUtils.getCurrentUsername());
        return ApiResponse.success("Resource updated", resourceService.update(id, request, actor));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteResource(@PathVariable Long id) {
        User actor = userService.findUserByUsername(SecurityUtils.getCurrentUsername());
        resourceService.delete(id, actor);
        return ApiResponse.success("Resource deleted", null);
    }
}
