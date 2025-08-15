# Simple Crypto Converter API 🪙

A Spring Boot REST API that acts as a real-time currency converter. It uses live market data from the [Quidax API](https://docs.quidax.io/reference/introduction-user-accounts) to convert an amount from a source currency to a target currency.

## Features

- Performs real-time currency conversions.
- Fetches live market rates from the Quidax API.
- Automatically handles both direct (e.g., BTC to NGN) and inverse (e.g., NGN to BTC) market pairs.
- Uses `BigDecimal` for all financial calculations to ensure precision.
- Provides clear error messages for unsupported conversion pairs.

## Technologies Used

- Java 17
- Spring Boot 3
- Maven
- Lombok

## API Endpoint

The application exposes a single endpoint for performing conversions.

```
GET /api/v1/convert
```

## Query Parameters

| Parameter | Type   | Description                                         | Required |
|-----------|--------|-----------------------------------------------------|----------|
| from      | string | The source currency symbol (e.g., btc).            | Yes      |
| to        | string | The target currency symbol (e.g., ngn).            | Yes      |
| amount    | number | The amount of the source currency to convert.      | Yes      |

## Export to Sheets

### Example Requests

**Direct Conversion (0.5 BTC to NGN):**

```
http://localhost:8080/api/v1/convert?from=btc&to=ngn&amount=0.5
```

**Inverse Conversion (100,000 NGN to BTC):**

```
http://localhost:8080/api/v1/convert?from=ngn&to=btc&amount=100000
```

### Example Response

A successful request will return a JSON object with the conversion details:

```json
{
  "sourceAmount": 0.5,
  "sourceCurrency": "BTC",
  "targetAmount": 90166884.5,
  "targetCurrency": "NGN",
  "rate": 180333769.0
}
```

## Getting Started

To get a local copy up and running, follow these steps.

### Prerequisites

- JDK (Java Development Kit) 17 or later
- Maven

### Installation & Running the App

1. Clone the repository:

   ```
   git clone https://github.com/your-username/simple-crypto-converter.git
   ```

2. Navigate to the project directory:

   ```
   cd simple-crypto-converter
   ```

3. Run the application using the Maven wrapper:

   On macOS/Linux:

   ```
   ./mvnw spring-boot:run
   ```

   On Windows:

   ```
   mvnw.cmd spring-boot:run
   ```

The application will start on [http://localhost:8080](http://localhost:8080).