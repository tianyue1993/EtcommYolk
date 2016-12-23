package etcomm.com.etcommyolk.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import etcomm.com.etcommyolk.R;

public class ScrollHorizontalScrollViewAdapter {

	private LayoutInflater mInflater;
	private ArrayList<String> mDatas;


	public ScrollHorizontalScrollViewAdapter(Context mContext2, ArrayList<String> mMan) {
		// TODO Auto-generated constructor stub
		mInflater = LayoutInflater.from(mContext2);
		this.mDatas = mMan;
	}

	public int getCount() {
		return mDatas.size();
	}

	public Object getItem(int position) {
		return mDatas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.activity_index_gallery_item, parent, false);
			viewHolder.mImg = (SimpleDraweeView) convertView.findViewById(R.id.id_index_gallery_item_image);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mImg.setImageURI(mDatas.get(position));
		return convertView;
	}

	private class ViewHolder {
		SimpleDraweeView mImg;
	}

}
