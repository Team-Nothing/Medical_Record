--
-- PostgreSQL database dump
--

-- Dumped from database version 16.3 (Debian 16.3-1.pgdg120+1)
-- Dumped by pg_dump version 16.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


--
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


--
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: account; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.account (
    uid uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    username character varying(50) NOT NULL,
    password character varying(128) NOT NULL,
    last_login timestamp without time zone DEFAULT now(),
    last_active timestamp without time zone DEFAULT now()
);


ALTER TABLE public.account OWNER TO postgres;

--
-- Name: admission_record; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.admission_record (
    serial_id bigint NOT NULL,
    admission_date timestamp without time zone DEFAULT now(),
    discharge_date timestamp without time zone,
    uid character varying(50) NOT NULL,
    bed_id integer NOT NULL,
    doctor_id integer NOT NULL,
    nurse_id integer NOT NULL
);


ALTER TABLE public.admission_record OWNER TO postgres;

--
-- Name: admission_record_serial_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.admission_record_serial_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.admission_record_serial_id_seq OWNER TO postgres;

--
-- Name: admission_record_serial_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.admission_record_serial_id_seq OWNED BY public.admission_record.serial_id;


--
-- Name: bed; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bed (
    bed_id smallint NOT NULL,
    bed_type_id integer NOT NULL,
    floor integer NOT NULL,
    room_number integer NOT NULL,
    bed_position character varying(2) NOT NULL
);


ALTER TABLE public.bed OWNER TO postgres;

--
-- Name: bed_bed_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.bed_bed_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.bed_bed_id_seq OWNER TO postgres;

--
-- Name: bed_bed_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.bed_bed_id_seq OWNED BY public.bed.bed_id;


--
-- Name: bed_device; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bed_device (
    device_id integer NOT NULL,
    bed_id integer NOT NULL
);


ALTER TABLE public.bed_device OWNER TO postgres;

