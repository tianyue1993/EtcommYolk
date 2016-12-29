package etcomm.com.etcommyolk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.utils.DensityUtil;

public class AddTopicDisscussPhotoGridAdapter extends BaseAdapter {
    public interface DeleteOnClickListener {
        public void delete(int position, String str);
    }

    private DeleteOnClickListener deleteOnClickListener;
    private Context mContext;
    private ArrayList<String> medilist;
    private int width;
    private LayoutInflater infalter;

    public AddTopicDisscussPhotoGridAdapter(Context mContext, ArrayList<String> medilist, int scwidth, DeleteOnClickListener deleteOnClickListener) {

        // TODO Auto-generated constructor stub
        this.mContext = mContext;
        infalter = LayoutInflater.from(mContext);
        this.medilist = medilist;
        this.width = (scwidth - 2 * DensityUtil.dip2px(mContext, 35)) / 3;
        this.deleteOnClickListener = deleteOnClickListener;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return medilist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return medilist.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        String pic = medilist.get(position);
        if (pic.equals("0")) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder viewholder;
        String pic = medilist.get(position);
        final String picstr = pic;
        if (convertView == null) {
            viewholder = new ViewHolder();
            View view = infalter.inflate(R.layout.layout_photo_griditem, null);
            view.setLayoutParams(new AbsListView.LayoutParams(width, width));
            convertView = view;
            viewholder.imageview = (SimpleDraweeView) view.findViewById(R.id.iv_photo_lpsi);
            viewholder.imageview_delete = (ImageView) view.findViewById(R.id.iv_photo_delete_lpsi);
            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }
        if (picstr.equals("0")) {
//			viewholder.imageview.setBackgroundResource(R.drawable.add_picture);
            viewholder.imageview.setImageDrawable(null);
            viewholder.imageview.setImageResource(R.mipmap.add_picture_mid_s);
            viewholder.imageview_delete.setVisibility(View.GONE);
//			imageLoader.displayImage("file://"+picstr, viewholder.imageview);
        } else {
            viewholder.imageview.setImageURI("file://" + picstr);
            viewholder.imageview_delete.setVisibility(View.VISIBLE);
            viewholder.imageview_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (deleteOnClickListener != null) {
                        deleteOnClickListener.delete(position, picstr);
                        notifyDataSetChanged();
                    }
                }
            });
        }

        return convertView;
    }

    public class ViewHolder {
        SimpleDraweeView imageview;
        ImageView imageview_delete;
    }
}
