package doneroapp.globulaertech.com.doneroapplication.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.ion.Ion;
import com.nex3z.notificationbadge.NotificationBadge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import doneroapp.globulaertech.com.doneroapplication.R;
import doneroapp.globulaertech.com.doneroapplication.endpoints.Endpoints;
import doneroapp.globulaertech.com.doneroapplication.extras.Constant;
import doneroapp.globulaertech.com.doneroapplication.logs.L;
import doneroapp.globulaertech.com.doneroapplication.pojo.BeggarPojo;
import doneroapp.globulaertech.com.doneroapplication.utils.PreferenceString;
import doneroapp.globulaertech.com.doneroapplication.utils.PreferenceUtils;
import doneroapp.globulaertech.com.doneroapplication.volley.MyApplication;

import static android.R.attr.enabled;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private LinearLayout btnUpload, btnForum, btnOffers, btnHelp;
    private GoogleMap mMap;
    private MarkerOptions mMarkerOptions;
    private ArrayList<LatLng> mLatLngs;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private Context mContext;
    private Object activity;
    private long UPDATE_INTERVAL = 2 * 1000;
    private static final String TAG = "MapsActivity";
    private static final String JSON_TAG = "JSON_OBJECT_REQUEST_RESEND";
    private long FASTEST_INTERVAL = 2000;
    private Toolbar mToolbar;
    private PreferenceUtils preferenceUtils;
    private NotificationBadge notificationBadge;
    private int points;
    double latti = 0, longi = 0;
    private String User_id;
    private PreferenceUtils mPreferenceUtils;
    private String Begger_id;
    private String API_KEY;
    private ImageView point_img;
    private ArrayList<BeggarPojo> mBeggarPojoArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setTitle("Donero Home");
        btnUpload = (LinearLayout) findViewById(R.id.btn_upload);
        btnForum = (LinearLayout) findViewById(R.id.btn_forum);
        btnOffers = (LinearLayout) findViewById(R.id.btn_offers);
        btnHelp = (LinearLayout) findViewById(R.id.btn_help);
        notificationBadge = (NotificationBadge) findViewById(R.id.badge);
        point_img = (ImageView) findViewById(R.id.icon_pnts);
        notificationBadge.setMaxTextLength(5);
        notificationBadge.setAnimationEnabled(true);
        mContext = MapsActivity.this;
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        btnUpload.setOnClickListener(this);
        btnUpload.setBackgroundColor(Color.TRANSPARENT);
        btnForum.setOnClickListener(this);
        btnForum.setBackgroundColor(Color.TRANSPARENT);
        btnOffers.setOnClickListener(this);
        btnOffers.setBackgroundColor(Color.TRANSPARENT);
        btnHelp.setOnClickListener(this);
        btnHelp.setBackgroundColor(Color.TRANSPARENT);
        point_img.setOnClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        checkLocation();
        L.m(preferenceUtils.readString(mContext, PreferenceString.USER_API, ""));
        User_id = Integer.toString(mPreferenceUtils.readInteger(mContext, PreferenceString.USER_ID, 1));
        API_KEY = mPreferenceUtils.readString(mContext, PreferenceString.USER_API, "");
        mMarkerOptions = new MarkerOptions();
        mLatLngs = new ArrayList<>();
        mBeggarPojoArrayList = new ArrayList<>();
        getCollection_record();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Bitmap mBitmap = null;
        for (int i = 0; i < mBeggarPojoArrayList.size(); i++) {
            try {
                mBitmap = Ion.with(mContext).load(mBeggarPojoArrayList.get(i).getImageUrl()).asBitmap().get();
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mBitmap.setHeight(50);
                    mBitmap.setWidth(50);
                }*/
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            mMarkerOptions.position(new LatLng(mBeggarPojoArrayList.get(i).getLatitude(), mBeggarPojoArrayList.get(i).getLongitude()));
            mMarkerOptions.title(Integer.toString(mBeggarPojoArrayList.get(i).getId()));
            mMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(mBitmap));
            mMarkerOptions.snippet(mBeggarPojoArrayList.get(i).getDescription());
            mMap.addMarker(mMarkerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mBeggarPojoArrayList.get(i).getLatitude(), mBeggarPojoArrayList.get(i).getLongitude())));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
            mMarkerOptions.alpha(2.0f);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    mPreferenceUtils.writeInteger(mContext, PreferenceString.SOURCE_ID, Integer.parseInt(marker.getTitle()));
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.sherou));
                    return false;
                }
            });
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                mMap.setMyLocationEnabled(true);
                LatLng latLng = new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
                mMarkerOptions.position(new LatLng(mLocation.getLatitude(),mLocation.getLongitude()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16.0f));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16.0f));
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(MapsActivity.this, SignUpScreen.class));
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload:
                startActivity(new Intent(this, UploadActivity.class));

                break;
            case R.id.btn_forum:
                startActivity(new Intent(this, ForumActivity.class));

                break;
            case R.id.btn_offers:
                startActivity(new Intent(this, OffersActivity.class));

                break;
            case R.id.btn_help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.icon_pnts:
                startActivity(new Intent(this, RedeemsActivity.class));
                finish();
            default:

        }
    }

    public void getCollection_record() {
        final ProgressDialog loading = ProgressDialog.show(this, "Downloading...", "Please wait...", false, false);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Endpoints.getCurrentLocations() + User_id,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        try {
                            if (!response.getBoolean(getResources().getString(R.string.response_error))) {
                                points = response.getInt(getResources().getString(R.string.response_point));
                                parseJson(response.getJSONArray(getResources().getString(R.string.response_beggar)));
                                notificationBadge.setNumber(points);

                            } else {
                                L.T(mContext, response.getString(getResources().getString(R.string.response_message)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            L.T(mContext, e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Disimissing the progress dialog
                        Toast.makeText(MapsActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        loading.dismiss();
                        //     Toast.makeText(HomeActivity.this, error.getMessage().t(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("Authorization", API_KEY);
                return params;
            }
        };
        try {
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyApplication.getInstance().addToRequestQueue(jsonObjReq, JSON_TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseJson(JSONArray array) {
        double lat, lang;
        int bgr_id;
        for (int i = 0; i < array.length(); i++) {
            try {
                BeggarPojo mBeggarPojo = new BeggarPojo();
                JSONObject jsonObject = array.getJSONObject(i);
                mBeggarPojo.setId(jsonObject.getInt(Constant.KEY_BEGGER_ID));
                mBeggarPojo.setUserId(jsonObject.getInt(Constant.KEY_USER_ID));
                mBeggarPojo.setPointId(jsonObject.getInt(Constant.KEY_POINTS));
                mBeggarPojo.setImageUrl(jsonObject.getString(Constant.KEY_IMAGE_URL));
                mBeggarPojo.setDescription(jsonObject.getString(Constant.KEY_DESC));
                mBeggarPojo.setLatitude(jsonObject.getDouble(Constant.KEY_LATTITUDE));
                mBeggarPojo.setLongitude(jsonObject.getDouble(Constant.KEY_LONGITUD));
                mBeggarPojoArrayList.add(mBeggarPojo);
              /*
                lat = jsonObject.getDouble(Constant.KEY_LATTITUDE);
                lang = jsonObject.getDouble(Constant.KEY_LONGITUD);
                bgr_id = jsonObject.getInt(Constant.KEY_BEGGER_ID);
                mLatLngs.add(new LatLng(lat, lang));*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mapCall();
    }

    @Override
    public void onConnected(Bundle bundle) {
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

        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {

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
       /* LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.0f));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.0f));
        latti = location.getLatitude();
        longi = location.getLongitude();*/
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

    public void mapCall() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
