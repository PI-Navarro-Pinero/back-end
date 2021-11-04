-- 'user' table creation
create table if not exists user
(
    id           int primary key auto_increment,
    username     varchar(50) unique not null,
    password     varchar(150)       not null,
    fullname     varchar(45)        not null,
    license      varchar(45) unique not null,
    role_admin    boolean default FALSE,
    role_agent   boolean default FALSE
);

-- load user table
insert into user(id, username, password, fullname, license, role_admin, role_agent)
values (0, 'root', '$2a$10$o888DyNlXA5fRfQA14Yj4.ifjC04Dsv834zX81ccxOOcrHej.eQsu', 'Carl Sagan', '0', true, true);

commit;