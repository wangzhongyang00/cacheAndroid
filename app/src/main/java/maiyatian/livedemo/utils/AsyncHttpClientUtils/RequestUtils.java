package maiyatian.livedemo.utils.AsyncHttpClientUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

/**
 * Created by 王中阳 on 2016/2/14.
 */
public class RequestUtils {
    public static AsyncHttpClient client = new AsyncHttpClient();

    public static void ClientGet(String url, NetCallback cb) {
        client.get(url, cb);
    }

    public static void ClientPost(String url, RequestParams requestParams, NetCallback cb) {
        client.post(url, requestParams, cb);
    }
}
