# Project Documentation: Simple Crypto Converter

## Project Overview
The Simple Crypto Converter is a utility microservice built with Spring Boot. It provides a real-time currency conversion API that leverages live market data from the Quidax exchange. The service is designed to be robust, handling both direct market conversions (e.g., BTC to NGN) and inverse conversions (e.g., NGN to BTC) automatically.

> `GET /api/v1/convert`: Accepts from, to, and amount as query parameters and returns a structured JSON object with the conversion result.

## Core Dependencies
- **spring-boot-starter-web**: Provides all necessary components for building REST APIs, including an embedded Tomcat server and the Jackson JSON library.
- **lombok**: A utility library used to reduce boilerplate code like getters and setters.

## Project Structure and Components
The project uses a standard layered architecture. The DTO package contains both reused classes for parsing the Quidax API response and a new class for formatting our application's response.

```
/dto/
 ├── ConversionResult.java      (Our API's response model)
 ├── Ticker.java                (Reused: Innermost ticker data)
 ├── MarketData.java            (Reused: Middle layer)
 └── SingleTickerResponse.java  (Reused: Wrapper for Quidax response)
/service/
 └── ConverterService.java      (Core business logic)
/controller/
 └── ConverterController.java   (API Endpoint Layer)
```

## Detailed Class Explanations

### The DTO Layer (The Data Models)
> 📄 Ticker.java, MarketData.java, SingleTickerResponse.java

**Purpose**: These three classes are reused from previous projects. They work together as a chain to accurately model and deserialize the complex, nested JSON response that the Quidax API provides for a single market ticker.

> 📄 ConversionResult.java

**Purpose**: This is a new DTO that defines the structured JSON response our own API sends back to the user.

**Key Feature**: It uses the `java.math.BigDecimal` type for all monetary values. This is a critical best practice in financial applications to ensure calculations are precise and avoid common floating-point rounding errors.

```java
@Data
public class ConversionResult {
    private BigDecimal sourceAmount;
    private String sourceCurrency;
    private BigDecimal targetAmount;
    private String targetCurrency;
    private BigDecimal rate;
}
```

## service/ConverterService.java - The Business Logic

This class contains the core logic for the conversion, including fetching data and handling different conversion scenarios.

### `convert(...)` Method:

**Logic:** This is the primary method. It first attempts a direct conversion by creating a market pair (e.g., `btcngn`) and calling the `getTickerForMarket` helper. If a ticker is found, it calculates the result by multiplication (`amount * rate`).

If the direct market is not found (the helper returns null), it attempts an inverse conversion. It creates the inverse pair (e.g., `ngnbtc`) and calls the helper again. If successful, it calculates the result using division (`amount / rate`), specifying a precision of 8 decimal places and a rounding mode to ensure accuracy.

If both attempts fail, it throws an `IllegalArgumentException`, providing a clear error message to the client.

### `getTickerForMarket(...)` Method:

**Purpose:** This is a private helper method that encapsulates all interaction with the Quidax API.

**URL Construction:** It builds the correct URL for fetching a single ticker: `https://api.quidax.com/api/v1/markets/tickers/{market}`.

**Error Handling:** It's wrapped in a try-catch block. It specifically catches `HttpClientErrorException.NotFound`, which is the error RestTemplate throws for a 404 Not Found response. This allows the service to handle non-existent markets gracefully by returning null instead of crashing the application.

## controller/ConverterController.java - The API Layer

This class is the entry point for web requests. It's responsible for reading user input from the URL and passing it to the service layer.

**@RequestParam Annotation:** This is the key feature of this controller. It's used to extract values from the URL's query string (the part after the ?).

In a request like `.../convert?from=btc&to=ngn&amount=0.5`, `@RequestParam` maps the value of from to the from method parameter, to to the to parameter, and so on.

**Type Conversion:** A powerful feature of Spring is that it automatically converts the amount parameter from a String in the URL to a `BigDecimal` in our Java code, simplifying the logic.

### Code:

```java
@RestController
@RequestMapping("/api/v1")
public class ConverterController {

    private final ConverterService converterService;

    public ConverterController(ConverterService converterService) {
        this.converterService = converterService;
    }

    @GetMapping("/convert")
    public ConversionResult convert(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount) {
        return converterService.convert(from, to, amount);
    }
}
```