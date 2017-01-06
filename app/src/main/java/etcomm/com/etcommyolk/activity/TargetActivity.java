package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.RequestParams;

import org.joda.time.DateTime;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.utils.StringUtils;
import etcomm.com.etcommyolk.widget.ProgressSeekBar;

public class TargetActivity extends BaseActivity {
    @Bind(R.id.nickname)
    TextView nickname;
    @Bind(R.id.goal_avator)
    SimpleDraweeView goal_avator;
    @Bind(R.id.goal_age_tv)
    TextView goal_age_tv;
    @Bind(R.id.goal_height_tv)
    TextView goal_height_tv;
    @Bind(R.id.goal_weight_tv)
    TextView goal_weight_tv;
    @Bind(R.id.progressbar)
    ProgressSeekBar progressbar;
    @Bind(R.id.btn_next)
    Button btn_next;
    private Intent intent;
    private boolean isFirstSetUserInfo;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        ButterKnife.bind(this);
        initDatas();
    }


    // progress 5000 20000 1000
    @OnClick(R.id.btn_next)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:

                if (isFirstSetUserInfo) {// 注册完成
                    RequestParams params = new RequestParams();
                    params.put("user_id", prefs.getUserId());
                    params.put("birthday", prefs.getBirthday());
                    params.put("avatar", prefs.getAvatar());
                    params.put("height", prefs.getHeight());
                    params.put("weight", prefs.getWeight());
                    params.put("nick_name", prefs.getNickName());
                    params.put("gender", prefs.getGender());// 1男，2女
                    params.put("job_number", prefs.getJobNumber());// 工号
                    params.put("real_name", prefs.getRealName());// 姓名
                    params.put("pedometer_target", String.valueOf(progressbar.getProgress() * 1000 + 1000));// 最高2w步
                    prefs.setPedometerTarget("" + (int) (progressbar.getProgress() * 1000 + 1000));
                    Log.i(tag, "params: " + params.toString());
                    cancelmDialog();
                    showProgress(0, true);
                    client.toRegisterUpdateInfo(this, params, new CommenHandler() {

                        @Override
                        public void onCancel() {
                            super.onCancel();
                            cancelmDialog();
                        }

                        @Override
                        public void onSuccess(Commen commen) {
                            super.onSuccess(commen);
                            cancelmDialog();
                            showToast(commen.message);
                            startActivity(new Intent(mContext, MainActivity.class));
                            //用户信息是否完整
                            prefs.saveInfoState(true);
                            finish();
                        }

                        @Override
                        public void onFailure(BaseException exception) {
                            super.onFailure(exception);
                            cancelmDialog();
                        }
                    });
                } else {
                    prefs.setPedometerTarget("" + (int) (progressbar.getProgress() * 1000 + 1000));
                    editUserInfo("pedometer_target", "" + (int) (progressbar.getProgress() * 1000 + 1000));
                }
                break;

            default:
                break;
        }
    }

    private void editUserInfo(String field, final String value) {
        RequestParams params = new RequestParams();
        params.put("field", field);
        params.put("value", value);
        params.put("access_token", prefs.getAccessToken());
        Log.i(tag, "params: " + params.toString());
        cancelmDialog();
        showProgress(0, true);
        client.toUserEdit(this, params, new CommenHandler() {

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                cancelmDialog();
                showToast(commen.message);
                finish();
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });
    }


    private void initDatas() {
        EtcommApplication.addActivity(this);
        isFirstSetUserInfo = prefs.getFirstsetuserinfo();

        if (isFirstSetUserInfo) {
            setTitleTextView("目标设置", null);
            btn_next.setText("完成");
            String height = prefs.getHeight();
            String weight = prefs.getWeight();
            String avator = prefs.getAvatar();
            String nick_name = prefs.getNickName();
            nickname.setText(nick_name);
            goal_height_tv.setText(height);
            goal_weight_tv.setText(weight);
            String[] strings = prefs.getBirthday().split("/");
            int a = Integer.valueOf(Calendar.getInstance().get(Calendar.YEAR)) - Integer.valueOf(strings[0]);
            goal_age_tv.setText(a + "");
            // 29岁以下，10000步
            // 30-39岁，8000步
            // 40-49岁，6000步
            // 50岁以上，5000步

            int curpro = 4;
            if (a <= 29) {
                curpro = 9;
            } else if (a >= 30 && a <= 39) {
                curpro = 7;
            } else if (a >= 40 && a <= 49) {
                curpro = 5;
            } else {
                curpro = 4;
            }
            progressbar.setProgress(curpro);
            goal_avator.setImageURI(avator);

        } else {
            setTitleTextView("目标设置", null);
            String avator = prefs.getAvatar();
            // String avator = login.getAvatar();

            if (!StringUtils.isEmpty(avator)) {
                goal_avator.setImageURI(avator);
            } else {
                avator = prefs.getAvatar();
                if (!StringUtils.isEmpty(avator)) {
                    goal_avator.setImageURI(avator);
                }
            }
            goal_height_tv.setText(prefs.getHeight() + "");
            goal_weight_tv.setText(prefs.getWeight() + "");
            nickname.setText(prefs.getNickName());
            String[] strings = prefs.getBirthday().split("/");
            int a = Integer.valueOf(Calendar.getInstance().get(Calendar.YEAR)) - Integer.valueOf(strings[0]);
            goal_age_tv.setText(a + "");
            int progress = (Integer.parseInt(prefs.getPedometerTarget())) / 1000 - 1;
            progressbar.setProgress(progress);
        }

    }

}
