create table if not exists users(
    id identity,
    username varchar(50) not null unique,
    password varchar(255) not null,
    email varchar(50) not null,
    role varchar(10) not null
);

create table if not exists tasks(
    id identity,
    title varchar(50) not null,
    description varchar(500) not null,
    status varchar(20) not null,
    priority varchar(10) not null,
    created_at timestamp default current_timestamp,
    user_id bigint references users(id)
);