package ir.assignment;

import java.io.StringReader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * Tokenizes and processes text from documents
 */
public class Tokenizer {
    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\W+");

    /**
     * Tokenize the content of a document and apply stemming
     * @param content the text content to tokenize
     * @return a list of normalized and stemmed tokens
     */
    public List<String> tokenize(String content) {
        if (content == null || content.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();
        
        try {
            // First, split content into tokens using basic pattern and convert to lowercase
            List<String> basicTokens = Arrays.stream(SPLIT_PATTERN.split(content.toLowerCase()))
                    .filter(token -> !token.isEmpty())
                    .collect(Collectors.toList());
            
            // Join tokens with spaces to process through Lucene
            String tokensText = String.join(" ", basicTokens);
            
            // Create a Lucene whitespace tokenizer
            WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();
            tokenizer.setReader(new StringReader(tokensText));
            
            // Apply lowercase filter
            TokenStream tokenStream = new LowerCaseFilter(tokenizer);
            
            // Apply Porter stemming filter
            tokenStream = new PorterStemFilter(tokenStream);
            
            // Get stemmed tokens
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            
            while (tokenStream.incrementToken()) {
                String term = charTermAttribute.toString();
                if (!term.isEmpty()) {
                    result.add(term);
                }
            }
            
            tokenStream.end();
            tokenStream.close();
            
        } catch (Exception e) {
            // Fall back to basic tokenization without stemming if an error occurs
            System.err.println("Error during stemming: " + e.getMessage());
            return Arrays.stream(SPLIT_PATTERN.split(content.toLowerCase()))
                    .filter(token -> !token.isEmpty())
                    .collect(Collectors.toList());
        }
        
        return result;
    }
    
    /**
     * Process a document to compute term frequencies
     * @param document the document to process
     */
    public void processDocument(Document document) {
        String content = document.getContent();
        List<String> tokens = tokenize(content);
        
        // Calculate term frequencies
        Map<String, Integer> termFreq = new HashMap<>();
        for (String token : tokens) {
            termFreq.put(token, termFreq.getOrDefault(token, 0) + 1);
        }
        
        document.setTermFrequency(termFreq);
    }
}