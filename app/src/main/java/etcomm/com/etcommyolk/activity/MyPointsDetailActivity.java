package etcomm.com.etcommyolk.activity;

import android.os.Bundle;

import etcomm.com.etcommyolk.R;

public class MyPointsDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_points_detail);
        setTitleTextView("我的积分", null);
    }
}
