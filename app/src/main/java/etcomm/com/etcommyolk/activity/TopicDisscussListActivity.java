package etcomm.com.etcommyolk.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.ApiClient;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.adapter.CircleAdapter;
import etcomm.com.etcommyolk.adapter.TopicDisscussListAdapter;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.entity.Discussion;
import etcomm.com.etcommyolk.entity.DisscussItems;
import etcomm.com.etcommyolk.entity.Topic;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.handler.DicussionHandler;
import etcomm.com.etcommyolk.utils.DcareUtils;
import etcomm.com.etcommyolk.utils.GetPathFromUri4kitkat;
import etcomm.com.etcommyolk.utils.GlobalSetting;
import etcomm.com.etcommyolk.utils.Preferences;
import etcomm.com.etcommyolk.utils.StringUtils;
import etcomm.com.etcommyolk.widget.DialogFactory;
import etcomm.com.etcommyolk.widget.DownPullRefreshListView;
import etcomm.com.etcommyolk.widget.HorizontalListView;

public class TopicDisscussListActivity extends BaseActivity {

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
    RelativeLayout _root;
    @Bind(R.id.emptyview)
    View emptyview;
    @Bind(R.id.if_join)
    TextView if_join;


    public static final int PIC = 1;
    private static final int TAKE_PHOTO = 10;
    public static final int PICK_PHOTO_FROM_MEIZU = 16;
    public static final int Crop_PICK_PHOTO_FROM_MEIZU = 17;
    private static final int CAMERA_PIC = 21;
    private static final int CODE_CAMERA_REQUEST = 30;
    private static final int CODE_RESULT_REQUEST = 33;
    protected static final int ToTakePhoto = 0;
    public static final int TAKEPHOTO = 11;
    public static final int PICKPHOTO = 12;
    private static final int CROP_PIC = 22;
    private static final int Right = 3;
    private static final int LongClick = 4;
    protected Bitmap photo;
    protected File mCurrentPhotoFile;
    private Uri photoUri;
    private boolean isSave = true;
    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    private File mUriFile;
    private ArrayList<DisscussItems> list = new ArrayList<DisscussItems>();
    private ArrayList<DisscussItems> adaptList = new ArrayList<DisscussItems>();
    private TopicDisscussListAdapter mAdapter;
    private CircleAdapter circleAdapter;
    protected Dialog deletedisscuss;
    String topic_id;
    String topic_name;
    Topic topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_disscuss_list);
        ButterKnife.bind(this);
        hideSoftKeyBoard();
        EtcommApplication.addActivity(this);
        if (getIntent() != null) {
            topic_id = getIntent().getStringExtra("topic_id");
            topic_name = getIntent().getStringExtra("topic_name");
            setTitleTextView(topic_name, null);
        }
