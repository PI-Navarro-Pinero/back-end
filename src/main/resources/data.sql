-- load roles table
insert into roles(role) values ('root');
insert into roles(role) values ('admin');
insert into roles(role) values ('user');

-- load users table
insert into users(username, password, roleid) values ('Tomas', 'TomasPinp', 1);
insert into users(username, password, roleid) values ('Sebastian', 'SebastianPinp', 1);
insert into users(username, password, roleid) values ('Admin', 'AdminPinp', 2);
insert into users(username, password, roleid) values ('User', 'UserPinp', 3);
