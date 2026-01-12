package br.com.soat11.videosvc.infra.storage;

import br.com.soat11.videosvc.core.ports.VideoStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@Primary
public class S3StorageAdapter implements VideoStoragePort {

    private final S3Client s3Client;
    private final String bucketName;

    public S3StorageAdapter(
            @Value("${aws.s3.bucket-name}") String bucketName,
            @Value("${aws.region}") String region,
            @Value("${aws.access-key}") String accessKey,
            @Value("${aws.secret-key}") String secretKey) {

        this.bucketName = bucketName;
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Override
    public String store(MultipartFile file, String fileName) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Arquivo vazio detectado no upload.");
            }

            // 1. Definimos a pasta para uso interno no S3
            String folder = "videos/";

            // 2. Extraímos a extensão
            String originalFilename = file.getOriginalFilename();
            String extension = (originalFilename != null && originalFilename.contains("."))
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";

            // Nome do arquivo com extensão (ex: meu-video.mp4)
            String fileNameWithExt = fileName.endsWith(extension) ? fileName : fileName + extension;

            // 3. O 'key' do S3 leva a pasta, mas o retorno do método não
            String s3Key = folder + fileNameWithExt;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key) // O S3 recebe: videos/meu-video.mp4
                    .contentType(file.getContentType())
                    .build();

            // 4. Upload
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            System.out.println("DEBUG: Upload concluído na pasta 'videos/'. Arquivo: " + fileNameWithExt);

            // RETORNO: Apenas o nome do arquivo, sem o prefixo da pasta
            return fileNameWithExt;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar bytes do arquivo para o S3", e);
        }
    }
}