FRONT END:
1. implemented the delete api.
2. created a new penal assigned to editing items.
3. implemented a function to query the origin content of an entry and make the function to set the valid previous JSON content as the default text inside of editing model's text field.
4. Changed the logic of column: Created time, re-named it as last edited time.

BACK END:
1. Implemented a "DELETE" api, allows front end to request a "delete" method to backend with reqId.
2. Implemented a "PUT" api shared with the same link of "DELETE" api. Allows user to update the information of their previous requested reports and then override the documents. The api can only use original method (what users used as they first generated this entry) to update data in this version.
3. Optimized the document generate process for Sync API by using multi-thread feature of Java to simultaneously generate the Excel report and PDF report.
4. Serialized the HashMap to store generated files in Excel generating application to a Hibernate(H2) based database.
5. Built a Eureka server to access the Microservice Architecture, and create a new project as a client of the Eureka server and move all sync api part to the project as microservice parts. The testing proved the system works decently under the pressure. 

TESTING:
1. Conducted the pressure test to the GET method of main page and the Sync POST API with the help of PerfTest and Junit.

AWS RELATED:
1. Correctly set the AWS services to make the initial template work properly.
2. Changed the SQS logic for PDFrequest to PDFresponse as well as ExcelRequest to ExcelResponse due to default logic doesn't work at my side.
