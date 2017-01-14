package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.utils.Preferences;

public class ChangeNickNameActivity extends BaseActivity {
    @Bind(R.id.nickname_et)
    EditText nickname_et;
    @Bind(R.id.name_clear)
    Button nameClear;
    private Intent intent;
    private String nickname;
    String message = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nick_name);
        ButterKnife.bind(this);

        initView();

    }

    //杂项
    private void initView() {
        EtcommApplication.addActivity(this);
        getRightTextView().setText("保存");
        getRightTextView().setVisibility(View.VISIBLE);
        nameClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickname_et.setText("");
            }
        });
        intent = getIntent();
        nickname = intent.getStringExtra("name");
        setTitleTextView(intent.getStringExtra("type"), null);
        nickname_et.setText(nickname);
        nickname_et.requestFocus();
        if (intent.getStringExtra("type").contains("昵称")) {
            nickname_et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        } else {
            nickname_et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        }
        getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(nickname_et.getText().toString())) {
                    showToast("请填写要修改的信息");
                    return;
                }
                if (nickname_et.getText().toString().trim().length() < 2) {
                    showToast(R.string.nick_name_error);
                    return;
                }
                if (intent.getStringExtra("type").contains("昵称")) {
                    editUserInfo("nick_name", nickname_et.getText().toString());
                } else {
                    editUserInfo("real_name", nickname_et.getText().toString());
                }

            }
        });


    }


    private void editUserInfo(final String field, final String value) {
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
                if (intent.getStringExtra("type").contains("昵称")) {
                    prefs.setNickName(value);
                } else {
                    prefs.setRealName(value);
                }
                backWithData(Preferences.SelectNickName, nickname_et.getText().toString());
                finish();
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });
    }


}
