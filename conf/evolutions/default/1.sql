# --- !Ups

CREATE TABLE api_definitions (
    key varchar(255) NOT NULL PRIMARY KEY,
    backlog_domain varchar(255) NOT NULL,
    backlog_api_key varchar(255) NOT NULL
);

# --- !Downs

DROP TABLE api_definitions;
