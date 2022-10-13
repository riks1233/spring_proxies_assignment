-- WARNING! THIS FILE IS MEANT FOR INITIALIZING A LOCAL DB ONLY.
-- NOT TO BE MODIFIED WITH YOUR OWN PASSWORDS!
CREATE DATABASE proxies_assignment;
CREATE DATABASE proxies_assignment_testdb;
CREATE USER proxies_assignment_user WITH PASSWORD '<INSERT_PASSWORD_HERE>';
CREATE USER proxies_assignment_testdb_user WITH PASSWORD '<INSERT_PASSWORD_HERE';
-- Would be a good practice to revoke usage on public schema, all tables, functions, etc. for both users
-- and grant them only the least necessary privileges.
GRANT ALL ON DATABASE proxies_assignment TO proxies_assignment_user;
GRANT ALL ON DATABASE proxies_assignment_testdb TO proxies_assignment_testdb_user;

