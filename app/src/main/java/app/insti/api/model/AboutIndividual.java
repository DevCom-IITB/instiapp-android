package app.insti.api.model;

public class AboutIndividual {
    private String id;
    private String name;
    private String imageName;
    private int type;

    public static final int TYPE_HUMAN = 0;
    public static final int TYPE_LINK = 1;

    public AboutIndividual(String id, String name, String imageName) {
        this.id = id;
        this.name = name;
        this.imageName = imageName;
        this.type = TYPE_HUMAN;
    }

    public AboutIndividual(String id, String name, String imageName, int type) {
        this.id = id;
        this.name = name;
        this.imageName = imageName;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
