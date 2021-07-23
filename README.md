Reporting System
===========================
The project made the modifications and extensions of the simi-finsihed code branches and concept provided by the Antra Inc, based on the new knowledge I learnt from weeks of trainings. The main propose of this project is to practice the implementation of new learned cut-edge technologies. Thus, some methods are use different writing styles even they have the same functionality. <br>

This project is a full-stack application with a simple UI to allow users to easily generate PDF reports and Excel reports from JSON plain data typed in the front-end user interface. The application currently supports generate and waiting for documents being well-prepared(`SyncAPI`), or submit the request and being reminded when the document is ready(`AsyncAPI`). Editing an existed document, as well as deleting a document are also implemented in the current version of project.<br>

The applications are all built with `Spring Boot` framework, and `Hibernate(H2)`, `DynamoDB` are used to connect and handle the databases. `AWS` are also be critical contexts in all applications. Beyond these main tech stacks, `JQuery`, `BootStrap`, `Junit`, and `Jackson`, etc. are also involved in the project with different purposes.<br>


## Context
* [Deployment](#deployment)
* [Modifications](#modifications)
    * [Frontend](#frontend)
    * [Backend and Database](#backend)
    * [AWS Related](#aws)
    * [Testing](#testing)
* [Project Architecture](#architecture)
* [Author](#author)

Deployment
-----------
This project is built on IntelliJ. To avoid possible compatible errors, I suggest to use IntelliJ to import this project to test and play with it. Several manual settings should be done before initialize the project.
1. Set the `cloud.aws.region.static`, `cloud.aws.credentials.accessKey`, `cloud.aws.credentials.secretKey` in each `application.properties`. A default couple of access key and secret key is left in the setting file, in case other Credentials are work for this project due to unfitted aws settings.
2. If use customized AWS Credentials, set the `s3.bucket` in `application.properties` files of `ExcelService` and `PDFService`. __Note that: the ID of S3 bucket must be unique!__.
3. If use customized AWS Credentials, make sure the AWS IAM account has valid SQS/SNS/S3/DynamoDB settings correlated the project.
4. Make sure the port `8080`, `80`, `8888`, `9999`, and `7070` are vacant, or change the default server in any `application.properties` files. 

Then, ✨TATA~! You've been all set! Just initialize each of five applications and visit the client url!


Modifications
-----------
A lot of modifications have been done with this project. Per the modification target, I've classified the modification to several categories.

### Frontend
The modification related to HTML, CSS and JavaScript(JQuery) files are classified to frontend category. 
- new `DELETE` request: Implemented a new method with `Ajax` on deleting request to appropriate backend api.
    - completed the function `showDelete()` in `app.js`.
    - Temporarily uses alert windows to show reaction(deleted successfully) to users.
- new editing feature: In order to handle the request of editing or updating an existed report.
    - a new "editing window" added, based on "generating report" window.
    - straightened out the logic for submit an edited report: the update API used will be the same as the generated file that will be overrode.
    - created JQuery functions called `showEditing()` and `edit()` to control and handle the editing element behavior and requests.
    - added new feature that pre-load the exact document's JSON content which report record user queried to edit and show it on the textfield inside the editing window to bring convenience to users. the combination of JQuery functions `showEdit()` and `asJSON()` have realized the feature. 
    - designed to send `PUT` request to the backend.
- re-named `Created time` column to `Last Edited Time`: update the logic as every time edit the report, `Last Edited Time` will be assigned a new time stamp.
- Added new effect: the loading icon has been imported to give user feedback after submitted/deleted/edited.
    - Added a new `<figure>` tag as the container for the icon in HTML.
    - Created a new class `#mask` and assign it the attributes in CSS to implement the designed effect.
    - Applied the BootStrap default class `invisible` and use `.addClass()/.removeClass()` to hide/show the elements.
    - Successfully fixed the bug that the editing window, or the generation window, unexpectedly popped out if user manually close the window before it automatically closed at the point that frontend receiving the response from back end by implementation of exactly the loading design.

### Backend
All changes made inside the application Java code branches except for the AWS Service related parts have been classified to this category. This part is also one of the essential part of this project.
- new `DELETE` api in `ClientService` application: give a gate to frontend to make requests of deletion actions for reports.
    - Combined with frontend `DELETE` request, this api has successfully implemented the `DELETE` action of CRUD.
    - Used path variable `reqID` as the key to target the entry that needs to be removed from DAO of `ClientService`.
    - Received the HTTPRequest by `ReportController` class with `@deleteMapping()`.
    - Redirect the request to `ReportService` class to do the deletion action.
    - Designed connection between `ClientService` and `Excel/Pdf-Service` to delete the real document and DAO contents in those two applications.
- new `DELETE` api in `ExcelService` and `PdfService`: delete the real files and database entries in these applications.
    - received the HTTPRequest by Controller classes respectively and dispatch the task to Service classes respectively.
    - Used path variables `fileID`(not the same as `reqID`, while stored in the database of `ClientService` by the key `reqID`) to locate the file.
- new `PUT` api in `ClientService` application: give a gate to frontend to make update of reports.
    - Combined with frontend `PUT` request, this api has successfully implemented the `UPDATE` action of CRUD.
    - Receiving path variable `reqID` as the key to find the entries and files, and `method` as the label to decide which method to use when updating(Sync/Async).
    - sync and async updating will be executed by different methods in `ReportService`.
    - `generateReportsAsync` or `generateReportsSync` will be called to generate edited files.
    - new generated entries in databases will override the old entries after temporarily save the old entries to communicate with `ExcelService` and `PdfService`.
    - `DELETE` api of `ExcelService` and `PdfService` will be reached from `ClientService` to prevent any isolate entries or files.
- new multi-thread feature applied to SyncAPI: improve the performance for generating reports by sync method.
    - two thread classes inherited from Runnable created.
    - method `generateSyncFiles()` will call those two threads simultaneously.
    - `FutureTask` is used in order to receive response objects from other threads.
- `ExcelService` will no longer use `HashMap` to store entries: ~~Hibernate will take the role~~.
    - __this feature has already been overrode by other features.__ See [AWS](#aws) section, the third entry.
    - tuned the `ExcelFile` entity to auto convert to/from H2 object.
    - implemented the new `ExcelDatabaseRepo` interface extended to H2 repository client interface to operate the database.
- generated Excel files now will also be put in S3 Bucket: project folder will no longer appear generated files.
    - temporary files will be generated first before stored to S3.
    - the deletion api for ExcelService is retired. Downloading requests will directly send to S3 Bucket.
    - the original intention for designing this feature is that pressure test generated too many useless Excel files in project folder.
- micro service has been used for syncAPI: with the help of `Eureka`.
    - created a new application `EurekaClient` to serve the Eureka services.
    - moved all sync api related code branches to a new application: `SyncAPI`.
    - post requests to `SyncAPI` application will be made inside of `sendDirectRequests()` method in `ClientService`, and handle the response from it.
    - multiple instances of `SyncAPI` application can be initialized with the Eureka client, while it hasn't been realized currently in the project.
    
### AWS
AWS related parts are also important in this project. Without the help of AWS, implementation of async api will be way more complicated. This section lists the changes regarding AWS part from begin.
- SQS/SNS/S3 settings have been done to first initialize the project: without watching IWS videos (I can't log in to the system).
    - SQS/SNS names are keep as the default, while S3 name has been assigned a new one in project.
- uses DynamoDB for `ExcelService` and `PDFService` applications: drop the usage of H2 for `ExcelService` and MongoDB of `PDFService`.
    - rewrote the entities in both applications to fit the DynamoDB file format.
    - added a new configuration class to well set the connection to DynamoDB.
    - designed a new converter between `String` and `LocalDataTime` objects for DynamoDB entities.
    
### Testing
There hasn't been lots of modifications in testing parts for this project. Almost all previous test code branches are kept as what it was. The main change for testing is that a pressure test has been added in the `ClientService` application.

The pressure testing is designed to check the performance and tolerance of current system, and implemented with the help of `PerfTest` and `Junit`. `GET` method for the main user port and `POST` for sync api(within the Eureka) have been tested. 

For the first test, 9000 requests will be sent to the API on total, and the simultaneous request threads will be 150. The benchmark of the maximum response time for a single request is 1200ms, and average response time is 250ms, then the total time for the test is 30s. The test passes, proves the tolerance of `GET` api for "/" is acceptable.

As the second test will go through the hole process for sync generation api, which means distinct reports will be also generated by each request, I didn't set a high volume for total requests. 200 requests will be sent with 50 simultaneous threads. As generating procedure takes time, maximum response time has been set to 6s, and a total of procedure has been set as 30s as well. Acceptable average response delay is 4s as computed. The test passes as well. That give us a signal that the sync api is also decent.


Architecture
-----------
To be done soon.


Author
-----------
|Author|University|Degree
|---|---|---
|Ruoyu Shen|Michigan State University|Master's