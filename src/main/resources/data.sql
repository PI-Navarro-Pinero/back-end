-- 'roles' table creation
create table if not exists role
(
    id      int primary key auto_increment,
    user_id int         not null,
    role    varchar(15) not null,
    role_id int,

    foreign key (user_id) references user (id)
);

-- 'user' table creation
create table if not exists user
(
    id       int primary key auto_increment,
    username varchar(50) unique not null,
    password varchar(150)       not null,
    fullname varchar(45)        not null,
    license  varchar(45) unique not null
);

-- load user table
insert into user(id, username, password, fullname, license)
values (0, 'root', '$2a$10$o888DyNlXA5fRfQA14Yj4.ifjC04Dsv834zX81ccxOOcrHej.eQsu', 'Carl Sagan', '0');

-- load roles table
insert into role(user_id, role, role_id)
values (0, 'ROLE_R', 1),
       (0, 'ROLE_W', 2),
       (0, 'ROLE_X', 3);

commit;