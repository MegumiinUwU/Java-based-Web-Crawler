package ir.assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class for web search engine
 */
public class WikiSearchApplication {
    private static final String DEFAULT_SEED_URL_1 = "https://en.wikipedia.org/wiki/List_of_pharaohs";
    private static final String DEFAULT_SEED_URL_2 = "https://en.wikipedia.org/wiki/Pharaoh";
    private static final int TOP_RESULTS = 10;

    public static void main(String[] args) {
        System.out.println("Web Search Engine Starting...");
        
        Scanner scanner = new Scanner(System.in);
        List<String> seedUrls = new ArrayList<>();
        
        // Ask user if they want to use custom URLs
        System.out.println("\nWould you like to use custom seed URLs? (y/n)");
        String response = scanner.nextLine().trim().toLowerCase();
        
        if (response.equals("y") || response.equals("yes")) {
            System.out.println("Enter seed URLs (one per line, enter a blank line when done):");
            String input;
            while (true) {
                input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    break;
                }
                seedUrls.add(input);
            }
            
            if (seedUrls.isEmpty()) {
                System.out.println("No URLs provided, using defaults.");
                seedUrls.add(DEFAULT_SEED_URL_1);
                seedUrls.add(DEFAULT_SEED_URL_2);
            }
        } else {
            // Use defaults
            seedUrls.add(DEFAULT_SEED_URL_1);
            seedUrls.add(DEFAULT_SEED_URL_2);
        }
        
        // Ask if crawling should be restricted to seed domains
        System.out.println("Restrict crawling to the domains of the seed URLs? (y/n)");
        response = scanner.nextLine().trim().toLowerCase();
        boolean restrictToDomains = response.equals("y") || response.equals("yes");
        
        // Initialize components
        System.out.println("\n1. Starting web crawler...");
        WebCrawler webCrawler = new WebCrawler(seedUrls);
        webCrawler.setRestrictToDomains(restrictToDomains);
        List<Document> documents = webCrawler.crawl();
        
        if (documents.isEmpty()) {
            System.err.println("Error: No documents were crawled. Exiting.");
            return;
        }
        
        System.out.println("\n2. Processing documents and building inverted index...");
        Tokenizer tokenizer = new Tokenizer();
        for (Document doc : documents) {
            tokenizer.processDocument(doc);
        }
        
        InvertedIndex invertedIndex = new InvertedIndex();
        invertedIndex.buildIndex(documents);
        
        System.out.println("\n3. Initializing search engine...");
        SearchEngine searchEngine = new SearchEngine(invertedIndex, documents);
        
        // User interaction loop for searching
        boolean running = true;
        
        while (running) {
            System.out.println("\n==========================================================");
            System.out.println("Enter a search query (or 'exit' to quit):");
            String query = scanner.nextLine().trim();
            
            if (query.equalsIgnoreCase("exit")) {
                running = false;
                continue;
            }
            
            if (query.isEmpty()) {
                System.out.println("Please enter a valid query.");
                continue;
            }
            
            System.out.println("\nSearching for: " + query);
            
            // Perform search
            List<SearchEngine.RankedDocument> results = searchEngine.search(query);
            
            System.out.println("\nSearch Results:");
            if (results.isEmpty()) {
                System.out.println("No matching documents found.");
            } else {
                int count = 0;
                for (SearchEngine.RankedDocument result : results) {
                    Document doc = result.getDocument();
                    System.out.printf("%d. [%.4f] %s%n", ++count, result.getScore(), doc.getTitle());
                    System.out.println("   URL: " + doc.getUrl());
                    
                    if (count >= TOP_RESULTS) break;
                }
            }
        }
        
        System.out.println("\nExiting Web Search Engine.");
        scanner.close();
    }
}