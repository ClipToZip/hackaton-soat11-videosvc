package br.com.soat11.videosvc.infra.persistence;

import br.com.soat11.videosvc.core.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {
    // Aqui você já tem todos os métodos CRUD: save, findById, delete, etc.
}