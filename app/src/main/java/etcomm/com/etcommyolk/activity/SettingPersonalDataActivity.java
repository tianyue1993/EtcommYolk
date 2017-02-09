package etcomm.com.etcommyolk.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.limxing.library.AlertView;
import com.limxing.library.OnConfirmeListener;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.utils.DcareUtils;
import etcomm.com.etcommyolk.utils.GetPathFromUri4kitkat;
import etcomm.com.etcommyolk.utils.Preferences;
import etcomm.com.etcommyolk.utils.StringUtils;
import etcomm.com.etcommyolk.widget.WheelView;

public class SettingPersonalDataActivity extends BaseActivity implements View.OnClickListener, OnConfirmeListener {
    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    private static final int SEX = 0;
    public static final int PIC = 1;
    public static final int TAKEPHOTO = 11;
    public static final int PICKPHOTO = 12;
    public static final int PICK_PHOTO_FROM_MEIZU = 16;
    public static final int Crop_PICK_PHOTO_FROM_MEIZU = 17;
    private static final int NICKNAME = 2;
    private static final int DEPARTMENT = 3;
    private static final int HEIGHT = 4;
    private static final int WEIGHT = 5;
    private static final int AGE = 6;
    private static final int PICCrop = 13;
    private static final int TAKE_PHOTO = 10;
    private static final int USERNAME = 7;

    TextView age;
    TextView weight;
    TextView height;
    TextView cancel;
    TextView sure;
    LinearLayout layout_wl;
    TextView choosetext;
    WheelView wl_pickerage;
    WheelView wl_pickerweight;
    WheelView wl_pickerheight;
    private boolean isShow = true;

    RelativeLayout personalavator_rl;
    //头像
    SimpleDraweeView personalavator_ciriv;

    RelativeLayout personal_nickname_rl, personal_username_rl;
    //昵称和姓名
    TextView personal_nickname_tv, personal_username_tv;

    RelativeLayout personal_sex_rl;
    //性别和工号
    TextView personal_sex_tv, job_number_tv;


    TextView personal_deapartment_tv;

    RelativeLayout personal_height_rl;
    //身高
    TextView personal_height_tv;

    RelativeLayout personal_weight_rl;
    //体重
    TextView personal_weight_tv;

    RelativeLayout personal_age_rl, job_number_rl;

