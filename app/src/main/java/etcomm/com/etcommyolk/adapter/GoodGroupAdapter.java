package etcomm.com.etcommyolk.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.activity.TopicDisscussListActivity;
import etcomm.com.etcommyolk.entity.GoodGroup;


public class GoodGroupAdapter extends YolkBaseAdapter<GoodGroup.QuitGroup> {

    public int color = 0;

    public GoodGroupAdapter(Context context, List<GoodGroup.QuitGroup> List) {
        super(context);
        this.mList = List;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final GoodGroup.QuitGroup mInfo = mList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_goodgroup, null);
            holder.coverImage = (Button) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mInfo != null) {
            holder.coverImage.setText(mInfo.name);
            if (color == 0) {
                holder.coverImage.setBackgroundResource(R.drawable.group_bg1);
            } else if (color == 1) {
                holder.coverImage.setBackgroundResource(R.drawable.group_bg2);
            } else if (color == 3) {
                holder.coverImage.setBackgroundResource(R.drawable.group_bg3);
                color = -1;
            }

            holder.coverImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, TopicDisscussListActivity.class);
                    intent.putExtra("topic_id", mInfo.id);
                    intent.putExtra("topic_name", mInfo.name);
                    mContext.startActivity(intent);
                }
            });

            color++;
        }

        return convertView;
    }


    private static class ViewHolder {
        private Button coverImage;
    }

}