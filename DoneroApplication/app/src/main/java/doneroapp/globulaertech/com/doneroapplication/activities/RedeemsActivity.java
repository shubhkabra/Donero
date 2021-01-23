package doneroapp.globulaertech.com.doneroapplication.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

import doneroapp.globulaertech.com.doneroapplication.R;
import doneroapp.globulaertech.com.doneroapplication.endpoints.Endpoints;
import doneroapp.globulaertech.com.doneroapplication.extras.Constant;
import doneroapp.globulaertech.com.doneroapplication.logs.L;
import doneroapp.globulaertech.com.doneroapplication.utils.PreferenceString;
import doneroapp.globulaertech.com.doneroapplication.utils.PreferenceUtils;
import doneroapp.globulaertech.com.doneroapplication.volley.MyApplication;

public class RedeemsActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btSubmit;
    private EditText doneroPoints;
    private EditText redeemPoint;
    private EditText edit_redemPoints;
    private PreferenceUtils mPreferenceUtils;
    private Context mContext;
    private String User_id;
    private String API_KEY;
    private static final String JSON_TAG = "JSON_OBJECT_REQUEST_RESEND";
    private Toolbar mToolbar;
    String points;
    String redeemPoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeems);
         mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext = RedeemsActivity.this;
        User_id = Integer.toString(mPreferenceUtils.readInteger(mContext, PreferenceString.USER_ID, 1));
        API_KEY = mPreferenceUtils.readString(mContext, PreferenceString.USER_API, "");
        getCollection_record();
        btSubmit = (Button)findViewById(R.id.submitRpoints);
        doneroPoints = (EditText)findViewById(R.id.edt_doneropoints);
        redeemPoint = (EditText)findViewById(R.id.redeem_points);
        edit_redemPoints = (EditText)findViewById(R.id.deduct_points);
        setUpToolbar();
        btSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submitRpoints:
                if (validate()){
                    submitUpdaatedPoints();
                }

        }
    }

    public boolean validate(){
        if (edit_redemPoints.getText().toString().equals("")){
            edit_redemPoints.setError("please enter points for deduction");
            return false;
        }
        return true;
    }

    public void getCollection_record() {
        final ProgressDialog loading = ProgressDialog.show(this, "Downloading...", "Please wait...", false, false);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Endpoints.redeemPoints() + "/" + User_id,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        try {
                            if (!response.getBoolean(getResources().getString(R.string.response_error))) {
                                points = response.getString(getResources().getString(R.string.response_doneroPnts));
                                redeemPoints = response.getString(getResources().getString(R.string.response_redeem));
                                 doneroPoints.setText(points);
                                redeemPoint.setText(redeemPoints);


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
                        Toast.makeText(RedeemsActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void submitUpdaatedPoints() {
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.redeemPoints(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Disimissing the progress dialog
                        try {

                            JSONObject mJsonObject = new JSONObject(response);
                            if (!mJsonObject.getBoolean(getResources().getString(R.string.response_error))) {
                                L.T(mContext, mJsonObject.getString(getResources().getString(R.string.response_message)));
                                startActivity(new Intent(RedeemsActivity.this,MapsActivity.class));
                                finish();
                                Toast.makeText(RedeemsActivity.this, "You have successfully updated your points", Toast.LENGTH_SHORT).show();
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
                final String redeemPoints = edit_redemPoints.getText().toString().trim();
                //Converting Bitmap to String
                final String usernaame = User_id;
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put(Constant.KEY_USER_ID, usernaame);
                params.put(Constant.KEY_REDEEM, redeemPoints);
                //returning parameters
                return params;
            }
        };
        try {
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyApplication.getInstance().addToRequestQueue(stringRequest, JSON_TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Creating a Request Queue
       /* RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestQueue.add(stringRequest);*/
    }
    public void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        setTitle("Redeem Points");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(RedeemsActivity.this, MapsActivity.class));
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MapsActivity.class));
        finish();
    }
}
