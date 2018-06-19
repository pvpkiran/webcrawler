**Web Crawler**
  
  Given a search string, this piece of software, searches google for the results. And goes through each link 
  from the results(top 10) and crawls the page to see javascript frameworks used.  
  
  As it is practically impossible to find all the frameworks used on a page, I have used the logic to search for
  ``<script>`` tag and try to deduce from it.  
  
  Have handled simple cases of deduplication like one's with version numbers and one's with minified(``min``) versions.
  
  
  _**_Usage Instructions_**_  
  
  1. ``mvn clean package``
  
  2. ``java -jar target/webcrawler-0.0.1-SNAPSHOT.jar``   
  
     Please provide the keyword for search when you see this message 
     
     **Please provide a keyword for search**  
     
     
   If you wish to see whats happening in the background, you can set the log level to debug as shown below
  
   `java -Dlogging.level.de.scacap.webcrawler=DEBUG -jar target/webcrawler-0.0.1-SNAPSHOT.jar` 