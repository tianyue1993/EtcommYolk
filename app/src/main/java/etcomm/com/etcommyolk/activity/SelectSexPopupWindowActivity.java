package etcomm.com.etcommyolk.activity;

import android.app.Activity;
import android.content.Intent;
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

public class SelectSexPopupWindowActivity extends Activity {
    @Bind(R.id.btn_pick_photo)
    Button btnPickPhoto;
    @Bind(R.id.btn_take_photo)
    Button btnTakePhoto;
    @Bind(R.id.btn_cancel)
    Button btnCancel;
    @Bind(R.id.pop_layout)
    LinearLayout popLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sex_popup_window);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        EtcommApplication.addActivity(this);
        btnTakePhoto.setText("女");
        btnPickPhoto.setText("男");
    }


    // 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @OnClick({R.id.btn_take_photo, R.id.btn_pick_photo, R.id.btn_cancel})
    public void onClick(View v) {
        Intent data = new Intent();
        switch (v.getId()) {
            case R.id.btn_take_photo: // 女
                data.putExtra(Preferences.SelectSex, "女");
                setResult(RESULT_OK, data);
                break;
            case R.id.btn_pick_photo: // 男
                data.putExtra(Preferences.SelectSex, "男");
                setResult(RESULT_OK, data);
                break;
            case R.id.btn_cancel:

                break;
            default:
                break;
        }
        finish();
    }

}
