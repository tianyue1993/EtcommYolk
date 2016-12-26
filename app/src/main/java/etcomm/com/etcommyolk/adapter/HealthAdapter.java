package etcomm.com.etcommyolk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.RecommendItems;

/**
 * Created by ${tianyue} on 2016/12/16.
 */
public class HealthAdapter extends YolkBaseAdapter<RecommendItems> {

    public HealthAdapter(Context context, ArrayList<RecommendItems> list) {
        super(context);
        this.mList = list;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_healht_news, null);
            viewHolder.healthtopic = (TextView) convertView.findViewById(R.id.healthtopic);
            viewHolder.health_image = (SimpleDraweeView) convertView.findViewById(R.id.health_image);
            viewHolder.readingamount = (TextView) convertView.findViewById(R.id.readingamount);
            viewHolder.item_healthnews_sumary = (TextView) convertView.findViewById(R.id.item_healthnews_sumary);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        RecommendItems recommendactivity = getItem(position);
        if (recommendactivity != null) {
            viewHolder.health_image.setImageURI(recommendactivity.image);
            viewHolder.healthtopic.setText(recommendactivity.title);
            viewHolder.readingamount.setText(recommendactivity.pv);
            viewHolder.item_healthnews_sumary.setText(recommendactivity.desc);
        }

        return convertView;
    }

    public static class ViewHolder {
        com.facebook.drawee.view.SimpleDraweeView health_image;
        TextView healthtopic;
        TextView readingamount;
        TextView item_healthnews_sumary;

    }
}
