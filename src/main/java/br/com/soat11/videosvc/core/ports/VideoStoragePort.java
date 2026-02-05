package br.com.soat11.videosvc.core.ports;

import org.springframework.web.multipart.MultipartFile;

public interface VideoStoragePort {
    String store(MultipartFile file, String fileName);

    String generateDownloadUrl(String zipName);
}
