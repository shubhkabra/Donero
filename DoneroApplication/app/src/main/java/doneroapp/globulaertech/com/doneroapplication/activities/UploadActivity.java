package doneroapp.globulaertech.com.doneroapplication.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


import doneroapp.globulaertech.com.doneroapplication.endpoints.Endpoints;
import doneroapp.globulaertech.com.doneroapplication.extras.Constant;
import doneroapp.globulaertech.com.doneroapplication.logs.L;
import doneroapp.globulaertech.com.doneroapplication.utils.PreferenceString;
import doneroapp.globulaertech.com.doneroapplication.utils.PreferenceUtils;
import doneroapp.globulaertech.com.doneroapplication.R;
import doneroapp.globulaertech.com.doneroapplication.volley.MyApplication;

import static doneroapp.globulaertech.com.doneroapplication.R.id.spinner;

public class UploadActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, AdapterView.OnItemSelectedListener {
    private ImageView photo_of_needy;
    private Bitmap bitmap;
    private Button take_pic, submit_pic;
    private TextView imageerror;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;
    private long FASTEST_INTERVAL = 2000;
    private EditText lattitude_location_of_needy;
    private EditText longitude_location_of_needy;
    private static final String TAG = "UploadActivity";
    private static final String JSON_UPLOAD_TAG = "JSON_OBJECT_REQUEST_SEND";
    private SharedPreferences sharedPreferences;
    private EditText desc_of_neeedy;
    private PreferenceUtils mPreferenceUtils;
    private Context mContext;
    private Toolbar mToolbar;
    private String API_KEY;
    private Spinner mSpinner;
    String User_id;
    String spinner_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        mContext = UploadActivity.this;
        API_KEY = mPreferenceUtils.readString(mContext, PreferenceString.USER_API, "");
        photo_of_needy = (ImageView) findViewById(R.id.pic_of_needy);
        lattitude_location_of_needy = (EditText) findViewById(R.id.lattitude_uplod);
        longitude_location_of_needy = (EditText) findViewById(R.id.longitude_uplod);
        desc_of_neeedy = (EditText) findViewById(R.id.detail_of_needy);
        take_pic = (Button) findViewById(R.id.btn_takephoto);
        submit_pic = (Button) findViewById(R.id.submitphoto);
        imageerror = (TextView) findViewById(R.id.errorimage);
        mSpinner = (Spinner)findViewById(spinner);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        User_id = Integer.toString(mPreferenceUtils.readInteger(mContext, PreferenceString.USER_ID, 1));
        List<String> categories = new ArrayList<String>();
        categories.add("45 miutes");
        categories.add("24 hours");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mSpinner.setAdapter(dataAdapter);
        mSpinner.setOnItemSelectedListener(this);
        checkLocation(); //check whether location service is enable or not in your  phone
        takephoto();
        setUpToolbar();
        submit_pic_of_needyPerson();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }
        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {

            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onLocationChanged(Location location) {

        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        lattitude_location_of_needy.setText(String.valueOf(location.getLatitude()));
        longitude_location_of_needy.setText(String.valueOf(location.getLongitude()));
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
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
        dialog.show();
    }

    private boolean isLocationEnabled() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void takephoto() {
        take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
    }

    private void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 6;
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bao);
            photo_of_needy.setImageBitmap(bitmap);
            photo_of_needy.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void submitNeedyPersonDetails() {
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.needyPersonRegistration(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Disimissing the progress dialog
                        try {

                            JSONObject mJsonObject = new JSONObject(response);
                            if (!mJsonObject.getBoolean(getResources().getString(R.string.response_error))) {
                                L.T(mContext, mJsonObject.getString(getResources().getString(R.string.response_message)));
                                openActivity();
                                loading.dismiss();
                            } else {
                                L.T(mContext, mJsonObject.getString(getResources().getString(R.string.response_message)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        L.T(mContext, volleyError.toString());
                        handleVolleyError(volleyError);
                        loading.dismiss();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("Authorization", API_KEY);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final String details = desc_of_neeedy.getText().toString().trim();
                //Converting Bitmap to String
                String image = getStringImage(bitmap);
                final String lattti = lattitude_location_of_needy.getText().toString().trim();
                final String longi = longitude_location_of_needy.getText().toString().trim();
                final String timePeriod = spinner_item;
                final String usernaame = User_id;
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put(Constant.KEY_IMAGE, image);
                params.put(Constant.KEY_LATTITUDE_ADDRESS, lattti);
                params.put(Constant.KEY_LONGITUDE_ADDRESS, longi);
                params.put(Constant.KEY_DESC, details);
                params.put(Constant.KEY_TIME_PERIOD,timePeriod);
                params.put(Constant.KEY_USER_ID, usernaame);
                //returning parameters
                return params;
            }
        };
        try {
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyApplication.getInstance().addToRequestQueue(stringRequest, JSON_UPLOAD_TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Creating a Request Queue
       /* RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestQueue.add(stringRequest);*/
    }

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

    public boolean validate() {
        if (desc_of_neeedy.getText().toString().equals("")) {
            desc_of_neeedy.setError(getResources().getString(R.string.error_desc));
            return false;
        }
        if (photo_of_needy.getDrawable() == null) {
            imageerror.setError(getResources().getString(R.string.error_pic));
            return false;
        }
        return true;
    }

    public void openActivity() {
        startActivity(new Intent(mContext, MapsActivity.class));
    }

    public void submit_pic_of_needyPerson() {
        submit_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    submitNeedyPersonDetails();
                }
            }
        });
    }

    public void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        setTitle("Upload Needy's Location");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(UploadActivity.this, MapsActivity.class));
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       /* MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_screen, menu);*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(UploadActivity.this, MapsActivity.class));
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinner_item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
/*
       Toast.makeText(parent.getContext(), "Selected: " + spinner_item, Toast.LENGTH_LONG).show();
*/

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
