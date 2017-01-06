package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.loopj.android.http.RequestParams;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.entity.DefaultAvatorEntity;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.handler.DefaultAvatorHandler;
import etcomm.com.etcommyolk.widget.ScrollHorizontalScrollView;
import etcomm.com.etcommyolk.widget.ScrollHorizontalScrollViewAdapter;

public class ChoosePictureActivity extends BaseActivity implements TextWatcher {

    @Bind(R.id.btn_enterusernamechooseavatarnext)
    Button btn_enterusernamechooseavatarnext;
    //性别选择
    @Bind(R.id.sex_switch)
    CheckBox sexSwitch;
    //昵称输入
    @Bind(R.id.input_choose)
    EditText inputChoose;
    private ArrayList<String> mMan = new ArrayList<String>();
    private ArrayList<String> mWeMen = new ArrayList<String>();
    /**
     * 性别 true 男 false女
     */
    private boolean isMan = true;
    private ScrollHorizontalScrollView scrollhsv;
    private ScrollHorizontalScrollViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_picture);
        ButterKnife.bind(this);
        initview();
        getDefaultAvator();
    }


    /**
     * 获取默认头像
     */
    private void getDefaultAvator() {
        cancelmDialog();
        showProgress(0, true);
        client.getDefaultAvator(this, null, new DefaultAvatorHandler() {
            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(DefaultAvatorEntity defaultAvatorEntity) {
                super.onSuccess(defaultAvatorEntity);
                cancelmDialog();
                List<DefaultAvatorEntity.DefaultAvator> ava = defaultAvatorEntity.content;
                mMan.clear();
                mWeMen.clear();
                for (Iterator iterator = ava.iterator(); iterator.hasNext(); ) {
                    DefaultAvatorEntity.DefaultAvator avatarContent = (DefaultAvatorEntity.DefaultAvator) iterator.next();
                    if (avatarContent != null) {
                        if (avatarContent.gender == 1) {// 性别： 0：女
                            // 1：男
                            mMan.add(avatarContent.avatar);
                            mAdapter = new ScrollHorizontalScrollViewAdapter(mContext, mMan);
                            scrollhsv.initData(mAdapter);
                        } else {
                            mWeMen.add(avatarContent.avatar);
                        }
                    }
                }

                if (isMan) {
                    /**
                     * 设置用户没有选择头像的时候，默认选中第一个头像
                     */
                    prefs.setAvatar(mMan.get(0));
                    prefs.saveLoginUserAvatar(mMan.get(0));
                    prefs.setGender("1");

                } else {
                    prefs.setAvatar(mMan.get(0));
                    prefs.saveLoginUserAvatar(mMan.get(0));
                    prefs.setGender("2");
                }

            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });
    }

    //杂项
    private void initview() {
        setTitleTextView("个人资料", null);
        EtcommApplication.addActivity(this);
        scrollhsv = (ScrollHorizontalScrollView) findViewById(R.id.scrollhsv);
        scrollhsv.setHorizontalFadingEdgeEnabled(false);
        inputChoose.addTextChangedListener(this);

        scrollhsv.setOnItemClickListener(new ScrollHorizontalScrollView.OnItemClickListener() {

            @Override
            public void onClick(int index) {
                if (isMan) {
                    prefs.setAvatar(mMan.get(index));
                    prefs.saveLoginUserAvatar(mMan.get(index));
                    prefs.setGender("1");
                } else {
                    prefs.setAvatar(mWeMen.get(index));
                    prefs.saveLoginUserAvatar(mWeMen.get(index));
                    prefs.setGender("2");
                }

            }
        });
        btn_enterusernamechooseavatarnext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!inputChoose.getText().toString().trim().isEmpty()) {
                    if (!checkName(inputChoose.getText().toString().trim())) {
                        showToast("用户昵称只能为中英文、数字、下划线、中杠线、星号");
                        return;
                    }
                    if (inputChoose.getText().toString().trim().length() >= 1 && inputChoose.getText().toString().trim().length() <= 10) {
                    } else {
                        showToast("昵称长度为1至10个字，请核实昵称");
                        return;
                    }
                }else {
                    return;
                }

                // 校验昵称是否重复
                if (inputChoose.getText().toString().trim() != null && inputChoose.getText().toString().trim() != "") {
                    editNickName();
                }
            }
        });

        sexSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) { // 男
                    mAdapter = new ScrollHorizontalScrollViewAdapter(mContext, mMan);
                    scrollhsv.initData(mAdapter);
                    isMan = true;
                } else {
                    mAdapter = new ScrollHorizontalScrollViewAdapter(mContext, mWeMen);
                    scrollhsv.initData(mAdapter);
                    isMan = false;
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (scrollhsv != null) {
            scrollhsv.destroy();
        }
        super.onDestroy();
    }


    /**
     * 确认并修改用户昵称
     */
    private void editNickName() {

        RequestParams object = new RequestParams();
        object.put("access_token", prefs.getAccessToken());
        object.put("user_id", prefs.getUserId());
        object.put("nick_name", inputChoose.getText().toString().trim());
        object.put("type", "check");
        cancelmDialog();
        showProgress(0, true);
        client.toRegisterUpdateInfo(this, object, new CommenHandler() {

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                cancelmDialog();
                prefs.setFirstsetuserinfo(true);
                prefs.setNickName(inputChoose.getText().toString().trim());
                startActivity(new Intent(ChoosePictureActivity.this, PersonalProfileActivity.class));
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });
    }

    public static boolean checkName(String username) {
        boolean isMaches = true;
        for (int i = 0; i < username.length(); i++) {
            String reg = "^[a-zA-Z0-9_\u4e00-\u9fa5]+$";
            Pattern pattern = Pattern.compile(reg);
            pattern.matcher(reg);
            String s = username.charAt(i) + "";
            if (s.matches(reg) || s.equals("*") || s.equals("_") || s.equals("-")) {
                isMaches = true;
            } else {
                isMaches = false;
                break;
            }
        }
        return isMaches;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (inputChoose.getText().toString().trim().isEmpty()) {
            btn_enterusernamechooseavatarnext.setBackgroundResource(R.mipmap.all_fil_button);
            btn_enterusernamechooseavatarnext.setClickable(false);
        }else {
            btn_enterusernamechooseavatarnext.setBackgroundResource(R.mipmap.all_ok_button);
            btn_enterusernamechooseavatarnext.setClickable(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
