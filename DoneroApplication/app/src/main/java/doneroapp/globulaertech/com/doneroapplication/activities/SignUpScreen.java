package doneroapp.globulaertech.com.doneroapplication.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import doneroapp.globulaertech.com.doneroapplication.extras.Constant;
import doneroapp.globulaertech.com.doneroapplication.extras.Functions;
import doneroapp.globulaertech.com.doneroapplication.logs.L;
import doneroapp.globulaertech.com.doneroapplication.utils.PreferenceUtils;
import doneroapp.globulaertech.com.doneroapplication.R;
import doneroapp.globulaertech.com.doneroapplication.extras.Config;
import doneroapp.globulaertech.com.doneroapplication.endpoints.Endpoints;
import doneroapp.globulaertech.com.doneroapplication.volley.MyApplication;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SignUpScreen extends AppCompatActivity implements View.OnClickListener {
    private EditText enter_doneroName;
    private EditText enter_doneroNo;
    private EditText enter_Email;
    private Button user_SignUp;
    private boolean mIsLog = false;
    private Context mContext;
    private PreferenceUtils mPreferenceUtils;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private ProgressDialog mProgressDialog;
    private static final String JSON_TAG = "JSON_OBJECT_REQUEST_REGISTRATION";
    private static final int PERMISSION_REQUEST_CODE = 200;
    private String otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);
        enter_doneroName = (EditText) findViewById(R.id.doneroname);
        enter_doneroNo = (EditText) findViewById(R.id.doneroPhone_no);
        enter_Email = (EditText) findViewById(R.id.donero_email);
        user_SignUp = (Button) findViewById(R.id.newUser_Register);
        otp = Functions.code(6);
        L.m(otp);
        mContext = SignUpScreen.this;
        user_SignUp.setOnClickListener(this);
        checkLocation();
    }

    private void userRegistration(final String name, final String mobile, final String email, final String code) {
      //  progressDialog();
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, Endpoints.userRegisteration(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                         loading.dismiss();
                            JSONObject mJsonObject = new JSONObject(response);
                            if (!mJsonObject.getBoolean(getResources().getString(R.string.response_error))) {
                                L.T(mContext, mJsonObject.getString(getResources().getString(R.string.response_message)));
                                startActivity(new Intent(mContext, OtpActivity.class)
                                        .putExtra("name", name)
                                        .putExtra("mobile", mobile)
                                        .putExtra("email", email)
                                );
                                finish();
                            } else {
                                L.T(mContext, mJsonObject.getString(getResources().getString(R.string.response_message)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleVolleyError(error);
                loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put(Constant.KEY_NAME, name);
                params.put(Constant.KEY_MOBILE, mobile);
                params.put(Constant.KEY_EMAIL, email);
                params.put(Constant.KEY_CODE, code);
                return params;
            }
        };
        try {
            mStringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyApplication.getInstance().addToRequestQueue(mStringRequest, JSON_TAG);
        }catch (Exception e){
            e.printStackTrace();
        }
        //  handleDialog();
    }

 /*   private final void progressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.dialog_message));
        mProgressDialog.show();
    }

    private final void handleDialog() {
        mProgressDialog.dismiss();
    }*/

    private void handleVolleyError(VolleyError error) {
        //if any error occurs in the network operations, show the TextView that contains the error message
        String message = null;
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            message = getResources().getString(R.string.error_timeout);
        } else if (error instanceof AuthFailureError) {
            message = getResources().getString(R.string.error_auth_failure);
            //TODO
        } else if (error instanceof ServerError) {
            message = getResources().getString(R.string.error_auth_failure);
            //TODO
        } else if (error instanceof NetworkError) {
            message = getResources().getString(R.string.error_network);
            //TODO
        } else if (error instanceof ParseError) {
            message = getResources().getString(R.string.error_parser);
            //TODO
        }
        L.T(mContext, message);
    }

    public boolean validations() {
        if (enter_doneroName.getText().toString().equals("")) {
            enter_doneroName.setError(Config.SET_ERROR_NAME);
            return true;
        }
        if (enter_doneroNo.getText().toString().length() <= 9 || enter_doneroNo.getText().toString().length() > 10) {
            enter_doneroNo.setError(Config.SET_ERROR_MOBILE);
            return true;
        }
        return false;
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.enable_Location))
                .setMessage(getResources().getString(R.string.message_display))
                .setPositiveButton(getResources().getString(R.string.location_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancle_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                        moveTaskToBack(true);
                    }
                });
        dialog.setCancelable(false);
        dialog.show();
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //what should happen on btton click
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newUser_Register:
                if (!validations()) {
                    userRegistration(enter_doneroName.getText().toString().trim(),
                            enter_doneroNo.getText().toString().trim(),
                            enter_Email.getText().toString().trim(),
                            otp
                    );
                } else {
                    L.T(mContext, getResources().getString(R.string.error_validation));
                }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
