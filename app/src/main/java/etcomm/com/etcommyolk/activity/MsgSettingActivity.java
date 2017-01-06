package etcomm.com.etcommyolk.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.widget.SwitchButton;

public class MsgSettingActivity extends BaseActivity {

    @Bind(R.id.msgsetting_msg_switch)
    SwitchButton msgsettingMsgSwitch;
    @Bind(R.id.msgsetting_msgdianzan_switch)
    SwitchButton msgsettingMsgdianzanSwitch;
    @Bind(R.id.msgsetting_msgpinglun_switch)
    SwitchButton msgsettingMsgpinglunSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_setting);
        ButterKnife.bind(this);

        initView();


    }

    private void initView() {
        EtcommApplication.addActivity(this);
        setTitleTextView("设置", null);

        if (prefs.getIsPushMsg()) {
            msgsettingMsgSwitch.setChecked(true);
        } else {
            msgsettingMsgSwitch.setChecked(false);
        }
        if (prefs.getIsPushMsg_Comment()) {
            msgsettingMsgpinglunSwitch.setChecked(true);
        } else {
            msgsettingMsgpinglunSwitch.setChecked(false);
        }
        if (prefs.getIsPushMsg_Like()) {
            msgsettingMsgdianzanSwitch.setChecked(true);
        } else {
            msgsettingMsgdianzanSwitch.setChecked(false);
        }
        msgsettingMsgSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                setPush("is_push", isChecked);
            }
        });
        msgsettingMsgdianzanSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                setPush("is_like", isChecked);
            }
        });
        msgsettingMsgpinglunSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                setPush("is_comment", isChecked);
            }
        });

    }




    private void setPush(final String type, final boolean status) {
        // TODO Auto-generated method stub
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        params.put("type", type);
        if (status) {
            params.put("status", "1");
        } else {
            params.put("status", "0");
        }
        cancelmDialog();
        showProgress(0, true);
        client.toSetPush(this, params, new CommenHandler() {

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            //返回Code非零时使用
            @Override
            public void onAllow(Commen commen) {
                super.onAllow(commen);
                cancelmDialog();
                Toast.makeText(mContext, commen.message, Toast.LENGTH_SHORT).show();
                if (type.equals("is_push")) {
                    msgsettingMsgSwitch.setChecked(!status);
                } else if (type.equals("is_like")) {
                    msgsettingMsgdianzanSwitch.setChecked(!status);
                } else if (type.equals("is_comment")) {
                    msgsettingMsgpinglunSwitch.setChecked(!status);
                }

            }

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                cancelmDialog();
                if (type.equals("is_push")) {
                    prefs.setIsPushMsg(status);
                    if (status) {
                        msgsettingMsgSwitch.setChecked(true);
                        msgsettingMsgdianzanSwitch.setChecked(true);
                        msgsettingMsgpinglunSwitch.setChecked(true);
                        msgsettingMsgdianzanSwitch.setEnabled(true);
                        msgsettingMsgpinglunSwitch.setEnabled(true);
                    } else {
                        msgsettingMsgSwitch.setChecked(false);
                        msgsettingMsgdianzanSwitch.setChecked(false);
                        msgsettingMsgpinglunSwitch.setChecked(false);
                        msgsettingMsgdianzanSwitch.setEnabled(false);
                        msgsettingMsgpinglunSwitch.setEnabled(false);
                    }
                } else if (type.equals("is_like")) {
                    prefs.setIsPushMsg_Like(status);
                    if (status) {
                        msgsettingMsgdianzanSwitch.setChecked(true);
                    } else {
                        msgsettingMsgdianzanSwitch.setChecked(false);
                    }
                } else if (type.equals("is_comment")) {
                    prefs.setIsPushMsg_Comment(status);
                    if (status) {
                        msgsettingMsgpinglunSwitch.setChecked(true);
                    } else {
                        msgsettingMsgpinglunSwitch.setChecked(false);
                    }
                }

            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });
    }
}
