package doneroapp.globulaertech.com.doneroapplication.endpoints;


import static doneroapp.globulaertech.com.doneroapplication.endpoints.UrlEndpoints.DOMAIN;
import static doneroapp.globulaertech.com.doneroapplication.endpoints.UrlEndpoints.PATH;
import static doneroapp.globulaertech.com.doneroapplication.endpoints.UrlEndpoints.URL_FORUM_POST;
import static doneroapp.globulaertech.com.doneroapplication.endpoints.UrlEndpoints.URL_GET_CURRENT_LOC;
import static doneroapp.globulaertech.com.doneroapplication.endpoints.UrlEndpoints.URL_GET_FORUM_DATA;
import static doneroapp.globulaertech.com.doneroapplication.endpoints.UrlEndpoints.URL_REDEEM_POINTS;
import static doneroapp.globulaertech.com.doneroapplication.endpoints.UrlEndpoints.URL_REGISTER;
import static doneroapp.globulaertech.com.doneroapplication.endpoints.UrlEndpoints.URL_UPLOAD;
import static doneroapp.globulaertech.com.doneroapplication.endpoints.UrlEndpoints.URL_VERIFY;
import static doneroapp.globulaertech.com.doneroapplication.endpoints.UrlEndpoints.URL_VERIFY_FORUM;

/**
 * Created by Dell on 4/6/2017.
 */
public class Endpoints {
    public static final String userRegisteration() {
        return DOMAIN
                +PATH
                + URL_REGISTER;
    }
    public static final String userVerification() {
        return DOMAIN
                + PATH
                + URL_VERIFY;
    }

    public static final String needyPersonRegistration(){
        return DOMAIN
                + PATH
                + URL_UPLOAD;
    }

    public static final String needyGotHelp(){
        return DOMAIN
                + PATH
                + URL_FORUM_POST;
    }

    public static final String getCurrentLocations(){
        return DOMAIN
                + PATH
                + URL_GET_CURRENT_LOC;
    }

    public static final String getForumData(){
        return DOMAIN
                + PATH
                + URL_GET_FORUM_DATA;
    }
    public static final String verifyForumData(){
        return DOMAIN
                + PATH
                + URL_VERIFY_FORUM;
    }
    public static final String redeemPoints(){
        return DOMAIN
                + PATH
                + URL_REDEEM_POINTS;
    }
}
