package com.inik.neonadeuri.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.inik.neonadeuri.ProfileSearchActivity;
import com.inik.neonadeuri.R;
import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.utils.CurrentUserManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    private LayoutInflater inflate;
    private ViewHolder viewHolder;
    public  UserDataChanger userDataChanger;
    public  User currentUser;


    public ChatListAdapter(List<String> list, Context context , UserDataChanger userDataChanger){
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
        currentUser = CurrentUserManager.getCurrentUser();
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
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("userSender", currentUser.getNickname());
                    intent.putExtra("userReceiver", list.get(pos));
                    context.startActivity(intent);
                }
            });
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // 리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.
        viewHolder.label.setText(list.get(position));

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