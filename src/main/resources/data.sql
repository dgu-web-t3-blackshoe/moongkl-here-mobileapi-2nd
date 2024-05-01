-- enterprises 테이블에 테스트 계정 삽입
INSERT INTO enterprises (id, name, country, manager_email, logo_img_url_id)
VALUES (UNHEX(REPLACE('4013c0f7-0c97-4bd7-a200-0de1392f1df0', '-', '')), 'Test Enterprise', '대한민국', 'manager@test.com', null)
ON DUPLICATE KEY UPDATE name = name;
