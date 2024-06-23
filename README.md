# Getting Started
  Simple Application to crawl the web. Given an url, the application crawls the page recursively to find  
all available hyperlinks upto a certain depth. Depth is configurable.

### Reference Documentation


### Running
The application is built as a maven project(with in built maven wrapper) using Java 21. We can either package it as a jar and then run it

 `1 ./mvnw clean package`

`2 java -jar target/webcrawler-0.0.1-SNAPSHOT.jar.original`

or you can open the application in an IDE and run the main class i.e [WebcrawlerApplication](src/main/java/at/ecosio/webcrawler/WebcrawlerApplication.java)

