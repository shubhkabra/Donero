package doneroapp.globulaertech.com.doneroapplication.pojo;

/**
 * Created by Dell on 7/15/2017.
 */

public class BeggarPojo {
    private int id;
    private int userId;
    private int pointId;
    private String imageUrl;
    private String description;
    private double latitude;
    private double longitude;
    private String createAt;
    private int status;

    public BeggarPojo() {

    }

    public BeggarPojo(int id, int userId, int pointId, String imageUrl, String description, double latitude, double longitude, String createAt, int status) {
        this.id = id;
        this.userId = userId;
        this.pointId = pointId;
        this.imageUrl = imageUrl;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createAt = createAt;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
