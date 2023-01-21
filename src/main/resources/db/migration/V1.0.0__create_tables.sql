create table OBJECTS
(
    OBJECT_ID    varchar(255) not null,
    `NAME`       varchar(255) not null,
    constraint PK_OBJECTS PRIMARY KEY (OBJECT_ID)
);

create table METADATA
(
    METADATA_ID  varchar(255) not null,
    OBJECT_ID    varchar(255) not null,
    DESCRIPTION  varchar(255) not null,
    COMMENT      varchar(255),
    constraint PK_METADATA PRIMARY KEY (METADATA_ID)
);

-- inline creation of index not possible with jooq-ddl-generator, see https://github.com/jOOQ/jOOQ/issues/9768
create index IDX_OBJECT_ID ON OBJECTS (OBJECT_ID);
create index IDX_METADATA_ID ON METADATA (METADATA_ID);
