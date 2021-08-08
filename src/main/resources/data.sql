-- 'roles' table creation
create table if not exists roles
(
    id   int primary key auto_increment,
    role varchar(15) unique not null
);


-- 'people' table creation
create table if not exists people
(
    id       int primary key auto_increment,
    fullname varchar(45) not null,
    cuil     varchar(45) not null unique,
    email    varchar(65) not null unique
);

-- 'users' table creation
create table if not exists users
(
    id       int primary key auto_increment,
    username varchar(50) unique not null,
    password varchar(50)        not null,
    roleid   int                not null,
    personid int                not null,

    foreign key (roleid) references roles (ID),
    foreign key (personid) references people (ID)
);

create unique index TABLE_NAME_COLUMN_1_UINDEX
    on users (personid);

-- load roles table
insert into roles(id, role)
values (1, 'root'),
       (2, 'admin'),
       (3, 'user');

-- load people table
insert into people(id, fullname, cuil, email)
values (1, 'Tomas Santiago Piñero', '20-39445871-7', 'address1@email.com'),
       (2, 'Sebastian Navarro', '20-38799486-7', 'address2@email.com'),
       (3, 'Ronald Smith', '20-40789552-7', 'address3@email.com'),
       (4, 'Howard Conan', '20-31589741-7', 'address4@email.com');

-- load users table
insert into users(id, username, password, roleid, personid)
values (1, 'falcon', 'TomasPinp', 1, 2),
       (2, 'hawkeye', 'SebastianPinp', 1, 1),
       (3, 'eagle', 'AdminPinp', 2, 3),
       (4, 'crow', 'UserPinp', 3, 4);

commit;