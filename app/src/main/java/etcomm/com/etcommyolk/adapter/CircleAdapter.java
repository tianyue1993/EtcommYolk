package etcomm.com.etcommyolk.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Topic;
import etcomm.com.etcommyolk.widget.CircleImageView;


public class CircleAdapter extends YolkBaseAdapter<Topic.TopicUser> {


    public CircleAdapter(Context context, List<Topic.TopicUser> List) {
        super(context);
        this.mList = List;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final Topic.TopicUser mInfo = mList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_circle, null);
            holder.coverImage = (CircleImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Log.d(tag + "---->avatar", "avatar==" + mInfo.avatar);
        if (mInfo != null) {
            if (mInfo.avatar != null) {
//                holder.coverImage.setImageURI(mInfo.avatar);
                ImageLoader.getInstance().displayImage(mInfo.avatar,holder.coverImage);
            }

        }

        return convertView;
    }


    private static class ViewHolder {
        private CircleImageView coverImage;
    }

}