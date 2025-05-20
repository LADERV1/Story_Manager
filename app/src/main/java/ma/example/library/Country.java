package ma.example.library;

public class Country {
    private int id;
    private String name;
    private String flagPath;

    public Country() {
        // Default constructor
    }

    public Country(int id, String name, String flagPath) {
        this.id = id;
        this.name = name;
        this.flagPath = flagPath;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlagPath() {
        return flagPath;
    }

    public void setFlagPath(String flagPath) {
        this.flagPath = flagPath;
    }

    @Override
    public String toString() {
        return name;
    }
}