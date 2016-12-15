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
import etcomm.com.etcommyolk.exception.BaseException;

public class CommenHandler extends JsonResponseHandler {

    /**
     * 用于返回值content仅仅只有一个string数据格式
     */
    public CommenHandler() {
        this(DEFAULT_CHARSET);
    }

    public CommenHandler(String encoding) {
        super(encoding);
    }

    public void onSuccess(Commen commen) {

    }

    public void onFailure(BaseException exception) {
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
                                if (code == 0) {
                                    onSuccess(JSON.parseObject(response.toString(), Commen.class));
                                } else if (code == 10000) {
                                    exceptionCode();
                                } else {
                                    //请求异常,弹出提示
                                    onFinish();
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
