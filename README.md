# threebunkers

> [According to the classic fairy tale, the first little pig built his house of straw, the second little pig built his house of sticks, and the third little pig built his house of bricks.](https://en.wikipedia.org/wiki/The_Three_Little_Pigs)

This project includes three minimalistic sub-projects to compare three different architectures:

- Traditional [Spring Boot](https://spring.io/projects/spring-boot) project with [MVC and JDBC](https://spring.io/guides/gs/serving-web-content)
- Reactive Spring Boot project with [WebFlux and R2DBC](https://spring.io/guides/gs/reactive-rest-service)
- Reactive [Vert.x](https://vertx.io/) project

Each project exposes an HTTP endpoint, which triggers two tasks: a heavy SQL query and an external HTTP request. The query can be slow, but the data traffic is minimal. The external request uses a service that delays a few random seconds and returns. The response for each request includes the timing details for that one request.

A monitor utility pools and logs the status of each application using a "health" endpoint exposed by each application.
