package etcomm.com.etcommyolk.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.widget.DialogFactory;

public class SettingActivity extends BaseActivity {
    @Bind(R.id.exit_btn)
    Button exitBtn;
    @Bind(R.id.setting_personal_rl)
    RelativeLayout settingPersonalRl;
    @Bind(R.id.setting_goal_rl)
    RelativeLayout settingGoalRl;
    @Bind(R.id.setting_changepassword_rl)
    RelativeLayout settingRelateworknumberRl;
    @Bind(R.id.setting_msgsetting_rl)
    RelativeLayout settingMsgsettingRl;
    @Bind(R.id.setting_aboutus_rl)
    RelativeLayout settingAboutusRl;
    private Dialog delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
    }

    //杂项
    private void initView() {
        EtcommApplication.addActivity(this);
        setTitleTextView("基本设置", null);
    }


    @OnClick({R.id.exit_btn, R.id.setting_personal_rl, R.id.setting_goal_rl, R.id.setting_changepassword_rl, R.id.setting_msgsetting_rl, R.id.setting_aboutus_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exit_btn:
                delete = DialogFactory.getDialogFactory().showCommonDialog(mContext, "确定要退出账号吗？", "取消", "确定", new View.OnClickListener() {
                    @SuppressWarnings("unused")
                    @Override
                    public void onClick(View v) {
                        delete.dismiss();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (delete != null && delete.isShowing()) {
                            delete.dismiss();
                            exitFromNet();

                        }
                    }
                }, mContext.getResources().getColor(R.color.black), mContext.getResources().getColor(R.color.black));
                break;
            case R.id.setting_personal_rl:
                //个人资料
                startActivity(new Intent(SettingActivity.this, SettingPersonalDataActivity.class));
                break;
            case R.id.setting_goal_rl:
                //目标设置
                startActivity(new Intent(SettingActivity.this, TargetActivity.class));
                break;
            case R.id.setting_changepassword_rl:
                //修改密码
                startActivity(new Intent(SettingActivity.this, ChangePasswordActivity.class));
                break;
            case R.id.setting_msgsetting_rl:
                //推送等信息设置
                startActivity(new Intent(SettingActivity.this, MsgSettingActivity.class));
                break;
            case R.id.setting_aboutus_rl:
                //关于我们
                startActivity(new Intent(SettingActivity.this, AboutUsActivity.class));
                break;
        }
    }

    /**
     * 退出登录
     */
    private void exitFromNet() {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        cancelmDialog();
        showProgress(0, true);
        client.toExit(this, params, new CommenHandler() {

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                cancelmDialog();
                Toast.makeText(mContext, commen.message, Toast.LENGTH_SHORT).show();
                exit();
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });

    }

    private void exit() {
        // 退出登陆清除用户相关数据
        prefs.clear();
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        EtcommApplication.finishActivity();
        finish();
    }
}
