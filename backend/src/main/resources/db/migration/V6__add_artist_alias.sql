create table artist_alias
(
    id         bigint auto_increment primary key,
    artist_id  bigint      not null,
    alias      varchar(20) not null,
    created_at datetime    null,
    updated_at datetime    null
);

