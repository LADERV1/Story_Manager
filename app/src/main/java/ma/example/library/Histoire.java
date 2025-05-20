package ma.example.library;

public class Histoire {
    private int id;
    private String titre;
    private String contenu;
    private String auteur;
    private String pays;
    private String date;
    private String imagePath;
    private String categorie; // Added category field

    public Histoire() {
        // Default constructor
    }

    public Histoire(int id, String titre, String contenu, String auteur, String pays, String date, String imagePath) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.auteur = auteur;
        this.pays = pays;
        this.date = date;
        this.imagePath = imagePath;
    }

    // Constructor with category
    public Histoire(int id, String titre, String contenu, String auteur, String pays, String date, String imagePath, String categorie) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.auteur = auteur;
        this.pays = pays;
        this.date = date;
        this.imagePath = imagePath;
        this.categorie = categorie;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    // Added getter and setter for category
    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
}
