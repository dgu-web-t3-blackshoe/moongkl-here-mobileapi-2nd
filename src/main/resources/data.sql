-- enterprises 테이블에 테스트 계정 삽입
INSERT INTO enterprises (id, name, country, manager_email, logo_img_url_id)
VALUES (UNHEX(REPLACE('4013c0f7-0c97-4bd7-a200-0de1392f1df0', '-', '')), 'Test Enterprise', '대한민국', 'manager@test.com', null)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO users (id, email, password, nickname, phone_number, gender, country, role, created_at,
                   favorite_count, like_count, status_message, updated_at)
VALUES (UNHEX(REPLACE('4014c0f7-0c97-4bd7-a200-0de1392f1df0', '-', '')),
        'test@user.com',
        '$2a$16$AQ6glwfKV7yEa1ngeyQXZejBrzaFdkjLo2GFI7mnh2/DdIlRIpJPW',
        'test_user',
        '01012341234',
        'male',
        '대한민국',
        'USER',
        NOW(),
        0,0,'',NOW())
ON DUPLICATE KEY UPDATE email = email;


/*
 4014c0f7-0c97-4bd7-a200-0de1392f1df0,대한민국,2024-05-15 05:42:03.000000,
 test@user.com,,male,,test_user,$2a$12$6Z7jU2XKn2PTPfEgDszAtO62ItWwmmqxLh72sgd6Ml.oOl/H8COJi,01012341234,,
 USER,,,,

 */