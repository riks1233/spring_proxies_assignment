# Proxies test assignment

# Versions

Created with:

- OpenJDK Java 17.0.2.0
- Spring 2.7.4
- Gradle 7.5

# Endpoints

**Also see the included postman collection `postman_requests_collection.json`.**

- `/api/v1/proxies` [GET] - Get paginated JSON list of proxies.
  - Params:
    - `page`: [integer > 0] - The page number.
    - `per_page` [integer > 0] - How many items there should be shown per page
  - Returns paginated JSON list of proxies.
- `/api/v1/proxies/filtered` [GET] - Get a JSON list of proxies, whose `name` and `tag` match params.
  - Params:
    - `name`: [string] - The part of `name` to match.
    - `type`: [string] - The part of `type` to match.
  - Returns filtered JSON list of proxies.
- `/api/v1/proxies` [POST] - Create new proxy
  - Body:
    - JSON with all of the Proxy model values.
      - Example:

            {
                "name" : "chorizo",
                "type" : "HTTP",
                "hostname" : "localhost",
                "port" : 8080,
                "username" : "barmalej123",
                "password" : "parolj4ik",
                "active" : false
            }
  - Returns the same proxy but with id.
- `/api/v1/proxies/{id}` [GET] - Get a specific proxy.
  - Returns that proxy.
- `/api/v1/proxies/{id}` [DELETE] - Delete a specific proxy.
  - Returns that proxy.
- `/api/v1/proxies/{id}` [PUT] - Update a specific proxy.
  - Body:
    - JSON with a subset of Proxy model values.
      - Example:

            {
                "name" : "boba",
                "active" : false
            }
  - Returns that proxy with updated values.

# Response types

## Success

Success JSON response object structure:

```json
{
  "success": true, // always true
  "data": {}, // anything
}
```

## Error

Error JSON response object structure

```json
{
  "success": false, // always false
  "data": {}, // anything, but typically `null`
  "error_message": "Error message.",
  "endpoint": "uri=/api/v1/proxies",
}
```

# Set up

## Local dev environment

- Have Java 17 installed.
- Create `.env` file from its distribution template file `.env.dist` and populate with relevant values. Those will be used by the Spring Boot application and to set up a database, as `app/build.gradle` and `app/src/main/resources/application.properties` use environment variables.
- Based on your OS, add those environment variables to your environment. Note that environment variables set in the following ways affect only current shell instance.
  - For Windows, run `bin/load_env_vars_win.ps1` script.
  - For linux, run `source .env`.
- Set up the PostgreSQL database (container or local machine.)
  - Container: launch a detached PostgreSQL database Docker container with `docker compose -f db_compose.yml --env-file .env up -d`. Note that Docker's documentation states "Values set in the shell environment override those set in the .env file."
  - Local machine: up to you :)
- Get into the `app/` directory and run the application with `./gradlew flywayMigrate bootRun`
  - If you are using GUI tools to launch the application, make sure that they consider the `.env` variables.

## Docker dev environment

The idea was to have a containerized development environment instead of your local machine. This failed, due to multiple reasons:
- Launching `gradle bootRun` inside the container was very slow, at least on Windows.
- To set up a separate database for tests, it needs a different port on the same network than 5432, which is not configurable for postgres docker image out-of-the-box.
- Cumbersome to utilize devtools package hot restart, when the app is launched inside the container.

But the benefits that were to be achieved are:
- Easy set-up of development environment independent of host machine. Would not require to download and reconfigure to jdk17 if host machine has another version of jdk.
- Easy containerized deploy to production which is also independent of host machine. The development, testing and production all could've used the same OS and jdk verison.

### Set up

This approach works, but does not support testing and is slow. If you're curious to test this, make sure:
- You are not running `localhost` as your `DB_HOST` environment variable in `.env`.
- Use `gradle` instead of `./gradlew`.
- To remember that Docker's documentation states "Values set in the shell environment override those set in the .env file." So restart the shell beforehand, if needed.

    docker compose -f docker_env_compose.yml --env-file .env up -d

`// attach to app-1 container CLI`

    / # cd app
    / # gradle flywayMigrate bootRun

The docker instance's `/app` directory is volume-linked to the repository's `app` directory. And as this project is using devtools, you can dispatch `./gradlew classes` command (locally) to rebuild classes and trigger hot restart of devtools, if the application is running.

# Testing

- Run `./gradlew flywayTestDBClean flywayTestDBMigrate test` to run integration tests. Note that `ProxyControllerTest` tests are sequential (`@TestMethodOrder`).

# Improvements

Here is a list of improvements that could have been made to the project, but haven't been due to either lack of knowledge or time.

- Make acceptance tests independent of eachother by calling `flywayClean` and `flywayMigrate` before each test. Tried and did not figure out how to call gradle tasks programmatically. Also, I do aknowledge that the numbered naming is stupid and is there only to support ordering by name.
- Make gradle pick up environment variables automatically before `bootRun` or `build` or other tasks. Tried and did not figure out how to do this.
- When a malformed JSON is sent by used and it is fed to the parser, it resolves to a generic JSON parse exception. It would be good to tell the end user which specific field failed to be parsed, instead of generic error.
- When a constraint is violated (for example, database unique record constraint), then a better error message could have been shown to user, instead of flashing an actual database constraint name.
