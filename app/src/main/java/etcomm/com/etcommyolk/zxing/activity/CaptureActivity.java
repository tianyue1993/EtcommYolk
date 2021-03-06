package etcomm.com.etcommyolk.zxing.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Timer;
import java.util.Vector;

import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.activity.BaseActivity;
import etcomm.com.etcommyolk.utils.NetUtil;
import etcomm.com.etcommyolk.zxing.camera.CameraManager;
import etcomm.com.etcommyolk.zxing.decoding.CaptureActivityHandler;
import etcomm.com.etcommyolk.zxing.decoding.InactivityTimer;
import etcomm.com.etcommyolk.zxing.view.ViewfinderView;

public class CaptureActivity extends Activity implements Callback, View.OnClickListener {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private String contentStr;
    private int orderId = -1;
    private Button scan_redo_bt;
    private Button scan_cancel_bt;

    protected void initCreate() {
        CameraManager.init(CaptureActivity.this.getApplication());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.main);
        super.onCreate(savedInstanceState);
        CameraManager.init(CaptureActivity.this.getApplication());
        initCreate();
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;
        playBeep = true;
        AudioManager audioService = (AudioManager) CaptureActivity.this.getSystemService(CaptureActivity.this.AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(CaptureActivity.this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    public void handleDecode(Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
        Intent intent;
        viewfinderView.drawResultBitmap(barcode);
        playBeepSoundAndVibrate();
        //得到扫描结果
        contentStr = obj.getText();
        boolean boo = NetUtil.isConnected(CaptureActivity.this);
        //比较后台所给唯一标示
        if (contentStr.contains("365happybonus.com")) {
            try {
                String[] strings = contentStr.split("\\:");

                Intent sendData = new Intent();
                sendData.putExtra("code", strings[1]);
                setResult(5, sendData);
                finish();
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);
            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };


    private void initView() {
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        scan_redo_bt = (Button) findViewById(R.id.scan_redo_bt);
        scan_cancel_bt = (Button) findViewById(R.id.scan_cancel_bt);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        initOnClickListener();
        EtcommApplication.addActivity(this);
    }

    /**
     * 初始化点击事件监听器
     */
    private void initOnClickListener() {
        scan_redo_bt.setOnClickListener(this);
        scan_cancel_bt.setOnClickListener(this);
    }


    @Override
    public void onStop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onStop();
    }

    //启动一个后台计时器。每隔X秒检查一下是否开启了相机权限，如果没有开启。就返回上一个界面
    private Timer timer;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_cancel_bt:
                CaptureActivity.this.finish();
                break;

            case R.id.scan_redo_bt:
                onPause();
                onResume();
                break;
            default:
                break;
        }
    }

}