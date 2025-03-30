create type useraccountstatus as enum ('NON_ACTIVE', 'NORMAL', 'BANNED', 'DEACTIVATE');

create type orderstatus as enum ('CANCELLED', 'ON_CONFIRM', 'ON_PROCESSING', 'ON_SHIPPING', 'SHIPPED');

create type paymentmethod as enum ('COD', 'PAYPAL', 'VNPAY');

create type paymentstatus as enum ('CHANGED', 'CREATED', 'PENDING', 'RECEIVED', 'REFUNDED', 'REFUNDING', 'DELIVERED' );

create type commenttype as enum ('POST', 'REPLY');

create type productstatus as enum ('IN_STOCK', 'NO_LONGER_IN_SALE', 'OUT_OF_STOCK');

create type producttype as enum ('MEALKIT', 'MEAT', 'SEASON', 'VEGETABLE');

create type commentstatus as enum ('NORMAL', 'REPORTED', 'DELETED');

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

create table blog
(
    id          varchar(255) not null
        primary key,
    title       varchar(255) not null,
    description text         not null,
    article     text         not null,
    thumbnail   varchar(255) not null,
    infos       jsonb        not null
);

create table user_account
(
    id                  varchar(255)      not null
        primary key,
    email               varchar           not null
        unique,
    username            varchar           not null
        unique,
    password            varchar           not null,
    status              useraccountstatus not null,
    address             varchar           not null,
    phone               varchar           not null
        unique,
    profile_pic_uri     varchar           not null,
    profile_description varchar           not null,
    token               varchar           not null,
    profile_name        varchar           not null,
    bookmarked_posts    varchar(255)[]    not null
);

create table coupon
(
    id            varchar not null
        primary key,
    expire_time   date    not null,
    sale_percent  real    not null,
    usage_amount  integer not null,
    usage_left    integer not null,
    minimum_price real    not null
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

create table mealkit_ingredients
(
    mealkit_id varchar(255) not null
        references product,
    ingredient varchar(255) not null
        references product,
    amount     integer      not null,
    primary key (mealkit_id, ingredient)
);

create table product_doc
(
    id                varchar(255)   not null
        primary key
        references product,
    description       varchar(255)   not null,
    images_url        varchar(255)[] not null,
    infos             jsonb          not null,
    ingredients       varchar(255)[],
    instructions      varchar(255)[],
    article_md        text           not null,
    day_before_expiry integer        not null
);

create table cart
(
    amount     integer      not null,
    account_id varchar(255) not null
        references user_account,
    product_id varchar(255) not null
        references product,
    primary key (account_id, product_id)
);

create table post_comment
(
    id             varchar(255)  not null
        primary key,
    timestamp      timestamp     not null,
    post_id        varchar(255)  not null
        references blog,
    account_id     varchar(255)
        references user_account,
    parent_comment varchar(255)
        references post_comment,
    comment        varchar(255)  not null,
    status         commentstatus not null,
    comment_type   commenttype   not null
);

create table account_otp
(
    id             bigserial
        primary key,
    account_id     varchar(255) not null
        references user_account,
    email          varchar(255) not null,
    otp            varchar(255) not null,
    activity_type  varchar(255) not null,
    otp_expiration timestamp    not null
);

create table order_history
(
    id               varchar(255)  not null
        primary key,
    user_id          varchar(255)  not null
        references user_account,
    order_date       timestamp     not null,
    delivery_address varchar(255)  not null,
    note             varchar(255)  not null,
    total_price      real          not null,
    receiver         varchar(255)  not null,
    phonenumber      varchar(255)  not null,
    coupon           varchar
        references coupon,
    payment_method   paymentmethod not null,
    payment_status   paymentstatus not null,
    order_status     orderstatus   not null
);

create table order_history_items
(
    order_history_id varchar(255)
        references order_history,
    product_id       varchar(255),
    date             timestamp,
    quantity         integer,
    foreign key (date, product_id) references product_price_history
);

create table payment_transaction
(
    order_id       varchar(255)  not null
        primary key
        references order_history,
    create_time    timestamp     not null,
    payment_id     varchar(255),
    refund_id      varchar(255),
    url            varchar(255),
    transaction_id varchar(255),
    status         paymentstatus not null,
    amount         real          not null
);