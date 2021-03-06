package etcomm.com.etcommyolk.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

import etcomm.com.etcommyolk.ApiClient;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.entity.GroupItems;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.utils.GlobalSetting;
import etcomm.com.etcommyolk.widget.ProgressDialog;

public class GroupListAdapter extends YolkBaseAdapter<GroupItems> {
    private static final String TAG = "AroundListAdapter";
    private mAttentioned mListener;
    ViewHolder viewHolder = null;

    public GroupListAdapter(Context context, ArrayList<GroupItems> mList, mAttentioned listener) {
        super(context);
        this.mList = mList;
        this.mListener = listener;
    }

    public interface mAttentioned {
        void onAttentioned(GroupItems item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_around_group, null);
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
        final GroupItems mInfo = getItem(position);
        if (null != mInfo) {
            viewHolder.attention_count.setText(mInfo.follows + "人关注");
            viewHolder.attention_topic.setText(mInfo.name + "");
            viewHolder.follow_count.setText(mInfo.discussion_number + "个帖子");
            if (mInfo.desc != null) {
                viewHolder.topic_discuss.setText(mInfo.desc);
            }
            if (!mInfo.avatar.isEmpty()) {
                viewHolder.topic_image.setImageURI(mInfo.avatar);
            } else {
                viewHolder.topic_image.setImageResource(R.mipmap.ic_header);

            }
            if (mInfo.is_followed.equals("1")) {
                viewHolder.attention.setText("已关注");
                viewHolder.attention.setEnabled(false);
            } else {
                viewHolder.attention.setText("关注");
                viewHolder.attention.setEnabled(true);
            }
            viewHolder.attention.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attention(mInfo);
                }
            });

        }
        return convertView;
    }

    protected void attention(final GroupItems mInfo) {
        RequestParams params = new RequestParams();
        params.put("access_token", GlobalSetting.getInstance(mContext).getAccessToken());
        params.put("topic_id", mInfo.topic_id);
        cancelmDialog();
        showProgress(0, true);
        ApiClient.getInstance().Attention(mContext, params, new CommenHandler() {
            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                cancelmDialog();
                Toast.makeText(mContext, "关注成功", Toast.LENGTH_SHORT).show();
                if (mListener != null) {
                    mListener.onAttentioned(mInfo);
                }
                //关注成功，刷新身边页面——添加关注小组到我的小组列表
                Intent intent = new Intent();
                intent.setAction("add");
                mContext.sendBroadcast(intent);
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });
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
