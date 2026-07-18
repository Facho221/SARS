--
-- PostgreSQL database dump
--

\restrict 9Pvmmg5iA7BQzRklJbpb6mTtjFdTJn3c291vtS2FrEUUBHCwEpdkXdhrJnfTHcA

-- Dumped from database version 18.3
-- Dumped by pg_dump version 18.3

-- Started on 2026-07-05 18:29:09

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 225 (class 1259 OID 16469)
-- Name: estancia; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.estancia (
    id_estancia integer NOT NULL,
    hora_ingreso timestamp without time zone DEFAULT now() NOT NULL,
    hora_salida timestamp without time zone,
    estado character varying(15) DEFAULT 'Normal'::character varying NOT NULL,
    destino character varying(100) NOT NULL,
    tipo_ingreso character varying(10) DEFAULT 'Peatón'::character varying NOT NULL,
    desc_vehiculo character varying(100),
    tiempo_max_minutos integer DEFAULT 60 NOT NULL,
    dni_visitante character varying(8) NOT NULL,
    id_tag integer NOT NULL,
    id_vigilante integer NOT NULL,
    CONSTRAINT estancia_estado_check CHECK (((estado)::text = ANY ((ARRAY['Normal'::character varying, 'Advertencia'::character varying, 'Alerta'::character varying, 'Finalizado'::character varying])::text[]))),
    CONSTRAINT estancia_tipo_ingreso_check CHECK (((tipo_ingreso)::text = ANY ((ARRAY['Peatón'::character varying, 'Vehículo'::character varying])::text[])))
);


ALTER TABLE public.estancia OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16468)
-- Name: estancia_id_estancia_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.estancia_id_estancia_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.estancia_id_estancia_seq OWNER TO postgres;

--
-- TOC entry 5062 (class 0 OID 0)
-- Dependencies: 224
-- Name: estancia_id_estancia_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.estancia_id_estancia_seq OWNED BY public.estancia.id_estancia;


--
-- TOC entry 223 (class 1259 OID 16455)
-- Name: tag; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tag (
    id_tag integer NOT NULL,
    codigo_rfid character varying(50) NOT NULL,
    estado_tag character varying(20) DEFAULT 'Disponible'::character varying NOT NULL,
    CONSTRAINT tag_estado_tag_check CHECK (((estado_tag)::text = ANY ((ARRAY['Disponible'::character varying, 'Asignado'::character varying])::text[])))
);


ALTER TABLE public.tag OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16454)
-- Name: tag_id_tag_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tag_id_tag_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tag_id_tag_seq OWNER TO postgres;

--
-- TOC entry 5064 (class 0 OID 0)
-- Dependencies: 222
-- Name: tag_id_tag_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tag_id_tag_seq OWNED BY public.tag.id_tag;


--
-- TOC entry 220 (class 1259 OID 16430)
-- Name: vigilante; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vigilante (
    id_vigilante integer NOT NULL,
    nom_vigilante character varying(100) NOT NULL,
    turno character varying(20) NOT NULL,
    usuario character varying(50) NOT NULL,
    contrasena character varying(255) NOT NULL,
    rol character varying(20) DEFAULT 'vigilante'::character varying,
    CONSTRAINT vigilante_turno_check CHECK (((turno)::text = ANY ((ARRAY['Mañana'::character varying, 'Tarde'::character varying, 'Noche'::character varying])::text[])))
);


ALTER TABLE public.vigilante OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16429)
-- Name: vigilante_id_vigilante_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.vigilante_id_vigilante_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.vigilante_id_vigilante_seq OWNER TO postgres;

--
-- TOC entry 5066 (class 0 OID 0)
-- Dependencies: 219
-- Name: vigilante_id_vigilante_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.vigilante_id_vigilante_seq OWNED BY public.vigilante.id_vigilante;


--
-- TOC entry 221 (class 1259 OID 16444)
-- Name: visitante; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.visitante (
    dni character varying(8) NOT NULL,
    nombre character varying(100) NOT NULL,
    tipo character varying(30) NOT NULL,
    subtipo character varying(20),
    telefono character varying(15),
    CONSTRAINT visitante_subtipo_check CHECK (((subtipo)::text = ANY ((ARRAY['Residente'::character varying, 'Negocio'::character varying, 'Externo'::character varying])::text[]))),
    CONSTRAINT visitante_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['Familiar'::character varying, 'Delivery'::character varying, 'Proveedor'::character varying, 'Residente'::character varying, 'Otro'::character varying])::text[])))
);


ALTER TABLE public.visitante OWNER TO postgres;

--
-- TOC entry 4874 (class 2604 OID 16472)
-- Name: estancia id_estancia; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.estancia ALTER COLUMN id_estancia SET DEFAULT nextval('public.estancia_id_estancia_seq'::regclass);


--
-- TOC entry 4872 (class 2604 OID 16458)
-- Name: tag id_tag; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tag ALTER COLUMN id_tag SET DEFAULT nextval('public.tag_id_tag_seq'::regclass);


