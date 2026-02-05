-- Remove as colunas antigas que não serão mais utilizadas
ALTER TABLE videos DROP COLUMN video_path;
ALTER TABLE videos DROP COLUMN zip_path;