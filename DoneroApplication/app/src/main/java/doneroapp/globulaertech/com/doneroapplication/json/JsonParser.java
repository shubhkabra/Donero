package doneroapp.globulaertech.com.doneroapplication.json;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import doneroapp.globulaertech.com.doneroapplication.logs.L;
import doneroapp.globulaertech.com.doneroapplication.pojo.UserPojo;

/**
 * Created by Dell on 7/10/2017.
 */

public class JsonParser {
    private static Context mContext;

    public JsonParser() {

    }

    public JsonParser(Context context) {
        this.mContext = context;
    }

    public static ArrayList<UserPojo> parseUserData(JSONObject jsonObject) {
        ArrayList<UserPojo> userList = new ArrayList<>();
        UserPojo userPojo = new UserPojo();
        try {
            if (jsonObject != null && jsonObject.length() > 0) {
                userPojo.setId(jsonObject.getInt("id"));
                userPojo.setName(jsonObject.getString("name"));
                userPojo.setMobile(jsonObject.getString("mobile_no"));
                userPojo.setEmail(jsonObject.getString("email_id"));
                userPojo.setApi_key(jsonObject.getString("api_key"));
                userPojo.setCreate_at(jsonObject.getString("created_at"));
                userPojo.setUpdate_at(jsonObject.getString("updated_at"));
                userPojo.setStatus(1);
                userList.add(userPojo);
            }
        } catch (JSONException e) {
            L.m(e.getMessage());
        }
        return userList;
    }
}
