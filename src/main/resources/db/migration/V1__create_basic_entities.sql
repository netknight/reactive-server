create table file_metadata(
    id uuid not null constraint file_metadata_pkey primary key,
    filename varchar(100) not null,
    mime_type varchar(128) not null,
    size int8 not null,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table accounts (
    id uuid not null constraint accounts_pkey primary key,
    username varchar(100) not null,
    email varchar(255) not null,
    password varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null
);

insert into accounts (id, username, email, password, created_at, updated_at) values ('f73ac98a-d034-4b20-b4e6-4ab647c74785', 'test1', 'test1@test.com', 'test1234', current_timestamp, current_timestamp);
