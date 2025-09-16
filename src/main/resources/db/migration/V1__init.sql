-- Esquema base para usuarios y roles

CREATE TABLE roles (
                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(100) NOT NULL,
                       enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE users_roles (
                             user_id BIGINT NOT NULL,
                             role_id BIGINT NOT NULL,
                             PRIMARY KEY (user_id, role_id),
                             CONSTRAINT fk_users_roles_user
                                 FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                             CONSTRAINT fk_users_roles_role
                                 FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);