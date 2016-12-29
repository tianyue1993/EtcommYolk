package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.adapter.MemberListAdapter;
import etcomm.com.etcommyolk.entity.TopicMember;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.TopicMemberHandler;

public class TopicMemberActivity extends BaseActivity {
    @Bind(R.id.attention)
    TextView attention;
    @Bind(R.id.memberpulllistview)
    ListView memberpulllistview;
    private MemberListAdapter mAttentionedAdapter;
    private List<TopicMember.TopicUser> UserList;
    protected static final String tag = TopicMemberActivity.class.getSimpleName();

    private String topic_id;
    private int attion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_member);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent != null) {
            topic_id = intent.getStringExtra("topic_id");
            attion = intent.getIntExtra("attion", 0);
        }

        setTitleTextView("小组成员（" + attion + "人）", null);
        getList();
    }


    public void getList() {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        params.put("topic_id", topic_id);
        Log.i(tag, "params: " + params.toString());
        cancelmDialog();
        showProgress(0, true);
        client.topicUser(mContext, params, new TopicMemberHandler() {
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
            public void onSuccess(TopicMember commen) {
                super.onSuccess(commen);
                cancelmDialog();
                UserList = commen.content;
                mAttentionedAdapter = new MemberListAdapter(mContext, UserList);
                memberpulllistview.setAdapter(mAttentionedAdapter);
                Log.i(tag, "UserList" + UserList.size() + UserList.toString());
            }
        });

    }
}
