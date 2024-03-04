# Task Management System

## Prerequisites

- JDK 17
- Maven
- MySQL 3.8.0

## Run locally

### Database

- MySQL instance should be up and running on localhost at port 3306. configure `application.properties` as meeded for connection details.
- i.e. for Docker instance use the following command

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

## APIs Flow

- Register user and login to retrieve JWT token through `/users/register` and `/users/login`.
- Add task using `/tasks` POST API and manage tasks through the same endpoint. Note that tasks APIs are protected and authentication needed for them to work (using `Authorization` header with `Bearer {token}` value that is retrieved from `/users/login` endpoint).
- Search APIs example using query params is executing GET request to `/tasks/search?dueDate=2024-03-16&page=0&size=5&sort=priority,asc`.
- Tasks operations along with the search are combined in `TaskController`.

## Assumptions and considerations

- Tests are just showcase of executing tests using Junit and Spring Boot test tools. Following KISS and YAGNI, not all branches of codde are covered for simplicity’s sake.
- Focus was on handling all kind of structural constrains like error handling of all kinds including authentication, authorization, business exceptions through layers, business error structures and validation errors. Producing unified error structure ready for contract based communication.
- Reduced the need for more DTOs and unnecessary classes just for simplicity.
- JavaDoc provided for main functionalities only for simplicity.
- Project developed locally and github repository created at the end, that is why there are not many commits.
- Authorization only applied to DELETE task API for simplicity.
- ROLES of the user are passed through registeration fo simplicity, in typical app, admin is not able to register.
