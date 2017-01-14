package etcomm.com.etcommyolk.adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Exchange;
import etcomm.com.etcommyolk.entity.PointsExchangeItems;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.ExchangeHandler;
import etcomm.com.etcommyolk.widget.DialogFactory;
import etcomm.com.etcommyolk.widget.ProgressDialog;

public class PointsExchangeListAdapter extends YolkBaseAdapter<PointsExchangeItems> {

    private int byeCount = 1;
    protected ProgressDialog mProgress;

    public PointsExchangeListAdapter(Context context, ArrayList<PointsExchangeItems> list) {
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
            viewHolder.add_count = (TextView) convertView.findViewById(R.id.add_count);
            viewHolder.bye_count = (TextView) convertView.findViewById(R.id.bye_count);
            viewHolder.reduce_count = (TextView) convertView.findViewById(R.id.reduce_count);
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
        viewHolder.describetext.setVisibility(View.GONE);

        if (null != mInfo) {
            //库存为0的时候
            if (Integer.parseInt(mInfo.inventory) == 0) {
                byeCount = 0;
                //库存为0的时候
                viewHolder.bye_count.setText("0");
                viewHolder.add_count.setEnabled(false);
                viewHolder.reduce_count.setEnabled(false);
            } else {
                byeCount = 1;
                viewHolder.bye_count.setText("1");
                viewHolder.add_count.setEnabled(true);
                viewHolder.reduce_count.setEnabled(true);
            }
            viewHolder.gift_image.setImageURI(mInfo.image);
            if (mInfo.show_money != null) {
                viewHolder.gift_realprice.setText(mInfo.show_money);
                viewHolder.rl_realprice.setVisibility(View.VISIBLE);

            } else {
                viewHolder.rl_realprice.setVisibility(View.GONE);
            }
            viewHolder.gift_realprice.setText(mInfo.show_money);
            viewHolder.gift_name.setText(mInfo.name);
            viewHolder.gift_price.setText(mInfo.score + "积分");
            viewHolder.gift_stock.setText("库    存：" + mInfo.inventory + "");
            if (Integer.parseInt(mInfo.inventory) < 1) {
                viewHolder.gift_exchange.setEnabled(false);
                viewHolder.gift_exchange.setClickable(false);
            } else {
                viewHolder.gift_exchange.setEnabled(true);
                viewHolder.gift_exchange.setClickable(true);
            }

            if (!mInfo.show_money.equals("")) {
                viewHolder.gift_realprice.setText(mInfo.show_money);
                viewHolder.rl_realprice.setVisibility(View.VISIBLE);

            } else {
                viewHolder.rl_realprice.setVisibility(View.GONE);
            }
            if (mInfo.detail != null) {
                viewHolder.gift_detail.setText("详    情：" + mInfo.detail);
            } else {
                viewHolder.gift_detail.setText("详    情：");
            }
            viewHolder.gift_detail.setOnClickListener(new OnClickListener() {
                Boolean flag = true;

                @Override
                public void onClick(View v) {
                    if (flag) {
                        flag = false;
                        viewHolder.gift_detail.setSingleLine(false);
                        viewHolder.gift_detail.setEllipsize(null); // 展开
                    } else {
                        flag = true;
                        viewHolder.gift_detail.setSingleLine(true);
                        viewHolder.gift_detail.setEllipsize(TruncateAt.END); // 收缩
                    }
                }
            });
            viewHolder.gift_exchange.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    byeCount = Integer.parseInt(viewHolder.bye_count.getText().toString());
                    if (byeCount > 0) {
                        exchangegift(viewHolder.gift_exchange, mInfo);
                    } else {
                        showToast("请添加兑换数量");
                    }

                }
            });
            viewHolder.add_count.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int count = Integer.parseInt(viewHolder.bye_count.getText().toString());
                    if (Integer.parseInt(mInfo.score) == 0) {
                        viewHolder.add_count.setClickable(false);
                        viewHolder.reduce_count.setEnabled(false);
                    } else {
                        viewHolder.add_count.setClickable(true);
                        viewHolder.reduce_count.setEnabled(true);
                    }
                    if (count < Integer.parseInt(mInfo.inventory) && Integer.parseInt(mInfo.score) != 0) {
                        count++;
                        byeCount = count;
                    } else {
                        viewHolder.add_count.setEnabled(false);
                    }

                    viewHolder.bye_count.setText(count + "");
                }
            });
            viewHolder.reduce_count.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(mInfo.score) == 0) {
                        viewHolder.add_count.setClickable(false);
                    } else {
                        viewHolder.add_count.setClickable(true);
                    }
                    int count = Integer.parseInt(viewHolder.bye_count.getText().toString());
                    if (count > 1) {
                        count--;
                        byeCount = count;
                        viewHolder.add_count.setEnabled(true);
                    } else {
                    }
                    viewHolder.bye_count.setText(count + "");

                }
            });
        }
        viewHolder.draw_address.setVisibility(View.GONE);
        return convertView;
    }

    public interface ExchangeGift {
        void exchangegift(int score);
    }

    private ExchangeGift mPointExchanged;
    protected Dialog tips;

    public ExchangeGift getmPointExchanged() {
        return mPointExchanged;
    }

    public void setmPointExchanged(ExchangeGift mPointExchanged) {
        this.mPointExchanged = mPointExchanged;
    }

    /**
     * 点击兑换按钮，兑换礼品
     *
     * @param gift_exchange
     * @param mInfo
     */
    protected void exchangegift(final TextView gift_exchange, final PointsExchangeItems mInfo) {
        if (Integer.valueOf(pres.getScore()) < Integer.parseInt(mInfo.score)) {
            Toast.makeText(mContext, "积分不足，无法兑换", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("access_token", pres.getAccessToken());
        params.put("gift_id", mInfo.gift_id);
        params.put("count", String.valueOf(byeCount));
        cancelmDialog();
        showProgress(0, true);
        client.invokeExch(mContext, params, new ExchangeHandler() {
            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(Exchange exchange) {
                super.onSuccess(exchange);
                cancelmDialog();
                if (exchange.code == 0) {
                    String gift_address = exchange.content.gift_address;
                    mInfo.status = "1";
//                    mInfo.inventory(mInfo.inventory() - byeCount);
                    if (mPointExchanged != null) {
                        mPointExchanged.exchangegift(Integer.parseInt(mInfo.score) * byeCount);
                    }
                    notifyDataSetChanged();
                    if (tips != null) {
                        tips.dismiss();
                        tips = null;
                    }
                    tips = DialogFactory.getDialogFactory().showTipsDialog(mContext, "提示", "我知道了", "你已兑换成功，请到" + gift_address + "处领取");
//
                } else {
                    Toast.makeText(mContext, exchange.message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });

    }

    private static class ViewHolder {
        SimpleDraweeView gift_image;
        TextView gift_name, reduce_count, bye_count, add_count, gift_realprice, draw_address;
        TextView gift_price;
        TextView gift_stock, describetext;
        TextView gift_detail;
        Button gift_exchange;
        RelativeLayout rl_realprice;
    }

    Toast toast = null;

    protected void showToast(String message) {
        if (toast != null) {
            return;
        }
        toast = Toast.makeText(mContext, (!TextUtils.isEmpty(message)) ? message : mContext.getString(R.string.network_error), Toast.LENGTH_SHORT);
        toast.show();
        baseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.cancel();
                    toast = null;
                }
            }
        }, 500);
    }

    public static final int MSG_SHOW_TOAST = 2;
    public Handler baseHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_SHOW_TOAST:
                    if (msg.obj != null) {
                        String mes = (String) msg.obj;
                        if (!TextUtils.isEmpty(mes)) {
                            showToast(mes);
                        }
                    }
                    break;
            }
        }

        ;
    };

    public void showProgress(int resId, boolean cancel) {
        mProgress = new ProgressDialog(mContext);
        if (resId <= 0) {
            mProgress.setMessage(R.string.loading_data, cancel);
        } else {
            mProgress.setMessage(resId, cancel);
        }
        mProgress.show();
    }

    public void cancelmDialog() {
        if (mProgress != null) {
            mProgress.dismiss();
        }

    }
}
