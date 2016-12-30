package etcomm.com.etcommyolk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.DisscussItems;

public class DisscussPhotoGridAdapter extends YolkBaseAdapter<DisscussItems.DisscussPhotosItems> {

    private int picwidth;

    public DisscussPhotoGridAdapter(Context mContext, List<DisscussItems.DisscussPhotosItems> photos, int mWidth) {
        super(mContext);
        this.mList = photos;
        this.picwidth = mWidth;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        DisscussItems.DisscussPhotosItems mInfo = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            View view = mInflater.inflate(R.layout.item_disscussphoto, null);
            view.setLayoutParams(new AbsListView.LayoutParams(picwidth, picwidth));
            convertView = view;
            holder = new ViewHolder();
            holder.photo_image = (ImageView) convertView.findViewById(R.id.photo_image);//view;
            holder.photo_image.setScaleType(ScaleType.CENTER_CROP);
            holder.photo_image.setLayoutParams(new AbsListView.LayoutParams(picwidth, picwidth));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage(mInfo.image, holder.photo_image);
        return convertView;
    }

    private static class ViewHolder {
        ImageView photo_image;
    }
}
