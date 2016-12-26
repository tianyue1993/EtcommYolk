package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.widget.WheelView;

public class PersonalProfileActivity extends BaseActivity {
    //用户头像
    @Bind(R.id.iv_avator)
    SimpleDraweeView ivAvator;
    //生日
    @Bind(R.id.age)
    TextView age;
    //生日点击事件
    @Bind(R.id.ll_age)
    LinearLayout ll_age;
    //身高
    @Bind(R.id.height)
    TextView height;
    //身高点击事件
    @Bind(R.id.ll_height)
    LinearLayout ll_height;
    //体重
    @Bind(R.id.weight)
    TextView weight;
    //体重点击事件
    @Bind(R.id.ll_weight)
    LinearLayout ll_weight;
    //下一步
    @Bind(R.id.btn_next)
    Button btnNext;
    //滑动框取消
    @Bind(R.id.cancel)
    TextView cancel;
    //滑动框确定
    @Bind(R.id.sure)
    TextView sure;
    //生日WheelView
    @Bind(R.id.wl_pickerage)
    WheelView wl_pickerage;
    //体重WheelView
    @Bind(R.id.wl_pickerweight)
    WheelView wl_pickerweight;
    //身高WheelView
    @Bind(R.id.wl_pickerheight)
    WheelView wl_pickerheight;
    //隐藏显示UI
    @Bind(R.id.layout_wl)
    LinearLayout layout_wl;
    //说明文字
    @Bind(R.id.choosetext)
    TextView choosetext;
    private boolean isShow = true;
    private int usersex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        ButterKnife.bind(this);
        initDatas();
    }


    @OnClick({R.id.btn_next, R.id.ll_age, R.id.ll_weight, R.id.ll_height, R.id.cancel, R.id.sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                prefs.setWeight(weight.getText().toString().trim());
                prefs.setHeight(height.getText().toString().trim());
                prefs.setBirthday(age.getText().toString().trim());
                startActivity(new Intent(mContext, TargetActivity.class));
                break;
            case R.id.ll_age:
                personal_age_rl();

                break;
            case R.id.ll_weight:
                personal_weight_rl();

                break;
            case R.id.ll_height:
                personal_height_rl();

                break;
            case R.id.cancel:
                cancel();
                break;
            case R.id.sure:
                sure();
                break;
        }
    }


    protected void initDatas() {
        EtcommApplication.addActivity(this);
        // //1男，2女) 0默认
        usersex = Integer.valueOf(prefs.getGender());
        // 头像设置
        if (prefs.getAvatar() != null && prefs.getAvatar() != "") {
            ivAvator.setImageURI(prefs.getAvatar());
        }
        setTitleTextView("个人资料", null);
        age.setText("1980");
        if (usersex == 1) {
            // 男
            weight.setText("65");
            height.setText("170");
        } else if (usersex == 2) {
            // 女
            weight.setText("50");
            height.setText("160");
        }
    }


    // 身高设置
    void personal_height_rl() {
        choosetext.setText("身　高：");
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
            int height1 = Integer.parseInt(height.getText().toString());
            if (height1 != 0) {
                wl_pickerheight.setSeletion(height1 - 120);

            } else {
                if (usersex == 1) {
                    wl_pickerheight.setSeletion(50);
                } else if (usersex == 2) {
                    wl_pickerheight.setSeletion(40);
                }
            }

            wl_pickerheight.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {
                    Log.d("", "selectedIndex: " + selectedIndex + ", item: " + item);
                    height.setText(item);
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
        choosetext.setText("体　重：");
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
            int weight1 = Integer.parseInt(weight.getText().toString());
            if (weight1 != 0) {
                wl_pickerweight.setSeletion(weight1 - 25);
            } else {

                if (usersex == 1) {
                    wl_pickerweight.setSeletion(40);
                } else if (usersex == 2) {
                    wl_pickerweight.setSeletion(25);
                }

            }

            wl_pickerweight.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {
                    Log.d("", "selectedIndex: " + selectedIndex + ", item: " + item);
                    weight.setText(item);
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
        choosetext.setText("生　日：");
        if (isShow) {
            layout_wl.setVisibility(View.VISIBLE);
            wl_pickerage.setVisibility(View.VISIBLE);
            wl_pickerheight.setVisibility(View.GONE);
            wl_pickerweight.setVisibility(View.GONE);
            isShow = false;
            ArrayList<String> ageList = new ArrayList<String>();
            for (int i = 0; i < 71; i++) {
                int age = i + 1930;
                ageList.add(age + "");
            }
            wl_pickerage.setOffset(1);
            wl_pickerage.setItems(ageList);

            int age1 = Integer.parseInt(age.getText().toString());
            if (age1 != 0) {
                wl_pickerage.setSeletion(age1 - 1930);
            } else {
                wl_pickerage.setSeletion(50);
            }

            wl_pickerage.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {
                    Log.d("", "selectedIndex: " + selectedIndex + ", item: " + item);
                    age.setText(item);
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

    private void cancel() {
        isShow = true;
        layout_wl.setVisibility(View.GONE);
        wl_pickerweight.setSeletion(0);
        wl_pickerage.setSeletion(0);
        wl_pickerheight.setSeletion(0);

    }

    private void sure() {
        wl_pickerweight.setSeletion(0);
        wl_pickerage.setSeletion(0);
        wl_pickerheight.setSeletion(0);
        isShow = true;
        layout_wl.setVisibility(View.GONE);
    }

}
