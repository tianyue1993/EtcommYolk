package etcomm.com.etcommyolk.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;

public class TopicDisscussReportActivity extends BaseActivity {

    @Bind(R.id.gview)
    GridView gview;
    @Bind(R.id.report_tv)
    EditText report_tv;
    @Bind(R.id.contact_tv)
    TextView contact_tv;
    @Bind(R.id.commit)
    Button commit;
    //举报页面
    private String topic_id;
    private String discussion_id;
    StringBuffer type = new StringBuffer("");
    String report_type;
    String url;
    String comment_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_disscuss_report);
        ButterKnife.bind(this);
        setTitleTextView("举报", null);
        initDatas();
    }


    protected void initDatas() {
        Intent intent = getIntent();
        report_type = intent.getStringExtra("type");
        if (intent != null) {
            topic_id = intent.getStringExtra("topic_id");
            discussion_id = intent.getStringExtra("discussion_id");
            comment_id = intent.getStringExtra("comment_id");
        }

        report_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int i = 100 - s.length();
                contact_tv.setText("您还可以输入" + i + "个字");
            }
        });

        gview.setAdapter(new ReportGridviewAdapterprivate(mContext));

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.toString() != "") {
                    Report();
                } else {
                    showToast("请选择举报类型");
                }

            }
        });
    }


    private void Report() {
        String Type = type.toString();//去掉最后一个分割逗号
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        params.put("content", report_tv.getText().toString());
        params.put("type", Type.substring(0, Type.length() - 1));
        cancelmDialog();
        showProgress(0, true);
        if (report_type.contains("discussion")) {
            //举报帖子
            params.put("id", discussion_id);
            params.put("report_type", "Discussion");
        } else if (report_type.contains("topic")) {
            //举报小组
            params.put("id", topic_id);
            params.put("report_type", "Topic");
        } else if (report_type.contains("conment")) {
            //举报评论
            params.put("id", comment_id);
            params.put("report_type", "DiscussionComment");
        }
        Log.e(tag, "  params: " + params.toString());
        client.Report(mContext, params, new CommenHandler() {
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

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }
        });

    }

    /**
     * Created by ${tianyue} on 2016/10/10.
     * 适配器
     */
    class ReportGridviewAdapterprivate extends BaseAdapter {
        private String[] imageId;

        public ReportGridviewAdapterprivate(Context context) {
            imageId = new String[]{"广告类", "恐怖类", "赌博类", "诈骗类", "政治有害类", "淫秽色情类", "损害他人类型", "其他类"}; // 定义并初始化的数组

        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final CheckBox imageview;
            if (convertView == null) {
                imageview = new CheckBox(mContext); // 实例化的对象
                imageview.setPadding(20, 0, 20, 0); // 设置内边距
                imageview.setButtonDrawable(R.mipmap.unchecked);
                imageview.setTextColor(getResources().getColor(R.color.grey));
                imageview.setTextSize(16);

            } else {
                imageview = (CheckBox) convertView;
            }

            imageview.setText(imageId[position]); // 为ImageView设置要显示的wenben
            imageview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        type.append(position + 1 + ",");
                        imageview.setButtonDrawable(R.mipmap.checked);
                    } else {
                        String string = type.toString().replace(position + 1 + ",", "");
                        type = new StringBuffer(string);
                        imageview.setButtonDrawable(R.mipmap.unchecked);
                    }


                }
            });
            return imageview;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return imageId.length;
        }
    }

}
