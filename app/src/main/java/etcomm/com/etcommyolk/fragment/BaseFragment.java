package etcomm.com.etcommyolk.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import etcomm.com.etcommyolk.ApiClient;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.activity.LoginActivity;
import etcomm.com.etcommyolk.activity.MineActivity;
import etcomm.com.etcommyolk.utils.GlobalSetting;
import etcomm.com.etcommyolk.utils.Preferences;
import etcomm.com.etcommyolk.widget.ProgressDialog;

/**
 * zuoh
 */
public abstract class BaseFragment extends Fragment {
    //推送
    public static boolean isActive = false;
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
    protected Context getBaseActivity() {
        return getActivity();
    }


    /**
     * 跳转进入我的页面
     */
    protected View.OnClickListener toMineOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getActivity(), MineActivity.class));
        }
    };

    //下来加载相关布局
    public int page_size = 10;
    public int page_number = 1;
    public AbsListView.OnScrollListener loadMoreListener;
    public boolean loadMore;
    public boolean loadStatus = false;
    public View footer;
    public ProgressBar loadingProgressBar;
    public TextView loadingText;
    //变更推送图标
    public abstract void receive_msg_data();


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check action just to be on the safe side.
            if (intent.getAction().equals(Preferences.ACTION_MSG_DATA)) {
                prefs.setHaveReceiveUnReadData(true);
                receive_msg_data();
            } else {
                // updateViewByBlueData();
            }
        }
    };

    void registerBroadcastReceiver(){
        IntentFilter filter = new IntentFilter(Preferences.ACTION_MSG_DATA);
        getActivity().registerReceiver(mReceiver, filter);
    }
    void unregisterBroadcastReceiver(){
        if(mReceiver!=null){
            getActivity().unregisterReceiver(mReceiver);
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        prefs = etcomm.com.etcommyolk.utils.GlobalSetting.getInstance(getBaseActivity());
        client = ApiClient.getInstance();
        /**
         * 下拉列表相关布局变量
         */
        footer = View.inflate(getActivity(), R.layout.loadmore, null);
        loadingProgressBar = (ProgressBar) footer.findViewById(R.id.progressBar);
        loadingText = (TextView) footer.findViewById(R.id.title);
        super.onCreate(savedInstanceState);
        registerBroadcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        initView(view, savedInstanceState);
        return view;
    }

    @Override
    public void onStart() {
        isActive = true;
        super.onStart();
    }

    @Override
    public void onStop() {
        isActive = false;
        super.onStop();
    }

    @Override
    public void onDestroy() {
        unregisterBroadcastReceiver();
        super.onDestroy();
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

    public ProgressDialog mProgress;

    public void showProgress(int resId, boolean cancel) {
        mProgress = new ProgressDialog(getActivity());
        if (resId <= 0) {
            mProgress.setMessage(R.string.loading_data, cancel);
        } else {
            mProgress.setMessage(resId, cancel);
        }
        mProgress.show();
    }

    public void cancelmDialog() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }
}
