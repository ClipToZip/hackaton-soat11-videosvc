package br.com.soat11.videosvc.infra.storage;

import br.com.soat11.videosvc.core.ports.VideoStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class LocalFileStorage implements VideoStoragePort {

    @Value("${storage.local-path:./uploads}")
    private String localPath;

    @Override
    public String store(MultipartFile file, String fileName) {
        try {
            Path root = Paths.get(localPath);
            if (!Files.exists(root)) Files.createDirectories(root);

            Path target = root.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toAbsolutePath().toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar arquivo localmente", e);
        }
    }
}
