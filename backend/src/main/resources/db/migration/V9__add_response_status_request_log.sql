alter table request_log
    add response_status int null after request_body;
