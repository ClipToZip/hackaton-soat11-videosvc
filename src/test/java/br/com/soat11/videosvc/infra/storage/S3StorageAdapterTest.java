package br.com.soat11.videosvc.infra.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class S3StorageAdapterTest {
    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private MultipartFile multipartFile;
    private S3StorageAdapter adapter;

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);
        s3Presigner = mock(S3Presigner.class);
        multipartFile = mock(MultipartFile.class);
        adapter = new S3StorageAdapter(s3Client, "bucket", "key", "secret", Region.US_EAST_1);
    }

    @Test
    void store_DeveRetornarNomeArquivo_QuandoUploadValido() throws IOException {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("video.mp4");
        when(multipartFile.getContentType()).thenReturn("video/mp4");
        when(multipartFile.getBytes()).thenReturn("conteudo".getBytes());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(null);

        String result = adapter.store(multipartFile, "video");
        assertEquals("video.mp4", result);
    }

    @Test
    void store_DeveLancarExcecao_QuandoArquivoVazio() {
        when(multipartFile.isEmpty()).thenReturn(true);
        Exception ex = assertThrows(RuntimeException.class, () -> adapter.store(multipartFile, "video"));
        assertTrue(ex.getMessage().contains("Arquivo vazio"));
    }

    @Test
    void store_DeveRetornarNomeArquivoSemExtensao() throws IOException {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(null);
        when(multipartFile.getContentType()).thenReturn("video/mp4");
        when(multipartFile.getBytes()).thenReturn("conteudo".getBytes());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(null);

        String result = adapter.store(multipartFile, "video");
        assertEquals("video", result);
    }

    @Test
    void generateDownloadUrl_DeveRetornarUrlPresignada() throws MalformedURLException {
        String zipName = "arquivo.zip";
        String urlEsperada = "https://s3.amazonaws.com/bucket/arquivo.zip";
        PresignedGetObjectRequest presigned = mock(PresignedGetObjectRequest.class, RETURNS_DEEP_STUBS);
        when(presigned.url()).thenReturn(URI.create(urlEsperada).toURL());

        S3Presigner presigner = mock(S3Presigner.class);
        when(presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presigned);
        S3Presigner.Builder builder = mock(S3Presigner.Builder.class);
        when(builder.region(any(Region.class))).thenReturn(builder);
        when(builder.credentialsProvider(any(StaticCredentialsProvider.class))).thenReturn(builder);
        when(builder.build()).thenReturn(presigner);

        try (MockedStatic<S3Presigner> mocked = Mockito.mockStatic(S3Presigner.class)) {
            mocked.when(S3Presigner::builder).thenReturn(builder);

            String url = adapter.generateDownloadUrl(zipName);
            assertEquals(urlEsperada, url);
        }
    }
}