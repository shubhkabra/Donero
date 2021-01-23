package doneroapp.globulaertech.com.doneroapplication.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import doneroapp.globulaertech.com.doneroapplication.R;
import doneroapp.globulaertech.com.doneroapplication.adapters.RecyclerAdapter;
import doneroapp.globulaertech.com.doneroapplication.endpoints.Endpoints;
import doneroapp.globulaertech.com.doneroapplication.extras.Constant;
import doneroapp.globulaertech.com.doneroapplication.logs.L;
import doneroapp.globulaertech.com.doneroapplication.pojo.BeggarPojo;
import doneroapp.globulaertech.com.doneroapplication.pojo.DividerItemDecoration;
import doneroapp.globulaertech.com.doneroapplication.pojo.ForumData;
import doneroapp.globulaertech.com.doneroapplication.utils.PreferenceString;
import doneroapp.globulaertech.com.doneroapplication.utils.PreferenceUtils;
import doneroapp.globulaertech.com.doneroapplication.volley.MyApplication;

public class ForumActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<ForumData> mforumlist = new ArrayList<>();
    private RecyclerAdapter mRecyclerAdapter;
    private Toolbar mToolbar;
    private Context context;
    private PreferenceUtils mPreferenceUtils;
    private static final String JSON_TAG = "JSON_OBJECT_REQUEST_RESEND";
    private String API_KEY;
    String User_id;
    int forum_userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        setUpToolbar();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                startActivity(new Intent(ForumActivity.this, ClickPhotoToUploadForum.class));

            }
        });
        context = ForumActivity.this;
        mRecyclerView = (RecyclerView) findViewById(R.id.forum_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        API_KEY = mPreferenceUtils.readString(context, PreferenceString.USER_API, "");

        try {
            mRecyclerAdapter = new RecyclerAdapter(getApplicationContext(), mforumlist, new RecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(ForumData item) {
                    try {
                        int rbtn_ID = item.getId();
                        mPreferenceUtils.writeInteger(context, PreferenceString.SOURCE_ID, rbtn_ID);
                        Toast.makeText(getApplicationContext(), "item is" + item.getId(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mRecyclerAdapter);
        loadUserData();
    }

    private void loadUserData() {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Endpoints.getForumData(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Disimissing the progress dialog
                        try {
                            loading.dismiss();
                            //  Toast.makeText(context, ""+response, Toast.LENGTH_LONG).show();
                            if (!response.getBoolean(getResources().getString(R.string.response_error))) {
                                parseJson(response.getJSONArray(getResources().getString(R.string.response_forum)));
                                //     Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();

                            }
                            // L.T(context, response.getString(getResources().getString(R.string.response_message)));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            L.T(context, e.getMessage());

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Disimissing the progress dialog
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                        loading.dismiss();
                    }
                }
        ) {
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
        for (int i = 0; i < array.length(); i++) {
            try {
                ForumData mforumpojo = new ForumData();
                JSONObject jsonObject = array.getJSONObject(i);
                mforumpojo.setId(jsonObject.getInt("id"));
                mforumpojo.setImageUrl(jsonObject.getString("img_url"));
                mforumpojo.setDescription(jsonObject.getString("description"));
                mforumpojo.setUser_id(jsonObject.getString("user_id"));
                //  Toast.makeText(context, ""+mforumpojo, Toast.LENGTH_SHORT).show();
             /*   forum_userId = jsonObject.getInt(Constant.KEY_VERIFY_USERID);
                mPreferenceUtils.writeInteger(context,PreferenceString.FORUM_VERIFIED_USER,forum_userId);*/
                mforumlist.add(mforumpojo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mRecyclerAdapter.notifyDataSetChanged();

    }

    public void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        setTitle("Donero Forum");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(ForumActivity.this, MapsActivity.class));
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForumActivity.this, MapsActivity.class));
        finish();
    }
}
