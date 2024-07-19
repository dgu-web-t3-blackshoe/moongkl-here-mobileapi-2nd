INSERT INTO logo_img_urls (id, s3url, cloudfront_url)
VALUES (UNHEX(REPLACE('4123c0f7-0c97-41fa-a220-0de1392f1df0', '-', '')), '', '')
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO enterprises (id, name, country, manager_email, logo_img_url_id)
VALUES (
           UNHEX(REPLACE('4013c0f7-0c97-4bd7-a200-0de1392f1df0', '-', '')),
           'Test Enterprise', '대한민국', 'manager@test.com',
           UNHEX(REPLACE('4123c0f7-0c97-41fa-a220-0de1392f1df0', '-', ''))
       )
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO story_urls (id, s3url, cloudfront_url, is_public, enterprise_id, created_at)
VALUES (UNHEX(REPLACE('445ac0f7-0c97-4bd7-a200-0de1392f1df0', '-', '')), '', '', TRUE, UNHEX(REPLACE('4013c0f7-0c97-4bd7-a200-0de1392f1df0', '-', '')), NOW())
    ON DUPLICATE KEY UPDATE cloudfront_url = cloudfront_url, s3url = s3url;

INSERT INTO profile_img_urls (id, s3url, cloudfront_url)
VALUES (UNHEX(REPLACE('4123c0f7-0c97-4bd7-a200-0de1392f1df0', '-', '')), '', '')
    ON DUPLICATE KEY UPDATE cloudfront_url = cloudfront_url, s3url = s3url;

INSERT INTO background_img_urls (id, s3url, cloudfront_url)
VALUES (UNHEX(REPLACE('4124c0f7-0c97-4bd7-a200-0de1392f1df0', '-', '')), '', '')
    ON DUPLICATE KEY UPDATE cloudfront_url = cloudfront_url, s3url = s3url;

INSERT INTO users (id, email, password, nickname, phone_number, gender, country, role, created_at, favorite_count, like_count, status_message, updated_at, background_img_url_id, profile_img_url_id)
VALUES (
           UNHEX(REPLACE('4014c0f7-0c97-4bd7-a200-0de1392f1df0', '-', '')),
           'test@user.com',
           '$2a$05$q64QX9M6pkXOj0Rk3uD70uY50/cBBT0IeqEbVfACYLIlPA9Wk/CSO',
           'test_user',
           '01012341234',
           'male',
           '대한민국',
           'USER',
           NOW(),
           0, 0, '', NOW(),
           UNHEX(REPLACE('4124c0f7-0c97-4bd7-a200-0de1392f1df0', '-', '')),
           UNHEX(REPLACE('4123c0f7-0c97-4bd7-a200-0de1392f1df0', '-', ''))
       )
    ON DUPLICATE KEY UPDATE email = email;
