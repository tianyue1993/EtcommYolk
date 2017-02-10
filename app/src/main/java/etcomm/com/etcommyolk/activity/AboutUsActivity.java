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
import etcomm.com.etcommyolk.utils.UpdateCheckUtils;
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
    private BroadcastReceiver receiver;
    private Dialog noticeDialog;
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


    //版本更新
    private void checkUpdate(String version) {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        params.put("version", version);
        cancelmDialog();
        showProgress(0, true);
        client.toUploadversion(this, params, new UpdateObjHandler() {

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }


            @Override
            public void onSuccess(UpdateObj commen) {
                super.onSuccess(commen);
                cancelmDialog();
                int code = commen.code;
                String message = commen.message;
                if (message.equals("已是最新版本")) {
                    Toast.makeText(mContext, "已是最新版本!", Toast.LENGTH_SHORT).show();
                    Log.i(tag, "message " + message);
                    return;
                }
                if (code == 45000) {
                    apkurl = commen.content.file;// getString("file");
                    UpdateCheckUtils.getInstanse().lookVersion(AboutUsActivity.this, true, commen);
                } else {
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
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
