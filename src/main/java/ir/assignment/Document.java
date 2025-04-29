package ir.assignment;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a crawled document with its content and metadata
 */
public class Document {
    private String url;
    private String title;
    private String content;
    private int id;
    private Map<String, Integer> termFrequency;

    public Document(int id, String url, String title, String content) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.content = content;
        this.termFrequency = new HashMap<>();
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

    public Map<String, Integer> getTermFrequency() {
        return termFrequency;
    }

    public void setTermFrequency(Map<String, Integer> termFrequency) {
        this.termFrequency = termFrequency;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}