package doneroapp.globulaertech.com.doneroapplication.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import doneroapp.globulaertech.com.doneroapplication.endpoints.Endpoints;
import doneroapp.globulaertech.com.doneroapplication.logs.L;
import doneroapp.globulaertech.com.doneroapplication.pojo.ForumData;
import doneroapp.globulaertech.com.doneroapplication.R;
import doneroapp.globulaertech.com.doneroapplication.utils.PreferenceString;
import doneroapp.globulaertech.com.doneroapplication.utils.PreferenceUtils;
import doneroapp.globulaertech.com.doneroapplication.volley.MyApplication;

/**
 * Created by Dell on 3/29/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    public interface OnItemClickListener {

        void onItemClick(ForumData item);
    }

    private List<ForumData> forumDataList = new ArrayList<>();
    private final OnItemClickListener listener;
    private ImageLoader mImageLoader;
    private Context mContext;
    PreferenceUtils mPreferenceUtils;
    public static final String JSON_TAG = "JSON_OBJECT_REQUEST_RESEND";
    private String API_KEY;
    private String User_id;
    public int rowId;
    public String forum_userid;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /* public RadioGroup radioGroup;
         public RadioButton btn_yes;*/
        public int rbposition = 0;
        public TextView user_name;
        public ImageView user_image;
        public Button btn_yesv, btn_nov;
        public Button btn_yes;
        public Button btn_no;

        public MyViewHolder(View view) {
            super(view);
            /*radioGroup = (RadioGroup) view.findViewById(R.id.radio_grup);*/
            user_name = (TextView) view.findViewById(R.id.txt_description);
            user_image = (ImageView) view.findViewById(R.id.img_forum_list);
            API_KEY = mPreferenceUtils.readString(mContext, PreferenceString.USER_API, "");
            User_id = Integer.toString(mPreferenceUtils.readInteger(mContext, PreferenceString.USER_ID, 1));
/*
            forum_verified_ID = Integer.toString(mPreferenceUtils.readInteger(mContext,PreferenceString.FORUM_VERIFIED_USER,1));
*/
            btn_yes = (Button) view.findViewById(R.id.yesverified);
            btn_no = (Button) view.findViewById(R.id.notverify);
            btn_yes.setOnClickListener(this);
            btn_no.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.yesverified:
                    ForumData forumData = forumDataList.get(getAdapterPosition());
                    mPreferenceUtils.writeBoolean(mContext, "FORUM" + forumData.getId(), true);
                    //  Toast.makeText(mContext, User_id+ " " +forum_userid, Toast.LENGTH_SHORT).show();
                    notifyUser("YES");
                    forumDataList.remove(getAdapterPosition());
                    Toast.makeText(mContext, "Verified", Toast.LENGTH_SHORT).show();
                    notifyItemRemoved(getAdapterPosition());
                    break;
                case R.id.notverify:
                    notifyUser("NO");
                    forumDataList.remove(getAdapterPosition());
                    Toast.makeText(mContext, "Not Verified", Toast.LENGTH_SHORT).show();
                    notifyItemRemoved(getAdapterPosition());
                    break;
                default:
            }
        }
    }

    public RecyclerAdapter(Context mContext, List<ForumData> userPojoList, OnItemClickListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        this.forumDataList = userPojoList;
        mImageLoader = MyApplication.getInstance().getImageLoader();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forudata_for_recycleview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ForumData forumData = forumDataList.get(position);
        holder.user_name.setText(forumData.getDescription());
        rowId = forumData.getId();
        forum_userid = forumData.getUser_id();
        if (User_id.equals(forum_userid)) {
            holder.btn_yes.setEnabled(false);
            holder.btn_no.setEnabled(false);
        }
        if (mPreferenceUtils.readBoolean(mContext, "FORUM" + forumData.getId(), false)) {
            holder.btn_yes.setEnabled(false);
            holder.btn_no.setEnabled(false);
        }
        mImageLoader.get(forumData.getImageUrl(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    // load image into imageview
                    holder.user_image.setImageBitmap(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    listener.onItemClick(forumData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
   /*     holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                try {
                    int rbID = group.getCheckedRadioButtonId();
                    holder.btn_yes = (RadioButton) group.findViewById(rbID);
                    if (holder.btn_yes.isPressed()) {
*//*
                        Toast.makeText(mContext, ""+position, Toast.LENGTH_SHORT).show();
*//*
                        forumDataList.remove(position);
                        notifyItemRemoved(position);
                    }
                    notifyUser();
                    Toast.makeText(mContext, holder.btn_yes.getText(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return forumDataList.size();
    }

    private void notifyUser(String tag) {
        //Showing the progress dialog
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, Endpoints.verifyForumData() + User_id + "/" + rowId + "/" + tag,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Disimissing the progress dialog
                        try {
                            //  Toast.makeText(context, ""+response, Toast.LENGTH_LONG).show();
                            if (!response.getBoolean("error")) {
                                // parseJson(response.getJSONArray(getResources().getString(R.string.response_forum)));
                                L.T(mContext, response.getString("message"));
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
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG).show();

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
        MyApplication.getInstance().addToRequestQueue(jsonObjReq, JSON_TAG);
    }
}
