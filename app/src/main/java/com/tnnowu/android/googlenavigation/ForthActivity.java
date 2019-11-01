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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

/**
 * Created by Tnno Wu on 2017/5/6.
 */

public class ForthActivity extends BaseActivity {

    private static final String TAG = "ForthActivity";

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treehole_login);
        initializeToolbar();

        TextView IdInputBox = (TextView)findViewById(R.id.TreeLoginId);
        TextView PwdInputBox = (TextView)findViewById(R.id.TreeLoginPwd);
        if(Global.USRID.equals("有问题"))
        {
            IdInputBox.setHint("Please Input ID");
            PwdInputBox.setHint("Please Input PWD");
        }
        else
        {
            IdInputBox.setText(Global.USRID);
            PwdInputBox.setText(Global.USRPWD);
        }
    }

    public void Login(View view) throws Exception
    {
        String UsrName = ((EditText)findViewById(R.id.TreeLoginId)).getText().toString();
        String UsrPWD = ((EditText)findViewById(R.id.TreeLoginPwd)).getText().toString();
        String toShow = "空！";

        if(!UsrName.equals(""))
        {
            String cmd;
            Client client = new Client();
            Vector<String> feedback;

            cmd = String.format("%d$%s$%s", 1, UsrName, UsrPWD);
            feedback = client.startConnect(cmd);

            String str = feedback.elementAt(0);

            switch (str) {
                case "Error":
                    toShow = "用户名或密码错误";
                    break;
                case "Illegal":
                    toShow = "非法输入";
                    break;
                case "Success":
                    toShow = "登录成功";
                    Global.setUsr(this, UsrName, UsrPWD);
                    break;
                default:
                    break;
            }
        }

        Toast.makeText(ForthActivity.this, toShow, Toast.LENGTH_LONG).show();
    }

    public void Register(View view) throws Exception
    {
        String UsrName = ((EditText)findViewById(R.id.TreeLoginId)).getText().toString();
        String UsrPWD = ((EditText)findViewById(R.id.TreeLoginPwd)).getText().toString();
        String toShow = "空！";

        if(!UsrName.equals(""))
        {
            String cmd;
            Client client = new Client();
            Vector<String> feedback;

            cmd = String.format("%d$%s$%s", 6, UsrName, UsrPWD);
            feedback = client.startConnect(cmd);

            String str = feedback.elementAt(0);

            switch (str) {
                case "TooLong":
                    toShow = "用户名或密码过长";
                    break;
                case "AlreadyExist":
                    toShow = "用户名已存在";
                    break;
                case "Success":
                    toShow = "注册成功";
                    Global.setUsr(this, UsrName, UsrPWD);
                    break;
                default:
                    break;
            }
        }
        Toast.makeText(ForthActivity.this, toShow, Toast.LENGTH_LONG).show();
    }

}
