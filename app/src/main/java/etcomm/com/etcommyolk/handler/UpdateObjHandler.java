package etcomm.com.etcommyolk.handler;

import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.entity.UpdateObj;
import etcomm.com.etcommyolk.exception.BaseException;

/**
 * Created by Hello on 2016/12/24.
 */
public class UpdateObjHandler extends JsonResponseHandler {


    public UpdateObjHandler() {
        this(DEFAULT_CHARSET);
    }

    public UpdateObjHandler(String encoding) {
        super(encoding);
    }

    public void onSuccess(UpdateObj commen) {

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
                        if (response.has("code") && !response.isNull("code")) {
                            onSuccess(JSON.parseObject(response.toString(), UpdateObj.class));
                        } else {
                            onFailure(new BaseException("Unexpected response " + response, -1));
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        Log.d("onFailure JSONObject", statusCode + "---" + throwable + "---" + errorResponse);
        onFailure(new BaseException("无网络连接，请查看网络配置", statusCode));
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