--
-- TOC entry 4870 (class 2604 OID 16433)
-- Name: vigilante id_vigilante; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vigilante ALTER COLUMN id_vigilante SET DEFAULT nextval('public.vigilante_id_vigilante_seq'::regclass);


--
-- TOC entry 5055 (class 0 OID 16469)
-- Dependencies: 225
-- Data for Name: estancia; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.estancia (id_estancia, hora_ingreso, hora_salida, estado, destino, tipo_ingreso, desc_vehiculo, tiempo_max_minutos, dni_visitante, id_tag, id_vigilante) FROM stdin;
1	2026-04-10 08:15:00	2026-04-10 09:10:00	Finalizado	Lote 12	Peatón	\N	60	12345678	1	1
2	2026-04-10 09:30:00	2026-04-10 10:00:00	Finalizado	Dpto 3B	Vehículo	ABC-123 Rojo	30	23456789	2	1
3	2026-04-11 10:00:00	2026-04-11 10:45:00	Finalizado	Lote 5	Peatón	\N	60	34567890	3	2
4	2026-04-12 11:20:00	2026-04-12 12:30:00	Finalizado	Dpto 7A	Peatón	\N	60	45678901	4	1
6	2026-04-14 16:30:00	2026-04-14 17:00:00	Finalizado	Dpto 1C	Peatón	\N	30	67890123	2	1
7	2026-04-15 08:00:00	2026-04-15 09:30:00	Finalizado	Lote 22	Peatón	\N	60	78901234	3	2
8	2026-04-18 10:15:00	2026-04-18 11:00:00	Finalizado	Dpto 5D	Vehículo	DEF-789 Negro	60	89012345	4	1
9	2026-05-02 09:15:00	2026-05-02 10:00:00	Finalizado	Dpto 1A	Peatón	\N	60	12345678	1	2
10	2026-05-04 11:00:00	2026-05-04 12:30:00	Finalizado	Lote 9	Vehículo	STU-258 Negro	90	23456789	2	1
11	2026-05-06 13:00:00	2026-05-06 14:00:00	Finalizado	Dpto 3C	Peatón	\N	60	34567890	3	2
12	2026-05-08 08:30:00	2026-05-08 09:15:00	Finalizado	Lote 14	Peatón	\N	30	45678901	4	1
14	2026-05-12 14:30:00	2026-05-12 16:00:00	Finalizado	Lote 2	Peatón	\N	60	67890123	2	1
15	2026-05-14 09:00:00	2026-05-14 10:30:00	Finalizado	Dpto 4D	Peatón	\N	90	78901234	3	2
16	2026-05-16 11:15:00	2026-05-16 12:00:00	Finalizado	Lote 19	Vehículo	YZA-741 Rojo	60	89012345	4	1
19	2026-06-02 08:00:00	2026-06-02 09:00:00	Finalizado	Dpto 5B	Peatón	\N	60	34567890	3	2
20	2026-06-03 10:30:00	2026-06-03 11:15:00	Finalizado	Lote 17	Vehículo	EFG-963 Plata	60	45678901	4	1
21	2026-06-04 13:00:00	2026-06-04 14:00:00	Finalizado	Dpto 1B	Peatón	\N	60	56789012	1	2
22	2026-06-05 09:00:00	2026-06-05 10:30:00	Finalizado	Lote 10	Peatón	\N	90	67890123	2	1
23	2026-06-06 11:00:00	2026-06-06 12:30:00	Finalizado	Dpto 9A	Vehículo	HIJ-174 Negro	90	78901234	3	2
24	2026-06-09 08:30:00	2026-06-09 09:15:00	Finalizado	Lote 21	Peatón	\N	60	89012345	4	1
18	2026-05-24 10:00:00	2026-07-05 11:34:42.509495	Finalizado	Lote 13	Vehículo	BCD-852 Azul	90	23456789	2	1
13	2026-05-10 10:00:00	2026-07-05 11:34:42.867859	Finalizado	Dpto 7C	Vehículo	VWX-369 Blanco	90	56789012	1	2
17	2026-05-22 08:00:00	2026-07-05 11:34:43.167556	Finalizado	Dpto 2C	Peatón	\N	60	12345678	1	2
5	2026-04-13 14:00:00	2026-07-05 11:34:43.347585	Finalizado	Lote 18	Vehículo	XYZ-456 Blanco	90	56789012	1	2
\.


--
-- TOC entry 5053 (class 0 OID 16455)
-- Dependencies: 223
-- Data for Name: tag; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tag (id_tag, codigo_rfid, estado_tag) FROM stdin;
3	8A66D80E	Disponible
4	B1BA341D	Disponible
2	8746DB05	Disponible
1	0634DB05	Disponible
\.


--
-- TOC entry 5050 (class 0 OID 16430)
-- Dependencies: 220
-- Data for Name: vigilante; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.vigilante (id_vigilante, nom_vigilante, turno, usuario, contrasena, rol) FROM stdin;
1	Fabian Guerra	Mañana	fguerra	$2a$10$lnsQZMigXkydF3eD7eGERexSnVIt1uO4BvNg7cEzP0vL//Sac9Wbm	vigilante
2	Hugo Flores	Tarde	hflores	$2a$10$lnsQZMigXkydF3eD7eGERexSnVIt1uO4BvNg7cEzP0vL//Sac9Wbm	vigilante
3	Admin Sistema	Noche	admin	$2a$10$nnfurbMRsvkueraeLPEa8.VC3EHFnB8Y0Bo6xfjAx5Bo/hT1mL6N.	admin
\.


