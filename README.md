# Task Management System

## Prerequisites

- JDK 17
- Maven
- MySQL 3.8.0

## Run locally

### Database

- MySQL instance should be up and running on local host. configure `application.properties` as meeded for connection details. For Docker instance use the following command

```sh
docker run --name some-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=tasks -e MYSQL_USER=u -e MYSQL_PASSWORD=p -d -p 3306:3306 mysql:8.3.0
```

### Test

```sh
mvn clean test
```

### Start App

```sh
mvn clean spring-boot:run 
```

## Assumptions and considerations

- Tests are just showcase of executing tests using Junit and Spring Boot test tools. Following KISS and YAGNI, not all branches of codde are covered for simplicity’s sake.
- Focus was on handling all kind of structural constrains like error handling of all kinds including authentication, authorization, business exceptions through layers, business error structures and validation errors. Producing unified error structure ready for contract based communication.
- Reduced the need for more DTOs and unnecessary classes just for simplicity.
- JavaDoc provided for main functionalities only for simplicity.