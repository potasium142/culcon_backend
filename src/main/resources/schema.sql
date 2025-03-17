-- public.blog definition

-- Drop table

-- DROP TABLE public.blog;

CREATE TABLE public.blog
(
    id          varchar(255) NOT NULL,
    title       varchar(255) NOT NULL,
    description text         NOT NULL,
    article     text         NOT NULL,
    thumbnail   varchar(255) NOT NULL,
    infos       jsonb        NOT NULL,
    CONSTRAINT blog_pkey PRIMARY KEY (id)
);


-- public.coupon definition

-- Drop table

-- DROP TABLE public.coupon;

CREATE TABLE public.coupon
(
    id            varchar NOT NULL,
    expire_time   date    NOT NULL,
    sale_percent  float4  NOT NULL,
    usage_amount  int4    NOT NULL,
    usage_left    int4    NOT NULL,
    minimum_price float4  NOT NULL,
    CONSTRAINT coupon_pkey PRIMARY KEY (id)
);


-- public.product definition

-- Drop table

-- DROP TABLE public.product;

CREATE TABLE public.product
(
    id                 varchar(255)           NOT NULL,
    product_name       varchar                NOT NULL,
    available_quantity int4                   NOT NULL,
    product_types      public."producttype"   NOT NULL,
    product_status     public."productstatus" NOT NULL,
    image_url          varchar                NOT NULL,
    price              float4                 NOT NULL,
    sale_percent       float4                 NOT NULL,
    CONSTRAINT product_pkey PRIMARY KEY (id)
);


-- public.user_account definition

-- Drop table

-- DROP TABLE public.user_account;

CREATE TABLE public.user_account
(
    id                  varchar(255)               NOT NULL,
    email               varchar                    NOT NULL,
    username            varchar                    NOT NULL,
    "password"          varchar                    NOT NULL,
    status              public."useraccountstatus" NOT NULL,
    address             varchar                    NOT NULL,
    phone               varchar                    NOT NULL,
    profile_pic_uri     varchar                    NOT NULL,
    profile_description varchar                    NOT NULL,
    "token"             varchar                    NOT NULL,
    bookmarked_posts    varchar                    NOT NULL,
    CONSTRAINT user_account_email_key UNIQUE (email),
    CONSTRAINT user_account_pkey PRIMARY KEY (id),
    CONSTRAINT user_account_username_key UNIQUE (username)
);


-- public.account_otp definition

-- Drop table

-- DROP TABLE public.account_otp;

CREATE TABLE public.account_otp
(
    id             bigserial    NOT NULL,
    account_id     varchar(255) NOT NULL,
    email          varchar(255) NOT NULL,
    otp            varchar(255) NOT NULL,
    activity_type  varchar(255) NOT NULL,
    otp_expiration timestamp    NOT NULL,
    CONSTRAINT account_otp_pkey PRIMARY KEY (id),
    CONSTRAINT account_otp_account_id_fkey FOREIGN KEY (account_id) REFERENCES public.user_account (id)
);


-- public.cart definition

-- Drop table

-- DROP TABLE public.cart;

CREATE TABLE public.cart
(
    amount     int4         NOT NULL,
    account_id varchar(255) NOT NULL,
    product_id varchar(255) NOT NULL,
    CONSTRAINT cart_pkey PRIMARY KEY (account_id, product_id),
    CONSTRAINT cart_account_id_fkey FOREIGN KEY (account_id) REFERENCES public.user_account (id),
    CONSTRAINT cart_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.product (id)
);


-- public.mealkit_ingredients definition

-- Drop table

-- DROP TABLE public.mealkit_ingredients;

CREATE TABLE public.mealkit_ingredients
(
    mealkit_id varchar(255) NOT NULL,
    ingredient varchar(255) NOT NULL,
    CONSTRAINT mealkit_ingredients_pkey PRIMARY KEY (mealkit_id, ingredient),
    CONSTRAINT mealkit_ingredients_ingredient_fkey FOREIGN KEY (ingredient) REFERENCES public.product (id),
    CONSTRAINT mealkit_ingredients_mealkit_id_fkey FOREIGN KEY (mealkit_id) REFERENCES public.product (id)
);


-- public.order_history definition

-- Drop table

-- DROP TABLE public.order_history;

CREATE TABLE public.order_history
(
    id               varchar(255)           NOT NULL,
    user_id          varchar(255)           NOT NULL,
    order_date       timestamp              NOT NULL,
    delivery_address varchar(255)           NOT NULL,
    note             varchar(255)           NOT NULL,
    total_price      float4                 NOT NULL,
    receiver         varchar(255)           NOT NULL,
    phonenumber      varchar(255)           NOT NULL,
    coupon           varchar                NULL,
    updated_coupon   bool                   NOT NULL,
    updated_payment  bool                   NOT NULL,
    payment_method   public."paymentmethod" NOT NULL,
    payment_status   public."paymentstatus" NOT NULL,
    order_status     public."orderstatus"   NOT NULL,
    CONSTRAINT order_history_pkey PRIMARY KEY (id),
    CONSTRAINT order_history_coupon_fkey FOREIGN KEY (coupon) REFERENCES public.coupon (id),
    CONSTRAINT order_history_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_account (id)
);


-- public.payment_transaction definition

-- Drop table

-- DROP TABLE public.payment_transaction;