--
-- Name: bed_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bed_type (
    bed_type_id smallint NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE public.bed_type OWNER TO postgres;

--
-- Name: bed_type_bed_type_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.bed_type_bed_type_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.bed_type_bed_type_id_seq OWNER TO postgres;

--
-- Name: bed_type_bed_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.bed_type_bed_type_id_seq OWNED BY public.bed_type.bed_type_id;


--
-- Name: blood; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.blood (
    blood_id smallint NOT NULL,
    name character varying(10) NOT NULL
);


ALTER TABLE public.blood OWNER TO postgres;

--
-- Name: blood_blood_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.blood_blood_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.blood_blood_id_seq OWNER TO postgres;

--
-- Name: blood_blood_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.blood_blood_id_seq OWNED BY public.blood.blood_id;


--
-- Name: device; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.device (
    device_id integer NOT NULL,
    device_type_id integer NOT NULL,
    account_uid uuid NOT NULL,
    bluetooth_mac character varying(50),
    ipv6 character varying(50),
    ipv4 character varying(50)
);


ALTER TABLE public.device OWNER TO postgres;

--
-- Name: device_device_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.device_device_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.device_device_id_seq OWNER TO postgres;

--
-- Name: device_device_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.device_device_id_seq OWNED BY public.device.device_id;


--
-- Name: device_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.device_type (
    device_type_id smallint NOT NULL,
    name character varying(50) NOT NULL,
    description text
);


ALTER TABLE public.device_type OWNER TO postgres;

--
-- Name: device_type_device_type_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.device_type_device_type_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.device_type_device_type_id_seq OWNER TO postgres;

--
-- Name: device_type_device_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.device_type_device_type_id_seq OWNED BY public.device_type.device_type_id;


--
-- Name: doctor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.doctor (
    doctor_id integer NOT NULL,
    name character varying(50) NOT NULL,
    email character varying(50),
    feature_id integer
);


ALTER TABLE public.doctor OWNER TO postgres;

--
-- Name: doctor_doctor_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.doctor_doctor_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.doctor_doctor_id_seq OWNER TO postgres;

--
-- Name: doctor_doctor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.doctor_doctor_id_seq OWNED BY public.doctor.doctor_id;


--
-- Name: family; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.family (
    uid character varying(50) NOT NULL,
    family_uid character varying(50) NOT NULL,
    family_type_id integer NOT NULL
);


ALTER TABLE public.family OWNER TO postgres;

--
-- Name: family_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.family_type (
    family_type_id smallint NOT NULL,
    name character varying(20) NOT NULL
);


ALTER TABLE public.family_type OWNER TO postgres;

--
-- Name: family_type_family_type_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.family_type_family_type_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.family_type_family_type_id_seq OWNER TO postgres;

--
-- Name: family_type_family_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.family_type_family_type_id_seq OWNED BY public.family_type.family_type_id;


--
-- Name: feature; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.feature (
    feature_id bigint NOT NULL,
    model_id integer NOT NULL
);


ALTER TABLE public.feature OWNER TO postgres;

--
-- Name: feature_feature_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.feature_feature_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.feature_feature_id_seq OWNER TO postgres;

--
-- Name: feature_feature_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.feature_feature_id_seq OWNED BY public.feature.feature_id;


--
-- Name: gender; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.gender (
    gender_id smallint NOT NULL,
    name character varying(20) NOT NULL
);


ALTER TABLE public.gender OWNER TO postgres;

--
-- Name: gender_gender_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.gender_gender_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.gender_gender_id_seq OWNER TO postgres;

--
-- Name: gender_gender_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.gender_gender_id_seq OWNED BY public.gender.gender_id;


--
-- Name: language; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.language (
    language_id smallint NOT NULL,
    name character varying(20) NOT NULL
);


ALTER TABLE public.language OWNER TO postgres;

--
-- Name: language_language_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.language_language_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.language_language_id_seq OWNER TO postgres;

--
-- Name: language_language_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.language_language_id_seq OWNED BY public.language.language_id;


--
-- Name: model; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.model (
    model_id smallint NOT NULL,
    name character varying(50) NOT NULL,
    version character varying(10) NOT NULL,
    description text
);


ALTER TABLE public.model OWNER TO postgres;

--
-- Name: model_model_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.model_model_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.model_model_id_seq OWNER TO postgres;

--
-- Name: model_model_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.model_model_id_seq OWNED BY public.model.model_id;


--
-- Name: nurse; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nurse (
    nurse_id integer NOT NULL,
    name character varying(50) NOT NULL,
    email character varying(50),
    feature_id integer
);


ALTER TABLE public.nurse OWNER TO postgres;

--
-- Name: nurse_nurse_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.nurse_nurse_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.nurse_nurse_id_seq OWNER TO postgres;

--
-- Name: nurse_nurse_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.nurse_nurse_id_seq OWNED BY public.nurse.nurse_id;


--
-- Name: patient; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.patient (
    uid character varying(50) NOT NULL,
    name character varying(50) NOT NULL,
    email character varying(50),
    phone character varying(15) NOT NULL,
    address character varying(250) NOT NULL,
    birth date NOT NULL,
    gender_id integer NOT NULL,
    blood_id integer NOT NULL,
    language_id integer NOT NULL,
    feature_id integer
);


ALTER TABLE public.patient OWNER TO postgres;

--
-- Name: transcript_record; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.transcript_record (
    serial_id bigint NOT NULL,
    admission_record_id integer NOT NULL,
    feature_id integer,
    datetime timestamp without time zone NOT NULL,
    content text
);


ALTER TABLE public.transcript_record OWNER TO postgres;

--
-- Name: transcript_record_serial_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.transcript_record_serial_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.transcript_record_serial_id_seq OWNER TO postgres;

--
-- Name: transcript_record_serial_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.transcript_record_serial_id_seq OWNED BY public.transcript_record.serial_id;


--
-- Name: admission_record serial_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_record ALTER COLUMN serial_id SET DEFAULT nextval('public.admission_record_serial_id_seq'::regclass);


--
-- Name: bed bed_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bed ALTER COLUMN bed_id SET DEFAULT nextval('public.bed_bed_id_seq'::regclass);


--
-- Name: bed_type bed_type_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bed_type ALTER COLUMN bed_type_id SET DEFAULT nextval('public.bed_type_bed_type_id_seq'::regclass);


--
-- Name: blood blood_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.blood ALTER COLUMN blood_id SET DEFAULT nextval('public.blood_blood_id_seq'::regclass);


--
-- Name: device device_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.device ALTER COLUMN device_id SET DEFAULT nextval('public.device_device_id_seq'::regclass);


--
-- Name: device_type device_type_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.device_type ALTER COLUMN device_type_id SET DEFAULT nextval('public.device_type_device_type_id_seq'::regclass);


--
-- Name: doctor doctor_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.doctor ALTER COLUMN doctor_id SET DEFAULT nextval('public.doctor_doctor_id_seq'::regclass);


--
-- Name: family_type family_type_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.family_type ALTER COLUMN family_type_id SET DEFAULT nextval('public.family_type_family_type_id_seq'::regclass);


--
-- Name: feature feature_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.feature ALTER COLUMN feature_id SET DEFAULT nextval('public.feature_feature_id_seq'::regclass);


--
-- Name: gender gender_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gender ALTER COLUMN gender_id SET DEFAULT nextval('public.gender_gender_id_seq'::regclass);


--
-- Name: language language_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.language ALTER COLUMN language_id SET DEFAULT nextval('public.language_language_id_seq'::regclass);


--
-- Name: model model_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.model ALTER COLUMN model_id SET DEFAULT nextval('public.model_model_id_seq'::regclass);


--
-- Name: nurse nurse_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nurse ALTER COLUMN nurse_id SET DEFAULT nextval('public.nurse_nurse_id_seq'::regclass);


--
-- Name: transcript_record serial_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transcript_record ALTER COLUMN serial_id SET DEFAULT nextval('public.transcript_record_serial_id_seq'::regclass);


--
-- Data for Name: account; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.account (uid, username, password, last_login, last_active) FROM stdin;
8ea39cec-a142-4925-a2cd-35d12189624d	nothing-chang2	$2a$06$npF3Hom0iTbNw83aopcBTORif8m.ke8ioXRXXy.uG6a9NPtIMs5Uy	2024-08-25 18:05:12.416195	2024-08-25 18:05:12.416195
\.


--
-- Data for Name: admission_record; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.admission_record (serial_id, admission_date, discharge_date, uid, bed_id, doctor_id, nurse_id) FROM stdin;
1	2024-08-25 18:03:56.082667	\N	dc9f8ae2-5a12-4cba-a366-2120417d6ba0	1	1	1
\.


--
-- Data for Name: bed; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.bed (bed_id, bed_type_id, floor, room_number, bed_position) FROM stdin;
1	1	3	338	C
\.


--
-- Data for Name: bed_device; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.bed_device (device_id, bed_id) FROM stdin;
\.


--
-- Data for Name: bed_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.bed_type (bed_type_id, name) FROM stdin;
1	Health Insurance Single
2	Health Insurance Double
3	Self-paid Single
4	Self-paid Double
\.


--
-- Data for Name: blood; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.blood (blood_id, name) FROM stdin;
1	A+
2	A-
3	B+
4	B-
5	AB+
6	AB-
7	O+
8	O-
\.


--
-- Data for Name: device; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.device (device_id, device_type_id, account_uid, bluetooth_mac, ipv6, ipv4) FROM stdin;
\.


--
-- Data for Name: device_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.device_type (device_type_id, name, description) FROM stdin;
1	Screen with Whisper	\N
\.


--
-- Data for Name: doctor; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.doctor (doctor_id, name, email, feature_id) FROM stdin;
1	Dr. LED	ledlab2391@gmail.com	2
\.


--
-- Data for Name: family; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.family (uid, family_uid, family_type_id) FROM stdin;
dc9f8ae2-5a12-4cba-a366-2120417d6ba0	1bdcd810-3f7d-4b5b-a647-38c2ec85bb78	1
\.


--
-- Data for Name: family_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.family_type (family_type_id, name) FROM stdin;
1	Mother
2	Father
3	Brother
4	Sister
5	Grandmother
6	Grandfather
\.


--
-- Data for Name: feature; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.feature (feature_id, model_id) FROM stdin;
1	1
2	1
\.


--
-- Data for Name: gender; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.gender (gender_id, name) FROM stdin;
1	Male
2	Female
\.


--
-- Data for Name: language; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.language (language_id, name) FROM stdin;
1	Chinese
2	English
3	Japanese
4	Vietnamese
\.


--
-- Data for Name: model; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.model (model_id, name, version, description) FROM stdin;
1	whisper-small-01	0.0.1	The first version of whisper small model
\.


--
-- Data for Name: nurse; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.nurse (nurse_id, name, email, feature_id) FROM stdin;
1	Fish	fish@gmail.com	\N
\.


--
-- Data for Name: patient; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.patient (uid, name, email, phone, address, birth, gender_id, blood_id, language_id, feature_id) FROM stdin;
dc9f8ae2-5a12-4cba-a366-2120417d6ba0	張聖坤	jdps99119@gmail.com	0918214333	台中市烏日區 XDD XDD XDD	2003-11-18	1	4	1	1
1bdcd810-3f7d-4b5b-a647-38c2ec85bb78	XDD	xdd@gmail.com	0987877887	高雄市楠梓區 XDD XDD XDD	1987-01-01	2	6	2	\N
\.


--
-- Data for Name: transcript_record; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.transcript_record (serial_id, admission_record_id, feature_id, datetime, content) FROM stdin;
1	1	1	2024-08-25 18:03:56.118277	Hello, I am Dr. LED
\.


--
-- Name: admission_record_serial_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.admission_record_serial_id_seq', 1, true);


--
-- Name: bed_bed_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.bed_bed_id_seq', 1, true);


--
-- Name: bed_type_bed_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.bed_type_bed_type_id_seq', 4, true);


--
-- Name: blood_blood_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.blood_blood_id_seq', 8, true);


--
-- Name: device_device_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.device_device_id_seq', 1, false);


--
-- Name: device_type_device_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.device_type_device_type_id_seq', 1, true);


--
-- Name: doctor_doctor_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.doctor_doctor_id_seq', 1, true);


--
-- Name: family_type_family_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.family_type_family_type_id_seq', 6, true);


--
-- Name: feature_feature_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.feature_feature_id_seq', 2, true);


--
-- Name: gender_gender_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.gender_gender_id_seq', 2, true);


--
-- Name: language_language_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.language_language_id_seq', 4, true);


--
-- Name: model_model_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.model_model_id_seq', 1, true);


--
-- Name: nurse_nurse_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.nurse_nurse_id_seq', 1, true);


--
-- Name: transcript_record_serial_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.transcript_record_serial_id_seq', 1, true);


--
-- Name: account account_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_pkey PRIMARY KEY (uid);


--
-- Name: account account_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_username_key UNIQUE (username);


--
-- Name: admission_record admission_record_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_record
    ADD CONSTRAINT admission_record_pkey PRIMARY KEY (serial_id);


--
-- Name: bed_device bed_device_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bed_device
    ADD CONSTRAINT bed_device_pkey PRIMARY KEY (device_id);


--
-- Name: bed bed_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bed
    ADD CONSTRAINT bed_pkey PRIMARY KEY (bed_id);


--
-- Name: bed_type bed_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bed_type
    ADD CONSTRAINT bed_type_pkey PRIMARY KEY (bed_type_id);


--
-- Name: blood blood_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.blood
    ADD CONSTRAINT blood_pkey PRIMARY KEY (blood_id);


--
-- Name: device device_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.device
    ADD CONSTRAINT device_pkey PRIMARY KEY (device_id);


--
-- Name: device_type device_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.device_type
    ADD CONSTRAINT device_type_pkey PRIMARY KEY (device_type_id);


--
-- Name: doctor doctor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.doctor
    ADD CONSTRAINT doctor_pkey PRIMARY KEY (doctor_id);


--
-- Name: family family_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.family
    ADD CONSTRAINT family_pkey PRIMARY KEY (uid, family_uid);


--
-- Name: family_type family_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.family_type
    ADD CONSTRAINT family_type_pkey PRIMARY KEY (family_type_id);


--
-- Name: feature feature_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.feature
    ADD CONSTRAINT feature_pkey PRIMARY KEY (feature_id);


--
-- Name: gender gender_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gender
    ADD CONSTRAINT gender_pkey PRIMARY KEY (gender_id);


--
-- Name: language language_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.language
    ADD CONSTRAINT language_pkey PRIMARY KEY (language_id);


--
-- Name: model model_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.model
    ADD CONSTRAINT model_pkey PRIMARY KEY (model_id);


--
-- Name: nurse nurse_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nurse
    ADD CONSTRAINT nurse_pkey PRIMARY KEY (nurse_id);


--
-- Name: patient patient_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.patient
    ADD CONSTRAINT patient_pkey PRIMARY KEY (uid);


--
-- Name: transcript_record transcript_record_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transcript_record
    ADD CONSTRAINT transcript_record_pkey PRIMARY KEY (serial_id);


--
-- Name: device unique_device_combination; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.device
    ADD CONSTRAINT unique_device_combination UNIQUE (device_type_id, account_uid, bluetooth_mac, ipv6, ipv4);


--
-- Name: admission_record admission_record_bed_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_record
    ADD CONSTRAINT admission_record_bed_id_fkey FOREIGN KEY (bed_id) REFERENCES public.bed(bed_id);


--
-- Name: admission_record admission_record_doctor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_record
    ADD CONSTRAINT admission_record_doctor_id_fkey FOREIGN KEY (doctor_id) REFERENCES public.doctor(doctor_id);


--
-- Name: admission_record admission_record_nurse_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_record
    ADD CONSTRAINT admission_record_nurse_id_fkey FOREIGN KEY (nurse_id) REFERENCES public.nurse(nurse_id);


--
-- Name: admission_record admission_record_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_record
    ADD CONSTRAINT admission_record_uid_fkey FOREIGN KEY (uid) REFERENCES public.patient(uid);


--
-- Name: bed bed_bed_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bed
    ADD CONSTRAINT bed_bed_type_id_fkey FOREIGN KEY (bed_type_id) REFERENCES public.bed_type(bed_type_id);


--
-- Name: bed_device bed_device_bed_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bed_device
    ADD CONSTRAINT bed_device_bed_id_fkey FOREIGN KEY (bed_id) REFERENCES public.bed(bed_id);


--
-- Name: bed_device bed_device_device_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bed_device
    ADD CONSTRAINT bed_device_device_id_fkey FOREIGN KEY (device_id) REFERENCES public.device(device_id);


--
-- Name: device device_account_uid_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.device
    ADD CONSTRAINT device_account_uid_fk FOREIGN KEY (account_uid) REFERENCES public.account(uid);


--
-- Name: device device_device_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.device
    ADD CONSTRAINT device_device_type_id_fkey FOREIGN KEY (device_type_id) REFERENCES public.device_type(device_type_id);


--
-- Name: doctor doctor_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.doctor
    ADD CONSTRAINT doctor_feature_id_fkey FOREIGN KEY (feature_id) REFERENCES public.feature(feature_id);


--
-- Name: family family_family_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.family
    ADD CONSTRAINT family_family_type_id_fkey FOREIGN KEY (family_type_id) REFERENCES public.family_type(family_type_id);


--
-- Name: family family_family_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.family
    ADD CONSTRAINT family_family_uid_fkey FOREIGN KEY (family_uid) REFERENCES public.patient(uid);


--
-- Name: family family_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.family
    ADD CONSTRAINT family_uid_fkey FOREIGN KEY (uid) REFERENCES public.patient(uid);


--
-- Name: feature feature_model_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.feature
    ADD CONSTRAINT feature_model_id_fkey FOREIGN KEY (model_id) REFERENCES public.model(model_id);


--
-- Name: nurse nurse_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nurse
    ADD CONSTRAINT nurse_feature_id_fkey FOREIGN KEY (feature_id) REFERENCES public.feature(feature_id);


--
-- Name: patient patient_blood_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.patient
    ADD CONSTRAINT patient_blood_id_fkey FOREIGN KEY (blood_id) REFERENCES public.blood(blood_id);


--
-- Name: patient patient_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.patient
    ADD CONSTRAINT patient_feature_id_fkey FOREIGN KEY (feature_id) REFERENCES public.feature(feature_id);


--
-- Name: patient patient_gender_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.patient
    ADD CONSTRAINT patient_gender_id_fkey FOREIGN KEY (gender_id) REFERENCES public.gender(gender_id);


--
-- Name: patient patient_language_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.patient
    ADD CONSTRAINT patient_language_id_fkey FOREIGN KEY (language_id) REFERENCES public.language(language_id);


--
-- Name: transcript_record transcript_record_admission_record_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transcript_record
    ADD CONSTRAINT transcript_record_admission_record_id_fkey FOREIGN KEY (admission_record_id) REFERENCES public.admission_record(serial_id);


--
-- Name: transcript_record transcript_record_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transcript_record
    ADD CONSTRAINT transcript_record_feature_id_fkey FOREIGN KEY (feature_id) REFERENCES public.feature(feature_id);


--
-- PostgreSQL database dump complete
--

