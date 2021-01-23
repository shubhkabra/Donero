package doneroapp.globulaertech.com.doneroapplication.extras;

import java.util.Random;

/**
 * Created by Dell on 7/10/2017.
 */

public class Functions {
    public static String code(int length){
        String result = "";
        String alphabet = "0123456789"; //9
        int n = alphabet.length(); //10

        Random r = new Random(); //11

        for (int i=0; i<length; i++) //12
            result = result + alphabet.charAt(r.nextInt(n)); //13

        return result;
    }
}
