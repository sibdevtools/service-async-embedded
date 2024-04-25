CREATE TABLE IF NOT EXISTS async_task
(
    task_uid           varchar(128) NOT NULL PRIMARY KEY,
    task_type          varchar(32)  NOT NULL,
    task_version       varchar(16)  NOT NULL,
    status             varchar(16)  NOT NULL,
    status_description varchar(512) NOT NULL,
    retry              INTEGER      NOT NULL,
    created_at         timestamp    NOT NULL,
    last_retry_at      timestamp    NOT NULL,
    next_retry_at      timestamp    NOT NULL
);

CREATE INDEX IF NOT EXISTS async_task_next_retry_at ON async_task (last_retry_at);

CREATE TABLE IF NOT EXISTS async_task_param
(
    task_uid    varchar(128)  NOT NULL,
    param_name  varchar(256)  NOT NULL,
    param_value varchar(1024) NOT NULL,
    PRIMARY KEY (task_uid, param_name),
    FOREIGN KEY (task_uid) REFERENCES async_task (task_uid)
);