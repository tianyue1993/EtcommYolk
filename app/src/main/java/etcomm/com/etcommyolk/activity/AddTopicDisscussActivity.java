package etcomm.com.etcommyolk.activity;

import android.os.Bundle;
import android.view.View;

import etcomm.com.etcommyolk.R;

public class AddTopicDisscussActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_topic_disscuss);
        setTitleTextView("发帖", null);
        setRightText("发布", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("发帖");
            }
        });
    }
}
