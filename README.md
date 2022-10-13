# Proxies test assignment

# Versions

Created with:

- OpenJDK Java 17.0.2.0
- Spring 2.7.4

# Endpoints

**Also see the included postman collection `app/postman_requests_collection.json`.**

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
- Create `.local_dev` file from its distribution template file `.local_dev.dist` and populate with relevant values. Those will be used to run the application, as `app/build.gradle` and `app/src/main/resources/application.properties` use environment variables.
Based on your OS, add those environment variables to your environment.
- For Windows, open PowerShell and execute the `bin/load_dev_env_windows.ps1` script. Note that environment variables will be set only in that PowerShell instance, not globally.
- For linux run `source .local_dev`. Also, note that environment variables are not added globally.
- Set up a detached PostgreSQL database Docker container with `docker compose -f db_only_compose.yml --env-file ./.local_env up -d`. Note that Docker's documentation states "Values set in the shell environment override those set in the .env file."
- Get into the `app/` directory and run the application with `./gradlew flywayMigrate bootRun`

## Docker dev environment

**TODO: Details**

    docker compose --env-file ./.docker_env up -d

`// attach to app-1 container CLI`

    / # cd app
    / # gradle flywayMigrate bootRun

The docker instance's `/app` directory is volume-linked to the repository's `app` directory. And as this project is using devtools, you can dispatch `./gradlew classes` command (locally) to rebuild classes and hence reboot the application, if it is running.

# Testing

**TODO: Details**

- Run `./gradlew flywayTestDBClean flywayTestDBMigrate test` to run integration tests. Note that `ProxyControllerTest` tests are sequential (`@TestMethodOrder`).
