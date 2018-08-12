--TABELA ENQUETE

CREATE SEQUENCE public.enquete_id_seq;

CREATE TABLE public.enquete
(
  id integer NOT NULL DEFAULT nextval('enquete_id_seq'),
  token text,
  pergunta text,
  iniciada boolean,
  encerrada boolean,
  data_criacao timestamp with time zone,
  data_atualizacao time with time zone,
  CONSTRAINT enquete_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
  
--TABELA OPCAO  
CREATE SEQUENCE public.opcao_id_seq; 

CREATE TABLE public.opcao
(
  id integer NOT NULL DEFAULT nextval('opcao_id_seq'),
  quantidade bigint,
  descricao text,
  data_criacao time with time zone,
  data_atualizacao time with time zone,
  id_enquete bigint,
  CONSTRAINT opcao_pk PRIMARY KEY (id),
  CONSTRAINT enquete_fk FOREIGN KEY (id_enquete)
      REFERENCES public.enquete (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);