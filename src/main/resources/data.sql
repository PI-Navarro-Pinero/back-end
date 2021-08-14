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
    password varchar(50)        not null,
    fullname varchar(45)        not null,
    cuil     varchar(45) unique not null,
    email    varchar(65) unique not null
);

-- load user table
insert into user(username, password, fullname, cuil, email)
values ('falcon', 'TomasPinp', 'Tomas Santiago Piñero', '20-39445871-7', 'address1@email.com'),
       ('hawkeye', 'SebastianPinp', 'Sebastian Navarro', '20-38799486-7', 'address2@email.com'),
       ('eagle', 'AdminPinp', 'Ronald Smith', '20-40789552-7', 'address3@email.com'),
       ('crow', 'UserPinp', 'Howard Conan', '20-31589741-7', 'address4@email.com');

-- load roles table
insert into role(user_id, role, role_id)
values (1, 'ROLE_ROOT', 1),
       (2, 'ROLE_ROOT', 1),
       (1, 'ROLE_ADMIN', 2),
       (3, 'ROLE_ADMIN', 2),
       (4, 'ROLE_USER', 3);

commit;