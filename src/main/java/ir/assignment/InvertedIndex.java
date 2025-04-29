package ir.assignment;

import java.util.*;

/**
 * Maintains an inverted index for the document collection
 */
public class InvertedIndex {
    // Map from term to a list of postings (document ID and term frequency)
    private final Map<String, List<Posting>> index;
    // Document frequency map: term -> number of documents containing the term
    private final Map<String, Integer> documentFrequency;
    // Total number of documents in the collection
    private int numberOfDocuments;

    /**
     * Represents a posting in the inverted index
     */
    public static class Posting {
        private final int documentId;
        private final int termFrequency;

        public Posting(int documentId, int termFrequency) {
            this.documentId = documentId;
            this.termFrequency = termFrequency;
        }

        public int getDocumentId() {
            return documentId;
        }

        public int getTermFrequency() {
            return termFrequency;
        }
    }

    public InvertedIndex() {
        index = new HashMap<>();
        documentFrequency = new HashMap<>();
        numberOfDocuments = 0;
    }

    /**
     * Build the inverted index from a list of documents
     * @param documents list of documents to index
     */
    public void buildIndex(List<Document> documents) {
        // Set the total number of documents
        numberOfDocuments = documents.size();

        // Process each document
        for (Document doc : documents) {
            Map<String, Integer> termFreq = doc.getTermFrequency();
            
            // Add each term to the inverted index
            for (Map.Entry<String, Integer> entry : termFreq.entrySet()) {
                String term = entry.getKey();
                int freq = entry.getValue();
                
                // Update document frequency
                documentFrequency.put(term, documentFrequency.getOrDefault(term, 0) + 1);
                
                // Add posting to inverted index
                List<Posting> postings = index.getOrDefault(term, new ArrayList<>());
                postings.add(new Posting(doc.getId(), freq));
                index.put(term, postings);
            }
        }
        
        System.out.println("Inverted index built with " + index.size() + " terms.");
    }

    /**
     * Calculate the inverse document frequency (IDF) for a term
     * @param term the term to calculate IDF for
     * @return the IDF value
     */
    public double getInverseDocumentFrequency(String term) {
        int df = documentFrequency.getOrDefault(term, 0);
        if (df == 0) return 0;
        
        return Math.log10((double) numberOfDocuments / df);
    }

    /**
     * Get the weighted (TF-IDF) value for a term in a document
     * @param term the term
     * @param documentId the document ID
     * @return the TF-IDF weight
     */
    public double getTfIdf(String term, int documentId) {
        // Find the posting for this term and document
        List<Posting> postings = index.getOrDefault(term, Collections.emptyList());
        Optional<Posting> posting = postings.stream()
                .filter(p -> p.getDocumentId() == documentId)
                .findFirst();
        
        if (posting.isEmpty()) {
            return 0.0;
        }
        
        // Calculate TF weight: 1 + log10(tf)
        double tf = 1.0 + Math.log10(posting.get().getTermFrequency());
        
        // Calculate IDF
        double idf = getInverseDocumentFrequency(term);
        
        return tf * idf;
    }

    /**
     * Get all terms in the index
     * @return set of all terms
     */
    public Set<String> getAllTerms() {
        return index.keySet();
    }

    /**
     * Get the number of documents in the collection
     * @return number of documents
     */
    public int getNumberOfDocuments() {
        return numberOfDocuments;
    }
}