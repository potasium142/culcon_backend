create sequence order_history_seq
    increment by 50;

create type accounttype as enum ('MANAGER', 'STAFF');

create type accountstatus as enum ('ACTIVE', 'DISABLE');

create type producttype as enum ('VEGETABLE', 'MEAT', 'SEASON', 'MEALKIT');

create type paymentmethod as enum ('BANKING', 'COD');

create type paymentstatus as enum ('PENDING', 'RECEIVED', 'REFUNDED', 'REFUNDING', 'CREATED', 'CHANGED');

create type orderstatus as enum ('ON_CONFIRM', 'ON_PROCESSING', 'ON_SHIPPING', 'SHIPPED', 'CANCELLED');

create type productstatus as enum ('IN_STOCK', 'OUT_OF_STOCK', 'NO_LONGER_IN_SALE');

create type useraccountstatus as enum ('NON_ACTIVE', 'NORMAL', 'BANNED', 'DEACTIVATE');

create table product
(
    id                 varchar(255)  not null
        primary key,
    product_name       varchar       not null,
    available_quantity integer       not null,
    product_types      producttype   not null,
    product_status     productstatus not null,
    image_url          varchar       not null,
    price              real          not null,
    sale_percent       real          not null
);

create table staff_account
(
    id       uuid          not null
        primary key,
    username varchar       not null
        unique,
    password varchar       not null,
    type     accounttype   not null,
    status   accountstatus not null,
    token    varchar       not null
);

create table user_account
(
    id                  uuid              not null
        primary key,
    email               varchar           not null
        unique,
    username            varchar           not null
        unique,
    password            varchar           not null,
    status              useraccountstatus not null,
    address             varchar           not null,
    phone               varchar           not null,
    profile_pic_uri     varchar           not null,
    profile_description varchar           not null,
    token               varchar           not null,
    bookmarked_posts    varchar(255)[]    not null
);

create table coupon
(
    id           varchar not null
        primary key,
    expire_time  date    not null,
    sale_percent real    not null,
    usage_amount integer not null,
    usage_left   integer not null
);

create table product_price_history
(
    price        real         not null,
    sale_percent real         not null,
    date         timestamp    not null,
    product_id   varchar(255) not null
        references product,
    constraint product_price_history_pk
        primary key (date, product_id)
);

create table employee_info
(
    account_id  uuid    not null
        primary key
        references staff_account,
    ssn         varchar not null
        unique,
    phonenumber varchar not null
        unique,
    realname    varchar not null,
    email       varchar not null
        unique,
    dob         date    not null
);

create table cart
(
    amount     integer      not null,
    account_id uuid         not null
        references user_account,
    product_id varchar(255) not null
        references product,
    primary key (account_id, product_id)
);

create table post_comment
(
    timestamp  timestamp    not null,
    post_id    varchar(255) not null,
    account_id uuid         not null
        references user_account,
    comment    varchar(255) not null,
    primary key (post_id, account_id)
);

create table account_otp
(
    id             bigserial
        primary key,
    account_id     uuid         not null
        references user_account,
    email          varchar(255) not null,
    otp            varchar(255) not null,
    activity_type  varchar(255) not null,
    otp_expiration timestamp    not null
);

create table order_history
(
    id               bigserial
        primary key,
    user_id          uuid          not null
        references user_account,
    order_date       timestamp     not null,
    delivery_address varchar(255)  not null,
    note             varchar(255)  not null,
    total_price      real          not null,
    receiver         varchar(255)  not null,
    phonenumber      varchar(12)   not null,
    coupon           varchar
        references coupon,
    updated_coupon   boolean       not null,
    updated_payment  boolean       not null,
    payment_method   paymentmethod not null,
    payment_status   paymentstatus not null,
    order_status     orderstatus   not null
);

create table order_history_items
(
    order_history_id      bigint
        references order_history,
    product_id_product_id varchar(255),
    product_id_date       timestamp,
    quantity              integer,
    foreign key (product_id_date, product_id_product_id) references product_price_history
);

create table payment_transaction
(
    order_id       bigint        not null
        primary key
        references order_history,
    create_time    timestamp     not null,
    payment_id     varchar(255),
    refund_id      varchar(255),
    transaction_id varchar(255),
    status         paymentstatus not null,
    amount         real          not null
);


