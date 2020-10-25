# Intuit Financial Aggregation System

The application will start on port `8080`.
Application allows for on-demand data aggregation and persists the data to storage.

For this POC, the implementation in one service. 
Going forward, the purposed design in this implementation can be broken down to multiple microservices:
1) Controllers can be broken into two separate exposed gateway services. One will handle the aggregation creation requests, the other will retrieve the aggregation.
2) The SourcesFactory can be moved to a dedicated microservice that acts in part as a proxy to the relevant data aggregator service.
3) Each source extractor implementation can be moved to a dedicated microservice.
4) DB storage can be replicated horizontally, and be Read-Only for all services (besides the persistence layer). 
5) Traffic between these components can be synchronous with HTTP requests, or by async using queues.

```shell script
    curl --location --request GET 'http://localhost:8080/users/12'
```

## APIs
#### POST /users
The method create/ update user's aggregation.

Headers: Content-Type=application/json
```
  {
        "id": Long,
        "username": String,
        "source": "WEBSITE"/"API"
  }
```
- All request params are required.
- If the userId is not in the system, he will be created on request.
- If it's the first time the specified user is trying to pull the source, all data will be saves in the db (source, accounts, transactions).
- If the specified user has already pulled the source:
    - Check the last aggregation time, along with the interval configuration, if not enough time has passed the request will be aborted with Unauthorized status.
    - If enough time has passed since last update:
        - Fetch the data.
        - Update aggregation time.
        - Save any new accounts if there are any.
        - Save only new transactions if there are any.
      
```shell script
    curl --location --request POST 'http://localhost:8080/users' \
    --header 'Content-Type: application/json' \
    --data-raw '  {
            "id": 12,
            "username": "bla bla",
            "source": "WEBSITE"
      }'
```
        
#### GET /users/:userId
- If the userId is not found, request will be aborted with NotFound status.
- If the user is found, all corresponding data will be returned (sources, accounts, transactions).
         

### Configuration
Application configuration specified in `application.yml` file.

```sh
    api.interval = integer, represents hours, 3rd party api interval constraint.
    api.target = string, The URL to for the api test source.
    website.interval = integer, represents hours, 3rd party website interval constraint.
    website.target = string, The Url for the test website source.
```
TODO: db configurations / usernames / passwords will be moved to a secure vault.
______________________________________________________________

### Building the source
Running the application including the Postgres DB.
```sh
  $ cd intuit-financial-server
  $ gradle build
  $ docker-compose up --build
```

Verify the deployment by navigating to your server address in your preferred browser.

```sh
127.0.0.1:8080
```
______________________________________________________________

### Post POC Implementation
1) Security layer (user permissions / authentication / monetization).
2) Configurations to Secret Vault.
3) More generics for source types and source targets.
4) Split to microservices & config Container orchestration.
5) Write MORE Tests & test all layer including mocks.
