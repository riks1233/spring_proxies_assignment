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
                "name" : "testname1",
                "type" : "HTTP",
                "hostname" : "localhost1",
                "port" : 8080,
                "username" : "testusername1",
                "password" : "password",
                "active" : true
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
                "name" : "testname2",
                "active" : false
            }
  - Returns that proxy with updated values.

# Response types

## Success

On success response status is 200 and success JSON response body structure is:

```json
{
  "success": true, // always true
  "data": {}, // anything
}
```

## Error

On error response status is 400 and error JSON response body structure is:

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
- From `.env.dist` template file create `.env` file and populate it with relevant values. Those will be used as environment variables by the Spring Boot application and to set up a database. Those can be found in `app/build.gradle`, `app/src/main/resources/application.properties` and Docker compose files.
- Based on your OS, add those environment variables to your environment. Note that environment variables set in the following ways will only affect current shell instance.
  - For Windows, run `bin/load_env_vars_win.ps1` script.
  - For Linux, run `source .env`.
- Set up the PostgreSQL database (container **or** local machine.)
  - For a containerized database, launch a detached PostgreSQL database Docker container with `docker compose -f db_compose.yml --env-file .env up -d`. **Note** that Docker's documentation states "**Values set in the shell environment override those set in the .env file.**"
  - If you want local machine database instead, then it is up to you. ;)
- Get into the `app/` directory and run the application with `./gradlew flywayMigrate bootRun`
  - **Note**: if you are using GUI tools to launch the application, make sure they consider the `.env` variables.

## Docker dev environment

The idea was to have a containerized development environment instead of your local machine. This failed, due to multiple reasons:
- Launching `gradle bootRun` inside the container was very slow, at least when running the containers on Windows.
- To set up a separate database for tests, it needs a different port on the same network than 5432, which is not configurable for `postgres` Docker image out-of-the-box.
- It is cumbersome to utilize devtools package's hot restart feature, when the app is launched inside the container.

But the benefits that were to be achieved are:
- Easy set-up of development environment independent of host machine. Would not require to download and reconfigure to JDK 17 if host machine has another version of JDK.
- Easy containerized deploy to production which is also independent of host machine. The development, testing and production all could've used the same OS and JDK verison.

### How to set it up

This approach still does work, but (as mentioned above) does not support testing and is very slow. If you're curious to set this up, make sure:
- That your `DB_HOST` environment variable in `.env` is not set to `localhost`.
- To Use `gradle` inside the dev container instead of `./gradlew`.

To set up:

- Launch a new shell (**should not** contain `.env` environment variables).
- Run

      docker compose -f docker_env_compose.yml --env-file .env up -d

- Attach to `app-1` container CLI, for example, through Docker desktop.
- In `app-1` container CLI Run

      / # cd app
      /app # gradle flywayMigrate bootRun
- The application should now be running fully inside the containers with port 8080 mapped to host machine's port 8080.

The Docker instance's `/app` directory is volume-linked to the repository's `app` directory. And as this project is using devtools, you can dispatch `./gradlew classes` command (locally) to rebuild classes and trigger hot restart of devtools, if the application is running.

# Testing

- Run `./gradlew flywayTestDBClean flywayTestDBMigrate test` to run tests. Note that `AcceptanceSuiteTest` tests are sequential (`@TestMethodOrder`). This seems to be a bad and hardly maintainable approach, but wanted to try it out and it seems sufficient for this particular application. So integration tests were skipped.

# Improvements

Here is a list of improvements that could have been made to the project, but haven't been due to either lack of knowledge or time.

- Make `AcceptanceSuiteTest` tests independent of eachother by calling `flywayClean` and `flywayMigrate` before each test. Tried and could not figure out how to call gradle tasks programmatically. Also, I do aknowledge that the numbered naming is stupid. It is there only to support test ordering by name.
- Make gradle pick up environment variables automatically before `bootRun` or `build` or other tasks. Tried and could not figure out how to do this.
- When a malformed JSON is sent by the user and it is fed to the parser, the parser resolves to a generic JSON parse exception. Instead of a generic error, it would be good to tell the end user which specific field failed to be parsed.
- When a constraint is violated (for example, database unique record constraint), then a better error message could have been shown to the user, instead of flashing an actual database constraint name.
- Test coverage can always be improved. ;)
