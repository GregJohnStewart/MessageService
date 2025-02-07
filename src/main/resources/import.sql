-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;
insert into PriorityMessage (id, title, priority, createdAt, content, postingUser) values (gen_random_uuid(), 'Message 1', '1', '2025-01-05', 'Message 1 content', 'import');
insert into PriorityMessage (id, title, priority, createdAt, content, postingUser) values (gen_random_uuid(), 'Message 2', '1', '2025-01-06', 'Message 2 content', 'import');
insert into PriorityMessage (id, title, priority, createdAt, content, postingUser) values (gen_random_uuid(), 'Message 3', '2', '2025-01-07', 'Message 3 content', 'import');
insert into PriorityMessage (id, title, priority, createdAt, content, postingUser) values (gen_random_uuid(), 'Message 4', '1', '2025-01-08', 'Message 4 content', 'import');