# Java-based-Web-Crawler
A Java-based web crawler and search engine application that enables searching through web content from any site using information retrieval techniques.


## Project Overview

This application crawls web pages starting from any two seed URLs of your choice, processes the text content, and enables searching through the indexed content using natural language queries. The search results are ranked by relevance using vector space model and cosine similarity.

## Features

- **Flexible Web Crawling**: Crawls web pages starting from any two seed URLs provided by the user
- **Universal Site Support**: Works with any website, not limited to a specific domain like Wikipedia
- **Domain Restriction**: Optional restriction of crawling to the domains of seed URLs
- **Text Processing**: Tokenization, normalization, and stemming of document content
- **TF-IDF Weighting**: Term frequency-inverse document frequency weighting for better search results
- **Vector Space Model**: Uses cosine similarity to rank documents by relevance
- **Interactive Search Interface**: User-friendly command-line search interface

## Technology Stack

- Java 11
- JSoup for HTML parsing
- Apache Lucene for text analysis and stemming
- Maven for dependency management

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Internet connection for web crawling


### Running the Application

1. Run the application using the JAR file:
   ```
   java -jar target/wikipedia-crawler-1.0-SNAPSHOT.jar
   ```

2. Alternatively, run it directly from your IDE by executing the `WikiSearchApplication` class.

## Usage

1. When prompted, enter any two seed URLs of your choice:
   - Can be from any website (news sites, blogs, educational resources, etc.)
   - URLs must be valid and accessible

2. Choose whether to restrict crawling to the domains of the seed URLs.

3. The application will crawl web pages, process content, and build an index.

4. Enter search queries using natural language.

5. View ranked search results with relevance scores and URLs.

6. Type 'exit' to quit the application.

