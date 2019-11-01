/*
 * Copyright (C) 2017 Tnno Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tnnowu.android.googlenavigation;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Tnno Wu on 2017/5/6.
 */

public class FirstActivity extends BaseActivity {
    private boolean ans = false;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_first);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Global.loadUsr(this);

        TextView _name = (TextView) findViewById(R.id.usrId), _password = (TextView) findViewById(R.id.pwd);
        try {
            _name.setText(Global.USRID);
            _password.setText(Global.USRPWD);
        }catch (Exception e){}
        initializeToolbar();
    }

    public void FirstReset(View view)
    {
        TextView name = (TextView) findViewById(R.id.usrId), password = (TextView) findViewById(R.id.pwd);

        name.setText("");
        password.setText("");

        Toast.makeText(FirstActivity.this, "第yi个按钮被点击了",Toast.LENGTH_SHORT).show();
    }

    public static String md5(String string)
    {
        MessageDigest md5 = null;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes)
            {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

//    private boolean loginValidate(String username,String passwd) throws Exception
//    {
//        final String html = HttpUtil.sendGetRequest("https://usereg.tsinghua.edu.cn/login.php", false, null, "gbk");
//        //使用正则表达式获取对应的填充数据
//        String p = "jsp\\?(.+?)'</script>";
//        Pattern reg = Pattern.compile(p);
//        Matcher m = reg.matcher(html);
//        String FillingStr = "";
//        if(m.find())
//        {
//            FillingStr = m.group(1);
//        }
//        //这里需要注意,需要使用utf-8格式进行编码
//        FillingStr = URLEncoder.encode(FillingStr,"utf-8");
//        final String url = "http://222.198.127.170/eportal/InterFace.do?method=login";
//        final String data="userId="+username+"&password="+passwd+"&service=%25E9%25BB%2598%25E8%25AE%25A4&queryString="+FillingStr+"&operatorPwd=&operatorUserId=&validcode=";
//        //发送登录请求
//        String html2=HttpUtil.sendPostRequest(url, data, false, null, "gbk");
//        if(html2.contains("success"))
//            return true;
//        return false;
//    }




    public void FirstConnect(View view) throws InterruptedException {
        TextView _name = (TextView) findViewById(R.id.usrId), _password = (TextView) findViewById(R.id.pwd);
        final String name = _name.getText().toString(), pwd = _password.getText().toString();
        final String convert_pwd = md5(pwd);
        if(name.equals(""))
        {
            Toast.makeText(FirstActivity.this, "请输入用户名",Toast.LENGTH_SHORT).show();
            return;
        }
        else if(pwd.equals(""))
        {
            Toast.makeText(FirstActivity.this, "请输入密码",Toast.LENGTH_SHORT).show();
            return;
        }

        ans = false;

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        new Thread() {
            public void run() {
                loginByPost(name, convert_pwd); // 调用loginByPost方法
                countDownLatch.countDown();
            };
        }.start();

//        mWebView.loadUrl("https://usereg.tsinghua.edu.cn/");



        //   mWebView.loadUrl("javascript:postData()");


//        mWebView.post(new Runnable() {
//            @Override
//            public void run() {
//
//                // 注意调用的JS方法名要对应上
//                // 调用javascript的callJS()方法
//            }
//        });
//
        countDownLatch.await();
        if(ans) Toast.makeText(FirstActivity.this, "连接成功",Toast.LENGTH_SHORT).show();
        else Toast.makeText(FirstActivity.this, "连接失败",Toast.LENGTH_SHORT).show();
    }



    public void loginByPost(String userName, String userPass) {

        try {

            // 请求的地址
            String spec = "http://net.tsinghua.edu.cn/do_login.php";
            // 根据地址创建URL对象
            URL url = new URL(spec);
            // 根据URL对象打开链接
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            // 传递的数据
            String data = "action=login&username=" + userName + "&password={MD5_HEX}" + URLEncoder.encode(userPass, "UTF-8") +
                    "&ac_id=1";
            // 设置请求的头
            urlConnection.setRequestProperty("Connection", "keep-alive");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Length",
                    String.valueOf(data.getBytes().length));
            // 设置请求的头
            urlConnection
                    .setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.1 Safari/605.1.15");

            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            //setDoInput的默认值就是true
            //获取输出流
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];

                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                baos.close();
                // 返回字符串
                final String result = new String(baos.toByteArray(), "utf-8");
                if(result.equals("Login is successful.")) ans = true;
                else if(result.equals("IP has been online, please logout.")) ans = true;


            } else {
                Toast.makeText(FirstActivity.this, "失败了",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void Login(View view) throws Exception
//    {
//        String UsrName = ((EditText)findViewById(R.id.usrId)).getText().toString();
//        String UsrPWD = ((EditText)findViewById(R.id.pwd)).getText().toString();
//
//        String cmd;
//        Client client = new Client();
//        Vector<String> feedback;
//
//        Global.setUsr(this, UsrName, UsrPWD);
//
//        cmd = String.format("%d$%s$%s", 6, Global.USRID, Global.USRPWD);
//        feedback = client.startConnect(cmd);
//
//        if(!feedback.elementAt(0).equals("注册成功"))
//            Toast.makeText(FirstActivity.this, "Please Check the Inputs!", Toast.LENGTH_LONG).show();
//    }
//
}
