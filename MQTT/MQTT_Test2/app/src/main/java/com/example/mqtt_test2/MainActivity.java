package com.example.mqtt_test2;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private final String host = "tcp://192.168.18.141:1883";
    private final String userName = "admin";
    private final String passWord = "public";
    private final String mqtt_id="client_2";
    private  int cnt = 0;
    private Handler handler;
    private MqttClient client;
    private final String mqtt_sub_topic = "first";
    private final String mqtt_pub_topic ="second";
    private MqttConnectOptions options;
    private ScheduledExecutorService scheduler;

    private boolean is_loop=false;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text1 = findViewById(R.id.textView);
        Button button1=findViewById(R.id.btn1);
        Button button2=findViewById(R.id.btn2);

        scheduler = Executors.newSingleThreadScheduledExecutor();


        init();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!client.isConnected()) {
                    Mqtt_connect();
                }
            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!is_loop) {
                    is_loop=true;
                    scheduler.scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                            publishmessageplus(mqtt_pub_topic,"第二个客户端发送的信息"+cnt);
                            cnt++;

                        }
                    }, 0, 10 * 1000, TimeUnit.MILLISECONDS);
                    text1.setText("Start Loop Pub");
                }
                else{
                    is_loop=false;
                    scheduler.shutdown();
                    text1.setText("Finished Loop Pub");
                }
            }
        });

        //startReconnect();

        handler = new Handler() {
            @SuppressLint({"SetTextIl8n", "HandlerLeak"})
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1: //开机校验更新回传
                        break;
                    case 2: //反馈回转
                        break;
                    case 3: //MQTT收到消息回传
                        text1.setText(msg.obj.toString());
                        break;
                    case 30: //连接失败
                        Toast.makeText(MainActivity.this,"连接失败",Toast.LENGTH_SHORT).show();
                        break;
                    case 31: //连接成功
                        Toast.makeText(MainActivity.this,"连接成功",Toast.LENGTH_SHORT).show();
                        try {
                            client.subscribe(mqtt_sub_topic,2);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                        Log.i(mqtt_id,Integer.toString(cnt));
                        publishmessageplus(mqtt_pub_topic,"第二个客户端发送的信息:"+cnt);
                        cnt++;

                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void init() {
        try {
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(host, mqtt_id, new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);

            //设置回调
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    System.out.println("connectionLost----------");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    //subscribe后得到的消息会执行到这里面
                    System.out.println("messageArrived----------");
                    Message msg = new Message();
                    msg.what = 3;
                    msg.obj = topicName + "---" + message.toString();
                    handler.sendMessage(msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void Mqtt_connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!(client.isConnected())){
                        System.out.println("new connect");
                        client.connect(options);
                        Message msg = new Message();
                        msg.what=31;
                        handler.sendMessage(msg);
                    }
                    else{
                        System.out.println("re-connect");

                        //即使连接上也要先断开再重新连接
                        client.disconnect();  //不这样就重连会报错
                        client.connect(options);
                        Message msg = new Message();
                        msg.what=31;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 30;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    private void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!client.isConnected()) {
                    Mqtt_connect();
                }
            }
        }, 0, 10 * 1000, TimeUnit.MILLISECONDS);
    }

    private void publishmessageplus(String topic,String message2){
        if (client == null || !client.isConnected()) {
            return;
        }
        MqttMessage message = new MqttMessage();
        message.setPayload(message2.getBytes());
        try {
            client.publish(topic,message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}