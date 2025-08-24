
USE favdb;


DELETE FROM like_list;
DELETE FROM products;
DELETE FROM users;


ALTER TABLE like_list AUTO_INCREMENT = 1;
ALTER TABLE products AUTO_INCREMENT = 1;


INSERT INTO users (user_id, user_name, email, account, password_hash) VALUES
('ADMIN001', '管理員', 'admin@example.com', '9999999999', '$2a$10$EIX3ULnVwXrJ0zF6r3zM6.XLNW9q8n3bKKPdIxLb.PnN8Rk0J8oWu');

INSERT INTO products (product_name, price, fee_rate) VALUES
('台積電股票', 500.00, 0.001425),
('聯發科股票', 800.00, 0.001425),
('鴻海股票', 120.00, 0.001425),
('中華電信股票', 110.00, 0.001425),
('台達電股票', 300.00, 0.001425),
('統一股票', 70.00, 0.001425),
('中鋼股票', 35.00, 0.001425),
('南亞股票', 60.00, 0.001425),
('台塑股票', 90.00, 0.001425),
('國泰金股票', 45.00, 0.001425),
('富邦金股票', 55.00, 0.001425),
('兆豐金股票', 32.00, 0.001425),
('玉山金股票', 25.00, 0.001425),
('中信金股票', 28.00, 0.001425),
('開發金股票', 15.00, 0.001425);


SELECT 'Users Table' as table_name, COUNT(*) as record_count FROM users
UNION ALL
SELECT 'Products Table', COUNT(*) FROM products;
