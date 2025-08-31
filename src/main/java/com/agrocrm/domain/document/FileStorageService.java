package com.agrocrm.domain.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {
    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    
    @Value("${app.document.storage.path:./documents}")
    private String storagePath;
    
    /**
     * Читает файл из хранилища
     */
    public byte[] readFile(String filePath) throws IOException {
        try {
            Path fullPath = Paths.get(storagePath, filePath);
            
            if (!Files.exists(fullPath)) {
                log.warn("File not found: {}", fullPath);
                throw new IOException("Файл не найден: " + filePath);
            }
            
            if (!Files.isReadable(fullPath)) {
                log.warn("File is not readable: {}", fullPath);
                throw new IOException("Файл недоступен для чтения: " + filePath);
            }
            
            byte[] content = Files.readAllBytes(fullPath);
            log.debug("Read file: {}, size: {} bytes", fullPath, content.length);
            return content;
        } catch (IOException e) {
            log.error("Failed to read file: {}", filePath, e);
            throw e;
        }
    }
    
    /**
     * Сохраняет файл в хранилище
     */
    public void saveFile(String filePath, byte[] content) throws IOException {
        try {
            Path fullPath = Paths.get(storagePath, filePath);
            
            // Создаем директории, если они не существуют
            Path parentDir = fullPath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            
            Files.write(fullPath, content);
            log.info("Saved file: {}, size: {} bytes", fullPath, content.length);
        } catch (IOException e) {
            log.error("Failed to save file: {}", filePath, e);
            throw e;
        }
    }
    
    /**
     * Удаляет файл из хранилища
     */
    public void deleteFile(String filePath) throws IOException {
        try {
            Path fullPath = Paths.get(storagePath, filePath);
            
            if (Files.exists(fullPath)) {
                Files.delete(fullPath);
                log.info("Deleted file: {}", fullPath);
            } else {
                log.warn("File not found for deletion: {}", fullPath);
            }
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath, e);
            throw e;
        }
    }
    
    /**
     * Проверяет существование файла
     */
    public boolean fileExists(String filePath) {
        Path fullPath = Paths.get(storagePath, filePath);
        return Files.exists(fullPath) && Files.isReadable(fullPath);
    }
    
    /**
     * Получает размер файла
     */
    public long getFileSize(String filePath) throws IOException {
        try {
            Path fullPath = Paths.get(storagePath, filePath);
            return Files.size(fullPath);
        } catch (IOException e) {
            log.error("Failed to get file size: {}", filePath, e);
            throw e;
        }
    }
}