    ImageView personal_deapartment_imageView1;
    //年龄
    TextView personal_age_tv;
    private String filename;
    protected Bitmap photo;
    protected File mCurrentPhotoFile;
    // private Dialog dialog;
    private Uri photoUri;
    private File mUriFile;
    private String editweight = "50";
    private String editage = "1988";
    private String editheight = "160";
    private static final int CAMERA_PIC = 21;
    private static final int CROP_PIC = 22;
    protected static final int SetStructure = 25;
    private static final int CODE_CAMERA_REQUEST = 30;
    private static final int CODE_RESULT_REQUEST = 33;
    protected static final int ToTakePhoto = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_personal_data);
        initviewfromlayout();
        initView();
        setclickevent();
    }
    //杂项
    private void initView() {
        EtcommApplication.addActivity(this);
        setTitleTextView("个人资料", null);
        personal_nickname_tv.setText(prefs.getNickName());
        personal_username_tv.setText(prefs.getRealName());
        personal_sex_tv.setText(prefs.getGender().equals("1") ? "男" : "女");
        personalavator_ciriv.setImageURI(prefs.getAvatar());
        personal_height_tv.setText(prefs.getHeight() + "cm");
        personal_weight_tv.setText(prefs.getWeight() + "kg");
        String[] strings = prefs.getBirthday().split("/");
        personal_age_tv.setText(Integer.valueOf(Calendar.getInstance().get(Calendar.YEAR)) - Integer.valueOf(strings[0]) + "岁");
    }




    private void initviewfromlayout() {
        // TODO Auto-generated method stub
        personalavator_rl = (RelativeLayout) findViewById(R.id.personalavator_rl);
        personalavator_ciriv = (SimpleDraweeView) findViewById(R.id.personalavator_ciriv);
        personal_nickname_rl = (RelativeLayout) findViewById(R.id.personal_nickname_rl);
        personal_nickname_tv = (TextView) findViewById(R.id.personal_nickname_tv);
        personal_username_tv = (TextView) findViewById(R.id.personal_username_tv);
        personal_sex_rl = (RelativeLayout) findViewById(R.id.personal_sex_rl);
        personal_sex_tv = (TextView) findViewById(R.id.personal_sex_tv);
        personal_height_rl = (RelativeLayout) findViewById(R.id.personal_height_rl);
        personal_height_tv = (TextView) findViewById(R.id.personal_height_tv);
        personal_weight_rl = (RelativeLayout) findViewById(R.id.personal_weight_rl);
        personal_weight_tv = (TextView) findViewById(R.id.personal_weight_tv);
        personal_age_rl = (RelativeLayout) findViewById(R.id.personal_age_rl);
        personal_age_tv = (TextView) findViewById(R.id.personal_age_tv);
        personal_username_rl = (RelativeLayout) findViewById(R.id.personal_username_rl);
        age = (TextView) findViewById(R.id.age);
        height = (TextView) findViewById(R.id.height);
        weight = (TextView) findViewById(R.id.weight);
        choosetext = (TextView) findViewById(R.id.choosetext);
        sure = (TextView) findViewById(R.id.sure);
        cancel = (TextView) findViewById(R.id.cancel);
        layout_wl = (LinearLayout) findViewById(R.id.layout_wl);
        wl_pickerage = (WheelView) findViewById(R.id.wl_pickerage);
        wl_pickerheight = (WheelView) findViewById(R.id.wl_pickerheight);
        wl_pickerweight = (WheelView) findViewById(R.id.wl_pickerweight);
    }

    //注册监听
    private void setclickevent() {
        // TODO Auto-generated method stub
        personalavator_rl.setOnClickListener(this);
        personal_username_rl.setOnClickListener(this);
        personal_nickname_rl.setOnClickListener(this);
        personal_sex_rl.setOnClickListener(this);
        personal_height_rl.setOnClickListener(this);
        personal_weight_rl.setOnClickListener(this);
        personal_age_rl.setOnClickListener(this);
        sure.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.personalavator_rl:
                personalavator_rl();
                break;
            case R.id.personal_nickname_rl:
                personal_nickname_rl();
                break;
            case R.id.personal_username_rl:
                personal_username_rl();
                break;
            case R.id.personal_sex_rl:
                personal_sex_rl();
                break;
            case R.id.personal_height_rl:
                personal_height_rl();
                break;
            case R.id.personal_weight_rl:
                personal_weight_rl();
                break;
            case R.id.personal_age_rl:
                personal_age_rl();
                break;
            case R.id.sure:
                sure();
                break;
            case R.id.cancel:
                cancel();
                break;
            default:
                break;
        }
    }

    private void cancel() {
        isShow = true;
        layout_wl.setVisibility(View.GONE);
        wl_pickerweight.setSeletion(0);
        wl_pickerage.setSeletion(0);
        wl_pickerheight.setSeletion(0);

    }

    private void sure() {

         if (choosetext.getText().toString().equals("选择身高")) {
            if (editheight != "") {
                editUserInfo("height", editheight);
                prefs.setHeight(editheight);
                personal_height_tv.setText(editheight + "cm");
                wl_pickerweight.setSeletion(0);
                wl_pickerage.setSeletion(0);
                wl_pickerheight.setSeletion(0);
            }

        } else if (choosetext.getText().toString().equals("选择体重")) {
            if (editweight != "") {
                editUserInfo("weight", editweight);
                prefs.setWeight(editweight);
                personal_weight_tv.setText(editweight + "kg");
                wl_pickerweight.setSeletion(0);
                wl_pickerage.setSeletion(0);
                wl_pickerheight.setSeletion(0);
            }

        }
        isShow = true;
        layout_wl.setVisibility(View.GONE);
    }

    // 头像设置
    void personalavator_rl() {
        Intent intent = new Intent(this, SelectPicPopupWindowActivity.class);
        startActivityForResult(intent, PIC);
    }

    // 昵称设置
    void personal_nickname_rl() {
        Intent intent = new Intent(this, ChangeNickNameActivity.class);
        intent.putExtra("name", prefs.getNickName());
        intent.putExtra("type", "修改昵称");
        startActivityForResult(intent, NICKNAME);
    }
    //修改姓名
    void personal_username_rl() {
        Intent intent = new Intent(this, ChangeNickNameActivity.class);
        intent.putExtra("name", prefs.getRealName());
        intent.putExtra("type", "修改姓名");
        startActivityForResult(intent, USERNAME);
    }

    // 性别设置
    void personal_sex_rl() {
        Intent intent = new Intent(this, SelectSexPopupWindowActivity.class);
        startActivityForResult(intent, SEX);
    }


    // 身高设置
    void personal_height_rl() {
        choosetext.setText("选择身高");
        if (isShow) {
            layout_wl.setVisibility(View.VISIBLE);
            wl_pickerage.setVisibility(View.GONE);
            wl_pickerheight.setVisibility(View.VISIBLE);
            wl_pickerweight.setVisibility(View.GONE);
            isShow = false;

            ArrayList<String> heightList = new ArrayList<String>();
            for (int i = 0; i < 111; i++) {
                // 120--230
                int weight = i + 120;
                heightList.add(weight + "");
            }
            wl_pickerheight.setOffset(1);
            wl_pickerheight.setItems(heightList);
            int height = Integer.parseInt(prefs.getHeight());
            if (height != 0) {
                wl_pickerheight.setSeletion(height - 120);

            } else {
                wl_pickerheight.setSeletion(40);
            }

            wl_pickerheight.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {
                    Log.d("", "selectedIndex: " + selectedIndex + ", item: " + item);
                    editheight = item;
                }
            });
        } else {
            isShow = true;
            wl_pickerweight.setSeletion(0);
            wl_pickerage.setSeletion(0);
            wl_pickerheight.setSeletion(0);
            layout_wl.setVisibility(View.GONE);
        }
    }

    // 体重设置
    void personal_weight_rl() {
        choosetext.setText("选择体重");
        if (isShow) {
            layout_wl.setVisibility(View.VISIBLE);
            wl_pickerage.setVisibility(View.GONE);
            wl_pickerheight.setVisibility(View.GONE);
            wl_pickerweight.setVisibility(View.VISIBLE);
            isShow = false;

            ArrayList<String> weightList = new ArrayList<String>();
            for (int i = 0; i < 276; i++) {
                int weight = i + 25;
                weightList.add(weight + "");
            }
            wl_pickerweight.setOffset(1);
            wl_pickerweight.setItems(weightList);
            int weight = Integer.parseInt(prefs.getWeight());
            if (weight != 0) {
                wl_pickerweight.setSeletion(weight - 25);
            } else {
                wl_pickerweight.setSeletion(25);
            }

            wl_pickerweight.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {
                    Log.d("", "selectedIndex: " + selectedIndex + ", item: " + item);
                    editweight = item;
                }
            });
        } else {
            isShow = true;
            wl_pickerweight.setSeletion(0);
            wl_pickerage.setSeletion(0);
            wl_pickerheight.setSeletion(0);
            layout_wl.setVisibility(View.GONE);
        }
    }

    // 年龄设置
    void personal_age_rl() {
        choosetext.setText("选择年龄");

        new AlertView("选择年龄", SettingPersonalDataActivity.this, 1931, 2000, SettingPersonalDataActivity.this).show();
        layout_wl.setVisibility(View.GONE);
    }

    private void editUserInfo(final String field, final String value) {
        RequestParams params = new RequestParams();
        params.put("field", field);
        params.put("value", value);
        params.put("access_token", prefs.getAccessToken());
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
                Toast.makeText(mContext, commen.message, Toast.LENGTH_SHORT).show();
                if (field.equals("nick_name")) {
                    personal_nickname_tv.setText(value);
                    prefs.setNickName(value);
                }

            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
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
                            Toast.makeText(SettingPersonalDataActivity.this, "没有SD卡", Toast.LENGTH_SHORT).show();
                        }
                    } else if (pic.equals("PICKPHOTO")) {
                        doPickPhoto();
                    }
                    break;
                case CODE_RESULT_REQUEST:
                    if (data != null) {
                        setImageToHeadView(data);
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
                            updateAvatorByFile(mCurrentPhotoFile.getAbsolutePath());
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
                            updateAvatorByFile(mCurrentPhotoFile.getAbsolutePath());
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
                            updateAvatorByFile(originalPath);
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
                case CROP_PIC:
                    Log.d(tag, "error");
                    break;
                case PICCrop:

                    break;
                case SEX:
                    String sex = data.getStringExtra(Preferences.SelectSex);
                    Log.i(tag, "onActivityResult  SEX : " + sex);
                    if (sex != null) {
                        personal_sex_tv.setText(sex);
                        prefs.setGender(sex.equals("男") ? "1" : "2");
                        editUserInfo("gender", sex.equals("男") ? "1" : "2");
                    }

                    break;
                case NICKNAME:
                    String nickname = data.getStringExtra(Preferences.SelectNickName);
                    Log.i(tag, "onActivityResult  NICKNAME : " + nickname);
                    if (nickname != null && !nickname.isEmpty()) {
                        personal_nickname_tv.setText(nickname);
                    }
                    break;
                case USERNAME:
                    String username = data.getStringExtra(Preferences.SelectNickName);
                    Log.i(tag, "onActivityResult  NICKNAME : " + username);
                    if (username != null && !username.isEmpty()) {
                        personal_username_tv.setText(username);
                    }
                    break;
                case DEPARTMENT:
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Log.i(tag, "setImageToHeadView");
        }
    }

    protected void onLicenseSelectedCallBack(Uri url) {

        personalavator_ciriv.setImageURI(url);
        updateAvatorByUrl(url);
    }

    // 启动手机相机拍摄照片作为头像
    private void choseHeadImageFromCameraCapture() {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intentFromCapture.putExtra("scale", true);
        mUriFile = new File(Environment.getExternalStorageDirectory() + "/dcare/" + System.currentTimeMillis() + ".jpg");
        try {
            mUriFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mUriFile));
        intentFromCapture.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intentFromCapture.putExtra("noFaceDetection", true);
        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
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
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    private void updateAvatorByUrl(Uri url) {
        updateAvatorByFile(getAbsoluteImagePath(url));
    }

    private void updateAvatorByFile(String absolutePath) {
        if (!StringUtils.isEmpty(absolutePath)) {
            if (new File(absolutePath).isFile()) {
                Log.e(tag, "startUpload PIC File : " + absolutePath);
                RequestParams params = new RequestParams();
                params.put("access_token", prefs.getAccessToken());
                try {
                    params.put("avatar", new File(absolutePath));
                } catch (FileNotFoundException e1) {
                    Log.e(tag, "文件没有找到");
                    e1.printStackTrace();
                    return;
                }
                cancelmDialog();
                showProgress(0, true);
                client.toUploadUserAvator(this, params, new CommenHandler(){

                    @Override
                    public void onCancel() {
                        super.onCancel();
                        cancelmDialog();
                    }

                    @Override
                    public void onSuccess(Commen commen) {
                        super.onSuccess(commen);
                        cancelmDialog();
                        Toast.makeText(mContext, commen.message, Toast.LENGTH_SHORT).show();
                        prefs.setAvatar(commen.content);
                        prefs.saveLoginUserAvatar(commen.content);

                    }

                    @Override
                    public void onFailure(BaseException exception) {
                        super.onFailure(exception);
                        cancelmDialog();
                    }

            });
        } else {
            Toast.makeText(mContext, "文件路径出错，上传头像失败", Toast.LENGTH_SHORT).show();
        }
    }
    }

    /**
     * 把Uri转化成文件路径
     */
    private String uri2filePath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(index);
        return path;
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
        personalavator_ciriv.setImageURI(Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bp, null,null)));
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
            intent.putExtra("outputX", 300);
            intent.putExtra("outputY", 300);
            intent.putExtra("return-data", true);
            Log.i(tag, "tryCropProfileImage  PICKPHOTO");
            startActivityForResult(intent, SettingPersonalDataActivity.PICKPHOTO);// test
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    //图片操作
    private void doPickPhoto() {
        Log.i(tag, Build.BOARD + " " + Build.MODEL + " " + Build.BRAND + " ");
        if (Build.BRAND.equalsIgnoreCase("Meizu")) {
            Intent itentFromGallery = new Intent();
            itentFromGallery.setType("image/*");
            itentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(itentFromGallery, PICK_PHOTO_FROM_MEIZU);
            return;
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
            intent.putExtra("outputX", 300);
            intent.putExtra("outputY", 300);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, SettingPersonalDataActivity.PICKPHOTO);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
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


    private void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        // intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CROP_PIC);
    }
    //选择年龄
    @Override
    public void result(String s) {
        if (editage != "") {
            String day = "";
            day = s.replace("年","/").replace("月","/").replace("日","");
            String[] strings = day.split("/");

            if (Integer.valueOf(strings[0]) > Calendar.getInstance().get(Calendar.YEAR)) {
                showToast("出生日期不能大于当前年份");
                return;
            }
            editUserInfo("birthday", day);
            prefs.setBirthday(day);
            personal_age_tv.setText(Integer.valueOf(Calendar.getInstance().get(Calendar.YEAR)) - Integer.valueOf(strings[0]) + "岁");
        }
    }
}
