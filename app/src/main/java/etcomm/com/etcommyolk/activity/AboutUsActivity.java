package etcomm.com.etcommyolk.activity;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.UpdateObj;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.UpdateObjHandler;
import etcomm.com.etcommyolk.utils.StringUtils;
import etcomm.com.etcommyolk.widget.DialogFactory;

/**
 * zuoh
 */
public class AboutUsActivity extends BaseActivity {

    @Bind(R.id.aboutus_version)
    TextView aboutusVersion;
    @Bind(R.id.version_update)
    TextView versionUpdate;
    protected String apkurl;
    private Dialog noticeDialog;
    private BroadcastReceiver receiver;
    private boolean flag = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);

        initView();

    }

    private void initView() {
        EtcommApplication.addActivity(this);
        setTitleTextView("关于我们", null);
        aboutusVersion.setText(getApplicationName() + " V" + getVersion());
    }

    @OnClick({R.id.version_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.version_update:
                checkUpdate(getVersion());
                break;
        }
    }



    private void checkUpdate(String version) {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        params.put("version", version);
        client.toUploadversion(this, params, new UpdateObjHandler() {
            @Override
            public void onSuccess(UpdateObj commen) {
                super.onSuccess(commen);

                int code = commen.code;
                String message = commen.message;
                if (message.equals("已是最新版本")) {
                    Toast.makeText(mContext, "已是最新版本!", Toast.LENGTH_SHORT).show();
                    Log.i(tag, "message " + message);
                    return;
                }
                if (code == 45000) {
                    String versiononServer = commen.content.version;// ("version");
                    String dec = commen.content.description;// getString("versiononServer");
                    apkurl = commen.content.file;// getString("file");
                    // String versionOnServer = content;
                    Long[] longVersionOnServer = stringVersionToLong(versiononServer);
                    Long[] longVersionOnClient = stringVersionToLong(getVersion().replaceAll("v", ""));
                    System.out.println(longVersionOnServer[0] + ",,,," + longVersionOnClient[0] + ",,," + longVersionOnServer[1] + ",,,," + longVersionOnClient[1]);
                    if (longVersionOnServer[0] > 0 && longVersionOnClient[0] > 0) {// 粗略检测version值合法性
                        if (longVersionOnServer[0] > longVersionOnClient[0]) {
                            showNoticeDialog(dec);
                            return;
                        } else if (longVersionOnServer[0] == longVersionOnClient[0] && longVersionOnServer[1] > longVersionOnClient[1]) {
                            showNoticeDialog(dec);
                            return;
                        } else {
                            Log.i(tag, "版本没有更新 ");
                            Toast.makeText(mContext, "已是最新版本!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.i(tag, "版本号有问题 ");
                    }
                } else {// code不为0 发生异常
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
            }
        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    /**
     * 根据版本号返回new long[3]。用于比较版本新旧。
     *
     * @param version
     * @return
     */
    public Long[] stringVersionToLong(String version) {
        Long[] longVersion = new Long[2];
        try {
            if (!StringUtils.isEmpty(version)) {
                String[] temp = version.split("\\.");
                if (temp != null && temp.length == 2) {
                    longVersion[0] = Long.parseLong(temp[0]);
                    longVersion[1] = Long.parseLong(temp[1]);
                }
            }
        } catch (NumberFormatException e) {
            Log.e(tag, "readLocalVersion " + e.toString());
            e.printStackTrace();
        }
        return longVersion;
    }

    private void showNoticeDialog(String updatecontent) {

        noticeDialog = DialogFactory.getDialogFactory().showUpdateVersionDialog(this, getString(R.string.update_title), updatecontent, getString(R.string.update_later), getString(R.string.update_now), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "建议现在更新", Toast.LENGTH_SHORT).show();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == true) {
                    intoDownloadManager();
                }
                Toast.makeText(mContext, "后台正在下载安装包", Toast.LENGTH_LONG).show();
            }
        }, R.color.switch_texton_color, R.color.switch_texton_color);
        noticeDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                } else {
                    return false; // 默认返回 false
                }
            }
        });
    }

    private void downloadApk() {
        Log.i(tag, "apkurl:  " + apkurl);
        intoDownloadManager();
    }

    private void intoDownloadManager() {
        flag = false;
        DownloadManager dManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Log.i(tag, "intoDownloadManager");
        Uri uri = Uri.parse(apkurl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        // 设置下载路径和文件名
        request.setDestinationInExternalPublicDir("dcare", "dcare.apk");
        request.setDescription(getString(R.string.app_name) + "更新");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        // 设置为可见和可管理
        request.setVisibleInDownloadsUi(true);
        long refernece = dManager.enqueue(request);
        // 把当前下载的ID保存起来
        SharedPreferences sPreferences = getSharedPreferences("downloadapp", 0);
        sPreferences.edit().putLong("plato", refernece).commit();

    }

    public class DownLoadBroadcastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            SharedPreferences sPreferences = context.getSharedPreferences("downloadapp", 0);
            long refernece = sPreferences.getLong("plato", 0);
            if (refernece == myDwonloadID) {
                String serviceString = Context.DOWNLOAD_SERVICE;
                DownloadManager dManager = (DownloadManager) context.getSystemService(serviceString);
                Intent install = new Intent(Intent.ACTION_VIEW);
                Uri downloadFileUri = dManager.getUriForDownloadedFile(myDwonloadID);
                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            }
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getApplicationName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }
}
