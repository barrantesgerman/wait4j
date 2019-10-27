## wait4j

`wait4j` is a Java utility designed to wait for the availability of various services through its host and port.

It is useful to synchronize the start of an application that requires other services to be available, such as docker containers.

It was inspired by [vishnubob/wait-for-it](https://github.com/vishnubob/wait-for-it) and [eficode/wait-for](https://github.com/eficode/wait-for). But it differs from them in two aspects:

 - It is written in Java, so it requires JRE 8
 - It can verify multiple hosts simultaneously

## Usage

```
Usage: java -jar waitj4.jar host:port [host:port] [-t timeout] [-q] -- command args
Options:
 -q, --quiet              Do not output any status messages
 -t, --timeout=TIMEOUT    Timeout in seconds, zero for no timeout, default 30 seconds
-- COMMAND ARGS           Execute command with args after the test finishes  
```

## Examples

To check if [HABV](https://www.habv.org/) is available:

```
$ java -jar wait4j.jar www.habv.org:80 -- echo 'HABV is up'

Connecting with www.habv.org:80
Connection to www.habv.org:80 succeeded!
HABV is up
```

To check if [Google](https://www.google.com/) and [Facebook](https://www.facebook.com/) and [Twitter](https://twitter.com/) are available and show a Hello World notification:

```
$ java -jar wait4j.jar www.google.com:80 www.facebook.com:80 twitter.com:80 -- notify-send 'Hello World!' 'This is a custom notification!'

Connecting with twitter.com:80
Connecting with www.google.com:80
Connecting with www.facebook.com:80
Connection to www.google.com:80 succeeded!
Connection to twitter.com:80 succeeded!
Connection to www.facebook.com:80 succeeded!
```

Set timeout to 10 seconds and try a service not available, the exit code will be 1

```
$ java -jar wait4j.jar mysql:3306 -t 10 -- echo 'MySQL is down'

Connecting with mysql:3306
Timeout occurred after waiting 10 seconds
```

Docker example:

```yaml
version: '3.7'
services:
  db:
    image: mysql:8.0
    ports:
      - "3306:3306"
    environment:
      MYSQL_DATABASE: exampledb
      MYSQL_USER: example
      MYSQL_PASSWORD: example
      MYSQL_ROOT_PASSWORD: root
    networks:
      - backend
  app-server:
    build:
      context: . 
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/exampledb?useSSL=false&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_USERNAME: example
      SPRING_DATASOURCE_PASSWORD: example
    networks:
      - backend
    command: ["java", "-jar", "/wait4j.jar", "db:3306", "--", "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/SpringBoot.jar"]
networks:
  backend:
```

