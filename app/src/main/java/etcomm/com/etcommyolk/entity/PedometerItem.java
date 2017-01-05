package etcomm.com.etcommyolk.entity;

import java.util.ArrayList;

/**
 * Created by zuoh on 2016/12/28.
 */
public class PedometerItem extends Entity {

    public int code;
    public String message;
    public Pedometer content;



    public class Pedometer{

        public String type;
        public String device_mac;
        public ArrayList<PedometerData> data;

        public class PedometerData{

            public String date;
            public String target;
            public String step;
            public String calorie;
            public String distance;
            public String total_time;
            public String calorie_text;
            public String distance_text;

        }


    }

//    {
//        "code": 0,
//            "message": "ok",
//            "content": {
//        "type": 1, //是否为903用户
//                "device_mac": "", //903设备号
//                "data": [
//        {
//            "date": "07/01", //日期
//                "target": 10000, //目标步数
//                "step": 8894,  //步数
//                "calorie": 397,  //卡路里
//                "distance": "6.9", //距离
//                "total_time": 1.3, //总时长
//                "calorie_text": "≈消耗了1个冰淇淋的热量",
//                "distance_text": "6.9公里，相当于绕故宫2圈"
//        },
//        ...
//        ]
//    }
//    }

}
