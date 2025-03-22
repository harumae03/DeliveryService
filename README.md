# Delivery Fee Calculator

A sub-functionality of a food delivery application that calculates delivery fees for couriers based on regional base fees, vehicle type, and current weather conditions. This project was developed as part of the Java Programming Trial Task 2024 for Fujitsu.

## Features

* **Weather Data Storage:** Utilizes an H2 database to store weather observations.
* **Scheduled Weather Import:** A configurable CronJob fetches weather data from the Estonian Environment Agency.
* **Delivery Fee Calculation:** Computes fees using business rules based on:
   * Regional Base Fee (RBF)
   * Extra fees for air temperature, wind speed, and weather phenomena
* **REST API:** Exposes an endpoint to request delivery fees with input parameters for city and vehicle type.
* **Robust Error Handling & Testing:** Implements error messages for forbidden conditions and includes unit and integration tests.

## Technologies

* **Java & Spring Boot**
* **Spring Data JPA & H2 Database**
* **RESTful API & Cron Scheduling**
* **JUnit** for testing

## How to Run

1. **Clone the Repository:**

```bash
git clone https://github.com/yourusername/your-repo.git
cd your-repo
```

2. **Build the Project:**

```bash
mvn clean install
```

3. **Run the Application:**

```bash
mvn spring-boot:run
```

4. **Access the API:** For example, to calculate a fee:

```bash
curl "http://localhost:8080/delivery-fee?city=TALLINN&vehicleType=BIKE"
```
View the API documentation at "http://localhost:8080/swagger-ui.html."

## Project Structure

* **/src/main/java:** Contains entities, repositories, services, controllers, and the scheduled task.
* **/src/main/resources:** Application configuration including the H2 database setup and CronJob schedule.
* **/src/test/java:** Unit and integration tests.

## Notes

* **CronJob Schedule:** By default, the job runs every hour at 15 minutes past the hour (HH:15:00).
* **Weather Data Source:** Data is imported from https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php.
* **Business Rules:** Fee calculations are implemented exactly as specified in the trial task requirements.
