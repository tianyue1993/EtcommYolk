package etcomm.com.etcommyolk.handler;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.entity.StructureContent;
import etcomm.com.etcommyolk.exception.BaseException;

/**
 * Created by zuoh on 2016/12/20.
 */
public class ActivationCodeHandler extends JsonResponseHandler {


    public ActivationCodeHandler() {
        this(DEFAULT_CHARSET);
    }

    public ActivationCodeHandler(String encoding) {
        super(encoding);
    }

    public void onSuccess(StructureContent commen) {

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
                                onSuccess(JSON.parseObject(response.toString(), StructureContent.class));
                            } else {
                                onFailure(new BaseException("Unexpected response " + response, -1));
                            }
                        } catch (Exception e) {
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