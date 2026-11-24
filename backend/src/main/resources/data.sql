-- Semilla de datos de ejemplo, idempotente fila a fila:
-- cada INSERT solo se ejecuta si ese producto concreto no existe aún.
INSERT INTO productos (nombre, precio, stock, categoria)
SELECT 'Portatil Lenovo IdeaPad', 899.99, 12, 'Informática' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Portatil Lenovo IdeaPad');

INSERT INTO productos (nombre, precio, stock, categoria)
SELECT 'Ratón inalámbrico', 25.50, 45, 'Periféricos' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Ratón inalámbrico');

INSERT INTO productos (nombre, precio, stock, categoria)
SELECT 'Teclado mecánico', 79.90, 4, 'Periféricos' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Teclado mecánico');

INSERT INTO productos (nombre, precio, stock, categoria)
SELECT 'Monitor 24 pulgadas', 189.00, 9, 'Pantallas' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Monitor 24 pulgadas');

INSERT INTO productos (nombre, precio, stock, categoria)
SELECT 'Webcam HD', 39.95, 0, 'Periféricos' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Webcam HD');

INSERT INTO productos (nombre, precio, stock, categoria)
SELECT 'Silla ergonómica', 249.00, 6, 'Mobiliario' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Silla ergonómica');

INSERT INTO productos (nombre, precio, stock, categoria)
SELECT 'USB Kingston 64 GB', 8.75, 3, 'Almacenamiento' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'USB Kingston 64 GB');

INSERT INTO productos (nombre, precio, stock, categoria)
SELECT 'Disco SSD 1 TB', 99.90, 15, 'Almacenamiento' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Disco SSD 1 TB');
