package etcomm.com.etcommyolk.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.ApiClient;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.utils.GlobalSetting;
import etcomm.com.etcommyolk.utils.StringUtils;
import etcomm.com.etcommyolk.widget.ExEditText;

public class AddNewTopicActivity extends BaseActivity {

    @Bind(R.id.tv_username)
    TextView tvUsername;
    @Bind(R.id.topic_name)
    ExEditText topic_name;
    @Bind(R.id.iv_del_name)
    ImageView ivDelName;
    @Bind(R.id.topic_discuss)
    ExEditText topic_discuss;
    @Bind(R.id.iv_del_discuss)
    ImageView ivDelDiscuss;
    @Bind(R.id.li)
    LinearLayout li;
    @Bind(R.id.newtopic_sucess_tv)
    TextView newtopic_sucess_tv;
    @Bind(R.id.newtopic_already_tv)
    TextView newtopic_already_tv;
    @Bind(R.id.btn_next)
    Button btn_next;

    String comment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_topic);
        ButterKnife.bind(this);
        EtcommApplication.addActivity(this);
        setTitleTextView("创建小组", null);
        checkCreate();
    }

    @OnClick({R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                if (btn_next.getText().toString().equals("提交审核")) {

                } else if (btn_next.getText().toString().equals("返回身边")) {
                    finish();
                    return;
                }
                comment = topic_name.getText().toString();
                if (StringUtils.isEmpty(comment) || comment.length() < 1) {
                    Toast.makeText(mContext, "" +
                            "话题字数太少", Toast.LENGTH_SHORT).show();
                    return;
                }
                TopicCreate();
                break;
        }
    }

    /**
     * 检测是否可以创建小组，显示布局
     */
    public void checkCreate() {
        RequestParams params = new RequestParams();
        params.put("access_token", GlobalSetting.getInstance(mContext).getAccessToken());
        ApiClient.getInstance().CheckCreate(mContext, params, new CommenHandler() {
            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
            }

            @Override
            public void onCancel() {
                super.onCancel();
            }

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                if (commen.code == 30005) {
                    //一周内只允许发布一次小组
                    newtopic_already_tv.setVisibility(View.VISIBLE);
                    newtopic_sucess_tv.setVisibility(View.INVISIBLE);
                    li.setVisibility(View.INVISIBLE);
                    btn_next.setEnabled(true);
                    btn_next.setVisibility(View.GONE);
//                    showToast(commen.message);
                } else {
//                    可以发布
                    li.setVisibility(View.VISIBLE);
                    btn_next.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    public void TopicCreate() {
        RequestParams params = new RequestParams();
        params.put("access_token", GlobalSetting.getInstance(mContext).getAccessToken());
        params.put("topic", comment);
        params.put("description", topic_discuss.getText().toString());
        cancelmDialog();
        showProgress(0, true);
        Log.i(tag, "params: " + params.toString());
        client.TopicCreate(mContext, params, new CommenHandler() {
            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                cancelmDialog();
                btn_next.setText("返回身边");
                btn_next.setEnabled(true);
                newtopic_sucess_tv.setVisibility(View.VISIBLE);
                li.setVisibility(View.INVISIBLE);
            }
        });

    }
}
