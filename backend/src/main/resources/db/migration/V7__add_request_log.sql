create table request_log
(
    id                    bigint auto_increment primary key,
    request_ip            varchar(20)                           null,
    method                varchar(6)                            null,
    request_uri           varchar(100)                          null,
    user_id               bigint                                null,
    role                  enum ('ANONYMOUS', 'ADMIN', 'MEMBER') null,
    request_size          int                                   null,
    request_content_type  varchar(50)                           null,
    request_body          text                                  null,
    response_size         int                                   null,
    response_content_type varchar(50)                           null,
    response_body         text                                  null,
    process_time          int                                   null,
    created_at            datetime                              null
);
