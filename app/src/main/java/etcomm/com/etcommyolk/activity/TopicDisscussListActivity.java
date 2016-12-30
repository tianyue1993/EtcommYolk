package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.adapter.CircleAdapter;
import etcomm.com.etcommyolk.entity.Discussion;
import etcomm.com.etcommyolk.entity.DisscussItems;
import etcomm.com.etcommyolk.entity.GroupItems;
import etcomm.com.etcommyolk.entity.Topic;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.handler.DicussionHandler;
import etcomm.com.etcommyolk.widget.DownPullRefreshListView;
import etcomm.com.etcommyolk.widget.HorizontalListView;

public class TopicDisscussListActivity extends BaseActivity {
    GroupItems item;
    @Bind(R.id.topic_image)
    SimpleDraweeView topic_image;
    @Bind(R.id.topic_discuss)
    EditText topic_discuss;
    @Bind(R.id.attion_image)
    HorizontalListView image_list;
    @Bind(R.id.attion_count)
    TextView attion_count;
    @Bind(R.id.depart_rank)
    ImageView depart_rank;
    @Bind(R.id.attention_member)
    RelativeLayout attention_member;
    @Bind(R.id.pulllistview)
    DownPullRefreshListView listView;
    @Bind(R.id.root)
    RelativeLayout root;
    @Bind(R.id.emptyview)
    View emptyview;


    public static final int PIC = 1;
    /* 头像文件 */
    private ArrayList<DisscussItems> list = new ArrayList<DisscussItems>();
    private ArrayList<DisscussItems> adaptList = new ArrayList<DisscussItems>();
    private List<Topic.TopicUser> image;
    private CircleAdapter circleAdapter;
    boolean isAttentioned;
    String topic_id;
    String user_id = "";
    String discuse;
    String activity_rank;
    String topic_avator;
    private int attion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_disscuss_list);
        ButterKnife.bind(this);
        EtcommApplication.addActivity(this);
        if (getIntent() != null) {
            item = (GroupItems) getIntent().getSerializableExtra("GroupItems");
            setTitleTextView(item.name, null);
        }
        initData();


    }

    public void initData() {
        topic_image.setImageURI(item.avatar);
        topic_discuss.setText(item.desc);
        attion_count.setText(item.follows);

        /**
         *点击活动小组的活动图标，进入活动排名页面
         **/
        depart_rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActivityRanktActivity.class);
                intent.putExtra("activity_rank", item.activity_rank);
                startActivity(intent);
            }
        });

        setRightImage(R.mipmap.ic_title_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        getList();
    }


    /**
     * 用户自己创建的小组，小组头像和简介点击可修改，修改接口调用
     */

    private void editUserInfo(final String field, final String value) {
        RequestParams params = new RequestParams();
        params.put("field", field);
        params.put("topic_id", topic_id);
        params.put("access_token", prefs.getAccessToken());
        cancelmDialog();
        showProgress(0, true);
        if (field.equals("avatar")) {
            Log.e(tag, "startUpload PIC File : " + value);
            if (new File(value).isFile()) {
                try {
                    params.put("value", new File(value));
                } catch (FileNotFoundException e1) {
                    Log.e(tag, "文件没有找到");
                    e1.printStackTrace();
                    return;
                }
            } else {
                cancelmDialog();
                Toast.makeText(mContext, "查找文件出错，上传头像失败", Toast.LENGTH_SHORT).show();
            }

        } else {
            params.put("value", value);
        }

        client.topicUpdate(mContext, params, new CommenHandler() {
        });
    }

    /**
     * 获取用户小组下面所有信息
     */
    public void getList() {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        params.put("page_size", String.valueOf(page_size));
        params.put("topic_id", item.topic_id);
        params.put("page", String.valueOf(page_number++));
        Log.i(tag, "params: " + params.toString());
        cancelmDialog();
        showProgress(0, true);
        client.GetDiscussion(mContext, params, new DicussionHandler() {
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
            public void onSuccess(Discussion discussion) {
                super.onSuccess(discussion);
                list = discussion.content.items;
                List<Topic.TopicUser> list1 = discussion.content.topic.user;
                image = list1;
                circleAdapter = new CircleAdapter(mContext, image);
                image_list.setAdapter(circleAdapter);
                image = discussion.content.topic.user;
                Log.d(tag, "image" + image.toString());
                topic_discuss.setText(discussion.content.topic.desc + "");
                if (discussion.content.topic.is_rank.equals("0")) {
                    depart_rank.setVisibility(View.GONE);
                } else {
                    depart_rank.setVisibility(View.VISIBLE);
                }
                activity_rank = discussion.content.topic.activity_rank;
                discuse = discussion.content.topic.desc + "";
                attion = Integer.parseInt(discussion.content.topic.user_number);
                user_id = discussion.content.topic.user_id;
                if (discussion.content.topic.is_followed.equals("0")) {
                    isAttentioned = false;
                } else {
                    isAttentioned = true;
                }
                /**如果是自己创建的小组，可修改头像，可点击修改相关信息
                 * * */
                if (item.user_id.equals(prefs.getUserId())) {
                    topic_discuss.setEnabled(true);
                    topic_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, SelectPicPopupWindowActivity.class);
                            startActivityForResult(intent, PIC);
                        }
                    });


                    topic_discuss.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            topic_discuss.setText("");
                            topic_discuss.setSelection(topic_discuss.getText().toString().length());
                            topic_discuss.setCursorVisible(true);
                            topic_discuss.requestFocus();
                        }
                    });
                    /**
                     * 击完成按钮，修改小组描述信息
                     */
                    topic_discuss.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                                if (topic_discuss.getText().toString() != null) {
                                    editUserInfo("description ", topic_discuss.getText().toString());
                                }
                                return true;

                            }
                            return false;
                        }
                    });

                }

                attion_count.setText(attion + "个成员   >");
                topic_avator = discussion.content.topic.avatar;
                if (!discussion.content.topic.avatar.isEmpty()) {
                    topic_image.setImageURI(topic_avator);
                }
                if (discussion.content.pages == 0) {
                    emptyview.setVisibility(View.VISIBLE);
                } else {
                    emptyview.setVisibility(View.INVISIBLE);
                }
                if (list != null && list.size() > 0) {
                    if (listView.getFooterViewsCount() == 0 && discussion.content.pages > 1) {
                        listView.addFooterView(footer);
//                        listView.setAdapter(mAdapter);
                    }
                    adaptList.addAll(list);
                } else {
                    showToast("暂无更多内容啦");
                    if (listView.getFooterViewsCount() > 0) {
                        listView.removeFooterView(footer);
                    }
                }
            }
        });
    }
}
