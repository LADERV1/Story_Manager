package ma.example.library;

public class Story {
    private int id;
    private String title;
    private String content;
    private String author;
    private String country;
    private String date;

    // Default constructor
    public Story() {
    }

    // Constructor with all fields
    public Story(int id, String title, String content, String author, String country, String date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.country = country;
        this.date = date;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public String getCountry() {
        return country;
    }

    public String getDate() {
        return date;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDate(String date) {
        this.date = date;
    }
}