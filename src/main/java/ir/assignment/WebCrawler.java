package ir.assignment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Web crawler that starts from seed URLs and crawls web pages
 */
public class WebCrawler {
    private static final int MAX_PAGES = 10;
    private final List<String> seedUrls;
    private final Set<String> visitedUrls;
    private final List<Document> documents;
    private int documentCounter;
    private final Set<String> allowedDomains;
    private boolean restrictToDomains;

    public WebCrawler(List<String> seedUrls) {
        this.seedUrls = seedUrls;
        this.visitedUrls = new HashSet<>();
        this.documents = new ArrayList<>();
        this.documentCounter = 0;
        this.allowedDomains = extractDomains(seedUrls);
        this.restrictToDomains = true; // By default, restrict crawling to seed domains
    }

    /**
     * Set whether to restrict crawling to the domains of the seed URLs
     * @param restrict true to restrict to seed domains, false to crawl any domain
     */
    public void setRestrictToDomains(boolean restrict) {
        this.restrictToDomains = restrict;
    }

    /**
     * Extract domain names from a list of URLs
     */
    private Set<String> extractDomains(List<String> urls) {
        Set<String> domains = new HashSet<>();
        for (String url : urls) {
            try {
                URI uri = new URI(url);
                domains.add(uri.getHost().toLowerCase());
            } catch (URISyntaxException e) {
                System.err.println("Invalid URL: " + url);
            }
        }
        return domains;
    }

    /**
     * Get the domain from a URL
     */
    private String getDomain(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost().toLowerCase();
        } catch (URISyntaxException e) {
            return "";
        }
    }

    /**
     * Normalize and resolve relative URLs
     */
    private String normalizeUrl(String baseUrl, String href) {
        if (href == null || href.isEmpty()) {
            return null;
        }

        // Skip fragment-only URLs and special links
        if (href.startsWith("#") || href.startsWith("javascript:") || 
            href.startsWith("mailto:") || href.equals("/")) {
            return null;
        }

        try {
            URI base = new URI(baseUrl);
            URI resolved = base.resolve(href);
            return resolved.toString();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * Start the crawling process from seed URLs
     * @return list of crawled documents
     */
    public List<Document> crawl() {
        Queue<String> urlQueue = new LinkedList<>(seedUrls);

        while (!urlQueue.isEmpty() && documents.size() < MAX_PAGES) {
            String url = urlQueue.poll();
            
            // Skip if URL has already been visited
            if (visitedUrls.contains(url)) {
                continue;
            }
            
            try {
                System.out.println("Crawling: " + url);
                
                // Connect to the URL and get the document
                org.jsoup.nodes.Document jsoupDoc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0")
                        .timeout(5000)
                        .get();
                
                // Mark URL as visited
                visitedUrls.add(url);
                
                // Extract title and content
                String title = jsoupDoc.title();
                
                // Extract main content - more generic approach
                String content = "";
                
                // First try common content containers
                Element contentElement = jsoupDoc.selectFirst("main, article, #content, .content, #main");
                
                // If no specific content container is found, use the body
                if (contentElement == null) {
                    contentElement = jsoupDoc.body();
                }
                
                if (contentElement != null) {
                    content = contentElement.text();
                }
                
                // Create and add document
                Document document = new Document(documentCounter++, url, title, content);
                documents.add(document);
                System.out.println("Added document: " + document.getTitle());
                
                // Find and enqueue new links
                if (documents.size() < MAX_PAGES) {
                    Elements links = jsoupDoc.select("a[href]");
                    for (Element link : links) {
                        String href = link.attr("href");
                        String newUrl = normalizeUrl(url, href);
                        
                        if (newUrl == null) {
                            continue;
                        }
                        
                        // Check domain restriction if enabled
                        if (restrictToDomains) {
                            String domain = getDomain(newUrl);
                            if (!allowedDomains.contains(domain)) {
                                continue;
                            }
                        }
                        
                        if (!visitedUrls.contains(newUrl)) {
                            urlQueue.add(newUrl);
                        }
                    }
                }
                
            } catch (IOException e) {
                System.err.println("Error crawling " + url + ": " + e.getMessage());
            }
        }
        
        return documents;
    }
}