//        当EidtText无焦点（focusable=false）时阻止输入法弹出
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(topic_discuss.getWindowToken(), 0);
        getList();
    }

    @Override
    public void onResume() {
        super.onResume();
//        page_number = 1;
//        adaptList.clear();
//        list.clear();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void initData() {
        if (topic != null) {
            topic_image.setImageURI(topic.avatar);
            topic_discuss.setText(topic.desc);
            attion_count.setText(topic.user_number);
            /**
             *点击活动小组的活动图标，进入活动排名页面
             **/
            depart_rank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ActivityRanktActivity.class);
                    intent.putExtra("activity_rank", topic.activity_rank);
                    startActivity(intent);
                }
            });

            setRightImage(R.mipmap.ic_title_more, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, TopicDiscussSettingActivity.class);
                    intent.putExtra("id", topic.user_id);
                    intent.putExtra("topic_id", topic_id);
                    intent.putExtra("isAttentioned", topic.is_followed);
                    intent.putExtra("detail_url", topic.detail_url);
                    intent.putExtra("is_activity", topic.is_activity);
                    intent.putExtra("discuse", topic.desc);
                    intent.putExtra("topic_name", topic_name);
                    intent.putExtra("image", topic.avatar);
                    startActivityForResult(intent, Right);
                }
            });
        }

        TopicDisscussListAdapter.DeleteOnClickListener deleteOnClickListener = new TopicDisscussListAdapter.DeleteOnClickListener() {
            @Override
            public void delete(DisscussItems mInfo) {
                // TODO Auto-generated method stub

                if (adaptList.contains(mInfo)) {
                    adaptList.remove(mInfo);
                }
                mAdapter.notifyDataSetChanged();
            }
        };
        TopicDisscussListAdapter.LikeOrUnLikeClickListener likeOrUnLikeClickListener = new TopicDisscussListAdapter.LikeOrUnLikeClickListener() {
            @Override
            public void delete(boolean islike, DisscussItems mInfo) {
                for (int i = 0; i < adaptList.size(); i++) {
                    DisscussItems d = adaptList.get(i);
                    if (d.discussion_id == mInfo.discussion_id) {
                        if (islike) {
                            d.like = (Integer.parseInt(d.like) + 1) + "";
                            d.is_like = "1";
                        } else {
                            d.like = (Integer.parseInt(d.like) - 1) + "";
                            d.is_like = "0";
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        };
        int mScreenWidth;
        WindowManager wm = getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        mScreenWidth = width;
        mAdapter = new TopicDisscussListAdapter(mContext, adaptList, mScreenWidth, topic_id, deleteOnClickListener, likeOrUnLikeClickListener);
        listView.setAdapter(mAdapter);
        listView.setDividerHeight(5);
        //点击角布局加载更多
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loadStatus && list.size() != 0) {
                    getList();
                }
            }
        });

        //上拉listview加载更多监听
        loadMoreListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (loadMore && !loadStatus) {
                        getList();
                    }
                    if (list.size() == 0) {
                        listView.removeFooterView(footer);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                listView.setFirstItemIndex(firstVisibleItem);
                if (firstVisibleItem != 1 && list.size() != 0) {
                    loadMore = firstVisibleItem + visibleItemCount == totalItemCount;
                } else {
                    loadMore = false;
                }
            }
        };
        listView.setOnScrollListener(loadMoreListener);
        listView.setOnRefreshListener(new DownPullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (listView.getFooterViewsCount() > 0) {
                    listView.removeFooterView(footer);
                }
                page_number = 1;
                adaptList.clear();
                getList();
            }
        });

        //长按举报帖子
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DisscussItems m = mAdapter.getItem(position - 1);
                Intent intent = new Intent(mContext, TopicReportPopActivity.class);
                intent.putExtra("discussion_id", m.discussion_id);
                intent.putExtra("type", "discussion");
                startActivity(intent);
                return true;
            }
        });
        //点击进入帖子详情
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DisscussItems mInfo = mAdapter.getItem(position - 1);
                if (mInfo.share_type.equals("1")) {
                    Intent intent1 = new Intent(mContext, TopicWebviewlActivity.class);
                    intent1.putExtra("type", "health");
                    intent1.putExtra("topic_name", mInfo.title);
                    intent1.putExtra("image", mInfo.photos.get(0).thumb_image);
                    intent1.putExtra("discuse", mInfo.content);
                    intent1.putExtra("topic_id", mInfo.share_id);
                    intent1.putExtra("detail_url", mInfo.detail_url);
                    intent1.putExtra("share_url", mInfo.share_url);
                    mContext.startActivity(intent1);
                } else if (mInfo.share_type.equals("2")) {
                    Intent intent2 = new Intent(mContext, TopicWebviewlActivity.class);
                    intent2.putExtra("type", "activity");
                    intent2.putExtra("topic_name", mInfo.title);
                    intent2.putExtra("image", mInfo.photos.get(0).thumb_image);
                    intent2.putExtra("discuse", mInfo.content);
                    intent2.putExtra("topic_id", mInfo.share_id);
                    intent2.putExtra("detail_url", mInfo.detail_url);
                    intent2.putExtra("share_url", mInfo.share_url);
                    mContext.startActivity(intent2);
                } else if (mInfo.share_type.equals("3")) {
                    Intent intent = new Intent(mContext, TopicDisscussListActivity.class);
                    intent.putExtra("topic_id", mInfo.share_id);
                    intent.putExtra("topic_name", mInfo.title);
                    intent.putExtra("Activity_id", mInfo.share_type);
                    intent.putExtra("activity_rank", mInfo.detail_url);
                    startActivity(intent);
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_CANCELED) {
            Log.i(tag, "resultCode Activity.RESULT_CANCELED  resultCode:" + resultCode);
            return;
        } else {
            Log.i(tag, "resultCode Activity.RESULT_OK");
            switch (requestCode) {
                case TAKE_PHOTO:
                    Log.i(tag, "onActivityResult  TAKE_PHOTO");
                    String originalPath = null;
                    Uri uri = null;
                    if (data != null && data.getData() != null) {
                        uri = data.getData();
                    }
                    // 一些机型无法从getData中获取uri，则需手动指定拍照后存储照片的Uri (魅族无法获取到URI)
                    if (uri == null) {
                        Log.i(tag, "uri  null");
                        if (photoUri != null) {
                            uri = photoUri;
                        }
                    }
                    // 头像正常情况 下，需要剪裁
                    tryCropProfileImage(uri);
                    break;
                default:
                    break;
            }

        }
        if (data != null) {
            switch (requestCode) {
                case PIC:
                    String pic = data.getStringExtra(Preferences.PICMethod);
                    Log.i(tag, "onActivityResult  PIC: " + pic);
                    if (pic.equals("TAKEPHOTO")) {
                        String state = Environment.getExternalStorageState();
                        if (state.equals(Environment.MEDIA_MOUNTED)) {
                            Intent takeintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            System.gc();
                            SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMM_dd_HH_mm_ss");
                            String filename = timeStampFormat.format(new Date());
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, filename);
                            photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            takeintent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            Log.i(tag, "take_photo");
                            startActivityForResult(takeintent, TAKE_PHOTO);
                        } else {
                            Toast.makeText(mContext, "没有SD卡", Toast.LENGTH_SHORT).show();
                        }
                    } else if (pic.equals("PICKPHOTO")) {
                        doPickPhoto();
                    }
                    break;

                case CODE_CAMERA_REQUEST:
                    if (hasSdcard()) {
                        File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                        cropRawPhoto(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(getApplication(), "没有SDCard!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Crop_PICK_PHOTO_FROM_MEIZU:
                    break;
                case PICK_PHOTO_FROM_MEIZU:
                    Log.i(tag, "PICK_PHOTO_FROM_MEIZU");
                    photo = data.getParcelableExtra("data");
                    if (photo == null) {
                        Uri originalUri;
                        if (data.getData() == null) {
                            String filePath = data.getStringExtra("filePath");
                            Log.i(tag, "filePath: " + filePath);
                            originalUri = Uri.fromFile(new File(filePath));
                        } else {
                            originalUri = data.getData();
                        }
                        Log.i(tag, "originalUri: " + originalUri + "  " + originalUri.toString());
                        String originalPath = uri2filePath(originalUri);
                        Log.i(tag, "originalPath:  " + originalPath);
                        originalPath = GetPathFromUri4kitkat.getPath(mContext, originalUri);
                        Log.i(tag, "originalPath:  " + originalPath);
                        tryCropProfileImage(originalUri);
                    } else {
                        mCurrentPhotoFile = new File(DcareUtils.getTmpCachePath() + "screenshot.png");
                        try {
                            saveBitmap(mCurrentPhotoFile.getAbsolutePath(), photo);
                            onHeaderSelectedCallBack(photo);
                            editUserInfo("avatar", mCurrentPhotoFile.getAbsolutePath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case PICKPHOTO:
                    Log.i(tag, "onActivityResult  PICKPHOTO");
                    if (mCurrentPhotoFile != null && mCurrentPhotoFile.exists()) {
                        mCurrentPhotoFile.delete();
                    }

                    photo = data.getParcelableExtra("data");
                    if (photo == null) {
                        // get photo url
                        Uri originalUri;
                        if (data.getData() == null) {
                            String filePath = data.getStringExtra("filePath");
                            originalUri = Uri.fromFile(new File(filePath));
                        } else {
                            originalUri = data.getData();
                        }
                        try {
                            onLicenseSelectedCallBack(originalUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        mCurrentPhotoFile = new File(DcareUtils.getTmpCachePath() + "screenshot.png");
                        try {
                            saveBitmap(mCurrentPhotoFile.getAbsolutePath(), photo);

                            onHeaderSelectedCallBack(photo);
                            editUserInfo("avatar", mCurrentPhotoFile.getAbsolutePath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case TAKE_PHOTO:
                    Log.i(tag, "onActivityResult  TAKE_PHOTO");
                    if (resultCode == RESULT_OK) {
                        String originalPath = null;
                        Uri uri = null;
                        if (data != null && data.getData() != null) {
                            uri = data.getData();
                        }
                        // 一些机型无法从getData中获取uri，则需手动指定拍照后存储照片的Uri
                        if (uri == null) {
                            if (photoUri != null) {
                                uri = photoUri;
                            }
                        }
                        originalPath = GetPathFromUri4kitkat.getPath(mContext, uri);
                        originalPath = uri2filePath(uri);
                        if (StringUtils.isEmpty(originalPath)) {
                            Log.i(tag, "originalPath:  " + originalPath);
                            editUserInfo("avatar", originalPath);
                        } else {
                            Log.i(tag, "error  拍照失败  ");
                            Toast.makeText(mContext, "拍照失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case TAKEPHOTO:
                    Uri originalUri = data.getData();
                    Log.i(tag, "onActivityResult  TAKEPHOTO URI: " + originalUri.toString());
                    // onLicenseSelectedCallBack(originalUri);
                    tryCropProfileImage(Uri.fromFile(mCurrentPhotoFile));
                    break;
                case CAMERA_PIC:
                    Log.i(tag, "CAMERA_PIC");
                    cropPhoto(Uri.fromFile(mUriFile));
                    break;
                case Right:
                    String type = data.getStringExtra(Preferences.TOPICSET);
                    if (type.equals("1")) {
                        //取关
                        if (topic.user_id.equals(prefs.getUserId())) {
                            int lefttextcolor;
                            int righttextcolor;

                            if (Build.VERSION.SDK_INT >= 23) {
                                lefttextcolor = mContext.getColor(R.color.common_dialog_btn_textcolor);
                                righttextcolor = mContext.getColor(R.color.common_dialog_btn_textcolor);
                            } else {
                                lefttextcolor = mContext.getResources().getColor(R.color.common_dialog_btn_textcolor);
                                righttextcolor = mContext.getResources().getColor(R.color.common_dialog_btn_textcolor);
                            }

                            deletedisscuss = DialogFactory.getDialogFactory().showCommonDialog(mContext, "确认删除这个小组吗？", "取消", "确认", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deletedisscuss.dismiss();
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (deletedisscuss != null && deletedisscuss.isShowing()) {
                                        deletedisscuss.dismiss();
                                        deleteTopic();
                                    }
                                }
                            }, lefttextcolor, righttextcolor);

                        } else {
                            if (topic.is_followed.equals("1")) {
                                unfollow();
                            } else {
                                follow();
                            }
                        }


                    } else if (type.equals("2")) {
                        //举报
                        showToast("举报");
                    }
                    break;

                case 100:
                    adaptList.clear();
                    page_number = 1;
                    getList();
                    break;
            }
        }
    }


    public void deleteTopic() {
        RequestParams params = new RequestParams();
        params.put("topic_id", topic_id);
        params.put("access_token", prefs.getAccessToken());
        cancelmDialog();
        showProgress(0, true);
        client.topicDelete(mContext, params, new CommenHandler() {
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
                showToast(commen.message);
                mContext.sendBroadcast(new Intent(Preferences.REFRESH_NOTATTENTION));
                finish();
            }
        });

    }

    public void follow() {
        RequestParams params = new RequestParams();
        params.put("access_token", GlobalSetting.getInstance(mContext).getAccessToken());
        params.put("topic_id", topic_id);
        cancelmDialog();
        showProgress(0, true);
        ApiClient.getInstance().Attention(mContext, params, new CommenHandler() {
            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                cancelmDialog();
                Toast.makeText(mContext, "关注成功", Toast.LENGTH_SHORT).show();
                //关注成功，刷新身边页面——添加关注小组到我的小组列表
                Intent intent = new Intent();
                intent.setAction("add");
                mContext.sendBroadcast(intent);
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });

    }

    public void unfollow() {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        params.put("topic_id", topic_id);
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
                Intent intent = new Intent();
                intent.setAction("add");
                mContext.sendBroadcast(intent);
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });

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
            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                cancelmDialog();

            }

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
        });
        if (field.equals("avatar")) {
            getList();
        }

    }

    /**
     * 获取用户小组下面所有信息
     */
    public void getList() {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        params.put("page_size", String.valueOf(page_size));
        params.put("topic_id", topic_id);
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
                listView.onRefreshComplete();
                loadStatus = false;
                loadingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Discussion discussion) {
                super.onSuccess(discussion);
                cancelmDialog();
                list = discussion.content.items;
                topic = discussion.content.topic;
                List<Topic.TopicUser> list1 = discussion.content.topic.user;
                initData();
                circleAdapter = new CircleAdapter(mContext, list1);
                image_list.setAdapter(circleAdapter);
                if (discussion.content.topic.is_rank.equals("0")) {
                    depart_rank.setVisibility(View.GONE);
                } else {
                    depart_rank.setVisibility(View.VISIBLE);
                }
                if (discussion.content.topic.is_followed.equals("0")) {
                    if_join.setVisibility(View.GONE);
                } else {
                    if_join.setVisibility(View.VISIBLE);
                    setRightText("发帖子", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AddTopicDisscussActivity.class);
                            intent.putExtra("topic_id", topic_id);
                            startActivityForResult(intent, 100);
                        }
                    });

                }
                /**如果是自己创建的小组，可修改头像，可点击修改相关信息
                 * * */
                if (discussion.content.topic.user_id.equals(prefs.getUserId())) {
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

                attion_count.setText(topic.user_number + "个成员   >");
                attention_member.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, TopicMemberActivity.class);
                        intent.putExtra("topic_id", topic_id);
                        intent.putExtra("user_number", topic.user_number);
                        startActivity(intent);
                    }
                });
                if (!discussion.content.topic.avatar.isEmpty()) {
                    topic_image.setImageURI(topic.avatar);
                }
                if (discussion.content.pages == 0) {
                    emptyview.setVisibility(View.VISIBLE);
                } else {
                    emptyview.setVisibility(View.INVISIBLE);
                }

                /**
                 * 键盘隐藏的时候，如果小组内容为空，设置原文本
                 */
