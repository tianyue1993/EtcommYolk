package etcomm.com.etcommyolk.entity;

/**
 * Created by zuoh on 2016/12/28.
 */
public class Weather extends Entity {

    public int code;
    public String message;
    public WeatherEntity content;

    public class WeatherEntity{

        public String aqi;
        public String qlty;
        public String temp;
        public String weather_status;

    }

}

//{
//        "code": 0,
//        "message": "加载成功",
//        "content": {
//        "aqi": "75", //空气质量指数
//        "qlty": "良", //空气状态
//        "temp": "3/-7", //温度
//        "weather_status": "晴" //天气状态
//        }
//        }
