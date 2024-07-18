package com.pfa.api.app.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.pfa.api.app.entity.Document;
import com.pfa.api.app.entity.Folder;
import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.DocumentRepository;
import com.pfa.api.app.repository.UserRepository;

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

    public  List<Document> saveDocuments(List<MultipartFile> files,Project project, Folder savedDocumentsFolder , String DIRECTORY ,DocumentRepository documentRepository ,User owner) {
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
                        .uploadDate(new Date())
                        .folder(savedDocumentsFolder)
                        .uploader(owner.getFirstName() + " " + owner.getLastName())
                        .fileSize(compressedFile.length)
                        .project(project)
                        .build());
            }
            return documentRepository.saveAll(documents);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store files", e);
        }
    }

    public Path createUserDirectory(String DIRECTORY, Long userId) throws IOException {
        Path directory = Paths.get(DIRECTORY + "/" + userId + "/").toAbsolutePath().normalize();
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        return directory;
    }

    public void saveUserImage(MultipartFile image, User user, String DIRECTORY, UserRepository userRepository) {
        try {
            // Resize and compress the image before storing
            byte[] resizedAndCompressedImage = resizeAndCompressImage(image.getBytes(), 100, 100);

            Path userDirectory = createUserDirectory(DIRECTORY, user.getId());
            String fileName = FileUtils.generateUniqueFileName(image.getOriginalFilename());
            Path fileStorage = userDirectory.resolve(fileName + ".jpg");
            Files.write(fileStorage, resizedAndCompressedImage);

            user.setProfileImage(fileName);
            userRepository.save(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store files", e);
        }
    }

    public ResponseEntity<byte[]> downloadUserImage(User user, String DIRECTORY) {
        try {
            Path userDirectory = createUserDirectory(DIRECTORY, user.getId());
            Path fileStorage = userDirectory.resolve(user.getProfileImage() + ".jpg");
            byte[] lowQualityImage = Files.readAllBytes(fileStorage);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentDispositionFormData("attachment", user.getProfileImage() + ".jpg");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(lowQualityImage);

        } catch (IOException e) {
            throw new RuntimeException("Failed to download file", e);
        }
    }

    private byte[] resizeAndCompressImage(byte[] imageBytes, int width, int height) throws IOException {
        // Convert byte array to BufferedImage
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        BufferedImage originalImage = ImageIO.read(bais);

        // Resize image to lower quality
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();

        // Compress image to JPEG with quality setting
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.95f); // Set quality between 0 (low) and 1 (high)

        writer.write(null, new IIOImage(resizedImage, null, null), param);
        writer.dispose();
        ios.close();

        return baos.toByteArray();
    }

    public void deleteUserImage(User user, String DIRECTORY,UserRepository userRepository) {
        try {
            Path userDirectory = createUserDirectory(DIRECTORY, user.getId());
            Path fileStorage = userDirectory.resolve(user.getProfileImage() + ".jpg");
            Files.deleteIfExists(fileStorage);

            user.setProfileImage(null);
            userRepository.save(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }
    private MediaType getMediaType(String fileExtension) {
        // Map file extensions to MIME types
        switch (fileExtension) {
            case "image/jpeg":
                return MediaType.IMAGE_JPEG;
            case "image/png":
                return MediaType.IMAGE_PNG;
            // Add other image types as needed
            default:
                return null;
        }
    }
    @SuppressWarnings("null")
    public  Document saveReport(MultipartFile report, Project project ,Folder savedReportFolder, String DIRECTORY ,DocumentRepository documentRepository,User owner) {
        try {
            Path projectDirectory = createProjectDirectory(DIRECTORY,project.getId());
                String fileName = FileUtils.generateUniqueFileName(report.getOriginalFilename());
                byte[] compressedFile = FileUtils.compressFile(report.getBytes());
                Path fileStorage = projectDirectory.resolve(fileName + ".gz");
                Files.write(fileStorage, compressedFile);
                Document document = Document.builder()
                        .docName(fileName)
                        .uploadDate(new Date())
                        .folder(savedReportFolder)
                        .uploader(owner.getFirstName() + " " + owner.getLastName())
                        .fileSize(compressedFile.length)
                        .reportOf(project)
                        .project(project)
                        .build();
                Document doc = documentRepository.save(document);
                project.setReport(doc);
                return doc;


        } catch (IOException e) {
            throw new RuntimeException("Failed to store files", e);
        }
    }

    public static ResponseEntity<byte[]> downloadFile(Project project, Document document, String DIRECTORY) {
        try {
            Path projectDirectory = createProjectDirectory(DIRECTORY, project.getId());
            Path fileStorage = projectDirectory.resolve(document.getDocName() + ".gz");

            byte[] compressedFile = Files.readAllBytes(fileStorage);
            byte[] decompressedFile = FileUtils.decompressFile(compressedFile);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", document.getDocName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(decompressedFile);

        } catch (IOException e) {
            throw new RuntimeException("Failed to download file", e);
        }
    }
}
