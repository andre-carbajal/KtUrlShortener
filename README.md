# KtUrlShortener - Kotlin URL Shortener

A simple URL shortening service built with Kotlin, Spring Boot, and Redis. It provides a web UI for shortening URLs and
viewing history, as well as a REST API for programmatic access and management.

## Features

* **Shorten URLs:** Convert long URLs into short, manageable codes.
* **Custom Codes:** Optionally provide your own desired short code.
* **Automatic Code Generation:** If no custom code is provided, a unique 6-character code is generated.
* **Redirection:** Short URLs redirect users to the original long URL.
* **Visit Tracking:** Counts the number of times a short URL is accessed.
* **Web UI:** Simple Thymeleaf-based interface to shorten URLs and view the history of created URLs.
* **REST API:** Manage URLs programmatically:
    * Get all URLs.
    * Get statistics (original URL, creation date, visits) for a specific URL code.
    * Update the original URL associated with a short code.
    * Delete a short URL.
* **Basic Authentication:** API endpoints for creating (via UI form), updating, and deleting require an authorization
  token set in the configuration.
* **Redis Persistence:** Uses Redis to store URL mappings and visit counts.
* **URL Validation:** Basic checks to ensure the provided URL format is valid.

## Technology Stack

* **Language:** Kotlin (JVM)
* **Framework:** Spring Boot 3.4.4
* **Database:** Redis (via Spring Data Redis)
* **Web:** Spring Web, Thymeleaf (for UI)
* **Build Tool:** Gradle
* **Java Version:** 21

## Prerequisites

* Java 21 SDK or later
* Gradle (or use the included Gradle wrapper `./gradlew`)
* A running Redis instance

## Setup and Running

1. Clone the repository:

```bash
git clone https://github.com/andre-carbajal/KtUrlShortener.git
cd KtUrlShortener
```

2. Ensure Redis is running: Make sure your Redis server is accessible on the host and port specified in
   `application.properties`.
3. Build the project:

```bash
./gradlew build
```

4. Run the application:

- Using Gradle:

```bash
./gradlew bootRun
```

- Using the JAR (check build.gradle.kts for group/version if name differs):

```bash
java -jar build/libs/KtUrlShortener-1.0.jar
```

5. Access the application:

- Web UI: Open `http://localhost:8080` (or your configured app.base-url ) in your browser.
- API: Use tools like `curl` or Postman to interact with the API endpoints (see below).

## API Endpoints

The following REST endpoints are available under the `/api` path. The `app.auth` value from `application.properties`
must be provided in the `Authorization` header for PUT and DELETE requests.

- `GET /api/urls`
    - Description: Retrieves a list of all stored URLs with their details.
    - Response: 200 OK with a JSON array of Url objects.
- `GET /api/urls/{urlCode}/stats`
    - Description: Retrieves statistics for a specific short URL code.
    - Path Variable: urlCode - The short code of the URL.
    - Response: 200 OK with a JSON Url object containing original URL, visits, etc., or 404 Not Found.
- `PUT /api/urls/{urlCode}`
    - Description: Updates the original URL associated with a short code.
    - Requires Header: Authorization: <your_app.auth_value>
    - Path Variable: urlCode - The short code to update.
    - Request Body: JSON object containing the new originalUrl. Example: { "
      originalUrl": "https://new-destination.com" }
    - Response: 200 OK on success.
- `DELETE /api/urls/{urlCode}`
    - Description: Deletes a short URL entry.
    - Requires Header: Authorization: <your_app.auth_value>
    - Path Variable: urlCode - The short code to delete.
    - Response: 204 No Content on success.

## Web UI

- `GET /`
  Displays the main page with a form to shorten a new URL and a history table of previously shortened URLs.

- `POST /ui/urls`
  Handles the form submission for shortening a URL. Requires originalUrl, optional urlCode, and the authInput (matching
  app.auth ). Redirects back to / with success or error messages.

- `GET /{urlCode}`
  Redirects the user to the original URL associated with the urlCode. Increments the visit count. Returns 404 Not Found
  if the code doesn't exist.

## Redis Data Structure

- `url:code:<urlCode>` -> `originalUrl` (String)
- `url:original:<originalUrl>` -> `urlCode` (String) - Used for duplicate checks.
- `url:visits:<urlCode>` -> `visitCount` (String representation of a Long)
- `url:created:<urlCode>` -> `creationTimestamp` (String representation of LocalDateTime)
- `urls:all` -> List of all `urlCodes` (Redis List)
