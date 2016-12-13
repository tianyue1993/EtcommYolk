package etcomm.com.etcommyolk.activity;

import android.os.Bundle;

import etcomm.com.etcommyolk.R;

public class MyExchangeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_exchange);
        setTitleTextView("我的兑换", null);
    }
}
