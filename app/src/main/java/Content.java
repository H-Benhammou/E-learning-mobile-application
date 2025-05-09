package model;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Content {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private String type;     // "video", "pdf", "quiz"
    private String url;      // chemin local ou URL
    private String category; // exemple : Math, Physique...
    private String level;    // exemple : Débutant, Avancé...

    public Content(String title, String description, String type, String url, String category, String level) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.url = url;
        this.category = category;
        this.level = level;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
}
