Web Crawler
===========

The web crawler crawls same domain only as required and builds a simple site map showing links to other pages under the
same domain, links to static content such as images, and to external URLs. Given a starting URL, it visits all pages within
the domain but not follow the links to external sites such as Google or Twitter.

The sitemap is printed every X seconds to the console.

How to build and run
--------------------

The langauge of choice is Java8 and Maven to build and run.

Build using Maven:

```mvn clean install```

To run, from the project root execute the following:

```mvn exec:java -Dexec.mainClass="com.micvog.Main" -Dwebsite=<website_url>```

For example:

```mvn exec:java -Dexec.mainClass="com.micvog.Main" -Dwebsite="https://en.wikipedia.org/wiki/Dodo"```

Assumptions
-----------

* Different urls can result to the same content and 3xx were not considered for the scope of this project. Considering
canonical URLs could also improve removing duplicates.

* The crawler runs as a single thread, but the solution is extensible to be able to become multithreaded in the future.

* The project uses Lombok dependency to create classes without boilerplate code (e.g. equals and hashcode, constructors, toString etc)

* No protection was made against downloading very large web entities. Content-Length checks should be configured in a real system.

* Jsoup was used only for its DOM parsing functionality. The html parser only identifies absolute paths for
this exercise (e.g. "http://www.google.com/images.html" and not "/images.html")

* The way the sitemap is printed is using a select * approach where in a real keyvalue / storage system this would not be feasible.
 We would probably make some batch reads in order to iteratively build the sitemap.
 Select * is not an option in a large-scale storage solution like cassandra.

* Printing the sitemap would require a richer XML library for creating and printing it. For speed, a string builder is
 constructing it here.

* The solution uses a job scheduler to assign tasks in order to avoid a recursive way of crawling. This allows for
better control of prioritization (and different crawling strategies if needed). That is, if you want to do a breadth first
search then the job scheduler will handle the task distribution differently. For simplicity a FIFO queue is used for now.

The output of the sitemap builder looks like this:

```
<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
  <url>
    <loc>http://www.example.net/?id=who</loc>
  </url>
  <url>
    <loc>http://www.example.net/?id=what</loc>
  </url>
  <url>
    <loc>http://www.example.net/?id=how</loc>
  </url>
</urlset>
```

We could enhance the output by introducing <image>, <video> and other tags as well as information about the URL such as
last modification, change frequency and priority. These are left out.

Logging
-------
Using Slf4j-simple for logging.

