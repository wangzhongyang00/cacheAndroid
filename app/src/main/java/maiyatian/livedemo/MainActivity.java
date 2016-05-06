package maiyatian.livedemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import maiyatian.livedemo.UI.IResultAdapter;
import maiyatian.livedemo.utils.AsyncHttpClientUtils.NetCallback;
import maiyatian.livedemo.utils.AsyncHttpClientUtils.NetConstantInfo;
import maiyatian.livedemo.utils.AsyncHttpClientUtils.RequestUtils;
import maiyatian.livedemo.utils.SPUtils;
import maiyatian.livedemo.widget.DividerItemDecoration;
import maiyatian.livedemo.widget.FullyLinearLayoutManager;

public class MainActivity extends Activity implements IResultAdapter.IClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = "MainActivity";
    MainActivity context;
    TextView textView;
    String key = "weather";
    RecyclerView recyclerView;
    List<IResult> datas;
    //5网络加载更多
    private SwipeRefreshLayout mSwipeLayout;
    private IResultAdapter mAdapter;
    String[] citys = {"北京", "上海", "广州", "德州"};
    public final static String APP_FILE = "app_file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        textView = (TextView) findViewById(R.id.weather);
        if (SPUtils.getParam(context, key, "") != null) {
            textView.setText(SPUtils.getParam(context, key, "") + "");
        }
        datas = new ArrayList<>();
        updateList();
    }

    private void updateList() {
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        mSwipeLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) context.findViewById(R.id.rv_amusenews_list);
        recyclerView.getParent().requestDisallowInterceptTouchEvent(true);
        recyclerView.setLayoutManager(new FullyLinearLayoutManager(recyclerView.getContext()));
        //子布局装饰
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //适配器未装填内容
        recyclerView.setNestedScrollingEnabled(true);
        mAdapter = new IResultAdapter(this, this);
        recyclerView.setAdapter(mAdapter);
        //刷新数据
        datas = new ArrayList<>();
        if (isExistDataCache(APP_FILE)) {
            HashSet<IResult> iResults = new HashSet<>(); //不允许重复，是无序的
            iResults = (HashSet<IResult>) readObject(APP_FILE);
            Iterator<IResult> iterator = iResults.iterator();
            while (iterator.hasNext()) {
                datas.add(iterator.next());
            }
            mAdapter.setMdatas(datas);
            return;
        }
        updateFromNet();
    }


    private void updateFromNet() {
        datas.clear();
        for (int i = 0; i < citys.length; i++) {
            RequestUtils.ClientGet(NetConstantInfo.URL_BASE + citys[i] + NetConstantInfo.URL_END, new NetCallback() {
                @Override
                public void mySuccess(JSONObject jsonObject) {
                    Gson gson = new Gson();
                    IResult iResult = gson.fromJson(jsonObject.toString(), IResult.class);
                    datas.add(iResult);
                    mAdapter.setMdatas(datas);
                    saveLocalApps(datas);
                }

                @Override
                public void myFailure(Throwable throwable) {
                    Toast.makeText(MainActivity.this, throwable.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // 缓存本地第三方文件
    private void saveLocalApps(final List<IResult> appInfoList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashSet<IResult> set = new HashSet<IResult>();
                set.addAll(appInfoList);
                saveObject(set, APP_FILE);
            }
        }).start();

    }

    public void haha(View v) {
        RequestUtils.ClientGet(NetConstantInfo.URL_BASE + "北京" + NetConstantInfo.URL_END, new NetCallback() {
            @Override
            public void mySuccess(JSONObject jsonObject) {
                Gson gson = new Gson();
                IResult iResult = gson.fromJson(jsonObject.toString(), IResult.class);
                textView.setText(iResult.getResult().getSk().getTime());
                SPUtils.setParam(context, key, iResult.getResult().getSk().getTime());
            }

            @Override
            public void myFailure(Throwable throwable) {
                Toast.makeText(MainActivity.this, throwable.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClicked(int position) {

    }

    public synchronized boolean saveObject(Serializable ser, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = openFileOutput(file, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    public synchronized Serializable readObject(String file) {
        if (!isExistDataCache(file))
            return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
            // 反序列化失败 - 删除缓存文件
            if (e instanceof InvalidClassException) {
                File data = getFileStreamPath(file);
                data.delete();
            }
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    private boolean isExistDataCache(String cachefile) {
        boolean exist = false;
        File data = getFileStreamPath(cachefile);
        if (data.exists())
            exist = true;
        return exist;
    }

    @Override
    public void onRefresh() {
        updateFromNet();
    }
}
