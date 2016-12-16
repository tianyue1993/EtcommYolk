package etcomm.com.etcommyolk.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import etcomm.com.etcommyolk.ApiClient;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.activity.LoginActivity;
import etcomm.com.etcommyolk.activity.MineActivity;
import etcomm.com.etcommyolk.utils.GlobalSetting;

/**
 * zuoh
 */
public abstract class BaseFragment extends Fragment {
    //SP 存储更新
    protected GlobalSetting prefs;
    //网络请求
    protected ApiClient client;
    //吐司
    protected Toast toast = null;

    //onCreate方法 里面执行各种初始化及调用
    protected abstract void initView(View view, Bundle savedInstanceState);
    //获取布局文件ID
    protected abstract int getLayoutId();
    //获取上下文对象
    protected Context getBaseActivity(){
        return getActivity();
    };


    /**
     * 跳转进入我的页面
     */
    protected View.OnClickListener toMineOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        prefs = etcomm.com.etcommyolk.utils.GlobalSetting.getInstance(getBaseActivity());
        client = ApiClient.getInstance();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        initView(view, savedInstanceState);
        return view;
    }

    protected void showToast(String message) {
        if (toast != null) {
            return;
        }
        toast = Toast.makeText(getBaseActivity(), (!TextUtils.isEmpty(message)) ? message
                : this.getString(R.string.network_error), Toast.LENGTH_SHORT);
        toast.show();
    }

    protected void showToast(int resid) {
        if (toast != null) {
            return;
        }
        toast = Toast.makeText(getBaseActivity(), resid, Toast.LENGTH_SHORT);
        toast.show();
    }

}
