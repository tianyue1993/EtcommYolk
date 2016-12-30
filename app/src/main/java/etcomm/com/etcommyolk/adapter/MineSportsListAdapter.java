package etcomm.com.etcommyolk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;


import java.util.ArrayList;

import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.RecommendItems;

public class MineSportsListAdapter<T> extends YolkBaseAdapter<RecommendItems> {


    public MineSportsListAdapter(Context context, ArrayList<RecommendItems> mList) {
        super(context);
        this.mList = mList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.suggest_item_activity, null);
            viewHolder.atctivityImage = (SimpleDraweeView) convertView.findViewById(R.id.suggest_image);
            viewHolder.suggest_activity_topic = (TextView) convertView.findViewById(R.id.suggest_activity_topic);
            viewHolder.daterange = (TextView) convertView.findViewById(R.id.daterange);
            viewHolder.participation = (TextView) convertView.findViewById(R.id.participation);
            viewHolder.activity_status = (TextView) convertView.findViewById(R.id.activity_status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final RecommendItems mInfo = getItem(position);
        viewHolder.participation.setText(mInfo.number + " 人参与");
        viewHolder.suggest_activity_topic.setText(mInfo.title);
        viewHolder.activity_status.setText(mInfo.status);
        viewHolder.atctivityImage.setImageURI(mInfo.image);
        viewHolder.daterange.setText(mInfo.start_at + "-" + mInfo.end_at);
        return convertView;
    }

    private static class ViewHolder {
        SimpleDraweeView atctivityImage;
        TextView suggest_activity_topic;
        TextView daterange;
        TextView participation;
        TextView activity_status;
    }
}
