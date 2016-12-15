package etcomm.com.etcommyolk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.MyPointsDetail;


public class MyPointsDetailListAdapter extends YolkBaseAdapter<MyPointsDetail.Content.Score> {


    public MyPointsDetailListAdapter(Context context, ArrayList<MyPointsDetail.Content.Score> list) {
        super(context);
        this.mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_pointsdetail, null);
            viewHolder.points_image = (ImageView) convertView.findViewById(R.id.points_image);
            viewHolder.points_name = (TextView) convertView.findViewById(R.id.points_name);
            viewHolder.points_time = (TextView) convertView.findViewById(R.id.points_time);
            viewHolder.points_dec = (TextView) convertView.findViewById(R.id.points_dec);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final MyPointsDetail.Content.Score mInfo = getItem(position);
        if (null != mInfo) {
            viewHolder.points_name.setText(mInfo.type);
            viewHolder.points_time.setText("" + mInfo.created_at);//库存改成兑换时间
            viewHolder.points_dec.setText(mInfo.description + " 分");
        }
        return convertView;
    }


    private static class ViewHolder {
        ImageView points_image;
        TextView points_name;
        TextView points_time;
        TextView points_dec;
    }
}
