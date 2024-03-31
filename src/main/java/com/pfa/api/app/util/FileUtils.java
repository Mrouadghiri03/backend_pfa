package com.pfa.api.app.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import com.pfa.api.app.entity.Document;
import com.pfa.api.app.entity.Project;
import com.pfa.api.app.repository.DocumentRepository;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtils {

    public String determineContentType(byte[] content) {
        Tika tika = new Tika();
        System.out.println(tika.detect(content));
        return tika.detect(content);
    }

    public byte[] compressFile(byte[] content) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            gzipOutputStream.write(content);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public  byte[] decompressFile(byte[] compressedContent) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressedContent))) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    public  String generateUniqueFileName(String originalFileName) {
        String baseName = FilenameUtils.getBaseName(originalFileName);
        String extension = FilenameUtils.getExtension(originalFileName);
        String uniqueName = baseName + "_" + UUID.randomUUID().toString() + "." + extension;
        return uniqueName;
    }
    
    public  Path createProjectDirectory(String DIRECTORY ,Long projectId) throws IOException {
        Path directory = Paths.get(DIRECTORY + "/" + projectId + "/").toAbsolutePath().normalize();
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        return directory;
    }

    public  void saveDocuments(List<MultipartFile> files, Project project , String DIRECTORY ,DocumentRepository documentRepository) {
        try {
            Path projectDirectory = createProjectDirectory(DIRECTORY,project.getId());
            List<Document> documents = new ArrayList<>();
            for (MultipartFile file : files) {
                String fileName = FileUtils.generateUniqueFileName(file.getOriginalFilename());
                byte[] compressedFile = FileUtils.compressFile(file.getBytes());
                Path fileStorage = projectDirectory.resolve(fileName + ".gz");
                Files.write(fileStorage, compressedFile);
                documents.add(Document.builder()
                        .docName(fileName)
                        .fileSize(compressedFile.length)
                        .project(project)
                        .build());
            }
            documentRepository.saveAll(documents);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store files", e);
        }
    }
    @SuppressWarnings("null")
    public  void saveReport(MultipartFile report, Project project , String DIRECTORY ,DocumentRepository documentRepository) {
        try {
            Path projectDirectory = createProjectDirectory(DIRECTORY,project.getId());
                String fileName = FileUtils.generateUniqueFileName(report.getOriginalFilename());
                byte[] compressedFile = FileUtils.compressFile(report.getBytes());
                Path fileStorage = projectDirectory.resolve(fileName + ".gz");
                Files.write(fileStorage, compressedFile);
                Document document = Document.builder()
                        .docName(fileName)
                        .fileSize(compressedFile.length)
                        .reportOf(project)
                        .build();
            documentRepository.save(document);
            project.setReport(document);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store files", e);
        }
    }
}
