-- liquibase formatted sql

--changeset kolgotik:1
insert into users (username, first_name, last_name, password, role)
values
    ('admin', 'Admin', 'User', '$2a$10$4UyfXYuj8YDmWHyNmFtO7O43PutPUJMKa5vLRHFDSQ6NKTJXZ83zK', 'ADMIN'),
    ('user1', 'John', 'Doe', '$2a$10$4UyfXYuj8YDmWHyNmFtO7O43PutPUJMKa5vLRHFDSQ6NKTJXZ83zK', 'USER'),
    ('user2', 'Jane', 'Smith', '$2a$10$4UyfXYuj8YDmWHyNmFtO7O43PutPUJMKa5vLRHFDSQ6NKTJXZ83zK', 'USER');

--changeset kolgotik:2
insert into cards (card_number, owner_name, expiration_date, status, balance, user_id)
values
    ('u3MnAUmgqr0HMoBdrqEU1LOhU4iPFIr3LoJgSMTkyw4=', 'John Doe', '2025-12-31', 'ACTIVE', 10000.00, 2),
    ('u3MnAUmgqr0HMoBdrqEU1Baet9HNJ9siJaU+y6z3gNY=', 'Jane Smith', '2026-01-31', 'ACTIVE', 1500.00, 3),
    ('u3MnAUmgqr0HMoBdrqEU1IprSTW0AD2aedDxCk4sJFU=', 'Jane Smith', '2030-01-31', 'ACTIVE', 15000.00, 3),
    ('u3MnAUmgqr0HMoBdrqEU1On5ZhybeCrGx9iXV+ddbKo=', 'John Doe', '2024-05-31', 'EXPIRED', 2000.00, 2);
