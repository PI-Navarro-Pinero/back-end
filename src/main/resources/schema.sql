-- 'roles' table creation
create table roles
(
    id   int primary key auto_increment,
    role varchar(15) unique not null
);

-- 'users' table creation
create table users
(
    id       int primary key auto_increment,
    username varchar(50) unique not null,
    password varchar(50)        not null,
    roleid   int                not null,

    constraint USUARIOS_ROLES_ID_FK
        foreign key (roleid) references ROLES (ID)
);