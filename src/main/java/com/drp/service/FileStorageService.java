package com.drp.service;

import com.drp.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadPath;
    private final List<String> allowedTypes;

    public FileStorageService(
            @Value("${drp.file.upload-dir}") String uploadDir,
            @Value("${drp.file.allowed-types}") String allowedTypesConfig) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.allowedTypes = Arrays.stream(allowedTypesConfig.split(","))
                .map(String::trim)
                .filter(type -> !type.isEmpty())
                .toList();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException ex) {
            throw new BadRequestException("Could not create upload directory");
        }
    }

    public StoredFile store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new BadRequestException("File type not allowed: " + contentType);
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new BadRequestException("Invalid file name");
        }

        String storedFileName = UUID.randomUUID() + "_" + originalFilename;
        Path targetLocation = uploadPath.resolve(storedFileName);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return new StoredFile(storedFileName, originalFilename, contentType, file.getSize());
        } catch (IOException ex) {
            throw new BadRequestException("Failed to store file");
        }
    }

    public Resource loadAsResource(String storedFileName) {
        try {
            Path filePath = uploadPath.resolve(storedFileName).normalize();
            if (!filePath.startsWith(uploadPath)) {
                throw new BadRequestException("Invalid file path");
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new BadRequestException("File not found");
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new BadRequestException("File not found");
        }
    }

    public void delete(String storedFileName) {
        if (storedFileName == null || storedFileName.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(uploadPath.resolve(storedFileName));
        } catch (IOException ex) {
            throw new BadRequestException("Failed to delete file");
        }
    }

    public record StoredFile(String storedFileName, String originalFileName, String contentType, long size) {
    }
}
