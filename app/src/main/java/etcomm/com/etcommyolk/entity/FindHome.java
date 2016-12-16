package etcomm.com.etcommyolk.entity;

import java.util.ArrayList;

/**
 * Created by ${tianyue} on 2016/12/15.
 */
public class FindHome extends Entity {

    public int code;
    public String message;
    public Content content;

    public class Content {
        public ArrayList<RecommendItems> carousel;//广告轮播
        public ArrayList<App_module> app_module;//是否显示爱康企业图标
        public ArrayList<RecommendItems> recommend;//三个推荐条目
        public ArrayList<RecommendItems> activity;//首页最下面列表

    }

    @Override
    public String toString() {
        return "FindHome{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", content=" + content +
                '}';
    }
}
