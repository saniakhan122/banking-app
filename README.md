# Banking App Backend

A scalable and modular backend for the Banking App, providing RESTful API endpoints for online banking operations, account management, user authentication, admin workflows, and more.

---

## Technologies Used

- Java (Jakarta EE / JAX-RS)
- Maven or Gradle (for dependency management)
- Oracle Database (or compatible RDBMS)
- Standard Java libraries
- Service and DAO patterns for business logic and persistence

---



## Major REST Endpoints

### Account Creation (`/v1/account-requests`)
### Admin (`/v1/admin`)


### Customer Registration & Profiles (`/v1/customers`)

### Customer Login & Account Security (`/v1/customer-login`)


### Account & Banking Operations (`/v1/banking`)


---

## How To Run

1. **Prerequisites:**
    - JDK 17 or later
    - Maven or Gradle
    - Oracle Database or configured RDBMS

2. **Clone the repository and import into your IDE**

3. **Configure database access** in your datasource or `application.properties`

4. **Build and deploy:**  
   Use your preferred app server (WebLogic, Tomcat, Payara, etc.), or a JAX-RS compatible embedded runtime.

   ```sh
   mvn clean package

5. **Access API endpoints**  
   - Example: [http://localhost:8080/banking-web/api/v1/customers/register](http://localhost:8080/banking-web/api/v1/customers/register)
   - Use [Postman](https://www.postman.com/) or `curl` for testing the endpoints.  
     For example, to register a new customer via cURL:
     ```sh
     curl -X POST http://localhost:8080/banking-web/api/v1/customers/register \
        -H "Content-Type: application/json" \
        -d '{"fullName":"John Doe", "email":"john.doe@email.com", ... }'
     ```
