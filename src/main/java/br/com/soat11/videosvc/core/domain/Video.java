package br.com.soat11.videosvc.core.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "videos")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "video_id")
    private UUID videoId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @CreationTimestamp
    @Column(name = "data_video_up", updatable = false)
    private LocalDateTime videoUpDate;

    @Column(name = "status")
    private Integer status; // 1-RECEBIDO, 2-UPLOADED, etc.

    @Column(name = "video_path", length = 200)
    private String videoPath; // path/video-nome

    @Column(name = "zip_path", length = 200)
    private String zipPath;   // path/zip-nome

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "titulo", length = 150)
    private String titulo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadados", columnDefinition = "jsonb")
    private Map<String, Object> metadados;
}