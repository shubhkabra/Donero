package doneroapp.globulaertech.com.doneroapplication.logs;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Athar on 28-03-2017.
 */

public class L {
    public static void m(String message) {
        Log.d("DONERO", "" + message);
    }

    public static void t(Context context, String message) {
        Toast.makeText(context, message + "", Toast.LENGTH_SHORT).show();
    }
    public static void T(Context context, String message) {
        Toast.makeText(context, message + "", Toast.LENGTH_LONG).show();
    }
}
