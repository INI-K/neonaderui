package com.inik.neonadeuri.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.inik.neonadeuri.HomeActivity;
import com.inik.neonadeuri.ProfileEtcActivity;
import com.inik.neonadeuri.ProfileSearchActivity;
import com.inik.neonadeuri.R;
import com.inik.neonadeuri.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends BaseAdapter {

    private Context context;
    private List<User> list;
    private LayoutInflater inflate;
    private ViewHolder viewHolder;
    public  UserDataChanger userDataChanger;


    public SearchAdapter(List<User> list, Context context , UserDataChanger userDataChanger){
        this.userDataChanger = userDataChanger;
        this.list = list;
        this.context = context;
        this.inflate = LayoutInflater.from(context);

    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if(convertView == null){

            int pos = position;
            convertView = inflate.inflate(R.layout.row_listview,null);

            viewHolder = new ViewHolder();
            viewHolder.label = (TextView) convertView.findViewById(R.id.label);
            viewHolder.circleImageView = (CircleImageView) convertView.findViewById(R.id.search_circle_image_view);

            convertView.setTag(viewHolder);

            viewHolder.label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), list.get(pos).getNickname(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, ProfileSearchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("user", list.get(pos));
                    context.startActivity(intent);
                }
            });
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // 리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.
        viewHolder.label.setText(list.get(position).getNickname());

        return convertView;
    }

    class ViewHolder{
        public TextView label;
        public CircleImageView circleImageView;
    }
    
    public interface UserDataChanger {
        void userDataSetChanged();
    }

}