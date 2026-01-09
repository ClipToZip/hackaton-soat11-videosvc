--liquibase formatted sql

--changeset soat11:1
CREATE TABLE videos (
    video_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    data_video_up TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status INTEGER NOT NULL,
    video_path VARCHAR(200),
    zip_path VARCHAR(200),
    descricao VARCHAR(500),
    titulo VARCHAR(150),
    metadados JSONB
);