//                _root.getViewTreeObserver().addOnGlobalLayoutListener(
//                        new ViewTreeObserver.OnGlobalLayoutListener() {
//                            @Override
//                            public void onGlobalLayout() {
//                                if (isSave) {
//                                    prefs.saveHeigh(_root.getHeight());
//                                    isSave = false;
//                                }
//                                if (_root.getHeight() < prefs.getHeigh()) { // 说明键盘是弹出状态
//
//                                } else {
//                                    topic_discuss.setText(topic.desc);
//                                    topic_discuss.setCursorVisible(false);
//                                }
//                            }
//                        });

                if (list.size() > 0) {
                    if (listView.getFooterViewsCount() == 0 && discussion.content.pages > 1) {
                        listView.addFooterView(footer);
                        listView.setAdapter(mAdapter);
                    }
                    for (Iterator<DisscussItems> iterator = list.iterator(); iterator.hasNext(); ) {
                        DisscussItems disscussCommentItems = iterator.next();
                        adaptList.add(disscussCommentItems);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    showToast("已无更多内容");
                    if (listView.getFooterViewsCount() > 0) {
                        listView.removeFooterView(footer);
                    }

                }
                loadStatus = false;
                listView.onRefreshComplete();
                loadingProgressBar.setVisibility(View.GONE);
                loadingText.setText(getResources().getString(R.string.loadmore));
            }
        });
    }


    private void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 50);
        intent.putExtra("outputY", 50);
        intent.putExtra("scale", true);
        // intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CROP_PIC);
    }

    protected void onLicenseSelectedCallBack(Uri url) {

        topic_image.setImageURI(url);
        updateAvatorByUrl(url);
    }

    private void updateAvatorByUrl(Uri url) {
        editUserInfo("avatar", getAbsoluteImagePath(url));
    }

    protected String getAbsoluteImagePath(final Uri uri) {
        // can post image
        final String[] proj = {MediaStore.Images.Media.DATA};
        String path = GetPathFromUri4kitkat.getPath(mContext, uri);
        if (!StringUtils.isEmpty(path)) {
            return path;
        }
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    protected void onHeaderSelectedCallBack(Bitmap bp) {
        if (bp == null) {
            Log.e(tag, "发生异常");
        }
        topic_image.setImageBitmap(bp);
    }

//


    /**
     * 把Uri转化成文件路径
     */
    private String uri2filePath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        // Cursor cursor = managedQuery(uri, proj, null, null, null);
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(index);
        return path;
    }

    protected void saveBitmap(String filePath, Bitmap bitmap) throws IOException {
        if (bitmap != null) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream out = new FileOutputStream(file);
            bitmap = compressImage(bitmap);
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, out);// 图片设置为压缩一半
            // 11.24 18：50
            out.flush();
            out.close();
        }
    }

    protected Bitmap compressImage(Bitmap image) {
        Log.i(tag, "压缩图片  把图片压缩到50k以下");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 50) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        Log.i(tag, "压缩图片" + baos.size());
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    private boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }

    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", 50);
        intent.putExtra("outputY", 50);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    private void tryCropProfileImage(Uri uri) {
        try {
            // start gallery to crop photo
            Log.i(tag, "tryCropProfileImage: " + uri + "  " + uri.toString());
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 50);
            intent.putExtra("outputY", 50);
            intent.putExtra("return-data", true);
            Log.i(tag, "tryCropProfileImage  PICKPHOTO");
            startActivityForResult(intent, SettingPersonalDataActivity.PICKPHOTO);// test
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doPickPhoto() {
        Log.i(tag, Build.BOARD + " " + Build.MODEL + " " + Build.BRAND + " ");
        if (Build.BRAND.equalsIgnoreCase("Meizu")) {
            Intent itentFromGallery = new Intent();
            itentFromGallery.setType("image/*");
            itentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(itentFromGallery, PICK_PHOTO_FROM_MEIZU);
            return;
        } else {

        }

        try {
            // Launch picker to choose photo for selected contact
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setData(Uri.parse("content://media/internal/images/media"));
            intent.setType("image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 50);
            intent.putExtra("outputY", 50);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, SettingPersonalDataActivity.PICKPHOTO);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
