package etcomm.com.etcommyolk.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoPreviewActivity;
import com.photoselector.ui.PhotoSelectorActivity;
import com.photoselector.util.CommonUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.adapter.AddTopicDisscussPhotoGridAdapter;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.entity.CreateDiscuss;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.handler.CreateDiscussHandler;
import etcomm.com.etcommyolk.utils.Preferences;
import etcomm.com.etcommyolk.widget.ContainsEmojiEditText;

public class AddTopicDisscussActivity extends BaseActivity {


    private static final int PhotoList = 0;
    private static final int CHOOSEORTAKEPHOTO = 3;
    protected static final int PIC = 11;
    private static final int TAKE_PHOTO = 12;
    protected static final int StartPublishPic = 0;
    protected static final int PublishPicFail = 1;
    ArrayList<String> medilist = new ArrayList<String>();
    int scwidth;
    @Bind(R.id.topicdisscuss)
    ContainsEmojiEditText topicdisscuss;
    @Bind(R.id.picgridview)
    GridView picgridview;
    @Bind(R.id.addpictext)
    TextView addpictext;
    @Bind(R.id.text_count)
    TextView textCount;


    private AddTopicDisscussPhotoGridAdapter adapter;
    private List<PhotoModel> photos = new ArrayList<PhotoModel>();
    private View.OnClickListener publishListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showProgress(0, true);
            publishtext();
        }
    };
    private Intent intent;
    private String topic_id;
    private String topic_name;
    private int count = 0;
    protected String discuss_id;
    int curPicCount = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case StartPublishPic:
                    int curcount = msg.arg1;
                    curPicCount = curcount;
                    if (curcount >= medilist.size()) {
                        Toast.makeText(mContext, "发布成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        publishpic(curcount, discuss_id);
                    }
                    break;
                case PublishPicFail:
                    Toast.makeText(mContext, "第" + (int) (curPicCount + 1) + "张图片发送失败", Toast.LENGTH_SHORT).show();
                    if (medilist.size() >= curPicCount + 2) {
                        Toast.makeText(mContext, "发布成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {

                        Message m = mHandler.obtainMessage(StartPublishPic, curPicCount + 1, 0);
                        mHandler.sendMessage(m);
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_topic_disscuss);
        ButterKnife.bind(this);
        setTitleTextView("发帖", null);
        setRightText("发布", publishListener);
        initDatas();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(tag);
    }

    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(topicdisscuss, InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(topicdisscuss.getWindowToken(), 0); //强制隐藏键盘
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(tag);
    }

    protected void initDatas() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        scwidth = dm.widthPixels;
        intent = getIntent();
        topic_id = intent.getStringExtra("topic_id");
        topic_name = intent.getStringExtra("topic_name");
        medilist.add("0");
        picgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new AddTopicDisscussPhotoGridAdapter(mContext, medilist, scwidth, mDeleteOnClickListener);//

        picgridview.setAdapter(adapter);
        picgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // if (arg2 == BitmapList.bmp.size()) {
                if (count < 9) {
                    if (arg2 == medilist.size() - 1) {
                        // 选择拍照或者选择相册
                        Intent intent = new Intent(AddTopicDisscussActivity.this, SelectPicPopupWindowActivity.class);
                        startActivityForResult(intent, PIC);
                    } else {
                        preview(arg2);
                    }

                } else {

                    preview(arg2);
                }

            }
        });

        topicdisscuss.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int i = 140 - s.length();
                textCount.setText("您还可以输入" + i + "个字");
            }
        });
    }


    // 计算图片的缩放值

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    // 把bitmap转换成String
    public static String bitmapToString(String filePath) {

        Bitmap bm = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private String getPicContentToString(String file) {
        String piccontent = null;
        System.gc();
        if (file.equals("0")) {// 去除添加图标的空字符串
            return null;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);
        // 在内存中创建bitmap对象，这个对象按照缩放大小创建的
        options.inSampleSize = calculateInSampleSize(options, 720, 1080);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(file, options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] buffer = baos.toByteArray();
        Log.i(tag, "buffer.length:" + buffer.length);
        piccontent = Base64.encodeToString(buffer, 0, buffer.length, Base64.DEFAULT);
        return piccontent;
    }

    AddTopicDisscussPhotoGridAdapter.DeleteOnClickListener mDeleteOnClickListener = new AddTopicDisscussPhotoGridAdapter.DeleteOnClickListener() {
        @Override
        public void delete(int position, String str) {
            if (medilist.contains(str)) {
                Log.i(tag, "delete Pic  PathStr: " + str);
                medilist.remove(str);
                if (photos != null && photos.size() > 0) {
                    for (int i = 0; i < photos.size(); i++) {
                        PhotoModel pm = photos.get(i);
                        if (pm != null && pm.getOriginalPath().equals(str)) {
                            photos.remove(pm);
                        }
                    }
                }
            }
            if (!medilist.contains("0")) {
                medilist.add(medilist.size(), "0");
                count = medilist.size() - 1;
            }
            adapter.notifyDataSetChanged();
        }
    };

    /**
     * 预览照片
     *
     * @param id
     */
    private void preview(int id) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("photos", (Serializable) photos);
        bundle.putInt(Preferences.TOPIC_PHOTO_ID, id);
        CommonUtils.launchActivity(this, PhotoPreviewActivity.class, bundle);
    }

    /**
     * 拍照
     */
    Uri photoUri = null;

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {

        } else {
            Log.i(tag, "onActivityResult cancel ");
            return;
        }
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    String originalPath = null;
                    Uri uri = null;
                    if (intent != null && intent.getData() != null) {
                        uri = intent.getData();
                    }
                    // 一些机型无法从getData中获取uri，则需手动指定拍照后存储照片的Uri
                    if (uri == null) {
                        if (photoUri != null) {
                            uri = photoUri;
                        }
                    }
                    originalPath = uri2filePath(uri);
                    if (originalPath != null) {
                        Log.i(tag, "originalPath:  " + originalPath);
                        medilist.add(0, originalPath);
                        photos.clear();
                        for (int i = 0; i < medilist.size(); i++) {
                            if (medilist.get(i) != null && !medilist.get(i).equals("0") && medilist.get(i).length() > 6) {
                                photos.add(new PhotoModel(medilist.get(i), true));
                            }
                        }
                        if (medilist.size() > 9) {// 多于9张就不选择了
                            medilist.remove("0");
                            count = medilist.size();
                        } else {
                            count = medilist.size() - 1;
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Log.i(tag, "error  拍照失败  ");
                        Toast.makeText(AddTopicDisscussActivity.this, "拍照失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case PIC:
                String pic = intent.getStringExtra(Preferences.PICMethod);
                addpictext.setVisibility(View.GONE);
                Log.i(tag, "onActivityResult  PIC: " + pic);
                if (pic.equals("TAKEPHOTO")) {
                    String state = Environment.getExternalStorageState();
                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                        Intent takeintent = new Intent("android.media.action.IMAGE_CAPTURE");
                        System.gc();
                        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMM_dd_HH_mm_ss");
                        String filename = timeStampFormat.format(new Date());
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, filename);

                        photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                        takeintent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(takeintent, TAKE_PHOTO);
                    } else {
                        Toast.makeText(AddTopicDisscussActivity.this, "没有SD卡", Toast.LENGTH_SHORT).show();
                    }
                } else if (pic.equals("PICKPHOTO")) {
                    Intent picintent = new Intent(AddTopicDisscussActivity.this, PhotoSelectorActivity.class);
                    picintent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Bundle bundle = new Bundle();
                    photos.clear();
                    for (int i = 0; i < medilist.size(); i++) {
                        if (medilist.get(i) != null && !medilist.get(i).equals("0") && medilist.get(i).length() > 6) {
                            photos.add(new PhotoModel(medilist.get(i), true));
                        }
                    }
                    bundle.putSerializable("photos", (Serializable) photos);
                    picintent.putExtras(bundle);
                    AddTopicDisscussActivity.this.startActivityForResult(picintent, CHOOSEORTAKEPHOTO);
                }
                break;
            case PhotoList:
                photos = (List<PhotoModel>) intent.getExtras().getSerializable("photos");

                if (photos == null || photos.isEmpty()) {
                    Log.i(tag, "选择的图片为空，选择图出错");
                    return;
                }
                for (PhotoModel photoModel : photos) {
                    Log.i(tag, "photoModel.getOriginalPath():  " + photoModel.getOriginalPath());
                    medilist.add(photoModel.getOriginalPath());
                }
                if (medilist.size() >= 9) {// 多于9张就不选择了
                    count = medilist.size();

                } else {
                    medilist.add(medilist.size(), "0");
                    count = medilist.size() - 1;
                }

                break;
            case CHOOSEORTAKEPHOTO:
                if (resultCode == RESULT_OK) {

                    photos = (List<PhotoModel>) intent.getExtras().getSerializable("photos");
                    Log.i(tag, "选择图片个数" + photos.size());
                    if (photos == null || photos.isEmpty()) {
                        Log.i(tag, "选择的图片为空，选择图出错");
                        return;
                    }
                    medilist.clear();
                    for (PhotoModel photoModel : photos) {
                        Log.i(tag, "photoModel.getOriginalPath():  " + photoModel.getOriginalPath());
                        medilist.add(photoModel.getOriginalPath());
                    }
                    if (medilist.size() >= 9) {// 多于9张就不选择了
                        count = medilist.size();

                    } else {
                        medilist.add(medilist.size(), "0");
                        count = medilist.size() - 1;
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(tag, "Result cancel");
                }
                break;
            default:
                break;
        }

    }


    /**
     * 把Uri转化成文件路径
     */
    private String uri2filePath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, proj, null, null, null);
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(index);
        return path;
    }

    @SuppressLint("NewApi")
    protected byte[] compressImage(Bitmap image) {
        Log.i(tag, "压缩图片  把图片压缩到512k以下");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bytecount;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // API 19
            bytecount = image.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {// API
            // 12
            bytecount = image.getByteCount();
        } else {
            bytecount = image.getRowBytes() * image.getHeight(); // earlier
            // version
        }
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        Log.i(tag, "bytecount :  " + bytecount);
        if (bytecount > 1024 * 512) {
            int options = 512 * 1024 * 8 * 100 / (bytecount); // 106324 354k
            // options = options*10;
            if (options <= 0) {
                options = 50;
            } else if (options <= 40) {
                options = 40;
            }
            if (options > 100) {
                options = 100;
            }
            Log.i(tag, "options :  " + options);
            System.gc();
            // while ( baos.toByteArray().length / 1024>50) {
            // //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            // options -= 10;//每次都减少10
            // }
            Log.i(tag, "压缩图片" + baos.size());
        }
        return baos.toByteArray();
    }

    /**
     * 发布第N张图片
     *
     * @param i
     * @param discussion_id
     */
    protected void publishpic(final int i, final String discussion_id) {
        // TODO Auto-generated method stub
        Log.i(tag, "publishpic: " + i + "  discussion_id: " + discussion_id);
        if (medilist.get(i) == null) {
            Toast.makeText(mContext, "发布错误，图片路径错误！", Toast.LENGTH_SHORT).show();
            cancelmDialog();
            mHandler.sendEmptyMessage(PublishPicFail);
            return;
        }
        if (medilist.get(i).equals("0")) {
            Toast.makeText(mContext, "发布成功", Toast.LENGTH_SHORT).show();
            cancelmDialog();
            finish();
            return;
        }
        String image_data = getPicContentToString(medilist.get(i));
        ;
        if (image_data == null) {
            Toast.makeText(mContext, "文件读取错误！", Toast.LENGTH_SHORT).show();
            cancelmDialog();
            mHandler.sendEmptyMessage(PublishPicFail);
            return;
        }

        RequestParams params = new RequestParams();
        params.put("discussion_id", discussion_id);
        params.put("order", String.valueOf(i + 1));
        params.put("image_data", image_data);
        Log.i(tag, "image_data.length: " + image_data.length() + "  image_data:" + image_data);
        Log.i(tag, "image_data.length: " +params.toString());

        client.createDiscussionPic(mContext, params, new CommenHandler() {
            @Override
            public void onCancel() {
                super.onCancel();
            }

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                if (medilist.size() >= i + 2) {
                    Message msg = mHandler.obtainMessage(StartPublishPic, i + 1, 0);
                    mHandler.sendMessage(msg);
                } else {
                    Toast.makeText(mContext, "发布成功!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                mHandler.sendEmptyMessage(PublishPicFail);
            }
        });
    }


    protected void publishtext() {
        RequestParams params = new RequestParams();
        params.put("topic_id", topic_id);
        params.put("access_token", prefs.getAccessToken());
        params.put("content", topicdisscuss.getText().toString());
        cancelmDialog();
        Log.i(tag, "params: " + params.toString());
        client.createDiscussion(mContext, params, new CreateDiscussHandler() {
            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
            }

            @Override
            public void onSuccess(CreateDiscuss commen) {
                super.onSuccess(commen);
                String discussion_id = commen.content.discussion_id;
                discuss_id = discussion_id;
                if (medilist.size() > 1) {
                    Log.i(tag, "有图片，发送第1张图片");
                    Message msg = mHandler.obtainMessage(StartPublishPic, 0, 0);
                    mHandler.sendMessage(msg);
                    // publishpic(0,discussion_id);
                } else {
                    Toast.makeText(mContext, commen.message, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}

