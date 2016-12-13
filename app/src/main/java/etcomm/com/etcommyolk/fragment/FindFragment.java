package etcomm.com.etcommyolk.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.widget.DownPullRefreshListView;
import etcomm.com.etcommyolk.widget.SlideADView;

public class FindFragment extends BaseFragment {
    /**
     * 控制OnResume()执行次数
     */
    public static boolean limitResumeFind;
    @Bind(R.id.slideADView)
    SlideADView slideADView;
    @Bind(R.id.exchange)
    ImageView exchange;
    @Bind(R.id.group)
    ImageView group;
    @Bind(R.id.tv_more_activity)
    TextView tvMoreActivity;
    @Bind(R.id.header1)
    RelativeLayout header1;
    @Bind(R.id.suggest_activity_topic)
    TextView suggestActivityTopic;
    @Bind(R.id.participation)
    TextView participation;
    @Bind(R.id.daterange)
    TextView daterange;
    @Bind(R.id.activity_status)
    TextView activityStatus;
    @Bind(R.id.tv_more_health)
    TextView tvMoreHealth;
    @Bind(R.id.header2)
    RelativeLayout header2;
    @Bind(R.id.healthtopic)
    TextView healthtopic;
    @Bind(R.id.readingamount)
    TextView readingamount;
    @Bind(R.id.tv_more_walfe)
    TextView tvMoreWalfe;
    @Bind(R.id.header3)
    RelativeLayout header3;
    @Bind(R.id.welfaretopic)
    TextView welfaretopic;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.listview)
    DownPullRefreshListView listview;
    @Bind(R.id.activity_image)
    ImageView activityImage;
    @Bind(R.id.health_image)
    ImageView healthImage;
    @Bind(R.id.welfare_image)
    ImageView welfareImage;

    /**
     * onCreateView
     * @param view
     * @param savedInstanceState
     */
    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_find;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
