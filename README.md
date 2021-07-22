# Antra SEP java evaluation project
## 1. Setup the environment and make it run.
 All three projects are Sprintboot application.<br>

 Need to setup AWS SNS/SQS/S3 in order to use the async API.(Videos in LMS)<br>

 Make sure to update your <i>application.properties</i> file with your AWS IAM account secrets and region.(Videos in LMS)

 AWS Lambda(Sending email) is optional. Code is in [sendEmailCode.py](./lambda/sendEmailCode.py)

## 2. Understand the structure and details
Look at the [ReportingSystemArchitecture.pdf](./ReportingSystemArchitecture.pdf)

## 3. Make improvement in the code/system level.
Suggestions:
- [x] Add new features like update/delete/edit report. 
- [x] Improve sync API performance by using multithreading and sending request concurrently to both services.
- [x] Use a database instead of hashmap in the ExcelRepositoryImpl.
- [x] Improve code coverage by adding more tests.
- [x] Convert sync API into microservices by adding Eureka/Ribbon support.
- [x] Add pressure tests to benchmark the system.
- [ ] Change MongoDB to DynamoDB.
- [x] Fix bugs.
- [ ] Make the system more robust by adding fault tolerance such like : DeadLetter Queue, retry, cache, fallback etc.
- [ ] Add security and jwt support.
- [ ] Add more fancy UI using angular/react.
- [ ] Setup your CI/CD pipeline.
- [ ] Add new Services like PNGService, JPEGService etc
- [ ] ...

## 4. Send your code to [Dawei Zhuang(dawei.zhuang@antra.com)](dawei.zhuang@antra.com) using Github/Gitlab. 
Make sure there is README.MD to indicate what did you change/add to the project.

