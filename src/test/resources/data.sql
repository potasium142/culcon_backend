insert into account (id, address, bookmarked_posts, email, password, phone, profile_description, profile_pic_uri,
                     status, token, username)
values ('example@test996d4a65-9561-47d3-8fc2-31a5868dad52',
        '', '{}',
        'example@test',
        '$2a$10$n7NTAk2ymn6sYQEmwnqbI.mIqOBFSAWdXoZewi.PiPxQqnZiQq9zq',
        '0969996669', '',
        'defaultProfile',
        1, null,
        'test_account');
insert into PUBLIC.ACCOUNT (ID, ADDRESS, BOOKMARKED_POSTS, EMAIL, PASSWORD, PHONE, PROFILE_DESCRIPTION, PROFILE_PIC_URI, STATUS, TOKEN, USERNAME)
values  ('c75265e6-f5cc-419b-b183-7b43dbb9f27b', 'mei mei','' , 'trinhquangtung1@gmail.com', '$2a$10$eiP6w0So6payU/vsjmShCeoPOugB7mfdG2cZII3CKcrFSViHBO.ze', '0111111114', 'yummi', 'http://res.cloudinary.com/dolsfyjf7/image/upload/v1733998854/pfp_c75265e6-f5cc-419b-b183-7b43dbb9f27b.jpg', 1, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjNzUyNjVlNi1mNWNjLTQxOWItYjE4My03YjQzZGJiOWYyN2IiLCJyb2xlIjoiQ1VTVE9NRVIiLCJpYXQiOjE3MzM5OTc2NjksImV4cCI6MTczMzk5OTQ2OX0.bHmhvf29XgaQhvhObyC9rxEp7OFK8axPyg4ZaCqyF5g', 'Trịnh Trần Phương Tuấn'),
        ('5239ecbf-5985-41b5-bcf1-ca91c9f02995', 'mei mei', '', 'trinhquangtung3105@gmail.com', '$2a$10$DgCD4WjIwt3zaw6d.s4V7eVbdtIBOWRu4qCxmddKMrdZwSasU5x/G', '0981965778', null, 'defaultProfile', 1, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1MjM5ZWNiZi01OTg1LTQxYjUtYmNmMS1jYTkxYzlmMDI5OTUiLCJyb2xlIjoiQ1VTVE9NRVIiLCJpYXQiOjE3MzM5OTI1NzQsImV4cCI6MTczMzk5NDM3NH0.t9tODgVbVG4jXNgnOfytu7fjwEM43tUkOh5sbdO_rIE', 'Trịnh Quang Tùng'),
        ('94511231-59ce-45cb-9edc-196c378064a1', '69, Sussy town','' , 'test@email.com', '$2a$10$Z29kmHZUDNjSdqL4UMLzy.wNaSHneqI7NA53GEC1C1te2UBrFRso6', '0123456789', 'le sus', 'defaultProfile', 1, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI5NDUxMTIzMS01OWNlLTQ1Y2ItOWVkYy0xOTZjMzc4MDY0YTEiLCJyb2xlIjoiQ1VTVE9NRVIiLCJpYXQiOjE3MzQwNjQ0MzMsImV4cCI6MTczNTg2NDQzM30.gTV84SpP2QvIuoQ8XN_bQv9rValjwpcx2JyqBi3JsuI', 'test3'),
        ('4941b73e-9609-46b5-9059-d62a37c5238c', '','' , 'example@admin0', '$2a$10$n7NTAk2ymn6sYQEmwnqbI.mIqOBFSAWdXoZewi.PiPxQqnZiQq9zq', '0123456799', '', 'defaultProfile', 1, null, 'admin');
-- insert into PUBLIC.ACCOUNT (ID, ADDRESS, BOOKMARKED_POSTS, EMAIL, PASSWORD, PHONE, PROFILE_DESCRIPTION, PROFILE_PIC_URI, STATUS, TOKEN, USERNAME)
-- values  ('92e063c4-9b8c-4fbe-90fa-edc409fcb287', '','' , 'example@admin0', '$2a$10$n7NTAk2ymn6sYQEmwnqbI.mIqOBFSAWdXoZewi.PiPxQqnZiQq9zq', '0123456799', '', 'defaultProfile', 1, null, 'admin'),
--         ('4dd4db35-9748-4de7-93d9-6bc1e19aacb3', '69, Sussy town','' , 'test1e@email.com', '$2a$10$lWZdAr9W29DskVOhvbUeyuSCmMpZIl3LPCrHZ5/jJz/hJYBSO9dGu', '0123456789', 'le sus', 'defaultProfile', 1, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0ZGQ0ZGIzNS05NzQ4LTRkZTctOTNkOS02YmMxZTE5YWFjYjMiLCJyb2xlIjoiQ1VTVE9NRVIiLCJpYXQiOjE3MzM3MzQ1NDMsImV4cCI6MTczMzczNjM0M30.t7B4JfQZCkpvYL5evb4E3KgY7WRPUue1LWC5Yk1xrP8', 'test1'),
--         ('e7b5cd8f-698f-4b46-9028-c70501c3dda6', '69, Sussy town', '', 'trinhquangtung1@gmail.com', '$2a$10$LgtVnziQx33XYu1aPCHXJ.VMXUfX25LDqqYMxbt2ZZrqyxV7y6azq', '0123456780', 'le sus', 'defaultProfile', 1, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlN2I1Y2Q4Zi02OThmLTRiNDYtOTAyOC1jNzA1MDFjM2RkYTYiLCJyb2xlIjoiQ1VTVE9NRVIiLCJpYXQiOjE3MzM4MjcwNTUsImV4cCI6MTczMzgyODg1NX0.jU-I84i0WFGoHeYqVXM53wwmV1aL60u-jEsHY_oOhik', 'test3'),
--         ('48a30b9d-db8c-4ace-a753-80fa4b844c7b', '', '', 'trinh@gmail.com', '$2a$10$O6LQoPxd5bR2iaqTMdJE0O1XqX5WjJznq.Ohqaa/JrVzLd0nSgQ92', '0999999999', '', 'defaultProfile', 1, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0OGEzMGI5ZC1kYjhjLTRhY2UtYTc1My04MGZhNGI4NDRjN2IiLCJyb2xlIjoiQ1VTVE9NRVIiLCJpYXQiOjE3MzM3Njg0MDgsImV4cCI6MTczMzc3MDIwOH0.V4pXa1VHX3OIwqahOqj5zWytD3D2R_tfy3DvQNSWyDw', 'Trịnh Trần Phương Tuấn');
insert into PUBLIC.ACCOUNT_OTP (ID, ACCOUNT_ID, EMAIL, OTP, OTP_EXPIRATION, ACTIVITY_TYPE)
values  (2, '94511231-59ce-45cb-9edc-196c378064a1', 'trinhquangtung3105@gmail.com', 'Rgy8YTrwELqlh1', '2025-12-13 11:53:54.715089', null),
        (3, '5239ecbf-5985-41b5-bcf1-ca91c9f02995', 'trinhquangtung3105@gmail.com', 'WArrbxR2kIbPpG', '2024-12-13 11:54:15.561550', null),
        (4, 'c75265e6-f5cc-419b-b183-7b43dbb9f27b', 'trinhquangtung1@gmail.com', 'EO7XNGL45etGE4', '2025-12-13 11:54:27.629172', null);
-- insert into PUBLIC.ACCOUNT_OTP (ACCOUNT_ID, OTP, OTP_EXPIRATION, ID, EMAIL, ACTIVITY_TYPE)
-- values  ('e7b5cd8f-698f-4b46-9028-c70501c3dda6', 'rhKdtAJznpRx3b', '2025-12-11 10:53:08.736461', 3, 'trinhquangtung1@gmail.com', null);
insert into product (id, available_quantity, image_url, price, product_name, product_status, product_types,
                     sale_percent)
values ('PD_SNAKEHEAD_FISH_300G', 100,
        'https://homestory.com.vn/wp-content/uploads/2023/06/so-che-nguyen-lieu-cho-mon-ca-loc-kho-tieu.jpg', 5,
        'Snakehead Fish', 0, 1, 10),
       ('PD_TAMARIND_PASTE_100G', 100, 'https://thucphamdongxanh.com/wp-content/uploads/2019/09/me-vang.jpg', 2,
        'Tamarind Paste', 0, 1, 0),
       ('MK_01', 100, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRT9x9miekrqlY5dpoEFRTNLUeDWujSBuGuXQ&s',
        12.5, 'Snakehead Fish Braised with Pepper 🐟', 0, 3, 10),
       ('MK_02', 100, 'https://beptruong.edu.vn/wp-content/uploads/2021/04/suon-xao-chua-ngot.jpg', 15,
        'Sweet and Sour Pork Ribs 🍖', 0, 3, 12),
       ('MK_03', 100,
        'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:quality(100)/canh_chua_ca_bong_lau_1_7e63517315.jpg', 13.5,
        'Vietnamese Sour Soup with Pangasius Fish 🍲', 0, 3, 10),
       ('PD_BEAN_SPROUTS_200G', 100,
        'https://suckhoedoisong.qltns.mediacdn.vn/Images/bichvan/2016/07/28/gia-do-mon-an-ly-tuong-lam-dep-dang.JPG',
        0.8, 'Bean Sprouts', 0, 0, 0),
       ('PD_SUGAR_500G', 100, 'https://cdn.tgdd.vn/2020/12/CookProduct/1-1200x676-16.jpg', 1.2, 'Sugar', 0, 2, 0),
       ('PD_FISH_SAUCE_200ML', 100,
        'https://mamnamngu.com/wp-content/uploads/2024/04/nuoc-mam-nam-ngu-phu-quoc-chai-thuy-tinh-1.jpg', 2.5,
        'Fish Sauce', 0, 2, 5),
       ('PD_KETCHUP_300ML', 100, 'https://www.lottemart.vn/media/catalog/product/cache/0x0/8/9/8936136164083-1.jpg', 2,
        'Ketchup', 0, 2, 5),
       ('PD_CHILI_50G', 100,
        'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcShaKPpdftq5A8uhLnSiY2JMyPBs9qRG1Kwzw&s', 0.8, 'Chili', 0,
        2, 0),
       ('PD_TOMATO_200G', 100, 'https://suckhoedoisong.qltns.mediacdn.vn/Images/thanhloan/2016/06/30/ca-chua.jpg', 1,
        'Tomato', 0, 0, 0),
       ('PD_OKRA_150G', 100,
        'https://www.vinmec.com/static/uploads/20210604_012542_237817_qua_dau_bap_max_1800x1800_jpg_ba7d9800d8.jpg',
        1.5, 'Okra', 1, 0, 0),
       ('PD_VINEGAR_250ML', 100,
        'https://product.hstatic.net/1000296868/product/dam_trang_trungthanh_foods_47a40f5183c94a9dad60e80a2c23e56e.jpg',
        1, 'Vinegar', 1, 2, 0),
       ('PD_BLACK_PEPPER_50G', 100,
        'https://cdn.nhathuoclongchau.com.vn/unsafe/800x0/https://cms-prod.s3-sgn09.fptcloud.com/tieu_den_1_c1f3d5fbe3.jpg',
        1.5, 'Black Pepper', 1, 2, 5),
       ('PD_PORK_RIBS_300G', 100, 'https://media.loveitopcdn.com/22794/thumb/suon-non-heo-nk.jpg', 6, 'Pork Ribs', 1, 1,
        10),
       ('PD_PANGASIUS_FISH_300G', 100,
        'https://cooponline.vn/wp-content/uploads/2024/08/ca-bong-lau-cat-khuc-kg-vi-f-1722935616.jpg', 4.5,
        'Pangasius Fish', 2, 1, 10),
       ('PD_PINEAPPLE_200G', 100,
        'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSeTpBnEnLHdibCRlSP5MQkQKWMmDUM0mbd6g&s', 2, 'Pineapple',
        2, 0, 0),
       ('PD_GARLIC_100G', 100,
        'https://suckhoedoisong.qltns.mediacdn.vn/324455921873985536/2023/1/10/fresh-raw-garlic-ready-cook-scaled1-1673366730280487433643.jpg',
        1, 'Garlic', 2, 0, 0);

insert into product_price_history (date, price, sale_percent, product_id)
values ('2024-12-09 19:30:45.874514', 5, 10, 'PD_SNAKEHEAD_FISH_300G'),
       ('2024-12-09 19:30:45.884249', 2.5, 5, 'PD_FISH_SAUCE_200ML'),
       ('2024-12-09 19:30:45.889377', 1.5, 5, 'PD_BLACK_PEPPER_50G'),
       ('2024-12-09 19:30:45.894792', 1, 0, 'PD_GARLIC_100G'),
       ('2024-12-09 19:30:45.900075', 1.2, 0, 'PD_SUGAR_500G'),
       ('2024-12-09 19:30:45.905314', 0.8, 0, 'PD_CHILI_50G'),
       ('2024-12-09 19:30:45.910201', 6, 10, 'PD_PORK_RIBS_300G'),
       ('2024-12-09 19:30:45.914988', 1, 0, 'PD_VINEGAR_250ML'),
       ('2024-12-09 19:30:45.920154', 2, 5, 'PD_KETCHUP_300ML'),
       ('2024-12-09 19:30:45.924810', 4.5, 10, 'PD_PANGASIUS_FISH_300G'),
       ('2024-12-09 19:30:45.929753', 2, 0, 'PD_TAMARIND_PASTE_100G'),
       ('2024-12-09 19:30:45.934106', 1, 0, 'PD_TOMATO_200G'),
       ('2024-12-09 19:30:45.938123', 1.5, 0, 'PD_OKRA_150G'),
       ('2024-12-09 19:30:45.942209', 2, 0, 'PD_PINEAPPLE_200G'),
       ('2024-12-09 19:30:45.946725', 0.8, 0, 'PD_BEAN_SPROUTS_200G'),
       ('2024-12-09 19:30:45.982994', 12.5, 10, 'MK_01'),
       ('2024-12-09 19:30:45.987656', 15, 12, 'MK_02'),
       ('2024-12-09 19:30:45.991972', 13.5, 10, 'MK_03');
insert into PUBLIC.CART (ACCOUNT_ID, AMOUNT, PRODUCT_ID)
values  ('c75265e6-f5cc-419b-b183-7b43dbb9f27b', 2, 'MK_01');
insert into PUBLIC.COUPON (ID, EXPIRE_TIME, SALE_PERCENT, USAGE_AMOUNT, USAGE_LEFT)
values  ('cou132', '2025-12-30', 20, 20, 18);
insert into PUBLIC.ORDER_HISTORY (ID, ORDER_DATE, DELIVERY_ADDRESS, NOTE, ORDER_STATUS, PAYMENT_METHOD, PAYMENT_STATUS, PHONENUMBER, RECEIVER, TOTAL_PRICE, COUPON, USER_ID)
values
        (102, '2024-12-12 21:47:26.237815', '69, Sussy town', '', 0, 0, 0, '0123456789', 'test3', 22.5, null, '94511231-59ce-45cb-9edc-196c378064a1'),
        (152, '2024-12-13 01:24:37.705815', '69, Sussy town', '', 0, 0, 0, '0123456789', 'test3', 11.25, null, '94511231-59ce-45cb-9edc-196c378064a1'),
        (153, '2024-12-13 01:25:22.196430', '69, Sussy town', '', 0, 0, 0, '0123456789', 'test3', 11.25, null, '94511231-59ce-45cb-9edc-196c378064a1'),
        (202, '2024-12-13 01:30:12.698704', '69, Sussy town', '', 0, 0, 0, '123456789', 'test3', 22.5, null, '94511231-59ce-45cb-9edc-196c378064a1');

