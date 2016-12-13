package etcomm.com.etcommyolk.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import etcomm.com.etcommyolk.R;

public class SportFragment extends BaseFragment {
    /**
     * 控制OnResume()执行次数
     */
    public static boolean limitOnresumeSports;

    /**
     * onCreateView
     * @param view
     * @param savedInstanceState
     */
    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sports;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
