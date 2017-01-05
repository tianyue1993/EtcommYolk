package etcomm.com.etcommyolk.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;


import java.io.File;
import java.io.IOException;

import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.UpdateObj;
import etcomm.com.etcommyolk.widget.DialogFactory;

public class UpdateCheckUtils {


    /**
     * ***** 单例 **********
     */
    private UpdateCheckUtils() {
    }

    private static UpdateCheckUtils updateCheckUtils;

    public static UpdateCheckUtils getInstanse() {
        if (updateCheckUtils == null) {
            updateCheckUtils = new UpdateCheckUtils();
        }
        return updateCheckUtils;
    }

    private HttpHandler updateHandler;
    private TextView updateTV;
    private AlertDialog upDatadialog;
    private SeekBar pb;
    Activity activity;
    boolean isMain;//是否是从首页传送过来的
    private boolean isLoading = false;//是否正在下载Apk
    private Dialog noticeDialog;
    private boolean flag = true;

    public void lookVersion(final Activity activity1, boolean isMain, UpdateObj updateObj) {
        this.activity = activity1;
        this.isMain = isMain;
        if (isLoading) {
            //正在下载中，将对应布局继续弹出
            if (upDatadialog != null) {
                upDatadialog.show();
            }
        } else {
            initUpDate(updateObj);
        }
    }


    protected void initUpDate(UpdateObj updateObj) {
        String versionStr = getAppVersions();
        String versionServiceStr = updateObj.content.version;
        if (InterfaceUtils.compareVersion(versionServiceStr, versionStr) > 0) {
            showUpdateDialog(updateObj);
        } else {
            if (!isMain) {//从首页传送过来不需要提示
                Toast.makeText(activity, "当前为最新版本哦", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getAppVersions() {// 当前应用的版本号
        PackageManager manager = activity.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(
                    activity.getPackageName(), 0);
            return info.versionName;
        } catch (NameNotFoundException e) {
            // can't run
            e.printStackTrace();
            return "" + 1.0;
        }
    }

    private void showUpdateDialog(final UpdateObj version) {

        noticeDialog = DialogFactory.getDialogFactory().showUpdateVersionDialog(activity, activity.getString(R.string.update_title), version.content.description, activity.getString(R.string.update_later), activity.getString(R.string.update_now), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "建议现在更新", Toast.LENGTH_SHORT).show();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == true) {
                    if (Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        isLoading = true;
                        File file = new File(Environment
                                .getExternalStorageDirectory()
                                + "/EtcommYolk", "EtcommYolk.apk");
                        if (!file.exists()) {
                            try {
                                file.createNewFile();
                            } catch (IOException e) {
                            }
                        } else {
                            //若是文件夹存在，删除文件夹中东西，重新下载
                            if (deleteDir(file)) {
                                try {
                                    file.createNewFile();
                                } catch (IOException e) {
                                }
                            }
                        }
                        HttpUtils finalHttp = new HttpUtils();
                        updateHandler = finalHttp.download(
                                version.content.file,
                                file.getAbsolutePath(), true, true,
                                new RequestCallBack<File>() {
                                    @Override
                                    public void onStart() {
                                        // updateTV.setVisibility(View.VISIBLE);
                                        if (upDatadialog != null) {
                                            upDatadialog.dismiss();
                                        }
                                        showUpdataProcess(); //这二个顺序是要这样的
                                        updateTV.setText("连接中...");
                                    }

                                    @Override
                                    public void onLoading(long total,
                                                          long current,
                                                          boolean isUploading) {
                                        int process = (int) (current * 100 / total);
                                        updateTV.setText(process
                                                + "%");
                                        pb.setMax((int) total);
                                        pb.setProgress((int) current);
                                        updateTV.setX(process * 8f);
                                    }

                                    @Override
                                    public void onFailure(com.lidroid.xutils.exception.HttpException error, String msg) {
                                        if (upDatadialog != null) {
                                            upDatadialog.dismiss();
                                        }
                                        Toast.makeText(activity, "下载失败...", Toast.LENGTH_SHORT).show();
                                        isLoading = false;
                                    }


                                    @Override
                                    public void onSuccess(
                                            ResponseInfo<File> responseInfo) {
                                        // updateTV.setVisibility(View.GONE);
                                        isLoading = false;
                                        if (upDatadialog != null) {
                                            upDatadialog.dismiss();
                                        }

                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 加这个
                                        intent.addCategory("android.intent.category.DEFAULT");
                                        intent.setDataAndType(
                                                Uri.fromFile(responseInfo.result),
                                                "application/vnd.android.package-archive");
                                        activity.startActivity(intent);
                                    }
                                });
                    } else {
                        isLoading = false;
                        Toast.makeText(activity, "SD卡不可用,请检查", Toast.LENGTH_SHORT).show();
                    }
                }
                noticeDialog.dismiss();
            }
        }, R.color.switch_texton_color, R.color.switch_texton_color);
        if (version.content.force.equals("1")){
            noticeDialog.setCancelable(false);
        }else {
            noticeDialog.setCancelable(true);

        }
    }

    private void showUpdataProcess() {
        Builder builder = new Builder(activity);
        builder.setCancelable(false);
        View view = View.inflate(activity, R.layout.dialog_enter_pwd, null);
        updateTV = (TextView) view.findViewById(R.id.plan_text);
        upDatadialog = builder.create();
        upDatadialog.setView(view, 0, 0, 0, 0);
        pb = (SeekBar) view.findViewById(R.id.pb_updata);
        pb.setEnabled(false);


        view.findViewById(R.id.bt_hide).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                upDatadialog.dismiss();
            }
        });
        view.findViewById(R.id.bt_cancleok).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //取消升级
                //调用stop()方法停止下载
                updateHandler.cancel();
                upDatadialog.dismiss();
                updateCheckUtils = null;
                if (EtcommApplication.MUSTUPDATE) {
                    EtcommApplication.MUSTUPDATE = false;
                    activity.finish();
                }
            }
        });
        upDatadialog.show();
    }


    public void closeMissDialog() {
        if (upDatadialog != null) {
            //隐藏窗口
            upDatadialog.dismiss();
            //调用stop()方法停止下载
            updateHandler.cancel();
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
}
