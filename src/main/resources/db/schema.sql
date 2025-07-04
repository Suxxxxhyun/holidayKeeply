DROP TABLE IF EXISTS holiday CASCADE;
DROP TABLE IF EXISTS country CASCADE;

CREATE TABLE country (
                         id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                         country_code VARCHAR(255) NOT NULL,
                         name VARCHAR(255),
                         created_time TIMESTAMP(6),
                         last_modified_time TIMESTAMP(6)
);

CREATE TABLE holiday (
                         id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                         fixed BOOLEAN NOT NULL,
                         global BOOLEAN NOT NULL,
                         holiday_year DATE,
                         country_id BIGINT,
                         created_time TIMESTAMP(6),
                         last_modified_time TIMESTAMP(6),
                         counties VARCHAR(255),
                         launch_year VARCHAR(255),
                         local_name VARCHAR(255),
                         name VARCHAR(255),
                         CONSTRAINT fk_country FOREIGN KEY (country_id) REFERENCES country (id)
);
