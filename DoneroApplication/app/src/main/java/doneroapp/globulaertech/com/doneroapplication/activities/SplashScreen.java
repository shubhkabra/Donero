package doneroapp.globulaertech.com.doneroapplication.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import doneroapp.globulaertech.com.doneroapplication.R;
import doneroapp.globulaertech.com.doneroapplication.utils.PreferenceString;
import doneroapp.globulaertech.com.doneroapplication.utils.PreferenceUtils;

public class  SplashScreen extends Permissions {
    private static final int REQUEST_PERMISSION = 10;
    private PreferenceUtils mPreferenceUtils;
    private Context mContext;
    private boolean islogged;
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    /** Called when the activity is first created. */
    Thread splashTread;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mContext = SplashScreen.this;
        islogged = mPreferenceUtils.readBoolean(mContext, PreferenceString.LOGGED_IN, false);
        requestAppPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET,Manifest.permission.CAMERA,Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS,Manifest.permission.ACCESS_WIFI_STATE},R.string.msg_enble,REQUEST_PERMISSION);
        StartAnimations();
    }

    @Override
    public void onPermissionGranted(int requestCode) {
        Toast.makeText(mContext, "permission granted", Toast.LENGTH_SHORT).show();
    }

    private void StartAnimations() {
/*        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);
        anim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        anim.reset();*/
        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.bounce);
        TextView tv = (TextView) findViewById(R.id.name);
        tv.clearAnimation();
        tv.startAnimation(animation1);
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.translate);
        animation2.reset();
        ImageView imageView = (ImageView)findViewById(R.id.icond);
        imageView.startAnimation(animation2);
        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 4500) {
                        sleep(100);
                        waited += 100;
                    }
                    if (islogged) {
                        startActivity(new Intent(mContext, MapsActivity.class));
                    } else {
                        startActivity(new Intent(mContext, SignUpScreen.class).setFlags(
                                Intent.FLAG_ACTIVITY_NO_ANIMATION
                        ));
                    }
                    SplashScreen.this.finish();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashScreen.this.finish();
                }

            }
        };
        splashTread.start();

    }
}
