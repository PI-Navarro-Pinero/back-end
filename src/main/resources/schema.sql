-- 'roles' table creation
create table roles
(
    id int auto_increment,
    role varchar(15) not null
);

create unique index ROLES_ID_UINDEX
    on roles (id);

create unique index ROLES_ROLE_UINDEX
    on roles (role);

alter table roles
    add constraint ROLES_PK
        primary key (id);

-- 'users' table creation
create table users
(
    id int auto_increment,
    username varchar(50) not null,
    password varchar(50) not null,
    roleid int not null,
    constraint USUARIOS_ROLES_ID_FK
        foreign key (roleid) references ROLES (ID)
);

create unique index USUARIOS_ID_UINDEX
    on users (id);

create unique index USUARIOS_USERNAME_UINDEX
    on users (username);

alter table users
    add constraint USUARIOS_PK
        primary key (id);
