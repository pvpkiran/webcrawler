# WebCrawler
  Simple Application to crawl the web. Given an url, the application crawls the page recursively to find  
all available hyperlinks upto a certain depth. Depth is configurable.

### Problem Statement
(1) Connect to an arbitrary website (http/s)

(2) Search for all links on this site referring to other pages of the same domain (website) and collect them

(3) for each collected link start over with (1) - we would like to see some multithreading here since network tends to be slow

(4) When finished, output the full collection of links sorted by the link label

### Reference  

This project is built on Java 21 and Springboot version 3. The core logic of this program lies in [Webcrawler.java](/src/main/java/at/ecosio/webcrawler/service/Webcrawler.java).  
The depth to which we want to recursively crawl is configurable via application.properties or via run time parameter.  
I have configured connection timeout(4 seconds) and read timeout(4 seconds) to avoid long waits during connecting/reading a webpage. This is also configurable.  
There is an option to switch to virtual threads using the configuration parameter **webcrawler.isVirtual**.  
The application uses an in memory store to save the jobs and respective links.   

When a user submits a url to crawl via a REST endpoint(POST), a job is created and saved in memory. User will get the respective job id, with which he/she can query the status of the job.
Job can be in one of the following status SUBMITTED, RUNNING, COMPLETED, FAILED.  
Status of the running job(or result in case of COMPLETED) can be obtained via the GET endpoint. 

### Running
The application is built as a maven project(with in built maven wrapper) using Java 21. We can either package it as a jar and then run it

 `1 ./mvnw clean package`

`2 java -jar target/webcrawler-0.0.1-SNAPSHOT.jar.original`

or you can open the application in an IDE and run the main class i.e [WebcrawlerApplication](src/main/java/at/ecosio/webcrawler/WebcrawlerApplication.java)

Wait till you see the following message   

**Started WebcrawlerApplication in 21.22 seconds (process running for 21.441)**

### Web App
 Rest Endpoints can be accessed via curl or via the in built swagger ui  

 http://localhost:8080/swagger-ui/index.html

 There are two endpoints

 1. **POST /crawl/submit** ===> This accepts the url as input and returns an id.
 2. **GET /crawl/status/{id}** ===> This returns the status of the request with id {id}. If completed, returns the list of links.

Click on the desired link and press **Try it out** to trigger the endpoints from swagger.