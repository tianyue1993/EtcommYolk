package etcomm.com.etcommyolk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.RecommendItems;

/**
 * Created by ${tianyue} on 2016/12/16.
 */
public class ActivityAdapter extends YolkBaseAdapter<RecommendItems> {

    public ActivityAdapter(Context context, ArrayList<RecommendItems> list) {
        super(context);
        this.mList = list;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_activity, null);
            viewHolder.activityImage = (com.facebook.drawee.view.SimpleDraweeView) convertView.findViewById(R.id.activity_image);
            viewHolder.activityStatus = (TextView) convertView.findViewById(R.id.activity_status);
            viewHolder.suggestActivityTopic = (TextView) convertView.findViewById(R.id.suggest_activity_topic);
            viewHolder.daterange = (TextView) convertView.findViewById(R.id.daterange);
            viewHolder.participation = (TextView) convertView.findViewById(R.id.participation);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        RecommendItems recommendactivity = getItem(position);
        if (recommendactivity != null) {
            viewHolder.activityImage.setImageURI(recommendactivity.image);
            viewHolder.activityStatus.setText(recommendactivity.status);
            viewHolder.participation.setText(recommendactivity.user_num + "人参与");
            viewHolder.daterange.setText(recommendactivity.start_at + "——" + recommendactivity.end_at);
            viewHolder.suggestActivityTopic.setText(recommendactivity.title);
        }

        return convertView;
    }

    public static class ViewHolder {
        com.facebook.drawee.view.SimpleDraweeView activityImage;
        TextView suggestActivityTopic;
        TextView participation;
        TextView daterange;
        TextView activityStatus;

    }
}
