package etcomm.com.etcommyolk.entity;

/**
 * Created by zuoh on 2016/12/29.
 */
public class DeviceBind  extends Entity {
    public int code;
    public String message;
    public Device content;

        public class Device {
            public String status;
            public String text;
        }
}
//{
//        "code": 0,
//        "message": "加载成功",
//        "content": {
//        "status": "1", //绑定状态 1：自己绑定 -1：已被别人绑定
//"status": "0",
//        "text": "已解绑"
//        "text": "绑定成功"
//        }
//        }