package doneroapp.globulaertech.com.doneroapplication.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import doneroapp.globulaertech.com.doneroapplication.R;
import doneroapp.globulaertech.com.doneroapplication.endpoints.Endpoints;
import doneroapp.globulaertech.com.doneroapplication.extras.Constant;
import doneroapp.globulaertech.com.doneroapplication.extras.Functions;
import doneroapp.globulaertech.com.doneroapplication.json.JsonParser;
import doneroapp.globulaertech.com.doneroapplication.logs.L;
import doneroapp.globulaertech.com.doneroapplication.pojo.UserPojo;
import doneroapp.globulaertech.com.doneroapplication.utils.PreferenceString;
import doneroapp.globulaertech.com.doneroapplication.utils.PreferenceUtils;
import doneroapp.globulaertech.com.doneroapplication.volley.MyApplication;

public class OtpActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_verify;
    private TextView txt_timer;
    private EditText edt_otp;
    private Context mContext;
    private String name, mobile, email, otp;
    private ProgressDialog mProgressDialog;
    private static final String JSON_TAG = "JSON_OBJECT_REQUEST_RESEND";
    private ArrayList<UserPojo> userList;
    private PreferenceUtils mPreferenceUtils;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        mContext = OtpActivity.this;
        userList = new ArrayList<>();
        name = getIntent().getExtras().getString("name");
        mobile = getIntent().getExtras().getString("mobile");
        email = getIntent().getExtras().getString("email");
        otp = Functions.code(6);
        L.m(otp);
        txt_timer = (TextView) findViewById(R.id.text_timer);
        btn_verify = (Button) findViewById(R.id.btn_verify);
        edt_otp = (EditText) findViewById(R.id.otp);
        btn_verify.setOnClickListener(this);
        countDown();
    }

    private void countDown() {
        final SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                txt_timer.setText(sdf.format(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                txt_timer.setText("Resend");
                txt_timer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resendCode(name, mobile, email, otp);
                        countDown();
                    }
                });
            }
        }.start();
    }

    private void resendCode(final String name, final String mobile, final String email, final String code) {
        progressDialog();
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, Endpoints.userRegisteration(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject mJsonObject = new JSONObject(response);
                            if (!mJsonObject.getBoolean(getResources().getString(R.string.response_error))) {
                                L.T(mContext, mJsonObject.getString(getResources().getString(R.string.response_message)));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        handleDialog();
    }

    private void verificationCode(final String code) {
        progressDialog();
        JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Endpoints.userVerification() + code,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (!response.getBoolean(getResources().getString(R.string.response_error))) {
                                L.T(mContext, response.getString(getResources().getString(R.string.response_message)));
                                userList = JsonParser.parseUserData(response.getJSONObject(getResources().getString(R.string.response_user)));
                                UserPojo userPojo = new UserPojo();
                                if (!userList.isEmpty()) {
                                    setPreferences(userList);
                                }
                            } else {
                                L.T(mContext, response.getString(getResources().getString(R.string.response_message)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleVolleyError(error);
            }
        });
        MyApplication.getInstance().addToRequestQueue(mJsonObjectRequest, JSON_TAG);
        handleDialog();
    }

    private final void progressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.dialog_message));
        mProgressDialog.show();
    }

    private final void handleDialog() {
        mProgressDialog.dismiss();
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

    private boolean validate() {
        if (edt_otp.getText().toString().trim() == "") {
            edt_otp.setError(getResources().getString(R.string.error_otp));
            return true;
        } else if (edt_otp.getText().length() > 6) {
            edt_otp.setError(getResources().getString(R.string.error_otp));
            return true;
        }
        return false;
    }

    private void setPreferences(ArrayList<UserPojo> list) {
        mPreferenceUtils.writeBoolean(mContext, PreferenceString.LOGGED_IN, true);
        mPreferenceUtils.writeInteger(mContext, PreferenceString.USER_ID, list.get(0).getId());
        mPreferenceUtils.writeString(mContext, PreferenceString.USER_NAME, list.get(0).getName());
        mPreferenceUtils.writeString(mContext, PreferenceString.USER_MOBILE, list.get(0).getMobile());
        mPreferenceUtils.writeString(mContext, PreferenceString.USER_EMAIL, list.get(0).getEmail());
        mPreferenceUtils.writeString(mContext, PreferenceString.USER_API, list.get(0).getApi_key());
        mPreferenceUtils.writeString(mContext, PreferenceString.USER_CREATED, list.get(0).getCreate_at());
        mPreferenceUtils.writeString(mContext, PreferenceString.USER_UPDATED, list.get(0).getUpdate_at());
        mPreferenceUtils.writeInteger(mContext, PreferenceString.USER_STATUS, 1);
        openActivity();
    }

    private void openActivity() {
        startActivity(new Intent(mContext, WellcomeActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        if (!validate()) {
            verificationCode(edt_otp.getText().toString().trim());
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