CREATE TABLE public.payment_transaction
(
    order_id       uuid                   NOT NULL,
    create_time    timestamp              NOT NULL,
    payment_id     varchar(255)           NULL,
    refund_id      varchar(255)           NULL,
    url            varchar(255)           NULL,
    transaction_id varchar(255)           NULL,
    status         public."paymentstatus" NOT NULL,
    amount         float4                 NOT NULL,
    CONSTRAINT payment_transaction_pkey PRIMARY KEY (order_id),
    CONSTRAINT payment_transaction_order_id_fkey FOREIGN KEY (order_id) REFERENCES public.order_history (id)
);


-- public.post_comment definition

-- Drop table

-- DROP TABLE public.post_comment;

CREATE TABLE public.post_comment
(
    id             varchar(255)         NOT NULL,
    "timestamp"    timestamp            NOT NULL,
    post_id        varchar(255)         NOT NULL,
    account_id     varchar(255)         NULL,
    parent_comment varchar(255)         NULL,
    "comment"      varchar(255)         NOT NULL,
    deleted        bool                 NOT NULL,
    comment_type   public."commenttype" NOT NULL,
    CONSTRAINT post_comment_pkey PRIMARY KEY (id),
    CONSTRAINT post_comment_account_id_fkey FOREIGN KEY (account_id) REFERENCES public.user_account (id),
    CONSTRAINT post_comment_parent_comment_fkey FOREIGN KEY (parent_comment) REFERENCES public.post_comment (id),
    CONSTRAINT post_comment_post_id_fkey FOREIGN KEY (post_id) REFERENCES public.blog (id)
);


-- public.product_doc definition

-- Drop table

-- DROP TABLE public.product_doc;

CREATE TABLE public.product_doc
(
    id                varchar(255) NOT NULL,
    description       varchar(255) NOT NULL,
    images_url        varchar      NOT NULL,
    infos             jsonb        NOT NULL,
    ingredients       varchar      NULL,
    instructions      varchar      NULL,
    article_md        text         NOT NULL,
    day_before_expiry int4         NOT NULL,
    CONSTRAINT product_doc_pkey PRIMARY KEY (id),
    CONSTRAINT product_doc_id_fkey FOREIGN KEY (id) REFERENCES public.product (id)
);


-- public.product_embedding definition

-- Drop table

-- DROP TABLE public.product_embedding;

CREATE TABLE public.product_embedding
(
    id                varchar(255)  NOT NULL,
    images_embed_yolo public.vector NOT NULL,
    images_embed_clip public.vector NOT NULL,
    description_embed public.vector NOT NULL,
    CONSTRAINT product_embedding_pkey PRIMARY KEY (id),
    CONSTRAINT product_embedding_id_fkey FOREIGN KEY (id) REFERENCES public.product (id)
);


-- public.product_price_history definition

-- Drop table

-- DROP TABLE public.product_price_history;

CREATE TABLE public.product_price_history
(
    price        float4       NOT NULL,
    sale_percent float4       NOT NULL,
    "date"       timestamp    NOT NULL,
    product_id   varchar(255) NOT NULL,
    CONSTRAINT product_price_history_pk PRIMARY KEY (date, product_id),
    CONSTRAINT product_price_history_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.product (id)
);


-- public.order_history_items definition

-- Drop table

-- DROP TABLE public.order_history_items;

CREATE TABLE public.order_history_items
(
    order_history_id      uuid         NULL,
    product_id_product_id varchar(255) NULL,
    product_id_date       timestamp    NULL,
    quantity              int4         NULL,
    CONSTRAINT order_history_items_order_history_id_fkey FOREIGN KEY (order_history_id) REFERENCES public.order_history (id),
    CONSTRAINT order_history_items_product_id_date_product_id_product_id_fkey FOREIGN KEY (product_id_date, product_id_product_id) REFERENCES public.product_price_history ("date", product_id)
);
-- DROP SEQUENCE public.account_otp_id_seq;

CREATE SEQUENCE public.account_otp_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE;


-- DROP TYPE public."accountstatus";

CREATE TYPE public."accountstatus" AS ENUM (
    'ACTIVE',
    'DISABLE');

-- DROP TYPE public."accounttype";

CREATE TYPE public."accounttype" AS ENUM (
    'MANAGER',
    'STAFF');

-- DROP TYPE public."commenttype";

CREATE TYPE public."commenttype" AS ENUM (
    'POST',
    'REPLY');

-- DROP TYPE public."orderstatus";

CREATE TYPE public."orderstatus" AS ENUM (
    'ON_CONFIRM',
    'ON_PROCESSING',
    'ON_SHIPPING',
    'SHIPPED',
    'CANCELLED');

-- DROP TYPE public."paymentmethod";

CREATE TYPE public."paymentmethod" AS ENUM (
    'PAYPAL',
    'VNPAY',
    'COD');

-- DROP TYPE public."paymentstatus";

CREATE TYPE public."paymentstatus" AS ENUM (
    'PENDING',
    'RECEIVED',
    'REFUNDED',
    'REFUNDING',
    'CREATED',
    'CHANGED');

-- DROP TYPE public."productstatus";

CREATE TYPE public."productstatus" AS ENUM (
    'IN_STOCK',
    'OUT_OF_STOCK',
    'NO_LONGER_IN_SALE');

-- DROP TYPE public."producttype";

CREATE TYPE public."producttype" AS ENUM (
    'VEGETABLE',
    'MEAT',
    'SEASON',
    'MEALKIT');

-- DROP TYPE public."useraccountstatus";

CREATE TYPE public."useraccountstatus" AS ENUM (
    'NON_ACTIVE',
    'NORMAL',
    'BANNED',
    'DEACTIVATE');