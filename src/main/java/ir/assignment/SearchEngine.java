package ir.assignment;

import java.util.*;

/**
 * Search engine that processes queries and ranks documents using cosine similarity
 */
public class SearchEngine {
    private final InvertedIndex invertedIndex;
    private final List<Document> documents;
    private final Tokenizer tokenizer;

    public SearchEngine(InvertedIndex invertedIndex, List<Document> documents) {
        this.invertedIndex = invertedIndex;
        this.documents = documents;
        this.tokenizer = new Tokenizer();
    }

    /**
     * Process a query and rank documents by cosine similarity
     * @param queryText the query text
     * @return list of ranked documents with their scores
     */
    public List<RankedDocument> search(String queryText) {
        // Tokenize query
        List<String> queryTokens = tokenizer.tokenize(queryText);
        
        // Calculate query term frequencies
        Map<String, Integer> queryTermFreq = new HashMap<>();
        for (String token : queryTokens) {
            queryTermFreq.put(token, queryTermFreq.getOrDefault(token, 0) + 1);
        }
        
        // Calculate TF-IDF weights for query terms
        Map<String, Double> queryWeights = new HashMap<>();
        double queryNorm = 0.0;
        
        for (Map.Entry<String, Integer> entry : queryTermFreq.entrySet()) {
            String term = entry.getKey();
            int tf = entry.getValue();
            
            // TF weight: 1 + log10(tf)
            double tfWeight = 1.0 + Math.log10(tf);
            
            // IDF from document collection
            double idf = invertedIndex.getInverseDocumentFrequency(term);
            
            // TF-IDF weight
            double weight = tfWeight * idf;
            queryWeights.put(term, weight);
            
            // Accumulate for normalization
            queryNorm += weight * weight;
        }
        
        // Normalize query vector
        queryNorm = Math.sqrt(queryNorm);
        
        // Calculate cosine similarity for each document
        List<RankedDocument> rankedDocuments = new ArrayList<>();
        
        for (Document doc : documents) {
            double similarity = calculateCosineSimilarity(doc, queryWeights, queryNorm);
            rankedDocuments.add(new RankedDocument(doc, similarity));
        }
        
        // Sort by similarity score (descending)
        rankedDocuments.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        
        return rankedDocuments;
    }

    /**
     * Calculate cosine similarity between a document and query
     * @param document the document
     * @param queryWeights TF-IDF weights for query terms
     * @param queryNorm normalization factor for query vector
     * @return cosine similarity score
     */
    private double calculateCosineSimilarity(Document document, Map<String, Double> queryWeights, double queryNorm) {
        if (queryNorm == 0) return 0;
        
        double dotProduct = 0.0;
        double docNorm = 0.0;
        
        // Calculate dot product and document norm
        Set<String> allTerms = invertedIndex.getAllTerms();
        
        for (String term : allTerms) {
            double docWeight = invertedIndex.getTfIdf(term, document.getId());
            
            // Add to document norm calculation
            docNorm += docWeight * docWeight;
            
            // If term is in query, add to dot product
            if (queryWeights.containsKey(term)) {
                dotProduct += docWeight * queryWeights.get(term);
            }
        }
        
        // Normalize
        docNorm = Math.sqrt(docNorm);
        
        // Avoid division by zero
        if (docNorm == 0 || queryNorm == 0) return 0;
        
        return dotProduct / (docNorm * queryNorm);
    }

    /**
     * Represents a document with its similarity score for ranking
     */
    public static class RankedDocument {
        private final Document document;
        private final double score;

        public RankedDocument(Document document, double score) {
            this.document = document;
            this.score = score;
        }

        public Document getDocument() {
            return document;
        }

        public double getScore() {
            return score;
        }
    }
}