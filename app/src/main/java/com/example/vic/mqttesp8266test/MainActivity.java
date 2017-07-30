package com.example.vic.mqttesp8266test;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    static String MQTTHOST="tcp://m20.cloudmqtt.com:19409";
    static String USERNAME="jwaxhzer";
    static String PASSWORD="j80AdYWHUAt5";

    String topicStr="/placa2/salidaDigital";
    String topicIn="/placa2/pulsador";

    MqttAndroidClient client;

    TextView subText;
    MqttConnectOptions options;

    Vibrator vibrator;
    Ringtone myRingtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subText=(TextView)findViewById(R.id.subText) ;
        vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);

        Uri uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
         myRingtone= RingtoneManager.getRingtone(getApplicationContext(),uri);


       // **********************************************
        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(),
                        MQTTHOST,       //"tcp://broker.hivemq.com:1883",
                        clientId);

        options = new MqttConnectOptions();

        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());



        try {
            IMqttToken token = client.connect(options);// IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                 //   Log.d(TAG, "onSuccess");
                    Toast.makeText(MainActivity.this, "connected!!!", Toast.LENGTH_SHORT).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                 //   Log.d(TAG, "onFailure");
                    Toast.makeText(MainActivity.this, "connection failed!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        //***************************************************

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                subText.setText(new String(message.getPayload()));

                vibrator.vibrate(500);
                myRingtone.play();

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
/*
    public void pub(View v){
        String topic = topicStr;
        String payload = "the payload";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
    */


    public void pubLedOn(View v){
        String topic = topicStr;
        String message = "ON";
       // byte[] encodedPayload = new byte[0];
        try {

            client.publish(topic, message.getBytes(),0,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void pubLedOff(View v){
        String topic = topicStr;
        String message = "OFF";
        // byte[] encodedPayload = new byte[0];
        try {

            client.publish(topic, message.getBytes(),0,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private  void setSubscription(){
        try {
             client.subscribe(topicIn,0);
        } catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void conn(View v){
        try {
            IMqttToken token = client.connect(options);// IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //   Log.d(TAG, "onSuccess");
                    Toast.makeText(MainActivity.this, "connected!!!", Toast.LENGTH_SHORT).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    //   Log.d(TAG, "onFailure");
                    Toast.makeText(MainActivity.this, "connection failed!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconn(View v){
        try {
            IMqttToken token = client.disconnect();// IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //   Log.d(TAG, "onSuccess");
                    Toast.makeText(MainActivity.this, "disconnected!!!", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    //   Log.d(TAG, "onFailure");
                    Toast.makeText(MainActivity.this, "could not disconnected...", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
