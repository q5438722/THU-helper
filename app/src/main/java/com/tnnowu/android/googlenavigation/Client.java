package com.tnnowu.android.googlenavigation;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class Client implements Runnable
{
    Handler handler;
    public Handler revHandler;

    public Client(Handler handler) {
        this.handler = handler;
    }
    public Client(){}

    private Socket sock;
    private Thread thread;
    BufferedReader in;
    private PrintWriter out;
    private final static int DEFAULT_PORT = 1234;
    private final static String HOST = "47.93.199.31";

    String cmd;
    Vector<String>ret;

    public Vector<String> startConnect(String str) throws Exception
    {
        cmd = str;
        ret = new Vector<>();
//        try {
//            sock = new Socket(HOST, DEFAULT_PORT);
//            System.out.println("Connection ok");
//            in = new BufferedReader(
//                    new InputStreamReader(sock.getInputStream()));
//            out = new java.io.PrintWriter(sock.getOutputStream());
//        } catch(Exception e) {
//            String tmp = e.getMessage();
//            e.printStackTrace();
//            System.out.println("Connection failed");
//        }
        thread = new Thread(this);
        thread.start();
        thread.join();

        return ret;
    }

//    @SuppressLint("HandlerLeak")
    public void run()
    {
        try {
            sock = new Socket(HOST, DEFAULT_PORT);
            in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            out = new java.io.PrintWriter(sock.getOutputStream());

            try
            {
                sendMsg(cmd);
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            while (true)
            {
                try
                {
                    String msg = receiveMsg();
                    if(msg.equals("!!!"))
                        break;
                    ret.add(msg);
                } catch (IOException e)
                {
                    e.printStackTrace();
                } catch (Exception ei) {}
            }
//            new Thread()
//            {
//                @Override
//                public void run()
//                {
//                    try {
//                        while (true) {
//                            String str = in.readLine();
//                            Message msg = new Message();
//                            msg.what = 0x123;
//                            msg.obj = str;
//                            handler.sendMessage(msg);
//                            if (str.equals("!!!"))
//                                break;
//                        }
//                    }
//                    catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.start();
//
//            Looper.prepare();
//            revHandler = new Handler()
//            {
//                @Override
//                public void handleMessage(Message msg)
//                {
//                    if(msg.what==0x345)
//                    {
//                        try
//                        {
//                            //out.write((msg.obj.toString()+"\r\n").getBytes("utf-8"));
//                            sendMsg(msg.obj.toString());
//                        }
//                        catch (Exception e){
//                            String tmp = e.getMessage();
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            };
//            Looper.loop();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public  String receiveMsg() throws IOException
    {
        try {
            String msg = in.readLine();
            return msg;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public  void sendMsg(String msg) throws IOException
    {
        out.println(msg);
        out.flush();
    }
}
