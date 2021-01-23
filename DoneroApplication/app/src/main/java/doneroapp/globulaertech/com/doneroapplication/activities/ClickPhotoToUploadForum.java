package doneroapp.globulaertech.com.doneroapplication.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Map;
import doneroapp.globulaertech.com.doneroapplication.R;
import doneroapp.globulaertech.com.doneroapplication.endpoints.Endpoints;
import doneroapp.globulaertech.com.doneroapplication.extras.Config;
import doneroapp.globulaertech.com.doneroapplication.extras.Constant;
import doneroapp.globulaertech.com.doneroapplication.logs.L;
import doneroapp.globulaertech.com.doneroapplication.utils.PreferenceString;
import doneroapp.globulaertech.com.doneroapplication.utils.PreferenceUtils;
import doneroapp.globulaertech.com.doneroapplication.volley.MyApplication;

public class ClickPhotoToUploadForum extends AppCompatActivity implements View.OnClickListener {
    private EditText edt_description;
    private ImageView img_pic;
    private TextView txt_error_img;
    private Button btn_click;
    private Button btn_submit;
    private Bitmap bitmap;
    private Toolbar mToolbar;
    private PreferenceUtils mPreferenceUtils;
    private Context context;
    private String User_id;
    private String API_KEY;
    private String Beggar_ID;
    private static final String JSON_FORUM_TAG = "JSON_OBJECT_REQUEST_SEND";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_photo_to_upload_forum);
        context = ClickPhotoToUploadForum.this;
        edt_description = (EditText) findViewById(R.id.edt_descrption_reach);
        img_pic = (ImageView) findViewById(R.id.img_pic_forum);
        txt_error_img = (TextView) findViewById(R.id.text_error_image);
        btn_click = (Button) findViewById(R.id.btn_click_forum);
        btn_submit = (Button) findViewById(R.id.btn_submit_forum);
        API_KEY = mPreferenceUtils.readString(context, PreferenceString.USER_API, "");
        User_id = Integer.toString(mPreferenceUtils.readInteger(context, PreferenceString.USER_ID, 1));
        Beggar_ID = Integer.toString(mPreferenceUtils.readInteger(context, PreferenceString.SOURCE_ID, 1));
        setUpToolbar();
        btn_click.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    private void senToForum() {
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.needyGotHelp(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Disimissing the progress dialog
                        try {

                            JSONObject mJsonObject = new JSONObject(response);
                            if (!mJsonObject.getBoolean(getResources().getString(R.string.response_error))) {
                                L.T(context, mJsonObject.getString(getResources().getString(R.string.response_message)));
                                startActivity(new Intent(ClickPhotoToUploadForum.this, ForumActivity.class));
                                finish();
                            } else {
                                L.T(context, mJsonObject.getString(getResources().getString(R.string.response_message)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loading.dismiss();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
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
                final String details = edt_description.getText().toString().trim();
                //Converting Bitmap to String
                String image = getStringImage(bitmap);
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put(Constant.KEY_FORUM_IMG, image);
                params.put(Constant.KEY_FORUM_DESC, details);
                params.put(Constant.KEY_BEGGAR, Beggar_ID);
                params.put(Constant.KEY_USER_ID, User_id);
                //returning parameters
                return params;
            }
        };
        try {
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyApplication.getInstance().addToRequestQueue(stringRequest, JSON_FORUM_TAG);
        }catch (Exception e){
            e.printStackTrace();
        }

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
        L.T(context, message);
    }


    public void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        setTitle("Donero");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(ClickPhotoToUploadForum.this,MapsActivity.class));
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean validations() {
        if (edt_description.getText().toString().equals("")) {
            edt_description.setError(Config.SET_ERROR_NAME);
            return false;
        }
        if (img_pic.getDrawable() == null) {
            txt_error_img.setError(Config.SET_ERROR_IMAGE);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_click_forum:
                openCamera();
                break;
            case R.id.btn_submit_forum:
                if (validations()) {
                    senToForum();
                    break;
                }
            default:
        }
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
            img_pic.setImageBitmap(bitmap);
            img_pic.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ClickPhotoToUploadForum.this,MapsActivity.class));
        finish();
    }
}
