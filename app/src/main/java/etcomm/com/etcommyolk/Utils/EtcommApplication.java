package etcomm.com.etcommyolk.Utils;

import android.app.Application;
import android.os.Build;
import android.view.WindowManager;

/**
 * Created by zuohr on 2016/12/8.
 * 进行gradle数据处理及相关参数引用
 */
public class EtcommApplication extends Application {

    /**
     * 是否是第一次默认设置
     * 针对MainActivity默认设置Home为显示页面
     * <p/>
     * 每一次从其他页面返回MainActivity可见页面时，都会先初始化一下Home的初始值
     * 无故增多网络请求次数，因此添加限定
     */
    public static boolean isFirstSetDefault = true;


}
