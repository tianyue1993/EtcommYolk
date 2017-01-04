package etcomm.com.etcommyolk.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

import etcomm.com.etcommyolk.ApiClient;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.entity.DisscussItems;
import etcomm.com.etcommyolk.entity.GroupItems;
import etcomm.com.etcommyolk.entity.RecommendItems;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.utils.GlobalSetting;
import etcomm.com.etcommyolk.widget.ProgressDialog;

public class ShareGroupListAdapter extends YolkBaseAdapter<GroupItems> {
    private static final String TAG = "ShareGroupListAdapter";
    ViewHolder viewHolder = null;

    private Dialog coDialog;
    private Bundle bundle;
    private DisscussItems mDisscussItems;
    private RecommendItems recommendItems;
    private Intent mIntent;
    private String share_id;


    public ShareGroupListAdapter(Context context, ArrayList<GroupItems> mList, Intent intent) {
        super(context);
        this.mList = mList;
        this.mList = mList;
        mIntent = intent;
        share_id = mIntent.getStringExtra("topic_id");
        if (mIntent != null) {
            bundle = intent.getExtras();
            if (bundle != null) {
                recommendItems = (RecommendItems) bundle.getSerializable("RecommendItems");
            }
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_share_group, null);
            viewHolder.attention_count = (TextView) convertView.findViewById(R.id.attention_count);
            viewHolder.attention_topic = (TextView) convertView.findViewById(R.id.attention_topic);
            viewHolder.topic_image = (SimpleDraweeView) convertView.findViewById(R.id.topic_image);
            viewHolder.follow_count = (TextView) convertView.findViewById(R.id.follow_count);
            viewHolder.topic_discuss = (TextView) convertView.findViewById(R.id.topic_discuss);
            viewHolder.attention = (Button) convertView.findViewById(R.id.attention);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final GroupItems groupItem = getItem(position);
        if (null != groupItem) {
            viewHolder.attention_count.setText(groupItem.follows + "人关注");
            viewHolder.attention_topic.setText(groupItem.name + "");
            viewHolder.follow_count.setText(groupItem.discussion_number + "个帖子");
            if (groupItem.desc != null) {
                viewHolder.topic_discuss.setText(groupItem.desc);
            }
            if (!groupItem.avatar.isEmpty()) {
                viewHolder.topic_image.setImageURI(groupItem.avatar);
            } else {
                viewHolder.topic_image.setImageResource(R.mipmap.ic_header);

            }

            viewHolder.attention.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    coDialog = showShareDialog(mContext, groupItem, recommendItems);
                }
            });

        }
        return convertView;
    }


    public Dialog showShareDialog(Context context, final GroupItems groupItem, final RecommendItems mInfo) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_share, null);
        final Dialog customDialog = new Dialog(context, R.style.commonDialog);
        customDialog.setCanceledOnTouchOutside(true);
        customDialog.setCancelable(false); // 点击其他区域关闭
        customDialog.setContentView(view);
        customDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                } else {
                    return false;
                }
            }

        });
        TextView msgtv = (TextView) view.findViewById(R.id.tv_msg);
        TextView dialog_title = (TextView) view.findViewById(R.id.dialog_title);
        SimpleDraweeView news_image = (SimpleDraweeView) view.findViewById(R.id.news_image);
        TextView btnLeft = (TextView) view.findViewById(R.id.btn_left);
        TextView btnRight = (TextView) view.findViewById(R.id.btn_right);
        if (recommendItems != null) {
            msgtv.setText(mInfo.desc);
            dialog_title.setText(mInfo.title);
            if (mInfo.image != null) {
                news_image.setImageURI(mInfo.image);
            }
        } else {
            //从小组页面分享点击过来的
            msgtv.setText(mIntent.getStringExtra("discuse"));
            dialog_title.setText(mIntent.getStringExtra("topic_name"));
            news_image.setImageURI(mIntent.getStringExtra("image"));
        }


        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                SharetoGroup(groupItem);

            }
        });

        customDialog.show();
        WindowManager.LayoutParams lp = customDialog.getWindow().getAttributes();
        lp.width = 800; //设置宽度
        customDialog.getWindow().setAttributes(lp);
        return customDialog;
    }


    protected void SharetoGroup(GroupItems groupItem) {
        RequestParams params = new RequestParams();
        params.put("access_token", GlobalSetting.getInstance(mContext).getAccessToken());
        params.put("topic_id", groupItem.topic_id);
        String type = mIntent.getStringExtra("type");
        if (recommendItems != null) {
            //从正常列表页点击进入详情，分享
            if (recommendItems.type.equals("activity")) {
                params.put("share_type", "Activity");
            } else if (recommendItems.type.equals("health")) {
                params.put("share_type", "Health");
            }
            params.put("share_id", recommendItems.id);
        } else {
            //从小组列表点击进入详情分享
            if (type != null) {
                if (type.equals("activity")) {
                    params.put("share_type", "Activity");
                } else if (type.equals("health")) {
                    params.put("share_type", "Health");
                } else {
                    //小组分享到小组
                    params.put("share_type", "Topic");
                }
                params.put("share_id", mIntent.getStringExtra("topic_id"));
            }
        }
        Log.d(TAG, "SharetoGroup: " + params.toString());
        cancelmDialog();
        showProgress(0, true);
        ApiClient.getInstance().ShareToGroup(mContext, params, new CommenHandler() {
                    @Override
                    public void onCancel() {
                        super.onCancel();
                        cancelmDialog();
                    }

                    @Override
                    public void onSuccess(Commen commen) {
                        super.onSuccess(commen);
                        cancelmDialog();
                        coDialog = showCompleteDialog(mContext);
                        Toast.makeText(mContext, commen.message, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(BaseException exception) {
                        super.onFailure(exception);
                        cancelmDialog();
                    }
                }

        );
    }


    public Dialog showCompleteDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_sharecomplete, null);
        final Dialog customDialog = new Dialog(context, R.style.commonDialog);
        WindowManager.LayoutParams localLayoutParams = customDialog.getWindow().getAttributes();
        customDialog.onWindowAttributesChanged(localLayoutParams);
        customDialog.setCanceledOnTouchOutside(true);
        customDialog.setCancelable(false); // 点击其他区域关闭
        customDialog.setContentView(view);
        customDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                } else {
                    return false;
                }
            }

        });

        TextView btnLeft = (TextView) view.findViewById(R.id.btn_left);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
        // right btn
        TextView btnRight = (TextView) view.findViewById(R.id.btn_right);

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.sendBroadcast(new Intent("finish"));
            }
        });
        if (recommendItems != null) {
            if (recommendItems.type.equals("health")) {
                btnRight.setText("返回资讯");
            } else if (recommendItems.type.equals("activity")) {
                btnRight.setText("返回活动");
            }
        } else {
            btnRight.setText("返回小组");
        }
        customDialog.show();
        WindowManager.LayoutParams lp = customDialog.getWindow().getAttributes();
        customDialog.getWindow().setAttributes(lp);
        return customDialog;
    }

    private static class ViewHolder {
        TextView attention_count, follow_count;
        TextView attention_topic, topic_discuss;
        SimpleDraweeView topic_image;
        Button attention;
    }


    protected ProgressDialog mProgress;

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
