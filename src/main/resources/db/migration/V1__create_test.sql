create table accounts (
    id serial primary key,
    username varchar(100) not null ,
    email varchar(255) not null ,
    password varchar(255) not null
);

insert into accounts (username, email, password) values ('test1', 'test1@test.com', 'test1234');
