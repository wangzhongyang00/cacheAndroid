package maiyatian.livedemo.utils.AsyncHttpClientUtils;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

/**
 * Created by 王中阳 on 2016/2/14.
 */
public abstract class NetCallback extends JsonHttpResponseHandler {

    @Override
    public void onStart() {
        Log.e("info", "start");
        super.onStart();
    }

    @Override
    public void onSuccess(JSONObject jsonObject) {
        super.onSuccess(jsonObject);
        mySuccess(jsonObject);
    }

    @Override
    public void onFailure(Throwable throwable, String s) {
        super.onFailure(throwable, s);
        myFailure(throwable);
    }

    //abstract只声明不实现 由子类实现
    public abstract void mySuccess(JSONObject jsonObject);

    public abstract void myFailure(Throwable throwable);
}
