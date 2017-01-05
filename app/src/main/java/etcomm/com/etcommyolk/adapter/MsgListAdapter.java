package etcomm.com.etcommyolk.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.MsgListItems;
import etcomm.com.etcommyolk.utils.DensityUtil;
import etcomm.com.etcommyolk.utils.StringUtils;

public class MsgListAdapter extends YolkBaseAdapter<MsgListItems.MsgList> {

	private int mScreenWidth;

	public MsgListAdapter(Context context, List<MsgListItems.MsgList> list, int width) {
		super(context);
		this.mList = list;
		this.mScreenWidth = width;
		// TODO Auto-generated constructor stub
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_msglist, null);
			viewHolder.msg_avatar = (SimpleDraweeView) convertView.findViewById(R.id.msg_avatar);
			viewHolder.msg_title = (TextView) convertView.findViewById(R.id.msg_title);
			viewHolder.msg_detail = (TextView) convertView.findViewById(R.id.msg_detail);
			viewHolder.msg_time = (TextView) convertView.findViewById(R.id.msg_time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final MsgListItems.MsgList mInfo = getItem(position);
		if (null != mInfo) {
			viewHolder.msg_avatar.setImageURI(mInfo.avatar);
			viewHolder.msg_title.setText(mInfo.title);
			if (StringUtils.isEmpty(mInfo.detail)) {
				viewHolder.msg_detail.setVisibility(View.GONE);
			} else {
				viewHolder.msg_detail.setVisibility(View.VISIBLE);
				viewHolder.msg_detail.setText(mInfo.detail);
			}
			viewHolder.msg_time.setText(mInfo.time);
		}
		return convertView;
	}

	private static class ViewHolder {
		SimpleDraweeView msg_avatar;
		TextView msg_title;
		TextView msg_detail;
		TextView msg_time;
	}
}
