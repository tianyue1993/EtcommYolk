package etcomm.com.etcommyolk.handler;

import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.entity.DaySignUp;
import etcomm.com.etcommyolk.exception.BaseException;

/**
 * Created by zuoh on 2016/12/28.
 */
public class DaySignUpHandler extends JsonResponseHandler {

    /**
     * 用于返回值content仅仅只有一个string数据格式
     */
    public DaySignUpHandler() {
        this(DEFAULT_CHARSET);
    }

    public DaySignUpHandler(String encoding) {
        super(encoding);
    }

    public void onSuccess(DaySignUp daySignUp) {

    }

    public void onFailure(BaseException exception) {

    }

    public void onAllow(Commen commen){

    }


    @Override
    public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (response.has("code") && !response.isNull("code")) {
                                int code = response.getInt("code");
                                String messege = response.getString("message");
                                if (code == 0 || code == 30005) {
                                    onSuccess(JSON.parseObject(response.toString(), DaySignUp.class));
                                } else if (code == 10000) {
                                    exceptionCode();
                                } else {
                                    onAllow(JSON.parseObject(response.toString(), Commen.class));
                                    //请求异常,弹出提示
                                    onFailure(new BaseException("Unexpected response " + response, -1));
                                    Toast.makeText(EtcommApplication.getContext(), messege, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                onFailure(new BaseException("Unexpected response " + response, -1));
                            }
                        } catch (JSONException e) {
                            onFailure(new BaseException(-1, e));
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        Log.d("onFailure JSONObject", statusCode + "---" + throwable + "---" + errorResponse);
        onFailure(new BaseException("网络请求失败，请重试", statusCode));
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        Log.d("onFailure JSONArray", statusCode + "---" + throwable + "---" + errorResponse);
        onFailure(new BaseException(statusCode, throwable));
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        Log.d("onFailure String", statusCode + "---" + throwable + "---" + responseString);
        onFailure(new BaseException(statusCode, throwable));
    }
}