package etcomm.com.etcommyolk.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.DeviceBind;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.DeviceBindHandler;
import etcomm.com.etcommyolk.service.StepDataUploadService;
import etcomm.com.etcommyolk.utils.Preferences;
import etcomm.com.etcommyolk.widget.DialogFactory;
import etcomm.com.etcommyolk.widget.SwitchButton;
import me.chunyu.pedometerservice.PedometerCounterService;

public class PedometerActivity extends BaseActivity {

    @Bind(R.id.pedometer_switch)
    SwitchButton pedometer_switch;
    @Bind(R.id.screenlongon_switch)
    SwitchButton screenlongon_switch;
    @Bind(R.id.pedometer_plus)
    ImageView pedometer_plus;
    @Bind(R.id.pedometer_sensitivity)
    TextView pedometer_sensitivity;
    @Bind(R.id.pedometer_minus)
    ImageView pedometer_minus;
    @Bind(R.id.pedometer_device_rl)
    RelativeLayout pedometer_device_rl;
    @Bind(R.id.setting_submit_rl)
    Button setting_submit_rl;
    @Bind(R.id.synchronization_image)
    ImageView synchronizationImage;
    @Bind(R.id.synchronization_text)
    TextView synchronizationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        ButterKnife.bind(this);
        initView();
    }


    boolean isCheck = false; // 软件计步状态
    private Dialog coDialog;

    @Override
    public void onResume() {
        super.onResume();
        if (prefs.getMacAddress().length() < 5) {
            pedometer_switch.setChecked(true);
            prefs.setIfSoftPedometerOn(true);
            // showToast(""+prefs.isSoftPedometerOn());
        } else {
            pedometer_switch.setChecked(false);
            screenlongon_switch.setChecked(false);
            prefs.setIfSoftPedometerOn(false);
        }
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(tag);
    }


    @OnClick({R.id.pedometer_plus, R.id.pedometer_minus, R.id.setting_submit_rl, R.id.pedometer_device_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pedometer_plus:

                prefs.setSoftPedometerSensitivity(Integer.parseInt(pedometer_sensitivity.getText().toString()));
                pedometer_sensitivity.setText((prefs.getSoftPedometerSensitivity() + 1) + "");
                prefs.setSoftPedometerSensitivity(Integer.parseInt(pedometer_sensitivity.getText().toString()));
                if (Integer.parseInt(pedometer_sensitivity.getText().toString()) < 9 && Integer.parseInt(pedometer_sensitivity.getText().toString()) > 1) {
                    pedometer_plus.setEnabled(true);
                    pedometer_minus.setEnabled(true);
                } else if (Integer.parseInt(pedometer_sensitivity.getText().toString()) >= 9) {
                    pedometer_plus.setEnabled(false);
                    pedometer_minus.setEnabled(true);
                } else if (Integer.parseInt(pedometer_sensitivity.getText().toString()) <= 1) {
                    pedometer_plus.setEnabled(true);
                    pedometer_minus.setEnabled(false);
                }
                Intent intent = new Intent("changesensitivity");
                intent.putExtra("sensity", prefs.getSoftPedometerSensitivity());
                sendBroadcast(intent);
                break;
            case R.id.pedometer_minus:

                prefs.setSoftPedometerSensitivity(Integer.parseInt(pedometer_sensitivity.getText().toString()) - 2);
                pedometer_sensitivity.setText((prefs.getSoftPedometerSensitivity() + 1) + "");
                prefs.setSoftPedometerSensitivity(Integer.parseInt(pedometer_sensitivity.getText().toString()));
                Log.e(tag, "发送前灵敏度" + prefs.getSoftPedometerSensitivity());

                if (Integer.parseInt(pedometer_sensitivity.getText().toString()) < 9 && Integer.parseInt(pedometer_sensitivity.getText().toString()) > 1) {
                    pedometer_plus.setEnabled(true);
                    pedometer_minus.setEnabled(true);
                } else if (Integer.parseInt(pedometer_sensitivity.getText().toString()) >= 9) {
                    pedometer_plus.setEnabled(false);
                    pedometer_minus.setEnabled(true);
                } else if (Integer.parseInt(pedometer_sensitivity.getText().toString()) <= 1) {
                    pedometer_plus.setEnabled(true);
                    pedometer_minus.setEnabled(false);
                }
                Intent intent1 = new Intent("changesensitivity");
                intent1.putExtra("sensity", prefs.getSoftPedometerSensitivity());
                sendBroadcast(intent1);
                break;
            case R.id.setting_submit_rl:

                Intent service = new Intent(this, StepDataUploadService.class);
                service.putExtra("usersubmit", true);
                Toast.makeText(mContext, "正在上传", Toast.LENGTH_SHORT).show();
                startService(service);
                break;
            case R.id.pedometer_device_rl: // 计步设备管理
                if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    startActivity(new Intent(PedometerActivity.this, MineDeviceActivity.class));
                } else {
                    showToast("您的安卓系统版本过低，不支持此功能！");
                }
                break;
            default:
                break;
        }
    }

    private void initView() {
        EtcommApplication.addActivity(this);
        setTitleTextView("计步设置", null);
        pedometer_sensitivity.setText((prefs.getSoftPedometerSensitivity()) + "");

        if (Integer.parseInt(pedometer_sensitivity.getText().toString()) < 9 && Integer.parseInt(pedometer_sensitivity.getText().toString()) > 1) {
            pedometer_plus.setEnabled(true);
            pedometer_minus.setEnabled(true);
        } else if (Integer.parseInt(pedometer_sensitivity.getText().toString()) >= 9) {
            pedometer_plus.setEnabled(false);
            pedometer_minus.setEnabled(true);
        } else if (Integer.parseInt(pedometer_sensitivity.getText().toString()) <= 1) {
            pedometer_plus.setEnabled(true);
            pedometer_minus.setEnabled(false);
        }

        if (prefs.isSoftPedometerOn()) {
            // 软件计步方式
            // screenlongon_switch.setChecked(true);
            screenlongon_switch.setChecked(prefs.isScreenLongOn());


            if (prefs.isSoftPedometerOn()) {
                PedometerCounterService.initAppService(mContext);
            } else {
                PedometerCounterService.releaseAppService();
            }
        } else {

            // 低版本的硬件计步
            if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                coDialog = showTipsDialog(mContext, "提示", "我知道了", "由于您系统版本过低，不支持手环绑定，APP开启软件计步！");
                // 屏蔽返回键按钮
                coDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            return true;
                        } else {
                            return false; // 默认返回 false
                        }
                    }
                });
                pedometer_switch.setChecked(true);
                isCheck = true;
                prefs.setIfSoftPedometerOn(true);
            } else {
                // 蓝牙计步方式
                PedometerCounterService.releaseAppService();
                screenlongon_switch.setChecked(false);
                prefs.setIsScreenLongOn(false);
            }

        }

        if (prefs.isSoftPedometerOn()) {
            pedometer_switch.setChecked(true);
            screenlongon_switch.setChecked(prefs.isScreenLongOn());
        } else {
            pedometer_switch.setChecked(false);
        }
        screenlongon_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (prefs.getMacAddress().length() >= 5) {
                    screenlongon_switch.setChecked(false);
                    showToast("硬件计步时不可打开屏幕常亮");
                } else {
                    prefs.setIsScreenLongOn(isChecked);
                    screenlongon_switch.setChecked(isChecked);
                }
            }

        });
        pedometer_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    prefs.setIfSoftPedometerOn(isChecked);
                    isCheck = isChecked;
                    if (isChecked) {
                        if (prefs.getMacAddress().length() >= 5) {

                            coDialog = DialogFactory.getDialogFactory().showSettingDialog(mContext, "无法启用软件计步", "请先取消绑定硬件", "取消", "取消绑定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    coDialog.dismiss();
                                    if (!coDialog.isShowing()) {
                                        pedometer_switch.setChecked(false);
                                    }
                                }
                            }, new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    coDialog.dismiss();
                                    startActivity(new Intent(mContext, MineDeviceActivity.class));
                                }
                            }, Color.BLACK, Color.BLACK);

                        } else {
                            Intent broadCast = new Intent(Preferences.ACTION_ENALBE_PEDOMETER);
                            broadCast.putExtra("ENABLEPEDOMETER", isChecked);
                            sendBroadcast(broadCast);

                            if (prefs.isSoftPedometerOn()) {
                                PedometerCounterService.initAppService(mContext);

                            } else {
                                PedometerCounterService.releaseAppService();
                            }

                        }

                    } else {
                        if (prefs.getMacAddress().length() < 5) {
                            coDialog = DialogFactory.getDialogFactory().showSettingDialog(mContext, "无法关闭", "请先连接手环再关闭", "取消", "搜索手环", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    coDialog.dismiss();
                                    if (!coDialog.isShowing()) {
                                        pedometer_switch.setChecked(true);
                                    }

                                }
                            }, new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    coDialog.dismiss();
                                    mHandler.sendMessage(mHandler.obtainMessage(0));

                                }
                            }, Color.BLACK, Color.BLACK);

                        }

                        Intent unablePodometer = new Intent(Preferences.ACTION_UNENALBE_PEDOMETER);
                        unablePodometer.putExtra("ENABLEPEDOMETER", isChecked);
                        sendBroadcast(unablePodometer);
                    }
                } else {
                    pedometer_switch.setChecked(true);
                    pedometer_switch.setEnabled(false);
                    showToast("您的安卓系统版本过低，只能使用软件计步！");

                }

            }
        });
    }


    // 解决方案
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 0:
                    startActivity(new Intent(PedometerActivity.this, MineDeviceActivity.class));
                    if (coDialog != null) {
                        coDialog.dismiss();
                    }

                    break;

                default:
                    break;
            }
            super.handleMessage(msg);

        }

    };

    protected void unBindDeviceNet() {// 后台同步解绑操作
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        client.toBindOff(this, params, new DeviceBindHandler() {

            @Override
            public void onSuccess(DeviceBind deviceBind) {
                super.onSuccess(deviceBind);

                if (deviceBind.code == 0) {
                    if (deviceBind.content.status.equals("0")) {
                        showToast("解绑成功");
                        PedometerCounterService.initAppService(mContext);
                        prefs.setIfSoftPedometerOn(true);
                        prefs.setMacAddress("");
                        prefs.setBlueDeviceName("");
                        pedometer_switch.setChecked(true);
                        coDialog.dismiss();
                    } else {
                        showToast("解绑失败");
                    }
                } else {// code不为0 发生异常
                    PedometerCounterService.initAppService(mContext);
                }

            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
            }
        });
    }

    public Dialog showTipsDialog(Context mContext, String title, String button, String content) {
        // TODO Auto-generated method stub
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_tips, null);
        final Dialog customDialog = new Dialog(mContext, R.style.commonDialog);
        WindowManager.LayoutParams localLayoutParams = customDialog.getWindow().getAttributes();
        customDialog.onWindowAttributesChanged(localLayoutParams);
        customDialog.setCanceledOnTouchOutside(true);
        customDialog.setCancelable(true);
        customDialog.setContentView(view);
        TextView tips_title = (TextView) view.findViewById(R.id.tips_title);
        tips_title.setText(title);
        TextView tips_content = (TextView) view.findViewById(R.id.tips_content);
        tips_content.setText(content);
        TextView tips_button = (TextView) view.findViewById(R.id.tips_button);
        tips_button.setText(button);
        tips_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unBindDeviceNet();
            }
        });
        customDialog.show();
        return customDialog;
    }


}
