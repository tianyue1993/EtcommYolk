package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;

/**
 * zuoh
 */
public class MineActivity extends BaseActivity {
    //用户头像
    @Bind(R.id.user_photo)
    SimpleDraweeView userPhoto;
    //用户昵称
    @Bind(R.id.user_name)
    TextView userName;
    //用户积分
    @Bind(R.id.mine_signin_tv)
    TextView mineSigninTv;
    //公里数
    @Bind(R.id.total_tv_mileage)
    TextView totalTvMileage;
    //小时
    @Bind(R.id.total_tv_motiontimes)
    TextView totalTvMotiontimes;
    //卡路里
    @Bind(R.id.total_iv_caliries)
    TextView totalIvCaliries;
    //基本设置
    @Bind(R.id.mine_minesetting_rl)
    RelativeLayout mineMinesettingRl;
    //我的账号
    @Bind(R.id.mine_minecount_rl)
    RelativeLayout mineMinecountRl;
    //我的计步
    @Bind(R.id.mine_minedevice_rl)
    RelativeLayout mineMinedeviceRl;
    //我的收藏
    @Bind(R.id.mine_minecollect_rl)
    RelativeLayout mineMinecollectRl;
    //我的活动
    @Bind(R.id.mine_minesport_rl)
    RelativeLayout mineMinesportRl;
    //意见反馈
    @Bind(R.id.mine_minefeedback_rl)
    RelativeLayout mineMinefeedbackRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        ButterKnife.bind(this);
        initView();
    }

    //杂项
    private void initView() {
        setTitleTextView("我的", null);
        EtcommApplication.addActivity(this);
    }

    /**
     * 相关赋值
     */
    @Override
    public void onResume() {
        super.onResume();
        userPhoto.setImageURI(prefs.getAvatar());
        userName.setText(prefs.getNickName());
        totalTvMileage.setText(prefs.getPedometerDistance());
        totalTvMotiontimes.setText(prefs.getPedometerTime());
        totalIvCaliries.setText(prefs.getPedometerConsume());
        mineSigninTv.setText("可用积分 : " + prefs.getScore());


    }

    @OnClick({R.id.mine_signin_tv, R.id.mine_minesetting_rl, R.id.mine_minecount_rl, R.id.mine_minecollect_rl, R.id.mine_minesport_rl, R.id.mine_minefeedback_rl, R.id.mine_minedevice_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mine_signin_tv:
                //积分
                startActivity(new Intent(MineActivity.this, MyPointsDetailActivity.class));
                break;
            case R.id.mine_minesetting_rl:
                //设置
                startActivity(new Intent(MineActivity.this, SettingActivity.class));
                break;
            case R.id.mine_minecount_rl:
                //账号
                startActivity(new Intent(MineActivity.this, MyAccountActivity.class));
                break;
            case R.id.mine_minecollect_rl:
                //收藏
                showToast("收藏");
                break;
            case R.id.mine_minesport_rl:
                //活动
                showToast("活动");
                break;
            case R.id.mine_minefeedback_rl:
                //意见反馈
                startActivity(new Intent(MineActivity.this, MineFeedBackActivity.class));
                break;
            case R.id.mine_minedevice_rl:
                //我的计步
                showToast("花香氤");
                break;
        }
    }
}
