# Hiring Assignment

## Project Description
Backend service for a scheduled Reddit crawler, Telegram bot integration, and a public API exposing crawled data.

## Features
- Scheduled Reddit data crawling  
- Telegram bot for notifications/interactions  
- Public API exposing the latest crawled data

## Technologies Used
- Spring Boot 3.5.1  
- Java 21  
- MySQL  
- Telegram Bot API  
- Spring Scheduler

## Prerequisites
- Java 21  
- Maven 4  
- MySQL database

## Installation Instructions
1. Clone the repository:

   ```bash
   git clone <repository-url>
   cd hiringassignment
   ```
2. Set up a local MySQL database named hiringassignment.
3. Configure the database credentials and Telegram bot properties as described in the Configuration section.
4. Run the application in debug mode with the development Spring profile:
  
  ```bash
  mvn spring-boot:run -Dspring-boot.run.profiles=dev
  ```
## Usage
The application exposes a single public API endpoint:

```bash
GET /getLatestCrawledData
Fetch the latest data crawled from Reddit.
```

## Configuration
The following properties need to be set in your application-dev.properties or environment variables:

```bash
spring.application.name=hiringassignment

spring.datasource.url=jdbc:mysql://localhost:3306/hiringassignment
spring.datasource.username=<your-db-username>
spring.datasource.password=<your-db-password>

telegram.bot.api-key=<your-telegram-bot-api-key>
telegram.bot.name=<your-telegram-bot-name>

frontend.url=https://hiringassignmentfrontend-production.up.railway.app/
```
Obtain your Telegram bot API key from BotFather .
Replace placeholders (your-telegram-bot-api-key, your-dbpassword, etc.) accordingly.

## Contact
For more information, questions, or support, please contact:
teokuohongg@gmail.com

