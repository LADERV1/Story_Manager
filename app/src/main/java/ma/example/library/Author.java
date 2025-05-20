package ma.example.library;

public class Author {
    private int id;
    private String name;
    private String dob;
    private String bio;
    private String imagePath;

    public Author() {
        // Default constructor
    }

    public Author(int id, String name, String dob, String bio, String imagePath) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.bio = bio;
        this.imagePath = imagePath;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return name;
    }
}