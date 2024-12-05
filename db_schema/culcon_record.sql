--
-- PostgreSQL database dump
--

-- Dumped from database version 17.1 (Debian 17.1-1.pgdg120+1)
-- Dumped by pg_dump version 17.1 (Debian 17.1-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: orderstatus; Type: TYPE; Schema: public; Owner: culcon
--

CREATE TYPE public.orderstatus AS ENUM (
    'CANCELLED',
    'ON_CONFIRM',
    'ON_PROCESSING',
    'ON_SHIPPING',
    'SHIPPED',
    'WAIT_FOR_PAYMENT'
    );


ALTER TYPE public.orderstatus OWNER TO culcon;

--
-- Name: productstatus; Type: TYPE; Schema: public; Owner: culcon
--

CREATE TYPE public.productstatus AS ENUM (
    'IN_STOCK',
    'NO_LONGER_IN_SALE',
    'OUT_OF_STOCK'
    );


ALTER TYPE public.productstatus OWNER TO culcon;

--
-- Name: producttype; Type: TYPE; Schema: public; Owner: culcon
--

CREATE TYPE public.producttype AS ENUM (
    'MEALKIT',
    'MEAT',
    'SEASON',
    'VEGETABLE'
    );


ALTER TYPE public.producttype OWNER TO culcon;

--
-- Name: CAST (public.orderstatus AS character varying); Type: CAST; Schema: -; Owner: -
--

CREATE CAST (public.orderstatus AS character varying) WITH INOUT AS IMPLICIT;


--
-- Name: CAST (public.productstatus AS character varying); Type: CAST; Schema: -; Owner: -
--

CREATE CAST (public.productstatus AS character varying) WITH INOUT AS IMPLICIT;


--
-- Name: CAST (public.producttype AS character varying); Type: CAST; Schema: -; Owner: -
--

CREATE CAST (public.producttype AS character varying) WITH INOUT AS IMPLICIT;


--
-- Name: CAST (character varying AS public.orderstatus); Type: CAST; Schema: -; Owner: -
--

CREATE CAST (character varying AS public.orderstatus) WITH INOUT AS IMPLICIT;


--
-- Name: CAST (character varying AS public.productstatus); Type: CAST; Schema: -; Owner: -
--

CREATE CAST (character varying AS public.productstatus) WITH INOUT AS IMPLICIT;


--
-- Name: CAST (character varying AS public.producttype); Type: CAST; Schema: -; Owner: -
--

CREATE CAST (character varying AS public.producttype) WITH INOUT AS IMPLICIT;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: coupon; Type: TABLE; Schema: public; Owner: culcon
--

CREATE TABLE public.coupon
(
    id           character varying(255) NOT NULL,
    expire_time  date,
    sale_percent real,
    usage_amount integer,
    usage_left   integer
);


ALTER TABLE public.coupon
    OWNER TO culcon;

--
-- Name: order_history; Type: TABLE; Schema: public; Owner: culcon
--

CREATE TABLE public.order_history
(
    id           bigint NOT NULL,
    order_date   timestamp(6) without time zone,
    order_status public.orderstatus,
    total_price  real,
    user_id      character varying(255),
    coupon       character varying(255)
);


ALTER TABLE public.order_history
    OWNER TO culcon;

--
-- Name: order_history_seq; Type: SEQUENCE; Schema: public; Owner: culcon
--

CREATE SEQUENCE public.order_history_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.order_history_seq OWNER TO culcon;

--
-- Name: orderhistory_items; Type: TABLE; Schema: public; Owner: culcon
--

CREATE TABLE public.orderhistory_items
(
    orderhistory_id    bigint NOT NULL,
    orderid_date       timestamp(6) without time zone,
    orderid_product_id character varying(255),
    quantity           integer
);


ALTER TABLE public.orderhistory_items
    OWNER TO culcon;

--
-- Name: product; Type: TABLE; Schema: public; Owner: culcon
--

CREATE TABLE public.product
(
    id                 character varying(255) NOT NULL,
    available_quantity integer,
    image_url          character varying(255),
    price              real,
    product_name       character varying(255),
    product_status     public.productstatus,
    product_types      public.producttype,
    sale_percent       real
);


ALTER TABLE public.product
    OWNER TO culcon;

--
-- Name: product_price_history; Type: TABLE; Schema: public; Owner: culcon
--

CREATE TABLE public.product_price_history
(
    date         timestamp(6) without time zone NOT NULL,
    price        real,
    sale_percent real,
    product_id   character varying(255)         NOT NULL
);


ALTER TABLE public.product_price_history
    OWNER TO culcon;

--
-- Data for Name: coupon; Type: TABLE DATA; Schema: public; Owner: culcon
--

COPY public.coupon (id, expire_time, sale_percent, usage_amount, usage_left) FROM stdin;
\.


--
-- Data for Name: order_history; Type: TABLE DATA; Schema: public; Owner: culcon
--

COPY public.order_history (id, order_date, order_status, total_price, user_id, coupon) FROM stdin;
\.


--
-- Data for Name: orderhistory_items; Type: TABLE DATA; Schema: public; Owner: culcon
--

COPY public.orderhistory_items (orderhistory_id, orderid_date, orderid_product_id, quantity) FROM stdin;
\.


--
-- Data for Name: product; Type: TABLE DATA; Schema: public; Owner: culcon
--

COPY public.product (id, available_quantity, image_url, price, product_name, product_status, product_types,
                     sale_percent) FROM stdin;
\.


--
-- Data for Name: product_price_history; Type: TABLE DATA; Schema: public; Owner: culcon
--

COPY public.product_price_history (date, price, sale_percent, product_id) FROM stdin;
\.


--
-- Name: order_history_seq; Type: SEQUENCE SET; Schema: public; Owner: culcon
--

SELECT pg_catalog.setval('public.order_history_seq', 1, false);


--
-- Name: coupon coupon_pkey; Type: CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.coupon
    ADD CONSTRAINT coupon_pkey PRIMARY KEY (id);


--
-- Name: order_history order_history_pkey; Type: CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.order_history
    ADD CONSTRAINT order_history_pkey PRIMARY KEY (id);


--
-- Name: product product_pkey; Type: CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT product_pkey PRIMARY KEY (id);


--
-- Name: product_price_history product_price_history_pkey; Type: CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.product_price_history
    ADD CONSTRAINT product_price_history_pkey PRIMARY KEY (date, product_id);


--
-- Name: product_price_history fkf0bksln3xky2vmhp66s5md5jd; Type: FK CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.product_price_history
    ADD CONSTRAINT fkf0bksln3xky2vmhp66s5md5jd FOREIGN KEY (product_id) REFERENCES public.product (id);


--
-- Name: orderhistory_items fkivojmc9ml0647ypfhqxhf0ek3; Type: FK CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.orderhistory_items
    ADD CONSTRAINT fkivojmc9ml0647ypfhqxhf0ek3 FOREIGN KEY (orderhistory_id) REFERENCES public.order_history (id);


--
-- Name: order_history fkj9l7p822e6hoqm8qvmawjf2ik; Type: FK CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.order_history
    ADD CONSTRAINT fkj9l7p822e6hoqm8qvmawjf2ik FOREIGN KEY (coupon) REFERENCES public.coupon (id);


--
-- Name: orderhistory_items fksbals5ljfxyhql0ofmodann86; Type: FK CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.orderhistory_items
    ADD CONSTRAINT fksbals5ljfxyhql0ofmodann86 FOREIGN KEY (orderid_date, orderid_product_id) REFERENCES public.product_price_history (date, product_id);


--
-- PostgreSQL database dump complete
--