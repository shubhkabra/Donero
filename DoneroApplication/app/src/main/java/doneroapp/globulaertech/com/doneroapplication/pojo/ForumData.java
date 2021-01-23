package doneroapp.globulaertech.com.doneroapplication.pojo;

/**
 * Created by Dell on 7/15/2017.
 */

public class ForumData {

    private int id;
    private String img_url;
    private String description;
    private String user_id;

    public ForumData() {

    }

    public ForumData(int id, String imageUrl, String description, String user_id) {
        this.id = id;
        this.img_url = imageUrl;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return img_url;
    }

    public void setImageUrl(String imageUrl) {
        this.img_url = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


}
