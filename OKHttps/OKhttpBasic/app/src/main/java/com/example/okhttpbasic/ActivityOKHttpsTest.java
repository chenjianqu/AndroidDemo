package com.example.okhttpbasic;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.zhy.http.okhttp.OkHttpUtils;


public class ActivityOKHttpsTest extends AppCompatActivity {
    private final int GET=1;
    private final int POST = 2;
    final String TAG = ActivityOKHttpsTest.class.getSimpleName();

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    Button btn_get;
    Button btn_post;
    TextView tv_result;

    OkHttpClient client = new OkHttpClient();

    Handler handler = new Handler(){
      @SuppressLint("HandlerLeak")
      @Override
      public void handleMessage(Message msg){
          super.handleMessage(msg);
          switch (msg.what){
              case GET:
              case POST:
                  String result =(String) msg.obj;
                  tv_result.setText(result);
                  break;
          }
      }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttps_test);

        btn_get = findViewById(R.id.btn_get);
        btn_post = findViewById(R.id.btn_post);
        tv_result = findViewById(R.id.tv_result);

        ///使用原生的okhttp get请求网络数据
        btn_get.setOnClickListener((view)->{
            getDataFromGet();
        });

        ///使用原生的okhttp post请求网络
        btn_post.setOnClickListener((view)->{
            getDataFromPost();
        });

    }


    void getDataFromGet(){
        new Thread(()->{
            try {
                String result= get("http://t.weather.sojson.com/api/weather/city/101240301");
                Log.i(TAG,result);

                Message msg=  Message.obtain();
                msg.what = GET;
                msg.obj = result;
                handler.sendMessage(msg);


                //tv_result.setText(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Get请求
     * @param url
     * @return
     * @throws IOException
     */
    String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


    void getDataFromPost() {
        new Thread(() -> {
            try {
                String result= post("http://t.weather.sojson.com/api/weather/city/101240301","");
                Log.i(TAG,result);
                Message msg=  Message.obtain();
                msg.what = POST;
                msg.obj = result;
                handler.sendMessage(msg);

            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }


    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }




    public void getDataByOkHttpUtils() {
        String url = "http://www.zhiyun-tech.com/App/Rider-M/changelog-zh.txt";
        url="http://www.391k.com/api/xapi.ashx/info.json?key=bd_hyrzjjfb4modhj&size=10&page=1";
        OkHttpUtils
                .get()
                .url(url)
                .id(100)
                .build()
                .execute(new MyStringCallback());
    }



/*

    public class MyStringCallback extends StringCallback
    {
        @Override
        public void onBefore(Request request, int id)
        {
            setTitle("loading...");
        }

        @Override
        public void onAfter(int id)
        {
            setTitle("Sample-okHttp");
        }

        @Override
        public void onError(Call call, Exception e, int id)
        {
            e.printStackTrace();
            tv_result.setText("onError:" + e.getMessage());
        }

        @Override
        public void onResponse(String response, int id)
        {
            Log.e(TAG, "onResponse：complete");
            tv_result.setText("onResponse:" + response);

            switch (id)
            {
                case 100:
                    Toast.makeText(ActivityOKHttpsTest.this, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    Toast.makeText(ActivityOKHttpsTest.this, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void inProgress(float progress, long total, int id)
        {
            Log.e(TAG, "inProgress:" + progress);
            //mProgressBar.setProgress((int) (100 * progress));
        }
    }

*/


}