package etcomm.com.etcommyolk.adapter;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.PointsExchangeItems;
import etcomm.com.etcommyolk.utils.StringUtils;

public class MyExchangeListAdapter extends YolkBaseAdapter<PointsExchangeItems> {


    public MyExchangeListAdapter(Context context, ArrayList<PointsExchangeItems> list) {
        super(context);
        this.mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_pointsexchange, null);
            viewHolder.gift_image = (SimpleDraweeView) convertView.findViewById(R.id.gift_image);
            viewHolder.gift_name = (TextView) convertView.findViewById(R.id.gift_name);
            viewHolder.gift_price = (TextView) convertView.findViewById(R.id.gift_price);
            viewHolder.gift_stock = (TextView) convertView.findViewById(R.id.gift_stock);
            viewHolder.gift_detail = (TextView) convertView.findViewById(R.id.gift_detail);
            viewHolder.gift_exchange = (Button) convertView.findViewById(R.id.gift_exchange);
            viewHolder.ll_count = (LinearLayout) convertView.findViewById(R.id.ll_count);
            viewHolder.gift_realprice = (TextView) convertView.findViewById(R.id.gift_realprice);
            viewHolder.draw_address = (TextView) convertView.findViewById(R.id.draw_address);
            viewHolder.rl_realprice = (RelativeLayout) convertView.findViewById(R.id.rl_realprice);
            viewHolder.describetext = (TextView) convertView.findViewById(R.id.describetext);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Log.i(tag, "getview");
        final PointsExchangeItems mInfo = getItem(position);
        if (null != mInfo) {
            viewHolder.gift_image.setImageURI(mInfo.image);
            viewHolder.gift_realprice.setText(mInfo.show_money);
            viewHolder.draw_address.setText("领取地址：" + mInfo.draw_address);
            viewHolder.gift_name.setText(mInfo.name);
            if (!mInfo.show_money.equals("")) {
                viewHolder.gift_realprice.setText(mInfo.show_money);
                viewHolder.rl_realprice.setVisibility(View.VISIBLE);

            } else {
                viewHolder.rl_realprice.setVisibility(View.GONE);
            }

            viewHolder.gift_price.setText("兑换价："+mInfo.score + "积分");
            viewHolder.gift_stock.setText("数    量：" + mInfo.number);// 库存改成兑换时间
            if (Integer.valueOf(StringUtils.isEmpty(mInfo.inventory) ? "0" : mInfo.inventory) < 1) {
                viewHolder.gift_exchange.setEnabled(false);
                viewHolder.gift_exchange.setClickable(false);
            } else {
                viewHolder.gift_exchange.setEnabled(true);
                viewHolder.gift_exchange.setClickable(true);
            }
            viewHolder.gift_detail.setText("详    情：" + mInfo.detail);
            viewHolder.gift_detail.setOnClickListener(new OnClickListener() {
                Boolean flag = true;

                @Override
                public void onClick(View v) {
                    if (flag) {
                        flag = false;
                        viewHolder.gift_detail.setSingleLine(false);
                        viewHolder.gift_detail.setEllipsize(null); // 展开
                        // tv.setSingleLine(flag);
                    } else {
                        flag = true;
                        viewHolder.gift_detail.setSingleLine(true);
                        viewHolder.gift_detail.setEllipsize(TruncateAt.END); // 收缩
                        // tv.setSingleLine(flag);
                    }
                }
            });
            if (mInfo.status.equals("1")) {
                viewHolder.gift_exchange.setEnabled(false);
                viewHolder.gift_exchange.setClickable(false);
                viewHolder.gift_exchange.setText("已领取");
                viewHolder.describetext.setVisibility(View.GONE);
            } else {
                viewHolder.gift_exchange.setEnabled(true);
                viewHolder.gift_exchange.setClickable(true);
                viewHolder.gift_exchange.setText("已申请");
                viewHolder.describetext.setVisibility(View.VISIBLE);

            }
        }

        viewHolder.ll_count.setVisibility(View.GONE);
        return convertView;
    }

    private static class ViewHolder {
        SimpleDraweeView gift_image;
        TextView gift_name, draw_address;
        TextView gift_price, gift_realprice;
        TextView gift_stock, describetext;
        TextView gift_detail;
        Button gift_exchange;
        LinearLayout ll_count;
        RelativeLayout rl_realprice;
    }
}
