package etcomm.com.etcommyolk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import etcomm.com.etcommyolk.ApiClient;
import etcomm.com.etcommyolk.utils.GlobalSetting;

public class YolkBaseAdapter<T> extends BaseAdapter {
    protected final String tag = getClass().getSimpleName();
    protected List<T> mList = null;
    protected LayoutInflater mInflater = null;
    protected Context mContext = null;
    protected GlobalSetting pres;
    protected ApiClient client;

    public YolkBaseAdapter(Context context) {
        super();
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mList = new ArrayList<T>();
        pres = GlobalSetting.getInstance(mContext);
        client = ApiClient.getInstance();
    }


    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public T getItem(int position) {
        if (mList != null && position < mList.size()) {
            return mList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

}
