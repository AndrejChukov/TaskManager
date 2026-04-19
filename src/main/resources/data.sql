insert into users(username, password, email, role) values ('Andrej', '1234', 'andr@mail.ru', 'ADMIN');

insert into tasks(title, description, status, priority, created_at, user_id) values
    ('Make a dinner', '1. Cook the beef. 2. Cook the rice', 'NEW', 'HIGH', current_timestamp, 1);