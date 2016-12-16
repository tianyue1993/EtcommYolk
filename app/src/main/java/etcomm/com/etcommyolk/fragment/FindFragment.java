package etcomm.com.etcommyolk.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.activity.PointsExchangeActivity;
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
    //我的
    @Bind(R.id.base_left)
    ImageView baseLeft;
    @Bind(R.id.base_right)
    ImageView baseRight;

    private Context mContext;

    /**
     * onCreateView
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        baseLeft.setOnClickListener(toMineOnclickListener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_find;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick({R.id.exchange, R.id.group, R.id.tv_more_activity, R.id.tv_more_health, R.id.tv_more_walfe})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exchange:
                startActivity(new Intent(getBaseActivity(), PointsExchangeActivity.class));
                break;
            case R.id.group:
                break;
            case R.id.tv_more_activity:
                break;
            case R.id.tv_more_health:
                break;
            case R.id.tv_more_walfe:
                break;
        }
    }


}
