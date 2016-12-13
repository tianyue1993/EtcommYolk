package etcomm.com.etcommyolk.fragment;

import android.os.Bundle;
import android.view.View;

import etcomm.com.etcommyolk.R;

public class FindFragment extends BaseFragment {
    /**
     * 控制OnResume()执行次数
     */
    public static boolean limitResumeFind;

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
        return R.layout.fragment_find;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
