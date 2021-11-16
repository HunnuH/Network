package com.example.network.arduino;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.network.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LEDControlActivity extends AppCompatActivity {
    BufferedReader serverin; //서버에서 보내오는 메시지 읽기
    PrintWriter serverout;//서버로 메시지를 보내기
    Socket server;
    Handler handler;
    TextView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledcontrol);
        new LedThread().start();
        handler = new Handler(Looper.myLooper());
        view = findViewById(R.id.showdata);
    }

    public void send_msg(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message = " ";
                if(view.getId() == R.id.led_on) {
                    message = "led_on";
                }else {
                    message = "led_off";
                }
                serverout.println(message);
            }
        }).start();
    }

    class LedThread extends Thread{
       public void run() {
           try {
               server = new Socket("192.168.0.16",12345);
               if(server!=null) {
                   io_init();
               }
               Thread t1 = new Thread(new Runnable() {
                   @Override
                   public void run() {
                       while(true) {
                           String msg;
                           try {
                               msg = serverin.readLine();
                               Log.d("network","서버로 부터 수신된 메세지 : "+msg);
                               handler.post(new Runnable() {
                                   @Override
                                   public void run() {
                                        view.append(msg+"\n");
                                   }
                               });
                           } catch (IOException e) {
                               try {
                                   serverin.close();
                                   serverout.close();
                                   server.close();
                               } catch (IOException ioException) {
                                   ioException.printStackTrace();
                               }
                               break;
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
                serverin = new BufferedReader(new InputStreamReader(server.getInputStream()));
                serverout = new PrintWriter(server.getOutputStream(),true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}