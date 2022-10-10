CREATE SEQUENCE hibernate_sequence START 1; -- Needed for JPA/Hibernate to work.
CREATE TABLE proxies (
    id bigint NOT NULL,
    name varchar(120) NOT NULL,
    type varchar(10) NOT NULL,
    hostname varchar(120) NOT NULL,
    port integer NOT NULL,
    username varchar(120) NOT NULL,
    password varchar(60) NOT NULL,
    active boolean NOT NULL,
    CONSTRAINT PK_proxies PRIMARY KEY (id),
    CONSTRAINT UK_proxies_hostname UNIQUE (hostname),
    CONSTRAINT UK_proxies_name UNIQUE (name)
);
