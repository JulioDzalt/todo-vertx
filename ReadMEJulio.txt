

# Run Database


docker run --name vertx-todo-mysql -e MYSQL_ROOT_PASSWORD=my-secret-pw -p 3306:3306 -d mysql:5.7.37

// docker-compose run --service-ports bd_todo 

docker exec -it 679cb963640668959b7f4bd00f3eab4233df65debb4322d455d66958cdaf49a3 bin/bash

/etc/init.d/mysql start
mysql -u root -p
my-secret-pw

create database todos;

use todos;

drop table todos;

CREATE TABLE IF NOT EXISTS todos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status TEXT
);

select * from todos;

INSERT INTO todos (name, status) VALUES  ("Comprar un mango","to do");
INSERT INTO todos (name, status) VALUES  ("Lavar ropa","in progress");
INSERT INTO todos (name, status) VALUES  ("Hablar al banco","done");


CREATE USER 'julio'@'localhost' IDENTIFIED BY 'juliopass';
GRANT ALL PRIVILEGES ON * . * TO 'julio'@'localhost';
GRANT ALL PRIVILEGES ON todos.* TO 'julio'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;


CREATE USER 'username'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON *.* TO 'username'@'localhost' WITH GRANT OPTION;
CREATE USER 'username'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON *.* TO 'username'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;