DELETE FROM staffs WHERE created_by = 'init';
INSERT INTO staffs(id, first_name, last_name, email, password, tel, created_by, created_at, updated_by, updated_at) VALUES
(uuid(), 'john', 'doe', 'test@example.com', '$2a$06$hY5MzfruCds1t5uFLzrlBuw3HcrEGeysr9xJE4Cml5xEOVf425pmK', '09011112222', 'init', NOW(), 'init', NOW());

DELETE FROM users WHERE created_by = 'init';
INSERT INTO users(id, first_name, last_name, email, password, tel, address, created_by, created_at, updated_by, updated_at) VALUES
(uuid(), 'john', 'doe', 'test@example.com', '$2a$06$hY5MzfruCds1t5uFLzrlBuw3HcrEGeysr9xJE4Cml5xEOVf425pmK', '09011112222', 'tokyo, chuo-ku 1-2-3', 'init', NOW(), 'init', NOW());

DELETE FROM mail_templates WHERE created_by = 'init';
INSERT INTO mail_templates (id, category_code, template_code, subject, template_body, created_by, created_at, updated_by, updated_at) VALUES
(uuid(), NULL, 'passwordReset', 'パスワードリセット完了のお願い', CONCAT('[[$', '{staff.firstName}]]さん\r\n\r\n下記のリンクを開いてパスワードをリセットしてください。\r\n[[$', '{url}]]'), 'init', NOW(), 'init', NOW());
