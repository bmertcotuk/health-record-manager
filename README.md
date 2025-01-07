# Health Record Manager - Backend Application
## Introduction
This documentation provides useful information about the implementation of the project.

## Business Requirements
* User should be able to upload a CSV containing comma-separated health records.
* User should be able to see the list/download of health records.
* User should be able to see the get/download a health record by the unique field `code`.
* User should be able to delete all records.

## Technologies Used
* Java 21
* Spring Boot (Web, Security, Data JPA, Validation, Test)
* REST API
* H2 in-memory DB
* Apache Commons CSV
* Maven
* SLF4J
* Lombok
* Junit
* Mockito


## Specifications
### Backend Side

#### API
A RESTful API has been implemented by using Spring Boot.

#### DB
H2 in-memory DB keeps the records of health records uploaded as a CSV file by the user.

#### DB Communication
Spring Data JPA provides the abstraction for the communication between the H2 DB and the SpringBoot backend application.

#### CSV Operations
Apache Commons CSV has been used for reading and writing CSV files.

#### Exception Handling
ExceptionHandler & ControllerAdvice have been used for handling different types of exceptions in a readable and global way. Proper error responses are returned for application exceptions, authentication exceptions, invalid input exception, and so on.

#### Security
For the sake of simplicity of this assignment, basic authentication with username `admin` and password `Qwer1234` has been used. Communication between frontend and backend also gets through this authentication. One possible improvement might be using JWT in the future.

#### Code Quality
The entire source code has been analyzed with SonarLint to improve and maintain the code quality. Possible small improvements would be defining some strings as application constants or better usage of streams in ApplicationExceptionHandler.

#### Testing
JUnit and Mockito have been used while writing the unit tests. To be able to test the exception handler MockMVC has been used. Coverage is not 100% but it is close to that. Test cases can be found under test folder of the project. Possible improvements would be adding more test cases for the controller and ApplicationExceptionHandler.

#### Logging
SLF4J has been used for logging. Logs are mainly added to the service layer. They can be improved by logging starting and ending point of each method with arguments and, if any, return values.

### Other & Possible Improvements
* Application does not require any external configuration to be run.
* Application config can be improved from `.properties` to `.yml` for better readability.
* A RESTful API has been implemented by using Spring Boot.
* Maven has been used for dependency management and build.
* Interfaces have been separated from the classes.
* Code structure has been organized according to separation of concerns. One possible improvement would be separating the CSV management from HealthRecordService into CsvService.
* DTOs are used in order not to expose the application entities to outside world. Service layer could also use something like BO instead of interacting directly with repository model/entity.
* To keep operations atomic `@Transactional` keyword is used.
* Pagination with default values is used for the list of health records.
* Locks could be used to prevent concurrent access to DB records, however, the main use of this application is to upload and delete in bulk. Regarding the limited time, such feature has not been implemented.
* As an improved validation, file content type can be checked.
* Swagger Documentation can be added to the project for simpler testing.

### Testing the API
Postman collection is attached to the project. You can import it and test the API endpoints.

