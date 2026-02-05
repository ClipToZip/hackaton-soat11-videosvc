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
    private Integer status;

    @Column(name = "video_name", length = 200) // Mudou de video_path
    private String videoName;

    @Column(name = "zip_name", length = 200)   // Mudou de zip_path
    private String zipName;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "titulo", length = 150)
    private String titulo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadados", columnDefinition = "jsonb")
    private Map<String, Object> metadados;
}