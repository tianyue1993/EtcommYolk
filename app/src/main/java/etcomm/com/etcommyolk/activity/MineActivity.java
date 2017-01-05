package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Login;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.LoginHandler;

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

        RequestParams params = new RequestParams();

        client.toUserProfile(this, params, new LoginHandler(){

            @Override
            public void onSuccess(Login login) {
                super.onSuccess(login);
                prefs.setUserId(login.content.user_id);
                prefs.setDepartmentId(login.content.department_id);
                prefs.setCustomerId(login.content.customer_id);
                prefs.setSerialNumberId(login.content.serial_number_id);
                prefs.setNickName(login.content.nick_name);
                prefs.setRealName(login.content.real_name);
                prefs.setAccessToken(login.content.access_token);
                prefs.setBirthday(login.content.birthday);
                prefs.setBirthYear(login.content.birth_year);
                prefs.setHeight(login.content.height);
                prefs.setGender(login.content.gender);
                prefs.setWeight(login.content.weight);
                prefs.setAvatar(login.content.avatar);
                prefs.setMobile(login.content.mobile);
                prefs.setJobNumber(login.content.job_number);
                prefs.setScore(login.content.score);
                prefs.setTotalScore(login.content.total_score);
                prefs.setTotalscore(login.content.total_score);
                prefs.setPedometerTarget(login.content.pedometer_target);
                prefs.setPedometerDistance(login.content.pedometer_distance);
                prefs.setPedometerTime(login.content.pedometer_time);
                prefs.setPedometerConsume(login.content.pedometer_consume);
                prefs.setIsLeader(login.content.is_leader);
                prefs.setCreatedAt(login.content.created_at);
                prefs.setIsSign(login.content.is_sign);
                prefs.setCustomerImage(login.content.customer_image);
                prefs.setInfoStatus(login.content.info_status);
                prefs.setIsLike(login.content.is_like);
                prefs.setIsComment(login.content.is_comment);
                prefs.setIsComment(login.content.islevel);
                //
                userPhoto.setImageURI(login.content.avatar);
                userName.setText(login.content.nick_name);
                totalTvMileage.setText(login.content.pedometer_distance);
                totalTvMotiontimes.setText(login.content.pedometer_time);
                totalIvCaliries.setText(login.content.pedometer_consume);
                mineSigninTv.setText("可用积分 : " + login.content.score);
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
            }
        });

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
                startActivity(new Intent(MineActivity.this, MyCollectionActivity.class));
                break;
            case R.id.mine_minesport_rl:
                //活动
                startActivity(new Intent(MineActivity.this, MineSportsActivity.class));
                break;
            case R.id.mine_minefeedback_rl:
                //意见反馈
                startActivity(new Intent(MineActivity.this, MineFeedBackActivity.class));
                break;
            case R.id.mine_minedevice_rl:
                //我的计步
                startActivity(new Intent(MineActivity.this, PedometerActivity.class));
                break;
        }
    }
}
