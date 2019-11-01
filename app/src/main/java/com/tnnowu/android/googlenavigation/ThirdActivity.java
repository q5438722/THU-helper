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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Vector;


/**
 * Created by Tnno Wu on 2017/5/6.
 */

public class ThirdActivity extends BaseActivity
{
    private static final String TAG = "ThirdActivity";
    private static Deque<HoleButton> buttons;
    private static Deque<HoleButton> searchResults;
    private static Boolean SearchState = false;
    LinearLayout linearLayout;
    RelativeLayout.LayoutParams layoutParams;

    Client client;
    Vector<String> feedback;
    String cmd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        initializeToolbar();

        try {
            TryLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }

        linearLayout = findViewById(R.id.linearLayoutInScrollView);
        layoutParams = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(30,15,30,15);
        buttons = new ArrayDeque<>();
        searchResults = new ArrayDeque<>();

        try {
            FlashFunction(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void TryLogin() throws Exception
    {
        String cmd;
        Client client = new Client();
        Vector<String> feedback;

        cmd = String.format("%d$%s$%s", 1, Global.USRID, Global.USRPWD);
        feedback = client.startConnect(cmd);

        String str = feedback.elementAt(0);

        if(!str.equals("Success"))
            Toast.makeText(ThirdActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
        else
        {
            TextView IDShow = (TextView)findViewById(R.id.IdShow);
            IDShow.setText(Global.USRID);
        }
    }

    class HoleButton extends android.support.v7.widget.AppCompatButton
    {
        int id;
        HoleButton(Context context, final int msgId, final String msg)
        {
            super(context);
            id = msgId;
            String ID = ((Integer)msgId).toString();
            setText("["+ID+"]: "+msg);
            setAllCaps(false);
            setLayoutParams(layoutParams);
            setBackgroundResource(R.drawable.button_shape);
            setGravity(Gravity.LEFT);

            setOnClickListener(new OnClickListener(){
                public void onClick(View v) {
                    Intent intent = new Intent(ThirdActivity.this, ViewHoleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Msg", msg);
                    bundle.putInt("MsgId", msgId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }

    class message
    {
        int msgId;
        String msg = new String();
    }

    @SuppressLint("DefaultLocale")
    message[] ClientNewMsg(int t, int dir)throws Exception
    {
//        @SuppressLint("HandlerLeak") Handler handler = new Handler()
//        {
//            @Override
//            public void handleMessage(Message msg)
//            {
//                if(msg.what==0x123)
//                {
//                    Toast.makeText(ThirdActivity.this,"123",Toast.LENGTH_SHORT);
//                    //feedback.add(msg.obj.toString());
//                }
//            }
//        };
//        client = new Client(handler);
//        new Thread(client).start();
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        try
//        {
//            Message msg = new Message();
//            msg.what = 0x345;
//            msg.obj = cmd;
//            client.revHandler.handleMessage(msg);
//        }
//        catch (Exception e)
//        {
//            String tmp = e.getMessage();
//            e.printStackTrace();
//        }
        client = new Client();
        cmd = String.format("%d$%s$%s$%d$%d", 4, Global.USRID, Global.USRPWD, dir, t);
        feedback = client.startConnect(cmd);

        int size = feedback.size() / 2;

        message[] ret = new message[size];
        for(int i=0; i<size; i++)
        {
            ret[i]=new message();
            ret[i].msgId = Integer.parseInt(feedback.elementAt(2*i));
            ret[i].msg = feedback.elementAt(2*i+1).replaceAll(Global.Replace, "\n");
        }
        return ret;
    }

    void Reload()
    {
        linearLayout.removeAllViews();
        for(HoleButton b : buttons)
            linearLayout.addView(b);
    }

    public void FlashFunction(View view) throws Exception
    {
        int t;
        int dir = 1;
        try { t = buttons.getFirst().id; }
        catch (Exception e) {
            message[] newMsg = ClientNewMsg(1<<30, 0);
            for(int i=newMsg.length-1; i>=0; i--)
                buttons.addFirst(new HoleButton(this, newMsg[i].msgId, newMsg[i].msg));
            Reload();
            return;
        }
        message[] newMsg = ClientNewMsg(t, dir);
        for(message m : newMsg)
            buttons.addFirst(new HoleButton(this, m.msgId, m.msg));
        Reload();
    }

    public void MoreFunction(View view) throws Exception
    {
        int t;
        try { t = buttons.getLast().id; }
        catch (Exception e) {
            return;
        }                                      //需要有报错：下面已经没有了
        message[] newMsg = ClientNewMsg(t, 0);
        if(newMsg.length==0)
            return;
        for(message m : newMsg)
            buttons.addLast(new HoleButton(this, m.msgId, m.msg));
        Reload();
    }

    public void NewHoleFunction(View view)
    {
        Intent intent = new Intent(ThirdActivity.this, NewHoleActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("To", -1);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @SuppressLint("DefaultLocale")
    public void SearchFunction(View view) throws Exception
    {
        Button searchButton = (Button)findViewById(R.id.search_button);
        EditText searchText = (EditText)findViewById(R.id.search);

        if(!SearchState)
        {
            String key = ((EditText) findViewById(R.id.search)).getText().toString();

            if(key.equals(""))
                return;

            SearchState = true;

            ((Button) findViewById(R.id.MoreButton)).setEnabled(false);
            ((Button) findViewById(R.id.FlashButton)).setEnabled(false);
            ((Button) findViewById(R.id.writeNewHole)).setEnabled(false);

            searchButton.setText("Back");

            client = new Client();
            cmd = String.format("%d$%s$%s$%s", 7, Global.USRID, Global.USRPWD, key);
            feedback = client.startConnect(cmd);

            int size = feedback.size() / 2;

            message[] ret = new message[size];
            for (int i = 0; i < size; i++) {
                ret[i] = new message();
                ret[i].msgId = Integer.parseInt(feedback.elementAt(2 * i));
                ret[i].msg = feedback.elementAt(2 * i + 1).replaceAll(Global.Replace, "\n");
            }

            searchResults.clear();
            for (message m : ret)
                searchResults.add(new HoleButton(this, m.msgId, m.msg));

            linearLayout.removeAllViews();
            for (HoleButton b : searchResults)
                linearLayout.addView(b);
        }
        else
        {
            SearchState = false;

            ((Button) findViewById(R.id.MoreButton)).setEnabled(true);
            ((Button) findViewById(R.id.FlashButton)).setEnabled(true);
            ((Button) findViewById(R.id.writeNewHole)).setEnabled(true);

            searchButton.setText("Search");
            searchText.setText("");

            Reload();
        }

    }

}
