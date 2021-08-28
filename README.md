## wait4j

`wait4j` is a utility designed to wait for the availability of various services through its host and port.

It is useful to synchronize the start of an application that requires other services to be available, such as docker containers.

It was inspired by [vishnubob/wait-for-it](https://github.com/vishnubob/wait-for-it) and [eficode/wait-for](https://github.com/eficode/wait-for). But it can verify multiple hosts simultaneously.

## Usage

```
Usage: wait4j [-hvV] [-t=<timeout>] -a=address[,address...] [-a=address[,
              address...]]... [--] COMMAND...
Wait for availability of multiple services.
      COMMAND...            Execute command with args after the test finishes.
  -a, --address=address[,address...]
                            One or more addresses to check, in
                            format: 'host:port'.
  -h, --help                Show this help message and exit.
  -t, --timeout=<timeout>   Timeout in seconds, zero for no timeout
                            (default 30 seconds).
  -v, --verbose             Print more details (default: false).
  -V, --version             Print version information and exit.
  --                        This option can be used to separate command-line
                              options from the list of positional parameters.
```

## Examples

To check if [Github](https://github.com/) is available:

```
$ wait4j -v -a github.com:443 -- echo 'Github is up'
[➡] Connecting with github.com:443
[✔] Connection to github.com:443 succeeded!
Github is up
```

To check if [Google](https://www.google.com/) and [Facebook](https://www.facebook.com/) and [Twitter](https://twitter.com/) are available and show a Hello World notification:

```
$ wait4j -v -a www.google.com:443,www.facebook.com:443 -a twitter.com:443 -- notify-send 'Hello World!' 'This is a custom notification!'
[➡] Connecting with twitter.com:443
[➡] Connecting with www.facebook.com:443
[➡] Connecting with www.google.com:443
[✔] Connection to twitter.com:443 succeeded!
[✔] Connection to www.google.com:443 succeeded!
[✔] Connection to www.facebook.com:443 succeeded!
```

Set timeout to 10 seconds and try a service not available, the exit code will be 1

```
$ wait4j -v -a mysql:3306 -t 10 -- echo 'MySQL is up'
[➡] Connecting with mysql:3306
[✖] Timeout occurred after waiting 10 seconds
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
    command: ["wait4j", "-a", "db:3306", "--", "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/SpringBoot.jar"]
networks:
  backend:
```
