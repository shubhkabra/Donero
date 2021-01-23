package doneroapp.globulaertech.com.doneroapplication.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.View;

/**
 * Created by Dell on 7/18/2017.
 */

public abstract class Permissions extends AppCompatActivity{
    private SparseIntArray merrorString;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        merrorString = new SparseIntArray();

    }

    public abstract void onPermissionGranted(int requestCode);

    public void requestAppPermissions(final String[] requestedPermissions, final int stringId, final int requestCode){
        merrorString.put(requestCode, stringId);
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        boolean showRequestPermissions = false;
        for (String permission: requestedPermissions){
            permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(this,permission);
            showRequestPermissions = showRequestPermissions || ActivityCompat.shouldShowRequestPermissionRationale(this,permission);
        }
        if (permissionCheck!= PackageManager.PERMISSION_GRANTED){
            if (showRequestPermissions){
                Snackbar.make(findViewById(android.R.id.content),stringId,Snackbar.LENGTH_INDEFINITE).setAction("GRANT", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(Permissions.this,requestedPermissions,requestCode);
                    }
                }).show();
            }else {
                ActivityCompat.requestPermissions(this,requestedPermissions,requestCode);
            }
        }else {
            onPermissionGranted(requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int permission : grantResults){
            permissionCheck = permissionCheck + permission;
        }
        if ((grantResults.length > 0) && PackageManager.PERMISSION_GRANTED == permissionCheck){
            onPermissionGranted(requestCode);
        }
        else {
            Snackbar.make(findViewById(android.R.id.content), merrorString.get(requestCode),Snackbar.LENGTH_INDEFINITE).setAction("Enable", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.setData(Uri.parse("package:" + getPackageName()));
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(i);
                }
            }).dismiss();
        }
    }
}
