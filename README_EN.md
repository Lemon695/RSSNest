# RSSNest

RSSNest is a project for aggregating and parsing RSS Feeds. It provides the functionality to fetch data from specified sources and convert it into a standard RSS format.

## Key Features

*   **RSS Feed Parsing**: Capable of parsing RSS feeds from various sources.
*   **Data Caching**: Supports Redis caching to improve response speed and reduce server load (cache duration: 3 hours).
*   **Categorized Resources**: Ability to fetch the latest resource lists based on categories.

## Technology Stack

*   Java
*   Spring Boot
*   Jsoup (HTML Parsing)
*   OkHttp (HTTP Requests)
*   Swagger/Knife4j (API Documentation and UI)

## Code Structure

```
src/main/java/com/rss/nest/
├── controller      # RESTful API Controllers
├── function        # Core business logic and feature implementation
│   ├── rrdynb         # (Potentially a module for another feature)
│   └── zsxcool       # Zsxcool related RSS parsing and processing logic
├── models          # Data models (DTOs)
├── utils           # Utility classes (HTTP requests, RSS output, date parsing, etc.)
└── enums           # Enum classes
```


