# Bank Account Application for REST API

## Description
The application is primarily focused on managing bank accounts. It provides endpoints to view account balances, debit and credit money, and perform currency conversions. For currency conversion, users have two options: one endpoint uses a fixed conversion rate configured in the system, while another endpoint retrieves the latest conversion rates from an external API. The API is built using Java and the Spring Framework (with Spring Boot) and is using h2 embedded database.

> This project is primarily a backend application.


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
- Lombok 
- Jackson (for JSON processing)

## Setup instructions

1. Clone the repository  
Clone the project to your local computer using SSH / HTTPS:
```
git clone git@github.com:MarielleLepson/bank-account-application.git
```

2. Navigate to the project folder  
Open a terminal and move into the project’s repository and then to backend folder:
```
cd backend
```

3. Build the Docker image  
Build the Docker image (the initial build may take longer as dependencies are downloaded):  
```
docker build -t bank-account-app .
```
4. Run the Docker container  
Run the container by mapping port 8080:
```
docker run -p 8080:8080 bank-account-app
```

5. Access Swagger UI  
Once the application is running, open your browser and visit: http://localhost:8080/swagger-ui/index.html  
This is where you can view the API documentation and test the endpoints.

## Solution comments

1) For testing purposes, when the backend starts, it generates 4 random accounts and assigns each 2 balances with random currencies and amounts.  
2) The current solution supports 5 currencies: EUR, USD, SEK, RUB, and KRW. More can be added to the `Currency` enum class if needed.  
3) The database stores only account balances that have been created and received deposits. If a balance reaches 0.00, it remains in the database and is not deleted. For example, if a user initially has USD and EUR balances, other currency balances will not exist in the database.  
4) Users can perform currency exchanges using a fixed rate, which is hardcoded in a map.  
5) Users can also use real-time currency exchange rates via an external API.  
6) Flyway was used for database migrations and table creation. I find it best solution when I need to do changes, I can just add another migration file.
7) I chose Swagger for API documentation because it is the best way to test backend applications. It provides clear documentation, allows easy example setup, and is interactive. However, for long-term use, Postman is also useful for saving API requests and running them quickly.  
8) For data storage, I decided to keep accounts in one table and store all account balances in a separate table with a foreign key reference. A transactions table was also created to store and track all credit and debit transactions.  
9) The external API configuration is stored in `application.yml`, making it easy to access and modify. Additional settings like an `enabled` boolean or an API key can also be added there.
10) To run this project, I decided that a Dockerfile is sufficient because it automates the setup process by downloading all dependencies, configuring the environment, and ensuring that Java is properly installed. Docker provides a consistent runtime environment, making it easy to deploy and run the application on any system without worrying about manual setup or configuration issues.

### Recommended Testing Steps

When starting testing, I recommend these steps:

1) **Get All Bank Accounts** – Retrieve the list of existing accounts to see what is available.  
2) **Check Account Balances** – Copy one of the account numbers and fetch its balances to understand how the system stores them.  
3) **Create a New Bank Account** – Try creating your own bank account to verify account creation functionality.  
4) **Test Credit Before Debit** – Deposit money into an account before attempting to withdraw, ensuring the balance is sufficient.  
5) **Try Currency Exchange** – Perform a currency exchange, selecting an account that has enough balance for the transaction.  

## Testing 

For this project multiple test were written to test functionality and API endpoints. The primary purpose was to test all the main functionalities, but also have a good coverage. 

In this project there are two types of tests:
1) Integration tests
2) Unit tests


### Unit tests

To run unit tests, execute the following command from the backend folder:

```
mvn test
```

### Integration tests

To run integration tests, execute the following command from the backend folder:

```
mvn verify -DskipUnitTests=true
```

## API Documentation.

For full API documentation, run the project and access Swagger at http://localhost:8080/swagger-ui/index.html. This interactive interface provides endpoint details, including request/response payloads and descriptions.

You can also test the API using **Postman** by providing the requests and responses outlined below.

### Account Management

- Create Bank Account
  - Method: POST
  - Path: /api/account/create
  - Description: Creates a new bank account.
 
Request 
```
{
  "accountHolder": "Mari Maasikas"
}
```

Response:  
```
{
  "accountNumber": "EE123456789012345678",
  "accountHolder": "Mari Maasikas"
}
```

- Get All Bank Accounts
  - Method: GET
  - Path: /api/account
  - Description: Retrieves a list of all bank accounts.

Response:
```
[
  {
    "accountNumber": "EE123456789012345678",
    "accountHolder": "Mari Maasikas"
  },
  {
    "accountNumber": "EE987654321098765432",
    "accountHolder": "Mart Tamm"
  }
]
```

