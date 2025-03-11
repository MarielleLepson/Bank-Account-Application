# Bank Account Application for REST API

## Description
The application is primarily focused on managing bank accounts. It provides endpoints to view account balances, debit and credit money, and perform currency conversions. For currency conversion, users have two options: one endpoint uses a fixed conversion rate configured in the system, while another endpoint retrieves the latest conversion rates from an external API. The API is built using Java and the Spring Framework (with Spring Boot) and is using h2 embedded database.

This is currently primarly only backend project. 

## Technologies
- Java 21
- Spring Boot
- Maven
- SQL
- H2
- Docker
- JUnit (for testing)
- Mockito (for mocking in tests)
- Swagger (for API documentation)
- Lombok (for reducing boilerplate code)
- Jackson (for JSON processing)

## Setup instructions

1. Clone project repo to you local computer ( For example with ssh )
```
git clone git@github.com:MarielleLepson/bank-account-application.git
```

2. Open terminal 
3. Move to project root folder -> then to backend folder
```
cd backend
```
5. Build docker image. For the first time it might take longer time because it needs to download all dependencies.
```
docker build -t bank-account-app .
```
5. Run docker container
```
docker run -p 8080:8080 bank-account-app
```

6. Now the application should be running. You can view and start testing with swagger: http://localhost:8080/swagger-ui/index.html

## Testing 

For this project multiple test were written to test functionality and API endpoints. The primary purpose was to test all the main functionalities, but also have a good coverage. 

In this project there are two types of tests:
1) Integration tests
2) Unit tests


### Unit tests
From backend folder run tests:

```
mvn test
```

### Integration tests

From backend folder run tests:

```
mvn verify -DskipUnitTests=true
```

## API Documentation.

The best way to view API documentation is running the poject and accessing it from http://localhost:8080/swagger-ui/index.html .  It has all endpoints with payload and responses description. Also from swagger is a good way to test the endpoints functionality.

### Account Management

- Create Bank Account
  - Method: POST
  - Path: /api/account/create
  - Description: Creates a new bank account.

- Get All Bank Accounts
  - Method: GET
  - Path: /api/account
  - Description: Retrieves a list of all bank accounts.

- Get Bank Account by Account Number
  - Method: GET
  - Path: /api/account/{accountNumber}
  - Description: Retrieves details of a specific bank account.

### Account Balance Operations
- Debit (Withdraw Money)
  - Method: POST
  - Path: /api/account-balance/debit
  - Description: Withdraws money from a specified bank account.

- Credit (Deposit Money)
  - Method: POST
  - Path: /api/account-balance/credit
  - Description: Deposits money into a specified bank account.

- Get Account Balance
  - Method: GET
  - Path: /api/account-balance/{accountNumber}
  - Description: Retrieves the current balance of the specified bank account.

### Currency Exchange

I wanted to test external API call to get exchange rates. First I tested this https://www.exchangerate-api.com/, but for free version it didn't give me necessay data back. It was really difficult to find API endpoint that gives back exchange rates for free and specifically for requested currency. I found this : https://www.exchangerate-api.com/docs/free . This webpage has free version where you can add the currency code to the url and get back all exhange rates. Then it is quite easy to filter out necessary data. The results come in quite fast as well. 

I also added fixed rate, which means that all currencies were hard coded to the code. 

- Exchange Using Real-Time Rates
  - Method: POST
  - Path: /api/currency-exchange/floating
  - Description: Converts currency using real-time exchange rates from an external API.

- Exchange Using Fixed Rate
  - Method: POST
  - Path: /api/currency-exchange/fixed
  - Description: Converts currency using a predefined (fixed) exchange rate.

## Time management
1. Github project and springboot set up with conf ~1 hour
2. H2 database and flyway migrations ~30 min
4. Creating initial data ~2 hours
5. Writing code (services, entities, classes, enums etc) ~4 hours
6. Creating API endpoints ~4 hours
7. External API creating + conf ~30 min
8. Adding swagger documentation + testing with swagger ~2 hours
9. Writing integration and unit tests ~4 hours
10. Docker setup - ~30 min
11. Writing ReadMe documentation ~ 2 hours

Overall : ~21 hours of work 

## What else would I have done in this project?



