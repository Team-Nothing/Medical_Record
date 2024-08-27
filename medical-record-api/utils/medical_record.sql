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
    patient_uid character varying(50) NOT NULL,
    bed_id integer NOT NULL,
    doctor_id integer NOT NULL,
    nurse_id integer NOT NULL,
    department_id integer NOT NULL,
    resident_id integer NOT NULL
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
-- Name: admission_reminder; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.admission_reminder (
    serial_id bigint NOT NULL,
    admission_id integer NOT NULL,
    title character varying(255) NOT NULL,
    "order" bigint NOT NULL,
    finished_at timestamp without time zone
);


ALTER TABLE public.admission_reminder OWNER TO postgres;

--
-- Name: admission_reminder_order_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.admission_reminder_order_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.admission_reminder_order_seq OWNER TO postgres;

--
-- Name: admission_reminder_order_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.admission_reminder_order_seq OWNED BY public.admission_reminder."order";


--
-- Name: admission_reminder_serial_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.admission_reminder_serial_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.admission_reminder_serial_id_seq OWNER TO postgres;

--
-- Name: admission_reminder_serial_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.admission_reminder_serial_id_seq OWNED BY public.admission_reminder.serial_id;


--
-- Name: admission_routine; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.admission_routine (
    serial_id bigint NOT NULL,
    admission_id integer NOT NULL,
    "time" timestamp without time zone NOT NULL,
    title character varying(255) NOT NULL,
    description text,
    finished_at timestamp without time zone
);


ALTER TABLE public.admission_routine OWNER TO postgres;

--
-- Name: admission_routine_serial_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.admission_routine_serial_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.admission_routine_serial_id_seq OWNER TO postgres;

--
-- Name: admission_routine_serial_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.admission_routine_serial_id_seq OWNED BY public.admission_routine.serial_id;


--
-- Name: admission_tag; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.admission_tag (
    admission_id integer NOT NULL,
    tag_type_id integer NOT NULL,
    finished boolean DEFAULT false NOT NULL
);


ALTER TABLE public.admission_tag OWNER TO postgres;

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
-- Name: department; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.department (
    department_id integer NOT NULL,
    main_doctor_id integer NOT NULL,
    name character varying(255) NOT NULL,
    description text
);


ALTER TABLE public.department OWNER TO postgres;

--
-- Name: department_department_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.department_department_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.department_department_id_seq OWNER TO postgres;

--
-- Name: department_department_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.department_department_id_seq OWNED BY public.department.department_id;


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
    feature_id integer,
    account_uid uuid,
    image_uid uuid
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
    model_id integer NOT NULL,
    name character varying,
    account_uid uuid,
    dim_uid uuid NOT NULL,
    audio_uid uuid
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
-- Name: nearby_feature; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nearby_feature (
    audio_uid uuid NOT NULL,
    feature_id integer NOT NULL
);


ALTER TABLE public.nearby_feature OWNER TO postgres;

--
-- Name: nurse; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nurse (
    nurse_id integer NOT NULL,
    name character varying(50) NOT NULL,
    email character varying(50),
    feature_id integer,
    account_uid uuid,
    image_uid uuid
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
-- Name: object; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.object (
    uid uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    account_uid uuid NOT NULL,
    object_id character varying(50) NOT NULL,
    visibility boolean DEFAULT true NOT NULL,
    description text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    extension character varying(15)
);


ALTER TABLE public.object OWNER TO postgres;

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
    feature_id integer,
    image_uid uuid
);


ALTER TABLE public.patient OWNER TO postgres;

--
-- Name: session_token; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.session_token (
    token character varying(255) NOT NULL,
    account_uid uuid NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    disabled boolean DEFAULT false NOT NULL
);


ALTER TABLE public.session_token OWNER TO postgres;

--
-- Name: tag_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tag_type (
    tag_type_id integer NOT NULL,
    title character varying(50) NOT NULL,
    icon character varying(50),
    description text
);


ALTER TABLE public.tag_type OWNER TO postgres;

--
-- Name: tag_type_tag_type_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tag_type_tag_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tag_type_tag_type_id_seq OWNER TO postgres;

--
-- Name: tag_type_tag_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tag_type_tag_type_id_seq OWNED BY public.tag_type.tag_type_id;


--
-- Name: transcript_audio; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.transcript_audio (
    audio_uid uuid NOT NULL,
    admission_id integer NOT NULL,
    start_at timestamp without time zone NOT NULL,
    end_at timestamp without time zone,
    processed_at timestamp without time zone,
    previous_audio_uid uuid
);


ALTER TABLE public.transcript_audio OWNER TO postgres;

