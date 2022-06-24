DELETE FROM roles WHERE created_by = 'init';
INSERT INTO roles (id, role_code, role_name, created_by, created_at, updated_by, updated_at) VALUES
(uuid(), 'system_admin', 'システム管理者', 'init', NOW(), 'init', NOW()),
(uuid(), 'operation_admin', '運用管理者', 'init', NOW(), 'init', NOW()),
(uuid(), 'operator', '運用者', 'init', NOW(), 'init', NOW()),
(uuid(), 'user', 'ユーザ', 'init', NOW(), 'init', NOW());

DELETE FROM permissions WHERE created_by = 'init';
INSERT INTO permissions (id, permission_code, permission_name, created_by, created_at, updated_by, updated_at) VALUES
(uuid(), 'codeCategory:read', 'コード分類マスタ検索', 'init', NOW(), 'init', NOW()),
(uuid(), 'codeCategory:save', 'コード分類マスタ登録・編集', 'init', NOW(), 'init', NOW()),
(uuid(), 'code:read', 'コードマスタ検索', 'init', NOW(), 'init', NOW()),
(uuid(), 'code:save', 'コードマスタ登録・編集', 'init', NOW(), 'init', NOW()),
(uuid(), 'holiday:read', '祝日マスタ検索', 'init', NOW(), 'init', NOW()),
(uuid(), 'holiday:save', '祝日マスタ登録・編集', 'init', NOW(), 'init', NOW()),
(uuid(), 'mailTemplate:read', 'メールテンプレート検索', 'init', NOW(), 'init', NOW()),
(uuid(), 'mailTemplate:save', 'メールテンプレート登録・編集', 'init', NOW(), 'init', NOW()),
(uuid(), 'role:read', 'ロール検索', 'init', NOW(), 'init', NOW()),
(uuid(), 'role:save', 'ロール登録・編集', 'init', NOW(), 'init', NOW()),
(uuid(), 'uploadFile', 'ファイルアップロード', 'init', NOW(), 'init', NOW()),
(uuid(), 'user:read', 'ユーザマスタ検索', 'init', NOW(), 'init', NOW()),
(uuid(), 'user:save', 'ユーザマスタ登録・編集', 'init', NOW(), 'init', NOW()),
(uuid(), 'staff:read', '担当者マスタ検索', 'init', NOW(), 'init', NOW()),
(uuid(), 'staff:save', '担当者マスタ登録・編集', 'init', NOW(), 'init', NOW());

DELETE FROM role_permissions WHERE created_by = 'init';
INSERT INTO role_permissions (id, role_code, permission_code, is_enabled, created_by, created_at, updated_by, updated_at)
SELECT uuid(), 'system_admin', permission_code, 1, 'init', NOW(), 'init', NOW() FROM permissions;
INSERT INTO role_permissions (id, role_code, permission_code, is_enabled, created_by, created_at, updated_by, updated_at)
SELECT uuid(), 'operation_admin', permission_code, 0, 'init', NOW(), 'init', NOW() FROM permissions;
INSERT INTO role_permissions (id, role_code, permission_code, is_enabled, created_by, created_at, updated_by, updated_at)
SELECT uuid(), 'operator', permission_code, 0, 'init', NOW(), 'init', NOW() FROM permissions;

DELETE FROM staff_roles WHERE created_by = 'init';
INSERT INTO staff_roles (id, staff_id, role_code, created_by, created_at, updated_by, updated_at) VALUES
(uuid(), (SELECT id FROM staffs WHERE email = 'test@example.com'), 'system_admin', 'init', NOW(), 'init', NOW());

DELETE FROM user_roles WHERE created_by = 'init';
INSERT INTO user_roles (id, user_id, role_code, created_by, created_at, updated_by, updated_at) VALUES
(uuid(), (SELECT id FROM users WHERE email = 'test@example.com'), 'user', 'init', NOW(), 'init', NOW());