--
-- TOC entry 5051 (class 0 OID 16444)
-- Dependencies: 221
-- Data for Name: visitante; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.visitante (dni, nombre, tipo, subtipo, telefono) FROM stdin;
12345678	Juan Carlos Pérez	Familiar	Externo	987654321
23456789	María López Torres	Residente	Residente	976543210
34567890	Carlos Mendoza Ruiz	Delivery	Externo	965432109
45678901	Ana García Silva	Familiar	Externo	954321098
56789012	Pedro Ramírez Castro	Proveedor	Negocio	943210987
67890123	Lucía Fernández Vega	Residente	Residente	932109876
78901234	Roberto Díaz Flores	Delivery	Externo	921098765
89012345	Carmen Herrera Mora	Familiar	Externo	910987654
\.


--
-- TOC entry 5068 (class 0 OID 0)
-- Dependencies: 224
-- Name: estancia_id_estancia_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.estancia_id_estancia_seq', 24, true);


--
-- TOC entry 5069 (class 0 OID 0)
-- Dependencies: 222
-- Name: tag_id_tag_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tag_id_tag_seq', 4, true);


--
-- TOC entry 5070 (class 0 OID 0)
-- Dependencies: 219
-- Name: vigilante_id_vigilante_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.vigilante_id_vigilante_seq', 3, true);


--
-- TOC entry 4896 (class 2606 OID 16489)
-- Name: estancia estancia_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.estancia
    ADD CONSTRAINT estancia_pkey PRIMARY KEY (id_estancia);


--
-- TOC entry 4892 (class 2606 OID 16467)
-- Name: tag tag_codigo_rfid_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tag
    ADD CONSTRAINT tag_codigo_rfid_key UNIQUE (codigo_rfid);


--
-- TOC entry 4894 (class 2606 OID 16465)
-- Name: tag tag_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (id_tag);


--
-- TOC entry 4886 (class 2606 OID 16441)
-- Name: vigilante vigilante_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vigilante
    ADD CONSTRAINT vigilante_pkey PRIMARY KEY (id_vigilante);


--
-- TOC entry 4888 (class 2606 OID 16443)
-- Name: vigilante vigilante_usuario_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vigilante
    ADD CONSTRAINT vigilante_usuario_key UNIQUE (usuario);


--
-- TOC entry 4890 (class 2606 OID 16453)
-- Name: visitante visitante_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.visitante
    ADD CONSTRAINT visitante_pkey PRIMARY KEY (dni);


--
-- TOC entry 4897 (class 1259 OID 16505)
-- Name: idx_estado_estancia; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_estado_estancia ON public.estancia USING btree (estado);


--
-- TOC entry 4898 (class 1259 OID 16506)
-- Name: idx_hora_ingreso; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_hora_ingreso ON public.estancia USING btree (hora_ingreso DESC);


--
-- TOC entry 4899 (class 2606 OID 16490)
-- Name: estancia estancia_dni_visitante_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.estancia
    ADD CONSTRAINT estancia_dni_visitante_fkey FOREIGN KEY (dni_visitante) REFERENCES public.visitante(dni);


--
-- TOC entry 4900 (class 2606 OID 16495)
-- Name: estancia estancia_id_tag_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.estancia
    ADD CONSTRAINT estancia_id_tag_fkey FOREIGN KEY (id_tag) REFERENCES public.tag(id_tag);


--
-- TOC entry 4901 (class 2606 OID 16500)
-- Name: estancia estancia_id_vigilante_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.estancia
    ADD CONSTRAINT estancia_id_vigilante_fkey FOREIGN KEY (id_vigilante) REFERENCES public.vigilante(id_vigilante);


--
-- TOC entry 5061 (class 0 OID 0)
-- Dependencies: 225
-- Name: TABLE estancia; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT ON TABLE public.estancia TO vigilante_role;
GRANT ALL ON TABLE public.estancia TO admin_role;


--
-- TOC entry 5063 (class 0 OID 0)
-- Dependencies: 223
-- Name: TABLE tag; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT ON TABLE public.tag TO vigilante_role;
GRANT ALL ON TABLE public.tag TO admin_role;


--
-- TOC entry 5065 (class 0 OID 0)
-- Dependencies: 220
-- Name: TABLE vigilante; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.vigilante TO admin_role;


--
-- TOC entry 5067 (class 0 OID 0)
-- Dependencies: 221
-- Name: TABLE visitante; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT ON TABLE public.visitante TO vigilante_role;
GRANT ALL ON TABLE public.visitante TO admin_role;


-- Completed on 2026-07-05 18:29:10

--
-- PostgreSQL database dump complete
--

\unrestrict 9Pvmmg5iA7BQzRklJbpb6mTtjFdTJn3c291vtS2FrEUUBHCwEpdkXdhrJnfTHcA

