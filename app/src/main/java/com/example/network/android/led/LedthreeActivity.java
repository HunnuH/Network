package com.example.network.android.led;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.network.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LedthreeActivity extends AppCompatActivity {
BufferedReader server_in;
PrintWriter server_out;
Socket server_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledthree);
        new LED_Three_Thread().start();
    }

    public void send_mssage(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message = " ";
                if(view.getId() == R.id.led1) {
                    message = "led_1";
                }else if(view.getId() == R.id.led2) {
                    message = "led_2";
                }else if(view.getId() == R.id.led3) {
                    message = "led_3";
                }else if(view.getId() == R.id.led4){
                    message = "led_4";
                }else {
                    message = "led_5";
                }
                server_out.println(message);
            }
        }).start();
    }

    class LED_Three_Thread extends Thread {
        public void run() {
            try {
                server_main = new Socket("192.168.0.16",12345);
                if(server_main!=null) {
                    io_init();
                }
                Thread t1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true) {
                            String msg;
                            try {
                                msg = server_in.readLine();
                                Log.d("network","서버 수신 : "+msg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                t1.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        void io_init() {
            try {
                server_in = new BufferedReader(new InputStreamReader(server_main.getInputStream()));
                server_out = new PrintWriter(server_main.getOutputStream(),true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}