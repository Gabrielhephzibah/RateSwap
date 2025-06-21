# RateSwap

RateSwap is an Android application for exchanging currencies in a multi-currency account. The app is built using the MVVM + Clean Architecture pattern

## App Structure

### Data Layer
Consists of the following packages:
- **Local:**
   - Contains the Data Access Objects (DAO), Entity classes, Room Database, and Datastore Preference.
- **Mapper:**
  - Contains functions that map data between different data classes.
- **Remote:** 
  - Contains API endpoint and API DTOs.
- **Repository:** 
  - Implements the repository interface and contains the repository implementations.
    
### DI Layer
  - Handles dependency injection (using Hilt) and contains dependency modules.

### Domain Layer
Serves as an interface between the Data Layer and the UI Layer. It consists of:
- **Model:** 
  - Contains model or data classes used in the UI layer.
- **Repository:**
   - Contains methods that are implemented by the Repository in the Data Layer.
- **UseCases:**
    - Contains application business requirement logic implementation, and classes

### Presentation Layer
Handles everything related to the user interface:
- Contains screens, ViewModels, and other UI-related tasks.
  

### Test
Contains test classes that validate the logic in the Data Layer, Domain Layer and Presentation Layer.
- Uses JUnit, MockK, and Truth for unit testing.

## Libraries

- **Kotlin:**  
  The primary programming language used for building the application
  
- **Jetpack Compose:**  
  Uses Jetpack Compose for creating the UI and screens.
    
- **Room:**  
  Uses the Room library to store and manage multiple account balances..

- **Flows and Coroutines:**  
  Uses Kotlin Coroutines and Flows for asynchronous data handling and to achieve concurrency in a lifecycle-aware manner.

- **Dependency Injection:**  
  Uses the Hilt library for dependency injection, providing and injecting dependencies where needed.

- **Retrofit:**  
  Uses Retrofit as the HTTP client to handle network requests.

- **Mockk:**  
  Uses the Mockk library in tests to mock and stub interfaces for testing business logic.

- **JUnit:**  
  Uses JUnit as the testing framework to run unit tests.

- **Truth:**  
  Uses the Truth library for test assertions.
