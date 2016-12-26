package etcomm.com.etcommyolk.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;

/**
 * 意见反馈
 */
public class MineFeedBackActivity extends BaseActivity {

    @Bind(R.id.btn1)
    RadioButton btn1;
    @Bind(R.id.btn2)
    RadioButton btn2;
    @Bind(R.id.feedback_tv)
    EditText feedbackTv;
    @Bind(R.id.contact_tv)
    TextView contactTv;
    @Bind(R.id.feedback_contact_tv)
    EditText feedbackContactTv;
    @Bind(R.id.contact_info_tv)
    TextView contactInfoTv;
    @Bind(R.id.feedback_commit)
    Button feedbackCommit;
    protected String device_info;
    private String type = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_feed_back);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {

        EtcommApplication.addActivity(this);
        setTitleTextView("意见反馈", null);

        device_info = Build.MODEL + "_"
                + Build.VERSION.SDK + "_"
                + Build.VERSION.RELEASE;
        Log.i(tag, "device_info:" + device_info);
        btn1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    type = "0";
                } else {
                    type = "1";
                }
            }
        });
        feedbackTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int i = 200 - s.length();
                contactTv.setText("您还可以输入" + i + "个字");
            }
        });

        feedbackTv.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (feedbackTv.getText().length() >= 1) {
                    feedbackCommit.setClickable(true);
                    feedbackCommit.setEnabled(true);
                } else {
                    feedbackCommit.setClickable(false);
                    feedbackCommit.setEnabled(false);
                }
            }
        });

        feedbackCommit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (feedbackTv.getText().toString().length() < 1) {
                    Toast.makeText(mContext, "请输入您要吐槽的内容!", Toast.LENGTH_SHORT).show();
                    return;
                }
                RequestParams params = new RequestParams();
                params.put("access_token", prefs.getAccessToken());
                params.put("contact", feedbackContactTv.getText().toString());
                params.put("content", feedbackTv.getText().toString());
                params.put("device_info", device_info);
                params.put("type", type);
                client.toFeedBack(MineFeedBackActivity.this, params, new CommenHandler() {

                    @Override
                    public void onSuccess(Commen commen) {
                        super.onSuccess(commen);
                        Toast.makeText(mContext, "提交成功！", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(BaseException exception) {
                        super.onFailure(exception);
                    }
                });
            }
        });

    }
}
