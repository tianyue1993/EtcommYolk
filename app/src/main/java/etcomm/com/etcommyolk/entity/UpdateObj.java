package etcomm.com.etcommyolk.entity;

/**
 * Created by zuoh on 2016/12/24.
 */
public class UpdateObj extends Entity  {

    public int code;
    public String message;
    public Content content;

    public class Content {
        public int id;//": 1, //版本标识id
        public String version;//": "1.0", //当前版本号
        public String description;//": "第一个版本", //描述
        public String file;//": "http://113.59.227.10:81/upload/android/Dcare.apk", //地址
        public String created_at;//": "2016-01-18 13:21:46" //更新时间
    }

}
