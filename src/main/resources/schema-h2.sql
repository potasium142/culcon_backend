create sequence ORDER_HISTORY_SEQ
    increment by 50;

create table COUPON
(
    EXPIRE_TIME  DATE,
    SALE_PERCENT REAL,
    USAGE_AMOUNT INTEGER,
    USAGE_LEFT   INTEGER,
    ID           CHARACTER VARYING(255) not null
        primary key,
    check ("USAGE_AMOUNT" >= 0),
    check ("USAGE_LEFT" >= 0)
);

create table PRODUCT
(
    AVAILABLE_QUANTITY INTEGER,
    PRICE              REAL,
    SALE_PERCENT       REAL,
    ID                 CHARACTER VARYING(255) not null
        primary key,
    IMAGE_URL          CHARACTER VARYING(255),
    PRODUCT_NAME       CHARACTER VARYING(255),
    PRODUCT_STATUS     ENUM ('IN_STOCK', 'PENDING', 'OUT_OF_STOCK', 'RECEIVED', 'NO_LONGER_IN_SALE', 'REFUNDED', 'REFUNDING', 'CREATED', 'CHANGED'),
    PRODUCT_TYPES      ENUM ('VEGETABLE', 'MEAT', 'SEASON', 'MEALKIT'),
    check ("AVAILABLE_QUANTITY" >= 0)
);

create table PRODUCT_PRICE_HISTORY
(
    PRICE        REAL,
    SALE_PERCENT REAL,
    DATE         TIMESTAMP              not null,
    PRODUCT_ID   CHARACTER VARYING(255) not null,
    primary key (DATE, PRODUCT_ID),
    constraint FKF0BKSLN3XKY2VMHP66S5MD5JD
        foreign key (PRODUCT_ID) references PRODUCT
);

create table USER_ACCOUNT
(
    PHONE               CHARACTER VARYING(12)
        unique,
    ID                  UUID                   not null
        primary key,
    ADDRESS             CHARACTER VARYING(255),
    EMAIL               CHARACTER VARYING(255) not null
        unique,
    PASSWORD            CHARACTER VARYING(255) not null,
    PROFILE_DESCRIPTION CHARACTER VARYING(255),
    PROFILE_PIC_URI     CHARACTER VARYING(255),
    TOKEN               CHARACTER VARYING(255),
    USERNAME            CHARACTER VARYING(255) not null
        unique,
    BOOKMARKED_POSTS    CHARACTER VARYING(255) ARRAY,
    STATUS              ENUM ('NON_ACTIVE', 'NORMAL', 'BANNED', 'DEACTIVATE')
);

create table ACCOUNT_OTP
(
    ID             BIGINT auto_increment
        primary key,
    OTP_EXPIRATION TIMESTAMP,
    ACCOUNT_ID     UUID,
    ACTIVITY_TYPE  CHARACTER VARYING(255),
    EMAIL          CHARACTER VARYING(255),
    OTP            CHARACTER VARYING(255),
    constraint FKL6PCK1XNFJVCAF7X5TPOA1U1M
        foreign key (ACCOUNT_ID) references USER_ACCOUNT
);

create table CART
(
    AMOUNT     INTEGER,
    ACCOUNT_ID UUID                   not null,
    PRODUCT_ID CHARACTER VARYING(255) not null,
    primary key (ACCOUNT_ID, PRODUCT_ID),
    constraint FK3D704SLV66TW6X5HMBM6P2X3U
        foreign key (PRODUCT_ID) references PRODUCT,
    constraint FKG6RRY7RYLCGWNFI65QJU5IYTR
        foreign key (ACCOUNT_ID) references USER_ACCOUNT
);

create table ORDER_HISTORY
(
    TOTAL_PRICE      REAL,
    UPDATED_COUPON   BOOLEAN,
    UPDATED_PAYMENT  BOOLEAN,
    ID               BIGINT not null
        primary key,
    ORDER_DATE       TIMESTAMP,
    PHONENUMBER      CHARACTER VARYING(12),
    USER_ID          UUID,
    COUPON           CHARACTER VARYING(255),
    DELIVERY_ADDRESS CHARACTER VARYING(255),
    NOTE             CHARACTER VARYING(255),
    RECEIVER         CHARACTER VARYING(255),
    ORDER_STATUS     ENUM ('ON_CONFIRM', 'ON_PROCESSING', 'ON_SHIPPING', 'SHIPPED', 'CANCELLED'),
    PAYMENT_METHOD   ENUM ('BANKING', 'COD'),
    PAYMENT_STATUS   ENUM ('PENDING', 'RECEIVED', 'REFUNDED', 'REFUNDING', 'CREATED', 'CHANGED'),
    constraint FKJ9L7P822E6HOQM8QVMAWJF2IK
        foreign key (COUPON) references COUPON,
    constraint FKLUQCFGW75TS4L8QOCD54645VD
        foreign key (USER_ID) references USER_ACCOUNT
);

create table ORDER_HISTORY_ITEMS
(
    QUANTITY              INTEGER,
    ORDER_HISTORY_ID      BIGINT not null,
    PRODUCT_ID_DATE       TIMESTAMP,
    PRODUCT_ID_PRODUCT_ID CHARACTER VARYING(255),
    constraint FK9L7J66VU575MCUJDJAW20X6N8
        foreign key (ORDER_HISTORY_ID) references ORDER_HISTORY,
    constraint FKFV6ICNJ256DCESYEGYGTMDGWG
        foreign key (PRODUCT_ID_DATE, PRODUCT_ID_PRODUCT_ID) references PRODUCT_PRICE_HISTORY
);

create table PAYMENT_TRANSACTION
(
    AMOUNT         REAL,
    CREATE_TIME    TIMESTAMP,
    ORDER_ID       BIGINT not null
        primary key,
    PAYMENT_ID     CHARACTER VARYING(255),
    REFUND_ID      CHARACTER VARYING(255),
    TRANSACTION_ID CHARACTER VARYING(255),
    STATUS         ENUM ('IN_STOCK', 'PENDING', 'OUT_OF_STOCK', 'RECEIVED', 'NO_LONGER_IN_SALE', 'REFUNDED', 'REFUNDING', 'CREATED', 'CHANGED'),
    constraint FKISYDO9P32TRU3EWI5NID3NT74
        foreign key (ORDER_ID) references ORDER_HISTORY
);

create table POST_COMMENT
(
    TIMESTAMP  TIMESTAMP              not null,
    ACCOUNT_ID UUID                   not null,
    COMMENT    CHARACTER VARYING(255),
    POST_ID    CHARACTER VARYING(255) not null,
    primary key (TIMESTAMP, ACCOUNT_ID, POST_ID),
    constraint FK2QXWJUTXGLK0AUXWKFJO7SJB4
        foreign key (ACCOUNT_ID) references USER_ACCOUNT
);


