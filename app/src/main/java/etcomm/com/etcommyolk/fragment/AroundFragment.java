package etcomm.com.etcommyolk.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.ApiClient;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.activity.AddNewTopicActivity;
import etcomm.com.etcommyolk.activity.SearchGroupActivity;
import etcomm.com.etcommyolk.activity.TopicDisscussListActivity;
import etcomm.com.etcommyolk.adapter.MyGroupListAdapter;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.entity.GroupItems;
import etcomm.com.etcommyolk.entity.GroupList;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.handler.GroupListHandler;
import etcomm.com.etcommyolk.utils.GlobalSetting;

public class AroundFragment extends BaseFragment {
    /**
     * 控制OnResume()执行次数
     */
    public static boolean limitOnresumSide;
    public static String TAG = "AroundFragment";
    @Bind(R.id.base_left)
    ImageView baseLeft;
    @Bind(R.id.add_topic)
    ImageView addTopic;
    @Bind(R.id.topic_search)
    ImageView topicSearch;
    @Bind(R.id.base_right)
    ImageView baseRight;
    @Bind(R.id.listview)
    SlideAndDragListView listView;
    @Bind(R.id.base_title)
    TextView baseTitle;
    @Bind(R.id.title)
    RelativeLayout title;
    @Bind(R.id.empty)
    RelativeLayout empty;
    Context mContext;
    @Bind(R.id.to_see)
    TextView to_see;
    private ArrayList<GroupItems> adaptList = new ArrayList<>();
    protected ArrayList<GroupItems> list = new ArrayList<GroupItems>();
    private MyGroupListAdapter mAdapter;
    //关注成功，刷新身边页面————添加关注小组到我的小组列表
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("add")) {
                if (intent.getExtras() != null) {
                    GroupItems items = (GroupItems) intent.getExtras().getSerializable("item");
                    adaptList.add(items);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    /**
     * onCreateView
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    protected void initView(View view, Bundle savedInstanceState) {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_side;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        IntentFilter filter = new IntentFilter();
        filter.addAction("add");
        mContext.registerReceiver(receiver, filter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        limitOnresumSide = true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        getList();
        mAdapter = new MyGroupListAdapter(mContext, adaptList, new MyGroupListAdapter.mAttentioned() {
            @Override
            public void onAttentioned(GroupItems item) {
                item.is_followed = "1";
            }
        });
        //设置侧滑菜单按钮
        Menu menu = new Menu(new ColorDrawable(Color.LTGRAY), true, 0);// the
        menu.addItem(new MenuItem.Builder().setWidth(200)// set Width
                //设置背景
                .setBackground(new ColorDrawable(Color.LTGRAY))// set background
                .setText("取消关注")// set text string
                .setDirection(MenuItem.DIRECTION_RIGHT).setTextColor(Color.WHITE)// set
                .setTextSize(10)// set text size
                .build());
        listView.setMenu(menu);
        listView.setDividerHeight(0);
        listView.setAdapter(mAdapter);

        listView.setOnMenuItemClickListener(new SlideAndDragListView.OnMenuItemClickListener() {
            @Override
            public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
                switch (direction) {
                    case MenuItem.DIRECTION_LEFT:
                        switch (buttonPosition) {
                            case 0:// One
                                return Menu.ITEM_SCROLL_BACK;
                        }
                        break;
                    case MenuItem.DIRECTION_RIGHT:
                        switch (buttonPosition) {
                            case 0:// icon
                                showToast("取消关注");
                                unAttention(mAdapter.getItem(itemPosition));
                                return Menu.ITEM_NOTHING;
                        }
                        break;
                    default:
                        return Menu.ITEM_NOTHING;
                }
                return Menu.ITEM_NOTHING;
            }
        });

        listView.setOnListItemClickListener(new SlideAndDragListView.OnListItemClickListener() {
            @Override
            public void onListItemClick(View view, int i) {
                GroupItems m = mAdapter.getItem(i);
                Intent intent = new Intent(mContext, TopicDisscussListActivity.class);
                intent.putExtra("GroupItems",m);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @OnClick({R.id.base_left, R.id.add_topic, R.id.topic_search, R.id.base_right, R.id.to_see})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.base_left:
                break;
            case R.id.add_topic:
                startActivity(new Intent(mContext, AddNewTopicActivity.class));
                break;
            case R.id.topic_search:
                startActivity(new Intent(mContext, SearchGroupActivity.class));
                break;
            case R.id.base_right:
                break;
            case R.id.to_see:
                startActivity(new Intent(mContext, SearchGroupActivity.class));
                break;
        }
    }

    /**
     * 获取用户下面所有的小组，包括已关注和未关注
     */
    public void getList() {
        RequestParams params = new RequestParams();
        params.put("access_token", GlobalSetting.getInstance(mContext).getAccessToken());
        params.put("page", (page_number++) + "");
        params.put("page_size", 1000 + "");
        params.put("type", 1 + "");//2为搜索全部，1为已关注
        Log.d("", "getList: " + params.toString());
        cancelmDialog();
        showProgress(0, true);
        ApiClient.getInstance().GetGroupList(mContext, params, new GroupListHandler() {
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
            public void onSuccess(GroupList groupList) {
                super.onSuccess(groupList);
                cancelmDialog();
                list = groupList.content.items;
                if (list.size() > 0) {
                    empty.setVisibility(View.GONE);
                    for (Iterator<GroupItems> iterator = list.iterator(); iterator.hasNext(); ) {
                        GroupItems disscussCommentItems = iterator.next();
                        adaptList.add(disscussCommentItems);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    showToast("已无更多内容");
                    if (listView.getFooterViewsCount() > 0) {
                        listView.removeFooterView(footer);
                    }
                    empty.setVisibility(View.VISIBLE);

                }
            }
        });
    }


    /**
     * 取消关注
     *
     * @param mInfo
     * @return
     */
    protected int unAttention(final GroupItems mInfo) {
        RequestParams params = new RequestParams();
        params.put("access_token", GlobalSetting.getInstance(mContext).getAccessToken());
        params.put("topic_id", mInfo.topic_id);
        cancelmDialog();
        showProgress(0, true);
        ApiClient.getInstance().UnAttention(mContext, params, new CommenHandler() {
            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                cancelmDialog();
                Toast.makeText(mContext, "取消关注", Toast.LENGTH_SHORT).show();
                adaptList.remove(mInfo);
                mAdapter.notifyDataSetChanged();
                if (adaptList.size() < 1) {
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });
        return 0;
    }

}
