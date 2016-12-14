package etcomm.com.etcommyolk.activity;

import android.os.Bundle;
import android.view.View;

import etcomm.com.etcommyolk.R;

public class MyPointsDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_points_detail);
        setTitleTextView("我的积分", null);
        setRightTextView(R.mipmap.how_point_to, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("积分规则");
            }
        });
    }
}