--
-- Name: transcript_record; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.transcript_record (
    serial_id bigint NOT NULL,
    admission_id integer NOT NULL,
    feature_id integer,
    datetime timestamp without time zone NOT NULL,
    content text,
    audio_uid uuid NOT NULL
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
-- Name: admission_reminder serial_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_reminder ALTER COLUMN serial_id SET DEFAULT nextval('public.admission_reminder_serial_id_seq'::regclass);


--
-- Name: admission_reminder order; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_reminder ALTER COLUMN "order" SET DEFAULT nextval('public.admission_reminder_order_seq'::regclass);


--
-- Name: admission_routine serial_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_routine ALTER COLUMN serial_id SET DEFAULT nextval('public.admission_routine_serial_id_seq'::regclass);


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
-- Name: department department_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.department ALTER COLUMN department_id SET DEFAULT nextval('public.department_department_id_seq'::regclass);


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
-- Name: tag_type tag_type_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tag_type ALTER COLUMN tag_type_id SET DEFAULT nextval('public.tag_type_tag_type_id_seq'::regclass);


--
-- Name: transcript_record serial_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transcript_record ALTER COLUMN serial_id SET DEFAULT nextval('public.transcript_record_serial_id_seq'::regclass);


--
-- Data for Name: account; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.account (uid, username, password, last_login, last_active) FROM stdin;
4f9944c7-653a-45bd-85bc-da1712681a40	username	$2a$06$2e6DtejPwxOMlToYHRAlq.5P06aQLjHp5ORbxwBo8D/mioFeE9eZu	2024-08-27 13:47:36.233752	2024-08-27 13:47:36.233752
8ea39cec-a142-4925-a2cd-35d12189624d	nothing-chang2	$2a$06$npF3Hom0iTbNw83aopcBTORif8m.ke8ioXRXXy.uG6a9NPtIMs5Uy	2024-08-27 13:49:21.028062	2024-08-27 13:49:21.028062
\.


--
-- Data for Name: admission_record; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.admission_record (serial_id, admission_date, discharge_date, patient_uid, bed_id, doctor_id, nurse_id, department_id, resident_id) FROM stdin;
1	2024-08-25 18:03:56.082667	\N	dc9f8ae2-5a12-4cba-a366-2120417d6ba0	1	1	1	1	2
\.


--
-- Data for Name: admission_reminder; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.admission_reminder (serial_id, admission_id, title, "order", finished_at) FROM stdin;
2	1	貴重物品簽收單	1	\N
3	1	告知家人或朋友手術時間和地點，以便陪同	2	\N
1	1	晚上12點後禁止飲食，包含飲水	3	\N
\.


--
-- Data for Name: admission_routine; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.admission_routine (serial_id, admission_id, "time", title, description, finished_at) FROM stdin;
1	1	2024-08-27 07:00:00	吃降血壓藥	\N	\N
2	1	2024-08-27 08:00:00	手術及麻醉同意書	\N	\N
3	1	2024-08-27 08:05:00	手術部位註記	\N	\N
4	1	2024-08-27 08:15:00	等待進入手術室，並通知主要聯絡人	\N	\N
\.


--
-- Data for Name: admission_tag; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.admission_tag (admission_id, tag_type_id, finished) FROM stdin;
1	1	f
1	2	f
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
1	1
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
-- Data for Name: department; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.department (department_id, main_doctor_id, name, description) FROM stdin;
1	1	Psychiatry	A medical specialty dedicated to diagnosing, treating, and preventing mental health disorders. It involves therapies, medications, and counseling to help patients manage conditions like depression, anxiety, and schizophrenia.
\.


--
-- Data for Name: device; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.device (device_id, device_type_id, account_uid, bluetooth_mac, ipv6, ipv4) FROM stdin;
1	1	8ea39cec-a142-4925-a2cd-35d12189624d	00:1A:3F:F1:4C:C6	fe80::cf44:53e5:5c18:6be9	192.168.87.87
5	1	4f9944c7-653a-45bd-85bc-da1712681a40	00:1A:3F:F1:4C:C8	fe80::cf44:53e5:5c18:7cfa	192.168.87.88
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

COPY public.doctor (doctor_id, name, email, feature_id, account_uid, image_uid) FROM stdin;
1	Dr. LED	ledlab2391@gmail.com	2	\N	\N
2	Dr. Nothing	jdps99119@gmail.com	3	\N	\N
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

COPY public.feature (feature_id, model_id, name, account_uid, dim_uid, audio_uid) FROM stdin;
2	1	醫生-LED	\N	b175a290-e747-4372-89e4-f7334825b5fd	\N
1	1	病人-汪上安	4f9944c7-653a-45bd-85bc-da1712681a40	b175a290-e747-4372-89e4-f7334825b5f1	\N
3	1	醫生-Nothing	8ea39cec-a142-4925-a2cd-35d12189624d	b175a290-e747-4372-89e4-f7334825b5f2	\N
5	1	XDD - Nothing	8ea39cec-a142-4925-a2cd-35d12189624d	1106badf-be86-4f65-bc57-0749a6b0847e	7d3bd7eb-7b30-4798-809e-57f01a8f7762
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
1	speechbrain/spkrec-ecapa-voxceleb	05.23.21	provides all the necessary tools to perform speaker verification with a pretrained ECAPA-TDNN model using SpeechBrain. The system can be used to extract speaker embeddings as well. It is trained on Voxceleb 1+ Voxceleb2 training data.
\.


--
-- Data for Name: nearby_feature; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.nearby_feature (audio_uid, feature_id) FROM stdin;
b175a290-e747-4372-89e4-f7334825b5fd	1
b175a290-e747-4372-89e4-f7334825b5fd	3
23b064ed-c66a-45d9-8a9d-1ee9efed7af6	1
23b064ed-c66a-45d9-8a9d-1ee9efed7af6	3
165f94a2-807e-48bf-ad97-201a4d9f1376	1
165f94a2-807e-48bf-ad97-201a4d9f1376	3
1a7f5905-41a9-40f2-adc8-df3a0dbd876f	1
1a7f5905-41a9-40f2-adc8-df3a0dbd876f	3
\.


--
-- Data for Name: nurse; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.nurse (nurse_id, name, email, feature_id, account_uid, image_uid) FROM stdin;
1	Fish	fish@gmail.com	\N	\N	\N
\.


--
-- Data for Name: object; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.object (uid, account_uid, object_id, visibility, description, created_at, extension) FROM stdin;
b175a290-e747-4372-89e4-f7334825b5fd	8ea39cec-a142-4925-a2cd-35d12189624d	audio-record-08-27-2024-19-11	f	\N	2024-08-27 11:11:57.90846	.mp3
23b064ed-c66a-45d9-8a9d-1ee9efed7af6	8ea39cec-a142-4925-a2cd-35d12189624d	XDDd_1min.mp3	f	\N	2024-08-27 13:57:32.193877	.mp3
b175a290-e747-4372-89e4-f7334825b5f2	8ea39cec-a142-4925-a2cd-35d12189624d	BBB	f	\N	2024-08-27 18:26:35.594307	\N
b175a290-e747-4372-89e4-f7334825b5f3	8ea39cec-a142-4925-a2cd-35d12189624d	CCC	f	\N	2024-08-27 18:26:35.594307	\N
b175a290-e747-4372-89e4-f7334825b5f1	4f9944c7-653a-45bd-85bc-da1712681a40	AAA	f	\N	2024-08-27 18:26:35.594307	\N
165f94a2-807e-48bf-ad97-201a4d9f1376	8ea39cec-a142-4925-a2cd-35d12189624d	fda.mp3	f	\N	2024-08-27 14:15:24.748167	.mp3
1a7f5905-41a9-40f2-adc8-df3a0dbd876f	8ea39cec-a142-4925-a2cd-35d12189624d	fasf	f	\N	2024-08-27 21:02:10.228254	mp3
13bc2350-9005-4c47-b43a-003cd2341858	8ea39cec-a142-4925-a2cd-35d12189624d	fasfaf	f	\N	2024-08-27 21:21:18.676074	\N
c42a2ef0-1461-4ce4-bf59-61eb131ea65a	8ea39cec-a142-4925-a2cd-35d12189624d	XDD_1min.mp3	f	\N	2024-08-27 21:32:47.747672	\N
7d3bd7eb-7b30-4798-809e-57f01a8f7762	8ea39cec-a142-4925-a2cd-35d12189624d	XDD_1min	f	\N	2024-08-27 21:43:29.09291	mp3
1106badf-be86-4f65-bc57-0749a6b0847e	8ea39cec-a142-4925-a2cd-35d12189624d	feature	f	\N	2024-08-27 21:43:33.476983	npy
\.


--
-- Data for Name: patient; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.patient (uid, name, email, phone, address, birth, gender_id, blood_id, language_id, feature_id, image_uid) FROM stdin;
1bdcd810-3f7d-4b5b-a647-38c2ec85bb78	XDD	xdd@gmail.com	0987877887	高雄市楠梓區 XDD XDD XDD	1987-01-01	2	6	2	\N	\N
dc9f8ae2-5a12-4cba-a366-2120417d6ba0	汪上安	wsan92@gmail.com	0918214333	高雄市橋頭區 XDD XDD XDD	2003-03-08	1	4	1	1	\N
\.


--
-- Data for Name: session_token; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.session_token (token, account_uid, created_at, disabled) FROM stdin;
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOiI0Zjk5NDRjNy02NTNhLTQ1YmQtODViYy1kYTE3MTI2ODFhNDAiLCJleHAiOjE3MjU5NTI0NjUsImRldmljZV9pZCI6Im5vdGhpbmctY2hhbmcifQ.T38frQNUe3ut2a5mIoNFvtQ718rzMR2k0MIHk6S3Qr0	4f9944c7-653a-45bd-85bc-da1712681a40	2024-08-27 07:14:25.095808	t
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOiI0Zjk5NDRjNy02NTNhLTQ1YmQtODViYy1kYTE3MTI2ODFhNDAiLCJleHAiOjE3MjU5NTI5MDcsImRldmljZV9pZCI6Im5vdGhpbmctY2hhbmcifQ.Y01ntFw_6L1WdGcDLcueSf9W5Gqlcgh5aAsdjyfo3OU	4f9944c7-653a-45bd-85bc-da1712681a40	2024-08-27 07:21:47.15392	f
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOiI0Zjk5NDRjNy02NTNhLTQ1YmQtODViYy1kYTE3MTI2ODFhNDAiLCJleHAiOjE3MjU5NzYwNTYsImRldmljZV9pZCI6Im5vdGhpbmctY2hhbmcifQ.FMah9SgRfyZGHFu2SNrqYOcUdc9IUVvvnmdM3zFzhDw	4f9944c7-653a-45bd-85bc-da1712681a40	2024-08-27 13:47:36.262762	f
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOiI4ZWEzOWNlYy1hMTQyLTQ5MjUtYTJjZC0zNWQxMjE4OTYyNGQiLCJleHAiOjE3MjU5NzYxNjEsImRldmljZV9pZCI6Im5vdGhpbmctY2hhbmcifQ.mlDAEVUgOfb6A8J7pAKfT5EWppkKRjwa3bKi46KTD08	8ea39cec-a142-4925-a2cd-35d12189624d	2024-08-27 13:49:21.038804	f
\.


--
-- Data for Name: tag_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tag_type (tag_type_id, title, icon, description) FROM stdin;
1	進食	\N	\N
2	尚未吃藥	\N	\N
\.


--
-- Data for Name: transcript_audio; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.transcript_audio (audio_uid, admission_id, start_at, end_at, processed_at, previous_audio_uid) FROM stdin;
23b064ed-c66a-45d9-8a9d-1ee9efed7af6	1	2024-08-27 14:05:51.113097	2024-08-27 14:06:51	2024-08-27 20:40:50.766711	\N
165f94a2-807e-48bf-ad97-201a4d9f1376	1	2024-08-27 14:15:24.818851	2024-08-27 14:16:24	2024-08-27 20:41:15.393929	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
1a7f5905-41a9-40f2-adc8-df3a0dbd876f	1	2024-08-28 04:59:00	\N	\N	165f94a2-807e-48bf-ad97-201a4d9f1376
\.


--
-- Data for Name: transcript_record; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.transcript_record (serial_id, admission_id, feature_id, datetime, content, audio_uid) FROM stdin;
1	1	1	2024-08-25 18:03:56.118277	Hello, I am Dr. LED	b175a290-e747-4372-89e4-f7334825b5fd
441	1	1	2024-08-27 14:05:51	我覺得我至少在暴風以前的人生	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
442	1	3	2024-08-27 14:05:54	我當失敗者當得很帥	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
2	1	3	2024-08-27 19:46:52	fasfasfasdfad	b175a290-e747-4372-89e4-f7334825b5fd
443	1	3	2024-08-27 14:05:56	當得很自債	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
444	1	\N	2024-08-27 14:05:57	反正我很遷的	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
445	1	\N	2024-08-27 14:05:58	中央波羅文的節目	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
446	1	\N	2024-08-27 14:06:00	對	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
447	1	1	2024-08-27 14:06:01	第一幣	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
448	1	\N	2024-08-27 14:06:01	遠值住	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
449	1	1	2024-08-27 14:06:02	因為那個時候我們接觸的環境	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
450	1	3	2024-08-27 14:06:04	朋友全部都是	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
451	1	3	2024-08-27 14:06:05	聽團圈聽團展	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
452	1	1	2024-08-27 14:06:06	從頭到尾就直覺	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
453	1	3	2024-08-27 14:06:07	我們自己覺得好笑笑	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
454	1	1	2024-08-27 14:06:08	就圈的地下月團	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
455	1	3	2024-08-27 14:06:10	對	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
456	1	1	2024-08-27 14:06:10	像他在最討厭他們喜歡的團組的	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
457	1	3	2024-08-27 14:06:12	我話不是嗎	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
458	1	3	2024-08-27 14:06:13	那是比較低端的	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
459	1	\N	2024-08-27 14:06:15	對	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
460	1	\N	2024-08-27 14:06:16	聽團展真的好奇事練	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
461	1	\N	2024-08-27 14:06:17	其實練了	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
462	1	\N	2024-08-27 14:06:18	看看	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
463	1	1	2024-08-27 14:06:19	我個人反而比較冷結束	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
464	1	3	2024-08-27 14:06:22	有頭拍這樣	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
465	1	1	2024-08-27 14:06:23	結果我知道你是尊重我了	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
466	1	1	2024-08-27 14:06:25	我看完這樣	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
467	1	\N	2024-08-27 14:06:26	你只能在那	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
468	1	1	2024-08-27 14:06:27	永遠的在那兒拍	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
469	1	\N	2024-08-27 14:06:28	OK	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
470	1	1	2024-08-27 14:06:28	你憑什麼跟我這樣講話	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
471	1	3	2024-08-27 14:06:30	好 我就把他給他	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
472	1	\N	2024-08-27 14:06:31	給他	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
473	1	\N	2024-08-27 14:06:32	給他	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
474	1	\N	2024-08-27 14:06:33	給他	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
475	1	1	2024-08-27 14:06:33	OK	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
476	1	\N	2024-08-27 14:06:34	我們是不同的	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
477	1	\N	2024-08-27 14:06:34	讓人很感受	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
478	1	1	2024-08-27 14:06:37	如果你的心念心願	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
479	1	1	2024-08-27 14:06:38	包含提升英文能力	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
480	1	\N	2024-08-27 14:06:40	那你一定要實施這款	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
481	1	\N	2024-08-27 14:06:41	叫做Speak Day	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
482	1	\N	2024-08-27 14:06:42	Speak讓你設定任何會話場景	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
483	1	1	2024-08-27 14:06:44	和A.I.透過角色	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
484	1	\N	2024-08-27 14:06:45	辦理的方式練口說	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
485	1	\N	2024-08-27 14:06:46	Speak還會根據你的對話	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
486	1	\N	2024-08-27 14:06:48	推送你個人話	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
487	1	\N	2024-08-27 14:06:49	的祕密課程	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
488	1	\N	2024-08-27 14:06:50	每天只要十分	23b064ed-c66a-45d9-8a9d-1ee9efed7af6
489	1	\N	2024-08-27 14:14:24	我覺得我至少在暴風以前的人生	165f94a2-807e-48bf-ad97-201a4d9f1376
490	1	\N	2024-08-27 14:14:27	我當失敗者當得很帥	165f94a2-807e-48bf-ad97-201a4d9f1376
491	1	\N	2024-08-27 14:14:29	當得很自債	165f94a2-807e-48bf-ad97-201a4d9f1376
492	1	1	2024-08-27 14:14:30	反正我很遷的	165f94a2-807e-48bf-ad97-201a4d9f1376
493	1	1	2024-08-27 14:14:31	中央波羅文的節目	165f94a2-807e-48bf-ad97-201a4d9f1376
494	1	3	2024-08-27 14:14:33	對	165f94a2-807e-48bf-ad97-201a4d9f1376
495	1	\N	2024-08-27 14:14:34	1B	165f94a2-807e-48bf-ad97-201a4d9f1376
496	1	\N	2024-08-27 14:14:34	遠值住	165f94a2-807e-48bf-ad97-201a4d9f1376
497	1	\N	2024-08-27 14:14:35	因為那個時候我們接觸的環境	165f94a2-807e-48bf-ad97-201a4d9f1376
498	1	3	2024-08-27 14:14:37	朋友全部都是	165f94a2-807e-48bf-ad97-201a4d9f1376
499	1	\N	2024-08-27 14:14:38	聽團圈聽團展	165f94a2-807e-48bf-ad97-201a4d9f1376
500	1	\N	2024-08-27 14:14:39	從頭到尾就直覺	165f94a2-807e-48bf-ad97-201a4d9f1376
501	1	\N	2024-08-27 14:14:40	我們自己覺得好笑笑	165f94a2-807e-48bf-ad97-201a4d9f1376
502	1	\N	2024-08-27 14:14:41	一切就圈的地下月團	165f94a2-807e-48bf-ad97-201a4d9f1376
503	1	\N	2024-08-27 14:14:43	對	165f94a2-807e-48bf-ad97-201a4d9f1376
504	1	1	2024-08-27 14:14:43	像他在最討厭他們喜歡的團組的	165f94a2-807e-48bf-ad97-201a4d9f1376
505	1	3	2024-08-27 14:14:45	有話不是嗎	165f94a2-807e-48bf-ad97-201a4d9f1376
506	1	3	2024-08-27 14:14:46	嗯	165f94a2-807e-48bf-ad97-201a4d9f1376
507	1	\N	2024-08-27 14:14:46	那是比較低端的	165f94a2-807e-48bf-ad97-201a4d9f1376
508	1	3	2024-08-27 14:14:48	好可愛	165f94a2-807e-48bf-ad97-201a4d9f1376
509	1	\N	2024-08-27 14:14:48	你看他在這裏還有騎士練	165f94a2-807e-48bf-ad97-201a4d9f1376
510	1	\N	2024-08-27 14:14:50	有騎士練哦	165f94a2-807e-48bf-ad97-201a4d9f1376
511	1	3	2024-08-27 14:14:51	你看他哦	165f94a2-807e-48bf-ad97-201a4d9f1376
512	1	\N	2024-08-27 14:14:53	我個人反而比較冷結束	165f94a2-807e-48bf-ad97-201a4d9f1376
513	1	\N	2024-08-27 14:14:55	有偷拍這樣子	165f94a2-807e-48bf-ad97-201a4d9f1376
514	1	1	2024-08-27 14:14:56	結果我知道	165f94a2-807e-48bf-ad97-201a4d9f1376
515	1	3	2024-08-27 14:14:57	你是尊重我了	165f94a2-807e-48bf-ad97-201a4d9f1376
516	1	\N	2024-08-27 14:14:58	我看我這樣講	165f94a2-807e-48bf-ad97-201a4d9f1376
517	1	\N	2024-08-27 14:14:59	你只能在那	165f94a2-807e-48bf-ad97-201a4d9f1376
518	1	\N	2024-08-27 14:15:00	遠遠的在那兒	165f94a2-807e-48bf-ad97-201a4d9f1376
519	1	3	2024-08-27 14:15:01	OK	165f94a2-807e-48bf-ad97-201a4d9f1376
520	1	1	2024-08-27 14:15:01	你憑什麼跟我這樣講話	165f94a2-807e-48bf-ad97-201a4d9f1376
521	1	3	2024-08-27 14:15:03	好 我就要一把	165f94a2-807e-48bf-ad97-201a4d9f1376
522	1	3	2024-08-27 14:15:04	憑什麼	165f94a2-807e-48bf-ad97-201a4d9f1376
523	1	\N	2024-08-27 14:15:06	OK	165f94a2-807e-48bf-ad97-201a4d9f1376
524	1	1	2024-08-27 14:15:06	我們是不同了	165f94a2-807e-48bf-ad97-201a4d9f1376
525	1	3	2024-08-27 14:15:07	讓人很感動	165f94a2-807e-48bf-ad97-201a4d9f1376
526	1	\N	2024-08-27 14:15:10	如果你的心念心願	165f94a2-807e-48bf-ad97-201a4d9f1376
527	1	\N	2024-08-27 14:15:11	保和提升因為能力	165f94a2-807e-48bf-ad97-201a4d9f1376
528	1	\N	2024-08-27 14:15:13	那你一定要實施這款	165f94a2-807e-48bf-ad97-201a4d9f1376
529	1	\N	2024-08-27 14:15:14	叫做Speak Day	165f94a2-807e-48bf-ad97-201a4d9f1376
530	1	\N	2024-08-27 14:15:15	Speak讓你設定任何會話場景	165f94a2-807e-48bf-ad97-201a4d9f1376
531	1	\N	2024-08-27 14:15:17	和A.I.透過角色	165f94a2-807e-48bf-ad97-201a4d9f1376
532	1	\N	2024-08-27 14:15:18	半夜的方式練口說	165f94a2-807e-48bf-ad97-201a4d9f1376
533	1	\N	2024-08-27 14:15:19	Speak還會根據你的對話	165f94a2-807e-48bf-ad97-201a4d9f1376
534	1	\N	2024-08-27 14:15:21	推送你個人化的	165f94a2-807e-48bf-ad97-201a4d9f1376
535	1	\N	2024-08-27 14:15:22	Mini Kerchan	165f94a2-807e-48bf-ad97-201a4d9f1376
536	1	\N	2024-08-27 14:15:23	每天只要十分	165f94a2-807e-48bf-ad97-201a4d9f1376
537	1	\N	2024-08-27 14:15:24	我覺得我	165f94a2-807e-48bf-ad97-201a4d9f1376
538	1	\N	2024-08-27 14:15:25	至少在暴控以前的人生	165f94a2-807e-48bf-ad97-201a4d9f1376
539	1	\N	2024-08-27 14:15:27	我當失敗者當得很帥	165f94a2-807e-48bf-ad97-201a4d9f1376
540	1	\N	2024-08-27 14:15:29	當得很自在	165f94a2-807e-48bf-ad97-201a4d9f1376
541	1	1	2024-08-27 14:15:30	反正我很遷	165f94a2-807e-48bf-ad97-201a4d9f1376
542	1	1	2024-08-27 14:15:31	中央波羅門的節目	165f94a2-807e-48bf-ad97-201a4d9f1376
543	1	3	2024-08-27 14:15:33	Yeah	165f94a2-807e-48bf-ad97-201a4d9f1376
544	1	\N	2024-08-27 14:15:34	BB	165f94a2-807e-48bf-ad97-201a4d9f1376
545	1	\N	2024-08-27 14:15:35	原子住	165f94a2-807e-48bf-ad97-201a4d9f1376
546	1	\N	2024-08-27 14:15:35	因為那個時候	165f94a2-807e-48bf-ad97-201a4d9f1376
547	1	\N	2024-08-27 14:15:36	我們接觸的環境	165f94a2-807e-48bf-ad97-201a4d9f1376
548	1	3	2024-08-27 14:15:37	朋友全部都是	165f94a2-807e-48bf-ad97-201a4d9f1376
549	1	\N	2024-08-27 14:15:38	聽團圈聽團展	165f94a2-807e-48bf-ad97-201a4d9f1376
550	1	\N	2024-08-27 14:15:39	從頭到尾就直接覺得	165f94a2-807e-48bf-ad97-201a4d9f1376
551	1	\N	2024-08-27 14:15:40	我們自己覺得好笑笑	165f94a2-807e-48bf-ad97-201a4d9f1376
552	1	\N	2024-08-27 14:15:41	就圈得地下樂團	165f94a2-807e-48bf-ad97-201a4d9f1376
553	1	\N	2024-08-27 14:15:42	對	165f94a2-807e-48bf-ad97-201a4d9f1376
554	1	1	2024-08-27 14:15:43	像他們在最討厭他們	165f94a2-807e-48bf-ad97-201a4d9f1376
555	1	3	2024-08-27 14:15:44	喜歡的團主流化	165f94a2-807e-48bf-ad97-201a4d9f1376
556	1	3	2024-08-27 14:15:45	不是	165f94a2-807e-48bf-ad97-201a4d9f1376
557	1	3	2024-08-27 14:15:46	嗯	165f94a2-807e-48bf-ad97-201a4d9f1376
558	1	\N	2024-08-27 14:15:46	那是比較低端的	165f94a2-807e-48bf-ad97-201a4d9f1376
559	1	3	2024-08-27 14:15:48	好可愛	165f94a2-807e-48bf-ad97-201a4d9f1376
560	1	\N	2024-08-27 14:15:48	你看他在這裏還有騎士練哦	165f94a2-807e-48bf-ad97-201a4d9f1376
561	1	\N	2024-08-27 14:15:50	其實練哦	165f94a2-807e-48bf-ad97-201a4d9f1376
562	1	3	2024-08-27 14:15:51	我看他哦	165f94a2-807e-48bf-ad97-201a4d9f1376
563	1	\N	2024-08-27 14:15:53	我個人	165f94a2-807e-48bf-ad97-201a4d9f1376
564	1	\N	2024-08-27 14:15:54	反而比較冷結束	165f94a2-807e-48bf-ad97-201a4d9f1376
565	1	\N	2024-08-27 14:15:55	有頭拍這樣子	165f94a2-807e-48bf-ad97-201a4d9f1376
566	1	1	2024-08-27 14:15:56	結果我知道	165f94a2-807e-48bf-ad97-201a4d9f1376
567	1	3	2024-08-27 14:15:57	你是尊重我了	165f94a2-807e-48bf-ad97-201a4d9f1376
568	1	\N	2024-08-27 14:15:58	我看他	165f94a2-807e-48bf-ad97-201a4d9f1376
569	1	\N	2024-08-27 14:15:59	你只能在那	165f94a2-807e-48bf-ad97-201a4d9f1376
570	1	\N	2024-08-27 14:16:00	永遠的在那兒拍	165f94a2-807e-48bf-ad97-201a4d9f1376
571	1	3	2024-08-27 14:16:01	OK	165f94a2-807e-48bf-ad97-201a4d9f1376
572	1	1	2024-08-27 14:16:01	你憑什麼	165f94a2-807e-48bf-ad97-201a4d9f1376
573	1	1	2024-08-27 14:16:02	跟我這樣講話	165f94a2-807e-48bf-ad97-201a4d9f1376
574	1	3	2024-08-27 14:16:03	好	165f94a2-807e-48bf-ad97-201a4d9f1376
575	1	3	2024-08-27 14:16:03	好 我就發揮吧	165f94a2-807e-48bf-ad97-201a4d9f1376
576	1	3	2024-08-27 14:16:04	憑什麼	165f94a2-807e-48bf-ad97-201a4d9f1376
577	1	3	2024-08-27 14:16:05	你憑什麼	165f94a2-807e-48bf-ad97-201a4d9f1376
578	1	\N	2024-08-27 14:16:06	OK	165f94a2-807e-48bf-ad97-201a4d9f1376
579	1	1	2024-08-27 14:16:06	我們是不同了	165f94a2-807e-48bf-ad97-201a4d9f1376
580	1	3	2024-08-27 14:16:07	讓人很認識	165f94a2-807e-48bf-ad97-201a4d9f1376
581	1	3	2024-08-27 14:16:08	好笑	165f94a2-807e-48bf-ad97-201a4d9f1376
582	1	\N	2024-08-27 14:16:10	如果你的心念心念	165f94a2-807e-48bf-ad97-201a4d9f1376
583	1	\N	2024-08-27 14:16:11	包含提升英文能力	165f94a2-807e-48bf-ad97-201a4d9f1376
584	1	\N	2024-08-27 14:16:12	那你一定要實施這款	165f94a2-807e-48bf-ad97-201a4d9f1376
585	1	\N	2024-08-27 14:16:14	叫做Speak的	165f94a2-807e-48bf-ad97-201a4d9f1376
586	1	\N	2024-08-27 14:16:15	Speak讓你設定任何會話	165f94a2-807e-48bf-ad97-201a4d9f1376
587	1	\N	2024-08-27 14:16:16	場景	165f94a2-807e-48bf-ad97-201a4d9f1376
588	1	\N	2024-08-27 14:16:17	和A.I.透過角色	165f94a2-807e-48bf-ad97-201a4d9f1376
589	1	\N	2024-08-27 14:16:18	半夜的方式練口說	165f94a2-807e-48bf-ad97-201a4d9f1376
590	1	\N	2024-08-27 14:16:19	Speak還會根據你的對話	165f94a2-807e-48bf-ad97-201a4d9f1376
591	1	\N	2024-08-27 14:16:21	推送你個人話的	165f94a2-807e-48bf-ad97-201a4d9f1376
592	1	\N	2024-08-27 14:16:22	Mini Kerchan	165f94a2-807e-48bf-ad97-201a4d9f1376
593	1	\N	2024-08-27 14:16:23	每天只要十分	165f94a2-807e-48bf-ad97-201a4d9f1376
\.


--
-- Name: admission_record_serial_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.admission_record_serial_id_seq', 1, true);


--
-- Name: admission_reminder_order_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.admission_reminder_order_seq', 3, true);


--
-- Name: admission_reminder_serial_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.admission_reminder_serial_id_seq', 3, true);


--
-- Name: admission_routine_serial_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.admission_routine_serial_id_seq', 4, true);


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
-- Name: department_department_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.department_department_id_seq', 1, true);


--
-- Name: device_device_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.device_device_id_seq', 6, true);


--
-- Name: device_type_device_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.device_type_device_type_id_seq', 1, true);


--
-- Name: doctor_doctor_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.doctor_doctor_id_seq', 2, true);


--
-- Name: family_type_family_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.family_type_family_type_id_seq', 6, true);


--
-- Name: feature_feature_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.feature_feature_id_seq', 5, true);


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
-- Name: tag_type_tag_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tag_type_tag_type_id_seq', 2, true);


--
-- Name: transcript_record_serial_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.transcript_record_serial_id_seq', 593, true);


--
-- Name: object account_object_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.object
    ADD CONSTRAINT account_object_unique UNIQUE (account_uid, object_id);


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
-- Name: admission_reminder admission_reminder_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_reminder
    ADD CONSTRAINT admission_reminder_pkey PRIMARY KEY (serial_id);


--
-- Name: admission_routine admission_routine_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_routine
    ADD CONSTRAINT admission_routine_pkey PRIMARY KEY (serial_id);


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
-- Name: department department_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.department
    ADD CONSTRAINT department_pkey PRIMARY KEY (department_id);


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
-- Name: object image_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.object
    ADD CONSTRAINT image_pkey PRIMARY KEY (uid);


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
-- Name: nearby_feature nearby_feature_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nearby_feature
    ADD CONSTRAINT nearby_feature_pkey PRIMARY KEY (audio_uid, feature_id);


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
-- Name: session_token session_token_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.session_token
    ADD CONSTRAINT session_token_pkey PRIMARY KEY (token);


--
-- Name: tag_type tag_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tag_type
    ADD CONSTRAINT tag_type_pkey PRIMARY KEY (tag_type_id);


--
-- Name: transcript_audio transcript_audio_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transcript_audio
    ADD CONSTRAINT transcript_audio_pk PRIMARY KEY (audio_uid);


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
-- Name: admission_record admission_record_department_department_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_record
    ADD CONSTRAINT admission_record_department_department_id_fk FOREIGN KEY (department_id) REFERENCES public.department(department_id);


--
-- Name: admission_record admission_record_doctor_doctor_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_record
    ADD CONSTRAINT admission_record_doctor_doctor_id_fk FOREIGN KEY (resident_id) REFERENCES public.doctor(doctor_id);


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
    ADD CONSTRAINT admission_record_uid_fkey FOREIGN KEY (patient_uid) REFERENCES public.patient(uid);


--
-- Name: admission_reminder admission_reminder_admission_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_reminder
    ADD CONSTRAINT admission_reminder_admission_id_fkey FOREIGN KEY (admission_id) REFERENCES public.admission_record(serial_id);


--
-- Name: admission_routine admission_routine_admission_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_routine
    ADD CONSTRAINT admission_routine_admission_id_fkey FOREIGN KEY (admission_id) REFERENCES public.admission_record(serial_id);


--
-- Name: admission_tag admission_tag_admission_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_tag
    ADD CONSTRAINT admission_tag_admission_id_fkey FOREIGN KEY (admission_id) REFERENCES public.admission_record(serial_id);


--
-- Name: admission_tag admission_tag_tag_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admission_tag
    ADD CONSTRAINT admission_tag_tag_type_id_fkey FOREIGN KEY (tag_type_id) REFERENCES public.tag_type(tag_type_id);


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
-- Name: department department_main_doctor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.department
    ADD CONSTRAINT department_main_doctor_id_fkey FOREIGN KEY (main_doctor_id) REFERENCES public.doctor(doctor_id);


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
-- Name: doctor doctor_account_uid_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.doctor
    ADD CONSTRAINT doctor_account_uid_fk FOREIGN KEY (account_uid) REFERENCES public.account(uid);


--
-- Name: doctor doctor_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.doctor
    ADD CONSTRAINT doctor_feature_id_fkey FOREIGN KEY (feature_id) REFERENCES public.feature(feature_id);


--
-- Name: doctor doctor_image_uid_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.doctor
    ADD CONSTRAINT doctor_image_uid_fk FOREIGN KEY (image_uid) REFERENCES public.object(uid);


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
-- Name: feature feature_account_uid_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.feature
    ADD CONSTRAINT feature_account_uid_fk FOREIGN KEY (account_uid) REFERENCES public.account(uid);


--
-- Name: feature feature_model_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.feature
    ADD CONSTRAINT feature_model_id_fkey FOREIGN KEY (model_id) REFERENCES public.model(model_id);


--
-- Name: feature feature_object_uid_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.feature
    ADD CONSTRAINT feature_object_uid_fk FOREIGN KEY (dim_uid) REFERENCES public.object(uid);


--
-- Name: feature feature_object_uid_fk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.feature
    ADD CONSTRAINT feature_object_uid_fk_2 FOREIGN KEY (audio_uid) REFERENCES public.object(uid);


--
-- Name: object image_account_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.object
    ADD CONSTRAINT image_account_uid_fkey FOREIGN KEY (account_uid) REFERENCES public.account(uid);


--
-- Name: nearby_feature nearby_feature_audio_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nearby_feature
    ADD CONSTRAINT nearby_feature_audio_uid_fkey FOREIGN KEY (audio_uid) REFERENCES public.object(uid);


--
-- Name: nearby_feature nearby_feature_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nearby_feature
    ADD CONSTRAINT nearby_feature_feature_id_fkey FOREIGN KEY (feature_id) REFERENCES public.feature(feature_id);


--
-- Name: nurse nurse_account_uid_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nurse
    ADD CONSTRAINT nurse_account_uid_fk FOREIGN KEY (account_uid) REFERENCES public.account(uid);


--
-- Name: nurse nurse_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nurse
    ADD CONSTRAINT nurse_feature_id_fkey FOREIGN KEY (feature_id) REFERENCES public.feature(feature_id);


--
-- Name: nurse nurse_image_uid_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nurse
    ADD CONSTRAINT nurse_image_uid_fk FOREIGN KEY (image_uid) REFERENCES public.object(uid);


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
-- Name: patient patient_image_uid_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.patient
    ADD CONSTRAINT patient_image_uid_fk FOREIGN KEY (image_uid) REFERENCES public.object(uid);


--
-- Name: patient patient_language_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.patient
    ADD CONSTRAINT patient_language_id_fkey FOREIGN KEY (language_id) REFERENCES public.language(language_id);


--
-- Name: session_token session_token_account_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.session_token
    ADD CONSTRAINT session_token_account_uid_fkey FOREIGN KEY (account_uid) REFERENCES public.account(uid);


--
-- Name: transcript_audio transcript_audio_admission_record_serial_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transcript_audio
    ADD CONSTRAINT transcript_audio_admission_record_serial_id_fk FOREIGN KEY (admission_id) REFERENCES public.admission_record(serial_id);


--
-- Name: transcript_audio transcript_audio_transcript_audio_audio_uid_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transcript_audio
    ADD CONSTRAINT transcript_audio_transcript_audio_audio_uid_fk FOREIGN KEY (previous_audio_uid) REFERENCES public.transcript_audio(audio_uid);


--
-- Name: transcript_record transcript_record_admission_record_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transcript_record
    ADD CONSTRAINT transcript_record_admission_record_id_fkey FOREIGN KEY (admission_id) REFERENCES public.admission_record(serial_id);


--
-- Name: transcript_record transcript_record_nearby_feature_audio_uid_feature_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transcript_record
    ADD CONSTRAINT transcript_record_nearby_feature_audio_uid_feature_id_fk FOREIGN KEY (audio_uid, feature_id) REFERENCES public.nearby_feature(audio_uid, feature_id);


--
-- Name: transcript_audio transcript_unprocess_audio_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transcript_audio
    ADD CONSTRAINT transcript_unprocess_audio_uid_fkey FOREIGN KEY (audio_uid) REFERENCES public.object(uid);


--
-- PostgreSQL database dump complete
--

