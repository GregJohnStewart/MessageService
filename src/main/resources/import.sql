-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;
insert into PriorityMessage (id, subject, priority, content, postingUser, startDate, endDate, createdAt, lastUpdated) 
  values (gen_random_uuid(), 'Message 1', '1', 'Message 1 content', 'import', '2025-01-05', '2025-01-05', '2025-01-05 12:25:00', '2025-01-06 14:24:00');
insert into PriorityMessage (id, subject, priority, content, postingUser, startDate, endDate, createdAt, lastUpdated) 
  values (gen_random_uuid(), 'Message 2', '2', 'Message 2 content', 'import', '2025-01-06', '2025-01-08', '2025-01-05 12:35:00', '2025-01-06 14:34:00');
insert into PriorityMessage (id, subject, priority, content, postingUser, startDate, endDate, createdAt, lastUpdated) 
  values (gen_random_uuid(), 'Message 3', '3', 'Message 3 content', 'import', '2025-01-07', '2025-01-09', '2025-01-05 12:45:00', '2025-01-06 14:44:00');
insert into PriorityMessage (id, subject, priority, content, postingUser, startDate, endDate, createdAt, lastUpdated) 
  values (gen_random_uuid(), 'Message 4', '4', 'Message 4 content', 'import', '2025-01-08', '2025-01-10', '2025-01-05 12:55:00', '2025-01-06 14:54:00');
