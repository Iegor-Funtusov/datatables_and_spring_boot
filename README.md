## Install

### Prepare database

MySQL example:

```sql
drop schema if exists datatables;
create schema datatables;
alter database datatables charset=utf8 collate=utf8_bin; 
create user datatables@localhost identified by 'datatables';
grant all on datatables.* to datatables@localhost;