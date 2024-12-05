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
-- Name: accountstatus; Type: TYPE; Schema: public; Owner: culcon
--

CREATE TYPE public.accountstatus AS ENUM (
    'BANNED',
    'DEACTIVATE',
    'NON_ACTIVE',
    'NORMAL'
    );


ALTER TYPE public.accountstatus OWNER TO culcon;

--
-- Name: role; Type: TYPE; Schema: public; Owner: culcon
--

CREATE TYPE public.role AS ENUM (
    'CUSTOMER'
    );


ALTER TYPE public.role OWNER TO culcon;

--
-- Name: CAST (public.accountstatus AS character varying); Type: CAST; Schema: -; Owner: -
--

CREATE CAST (public.accountstatus AS character varying) WITH INOUT AS IMPLICIT;


--
-- Name: CAST (public.role AS character varying); Type: CAST; Schema: -; Owner: -
--

CREATE CAST (public.role AS character varying) WITH INOUT AS IMPLICIT;


--
-- Name: CAST (character varying AS public.accountstatus); Type: CAST; Schema: -; Owner: -
--

CREATE CAST (character varying AS public.accountstatus) WITH INOUT AS IMPLICIT;


--
-- Name: CAST (character varying AS public.role); Type: CAST; Schema: -; Owner: -
--

CREATE CAST (character varying AS public.role) WITH INOUT AS IMPLICIT;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: account; Type: TABLE; Schema: public; Owner: culcon
--

CREATE TABLE public.account
(
    id                  character varying(255) NOT NULL,
    address             character varying(255),
    email               character varying(255) NOT NULL,
    password            character varying(255) NOT NULL,
    phone               character varying(12),
    profile_description character varying(255),
    profile_pic_uri     character varying(255),
    token               character varying(255),
    username            character varying(255) NOT NULL,
    role                public.role,
    status              public.accountstatus
);


ALTER TABLE public.account
    OWNER TO culcon;

--
-- Name: account_otp; Type: TABLE; Schema: public; Owner: culcon
--

CREATE TABLE public.account_otp
(
    account_id     character varying(255) NOT NULL,
    otp            character varying(255),
    otp_expiration timestamp(6) without time zone
);


ALTER TABLE public.account_otp
    OWNER TO culcon;

--
-- Name: bookmarked_post; Type: TABLE; Schema: public; Owner: culcon
--

CREATE TABLE public.bookmarked_post
(
    postid      character varying(255)         NOT NULL,
    "timestamp" timestamp(6) without time zone NOT NULL,
    bookmarked  boolean,
    rated       integer,
    account_id  character varying(255)         NOT NULL
);


ALTER TABLE public.bookmarked_post
    OWNER TO culcon;

--
-- Name: cart; Type: TABLE; Schema: public; Owner: culcon
--

CREATE TABLE public.cart
(
    account_id character varying(255) NOT NULL,
    amount     integer,
    cart_key   character varying(255) NOT NULL
);


ALTER TABLE public.cart
    OWNER TO culcon;

--
-- Name: post_comment; Type: TABLE; Schema: public; Owner: culcon
--

CREATE TABLE public.post_comment
(
    postid      character varying(255)         NOT NULL,
    "timestamp" timestamp(6) without time zone NOT NULL,
    comment     character varying(255),
    account_id  character varying(255)         NOT NULL
);


ALTER TABLE public.post_comment
    OWNER TO culcon;

--
-- Data for Name: account; Type: TABLE DATA; Schema: public; Owner: culcon
--

COPY public.account (id, address, email, password, phone, profile_description, profile_pic_uri, token, username, role,
                     status) FROM stdin;
b1e303bd-664e-4374-a019-38d25c8eb003		example@admin0	$2a$10$n7NTAk2ymn6sYQEmwnqbI.mIqOBFSAWdXoZewi.PiPxQqnZiQq9zq	0123456799		defaultProfile	\N	admin	CUSTOMER	NORMAL
\.


--
-- Data for Name: account_otp; Type: TABLE DATA; Schema: public; Owner: culcon
--

COPY public.account_otp (account_id, otp, otp_expiration) FROM stdin;
\.


--
-- Data for Name: bookmarked_post; Type: TABLE DATA; Schema: public; Owner: culcon
--

COPY public.bookmarked_post (postid, "timestamp", bookmarked, rated, account_id) FROM stdin;
\.


--
-- Data for Name: cart; Type: TABLE DATA; Schema: public; Owner: culcon
--

COPY public.cart (account_id, amount, cart_key) FROM stdin;
\.


--
-- Data for Name: post_comment; Type: TABLE DATA; Schema: public; Owner: culcon
--

COPY public.post_comment (postid, "timestamp", comment, account_id) FROM stdin;
\.


--
-- Name: account_otp account_otp_pkey; Type: CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.account_otp
    ADD CONSTRAINT account_otp_pkey PRIMARY KEY (account_id);


--
-- Name: account account_pkey; Type: CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_pkey PRIMARY KEY (id);


--
-- Name: bookmarked_post bookmarked_post_pkey; Type: CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.bookmarked_post
    ADD CONSTRAINT bookmarked_post_pkey PRIMARY KEY (account_id, postid, "timestamp");


--
-- Name: cart cart_pkey; Type: CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.cart
    ADD CONSTRAINT cart_pkey PRIMARY KEY (account_id, cart_key);


--
-- Name: account email; Type: CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT email UNIQUE (email);


--
-- Name: account phone; Type: CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT phone UNIQUE (phone);


--
-- Name: post_comment post_comment_pkey; Type: CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.post_comment
    ADD CONSTRAINT post_comment_pkey PRIMARY KEY (account_id, postid, "timestamp");


--
-- Name: account username; Type: CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT username UNIQUE (username);


--
-- Name: account_otp fk4tfy69nwbjdvo3hgb70glaojf; Type: FK CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.account_otp
    ADD CONSTRAINT fk4tfy69nwbjdvo3hgb70glaojf FOREIGN KEY (account_id) REFERENCES public.account (id);


--
-- Name: post_comment fkd7qilsmcc7t1tey8hntgj7s5n; Type: FK CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.post_comment
    ADD CONSTRAINT fkd7qilsmcc7t1tey8hntgj7s5n FOREIGN KEY (account_id) REFERENCES public.account (id);


--
-- Name: cart fkfcnil942p5dgoolttai4kyg7k; Type: FK CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.cart
    ADD CONSTRAINT fkfcnil942p5dgoolttai4kyg7k FOREIGN KEY (account_id) REFERENCES public.account (id);


--
-- Name: bookmarked_post fksf1xs43r4i30xk0q1g49hy1je; Type: FK CONSTRAINT; Schema: public; Owner: culcon
--

ALTER TABLE ONLY public.bookmarked_post
    ADD CONSTRAINT fksf1xs43r4i30xk0q1g49hy1je FOREIGN KEY (account_id) REFERENCES public.account (id);


--
-- PostgreSQL database dump complete
--
