### How to run?
Create new table or specify existing in Postgres.
Configure access by specifying url, username and passowrd in `src/main/resources/application-default.properties` file.

Example:
```
##PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/survey_db
spring.datasource.username=postgres
spring.datasource.password=123456
```

Open folder with survey project in console.

Compile, test, package
```bash
gradlew clean bootJar
```

run
```bash
gradlew bootRun
```

Use your favourite REST Client to access survey API.

Also after app is started the API documentation is available on url
```
http://localhost:8080/swagger-ui.html#/survey45controller
```  
