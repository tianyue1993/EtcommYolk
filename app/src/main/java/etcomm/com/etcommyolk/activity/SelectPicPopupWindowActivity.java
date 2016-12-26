package etcomm.com.etcommyolk.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.utils.Preferences;

public class SelectPicPopupWindowActivity extends Activity implements View.OnClickListener {
    @Bind(R.id.pop_layout)
    LinearLayout layout;
    @Bind(R.id.btn_pick_photo)
    Button btnPickPhoto;
    @Bind(R.id.btn_take_photo)
    Button btnTakePhoto;
    @Bind(R.id.btn_cancel)
    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pic_popup_window);
        ButterKnife.bind(this);
        initView();

    }

    //杂项
    private void initView() {
        EtcommApplication.addActivity(this);
    }
import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import etcomm.com.etcommyolk.R;

public class SelectPicPopupWindowActivity extends BaseActivity implements
        View.OnClickListener {
    private static final String TAG = "SelectPicPopupWindowActivity";
    @Bind(R.id.btn_take_photo)
    Button btn_take_photo;
    @Bind(R.id.btn_pick_photo)
    Button btn_pick_photo;
    @Bind(R.id.btn_cancel)
    Button btn_cancel;
    @Bind(R.id.pop_layout)
    LinearLayout layout;
    protected Bitmap photo;
    protected File mCurrentPhotoFile;
    private String filename;

    // 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @OnClick({R.id.btn_take_photo, R.id.btn_pick_photo, R.id.btn_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_photo:
                backWithData(Preferences.PICMethod, "TAKEPHOTO");
                break;
            case R.id.btn_pick_photo:
                backWithData(Preferences.PICMethod, "PICKPHOTO");
//                backWithData(Preferences.PICMethod, "TAKEPHOTO");
                break;
            case R.id.btn_pick_photo:
//                backWithData(Preferences.PICMethod, "PICKPHOTO");
                break;
            case R.id.btn_cancel:
                break;
            default:
                break;
        }
        finish();
    }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picpop_dialog);
    }
}
