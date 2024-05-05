create table file_metadata(
    id uuid not null constraint file_metadata_pkey primary key,
    filename varchar(100) not null,
    mime_type varchar(128) not null,
    size int8 not null,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

create table accounts (
    id serial primary key,
    username varchar(100) not null ,
    email varchar(255) not null ,
    password varchar(255) not null,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

insert into accounts (username, email, password) values ('test1', 'test1@test.com', 'test1234');
