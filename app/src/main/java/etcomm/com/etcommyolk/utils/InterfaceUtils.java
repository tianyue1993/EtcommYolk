package etcomm.com.etcommyolk.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.igexin.sdk.PushManager;

import etcomm.com.etcommyolk.entity.Login;

/**
 * Created by zuoh on 2016/12/16.
 */
public class InterfaceUtils {

    /**
     * @Title: readDeviceId
     * @Description: 获取手机信息？ 后期优化
     * @param @param ctx
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     */
    public static String readDeviceId(Context ctx) {
        String device_id = "" + android.provider.Settings.Secure.getString(ctx.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        if (device_id.length() < 5) {
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(ctx.TELEPHONY_SERVICE);
            String tmdevice_id = "" + tm.getDeviceId();
            if (tmdevice_id.length() < 5) {
                String deviceSereisId = tm.getSimSerialNumber();
                if (deviceSereisId.length() < 5) {
                    String m_szDevIDShort = "35" + // we make this look like a
                            // valid IMEI
                            Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10 + Build.USER.length() % 10; // 13
                    // digits
                    device_id = "imei" + m_szDevIDShort;
                } else {
                    device_id = "tmSimSerialNumber" + deviceSereisId;
                }
            } else {
                device_id = "tmDeviceId" + tmdevice_id;
            }
        }
        return device_id;
    }

    /**
     * 获取客户端ID
     * @return
     */
    public static String getClientId(Context context){
        return PushManager.getInstance().getClientid(context.getApplicationContext());
    }



}
