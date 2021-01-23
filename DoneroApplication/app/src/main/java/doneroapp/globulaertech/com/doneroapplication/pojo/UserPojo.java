package doneroapp.globulaertech.com.doneroapplication.pojo;

/**
 * Created by Dell on 3/30/2017.
 */
public class UserPojo {
    private int id;
    private String name;
    private String mobile;
    private String email;
    private String api_key;
    private String create_at;
    private String update_at;
    private int status;

    public UserPojo() {

    }
    public UserPojo(int id, String name, String mobile, String email, String api_key, String create_at, String update_at, int status) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.api_key = api_key;
        this.create_at = create_at;
        this.update_at = update_at;
        this.status = status;
    }

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(String update_at) {
        this.update_at = update_at;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
