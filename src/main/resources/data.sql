-- load roles table
insert into roles(id, role)
values (1, 'root'),
       (2, 'admin'),
       (3, 'user');

-- load users table
insert into users(id, username, password, roleid) values (1, 'Tomas', 'TomasPinp', 1);
insert into users(id, username, password, roleid) values (2, 'Sebastian', 'SebastianPinp', 1);
insert into users(id, username, password, roleid) values (3, 'Admin', 'AdminPinp', 2);
insert into users(id, username, password, roleid) values (4, 'User', 'UserPinp', 3);

commit;