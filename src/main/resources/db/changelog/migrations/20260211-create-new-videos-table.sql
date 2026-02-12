CREATE TABLE cliptozip.videos (
                                  video_id UUID PRIMARY KEY,
                                  user_id UUID NOT NULL,
                                  data_video_up TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  status INTEGER NOT NULL,
                                  video_name VARCHAR(200),
                                  zip_name VARCHAR(200),
                                  descricao VARCHAR(500),
                                  titulo VARCHAR(150),
                                  metadados JSONB,
                                  CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES cliptozip.user(user_id)
);
