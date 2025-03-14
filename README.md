# lendingkart-assignment

## Project Description
The `lendingkart-assignment` project is designed to convert XML data to JSON format using a Spring Boot application. This project provides a RESTful API that allows users to perform the conversion operations efficiently.

## xmlToJsonConverter

/**
 * Main class to run the Spring Boot application.
 * 
 * This class contains the main method which serves as the entry point for the Spring Boot application.
 * It uses SpringApplication.run to launch the application.
 * 
 * To run the application:
 * 1. Ensure you have Java and Maven installed.
 * 2. Navigate to the project directory.
 * 3. Use the command `mvn spring-boot:run` to start the application.
 * 
 * Once the application is running, you can access the API at:
 * http://localhost:8080/api/convert
 * 
 * The API endpoint `/api/convert` can be used to perform specific conversion operations.

## Postman API Documentation

### POST : `http://localhost:8080/api/convert`

Sample raw(xml)
```
<?xml version="1.0" encoding="UTF-8"?>
<Response>
    <ResultBlock>
        <ErrorWarnings>
            <Errors errorCount="0" />
            <Warnings warningCount="1">
                <Warning>
                    <Number>102001</Number>
                    <Message>Minor mismatch in address</Message>
                    <Values>
                        <Value>Bellandur</Value>
                        <Value>Bangalore</Value>
                    </Values>
                </Warning>
            </Warnings>
        </ErrorWarnings>
        <MatchDetails>
            <Match>
                <Entity>John</Entity>
                <MatchType>Exact</MatchType>
                <Score>35</Score>
            </Match>
            <Match>
                <Entity>Doe</Entity>
                <MatchType>Exact</MatchType>
                <Score>50</Score>
            </Match>
        </MatchDetails>
        <API>
            <RetStatus>SUCCESS</RetStatus>
            <ErrorMessage />
            <SysErrorCode />
            <SysErrorMessage />
        </API>
    </ResultBlock>
</Response>
```

Sample Json
```
{"ResultBlock":{"ErrorWarnings":{"Errors":{"errorCount":"0"},"Warnings":{"warningCount":"1","Warning":{"Number":"102001","Message":"Minor mismatch in address","Values":{"Value":["Bellandur","Bangalore"]}}}},"MatchDetails":{"Match":[{"Entity":"John","MatchType":"Exact","Score":"35"},{"Entity":"Doe","MatchType":"Exact","Score":"50"}]},"API":{"RetStatus":"SUCCESS","ErrorMessage":"","SysErrorCode":"","SysErrorMessage":""}},"MatchSummary":{"TotalMatchScore":85}}

```
 
