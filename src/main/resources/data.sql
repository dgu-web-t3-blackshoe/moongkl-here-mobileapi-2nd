-- enterprises 테이블에 테스트 계정 삽입
INSERT INTO enterprises (id, name, country, manager_email, logo_img_url_id)
VALUES (UNHEX(REPLACE('4013c0f7-0c97-4bd7-a200-0de1392f1df0', '-', '')), 'Test Enterprise', '대한민국', 'manager@test.com', null)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO users (id, email, password, nickname, phone_number, gender, country, role, created_at)
VALUES (UNHEX(REPLACE('4014c0f7-0c97-4bd7-a200-0de1392f1df0', '-', '')),
        'test@user.com',
        '$2a$12$6Z7jU2XKn2PTPfEgDszAtO62ItWwmmqxLh72sgd6Ml.oOl/H8COJi',
        'test_user',
        '01012341234',
        'male',
        '대한민국',
        'ROLE_USER',
        NOW())
ON DUPLICATE KEY UPDATE email = email;