- Get Bank Account by Account Number
  - Method: GET
  - Path: /api/account/{accountNumber}
  - Description: Retrieves details of a specific bank account.
  - Example Request: (GET /api/account/EE123456789012345678)


Response: 
```
{
  "accountNumber": "EE123456789012345678",
  "accountHolder": "Mari Maasikas"
}
```



### Account Balance Operations
- Debit (Withdraw Money)
  - Method: POST
  - Path: /api/account-balance/debit
  - Description: Withdraws money from a specified bank account.

Request: 
```
{
  "accountNumber": "EE123456789012345678",
  "currency": "EUR",
  "amount": 50.00
}
```

Response: 
```
{
  "message": "Debit successful"
}
```

- Credit (Deposit Money)
  - Method: POST
  - Path: /api/account-balance/credit
  - Description: Deposits money into a specified bank account.
 
Request: 
```
{
  "accountNumber": "EE123456789012345678",
  "currency": "EUR",
  "amount": 100.00
}
```

Response: 
```
{
  "message": "Deposit/Credit successful"
}
```

- Get Account Balance
  - Method: GET
  - Path: /api/account-balance/{accountNumber}
  - Description: Retrieves the current balance of the specified bank account.
  - Example Request: (GET /api/account-balance/EE123456789012345678)  

Response: 
```
{
  "accountNumber": "EE123456789012345678",
  "balances": [
    {
      "currency": "EUR",
      "balance": 100.00
    },
    {
      "currency": "USD",
      "balance": 50.00
    },
    {
      "currency": "SEK",
      "balance": 50.00
    },
    {
      "currency": "RUB",
      "balance": 50.00
    },
    {
      "currency": "KRW",
      "balance": 50.00
    }

  ]
}

```


### Currency Exchange

For currency conversion, there are two approaches:  
- Exchange Using Real-Time Rates
  - Method: POST
  - Path: /api/currency-exchange/floating
  - Description: Converts currency using real-time exchange rates from an external API.
 
Request 
```
{
  "accountNumber": "EE123456789012345678",
  "fromCurrency": "USD",
  "toCurrency": "EUR",
  "amount": 100.00
}
```

Response:
```
{
  "accountNumber": "EE123456789012345678",
  "balances": [
    {
      "currency": "EUR",
      "balance": 100.00
    },
    {
      "currency": "USD",
      "balance": 50.00
    }
  ]
}
```


- Exchange Using Fixed Rate
  - Method: POST
  - Path: /api/currency-exchange/fixed
  - Description: Converts currency using a predefined (fixed) exchange rate.  

Request 
```
{
  "accountNumber": "EE123456789012345678",
  "fromCurrency": "USD",
  "toCurrency": "EUR",
  "amount": 100.00
}
```

Response:
```
{
  "accountNumber": "EE123456789012345678",
  "balances": [
    {
      "currency": "EUR",
      "balance": 100.00
    },
    {
      "currency": "USD",
      "balance": 50.00
    }
  ]
}
```

> Note: Initially, an external API from exchangerate-api.com was tested, but its free version did not provide the necessary data. I found an alternative free endpoint that allows adding the currency code to the URL to obtain all exchange rates, making it easier to filter the required information. The responses were quite fast.

## Time management
Below is an overview of the time spent on various tasks in this project:
- GitHub project and Spring Boot setup with configuration: ~1 hour
- H2 database setup and Flyway migrations: ~30 minutes
- Creating initial data: ~2 hours
- Writing code (services, entities, classes, enums, etc.): ~4 hours
- Creating API endpoints: ~4 hours
- External API setup and configuration: ~30 minutes
- Adding Swagger documentation and testing with Swagger: ~2 hours
- Writing integration and unit tests: ~4 hours
- Docker setup: ~30 minutes
- Writing README documentation: ~2 hours

Overall: ~21 hours of work

## What else would I have done in this project?
1) Add more data validation logic throughout the application and throw clear exceptions when necessary.
2) Add more logging to capture critical events and error conditions for easier debugging and monitoring.
3) Test with h2 storing to files or add postgreSQL
4) Expand testing by configuring H2 to store data in files or integrate a database like PostgreSQL.
5) Develop a frontend interface to allow users to interact with the API. I would use Vue or Angular for that. 
6) Define and write tests for edge cases to ensure robust handling of unexpected inputs and scenarios.
7) In some places double is used instead of Big Decimal. I would remove double type for balance, since it creats some type handling problems.
8) Save transaction, when currency exchange happens.
9) Now after looking at it, I would maybe change currency-exchange API endpoints responses, to return something like this. This would give better idea, what was done during currency exchange. 
```
{
  "fromCurrency": "USD",
  "toCurrency": "EUR",
  "amount": 100.00,
  "convertedAmount": 85.00,
  "exchangeRate": 0.85
}
```





