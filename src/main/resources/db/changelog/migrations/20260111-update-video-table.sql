-- Renomeia a coluna video_path para video_name
ALTER TABLE videos RENAME COLUMN video_path TO video_name;

-- Renomeia a coluna zip_path para zip_name
ALTER TABLE videos RENAME COLUMN zip_path TO zip_name;

-- Opcional: Garante que as colunas aceitam o tamanho de 200 caracteres definido na Entity
ALTER TABLE videos ALTER COLUMN video_name TYPE VARCHAR(200);
ALTER TABLE videos ALTER COLUMN zip_name TYPE VARCHAR(200);