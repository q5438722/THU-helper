package com.tnnowu.android.googlenavigation;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

public class TreeHoleLogin  extends BaseActivity
{
    private static final String TAG = "TreeHoleLoginActivity";

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treehole_login);
        initializeToolbar();

        AutoCompleteTextView IdInputBox = (AutoCompleteTextView)findViewById(R.id.TreeLoginId);
        TextView PwdInputBox = (TextView)findViewById(R.id.TreeLoginPwd);

        IdInputBox.setText(Global.USRID);
        PwdInputBox.setText(Global.USRPWD);
    }

    public void Login(View view) throws Exception
    {
        String UsrName = ((EditText)findViewById(R.id.TreeLoginId)).getText().toString();
        String UsrPWD = ((EditText)findViewById(R.id.TreeLoginPwd)).getText().toString();

        String cmd;
        Client client = new Client();
        Vector<String> feedback;

        cmd = String.format("%d$%s$%s", 1, UsrName, UsrPWD);
        feedback = client.startConnect(cmd);

        String str = feedback.elementAt(0);
        String toShow = "";

        switch (str)
        {
            case "Error": toShow = "用户名或密码错误"; break;
            case "Illegal": toShow = "非法输入"; break;
            case "Success": toShow = "登录成功";
                            Global.setUsr(this, UsrName, UsrPWD);
                            break;
            default:break;
        }

        Toast.makeText(TreeHoleLogin.this, toShow, Toast.LENGTH_LONG).show();
    }

    public void Register(View view) throws Exception
    {
        String UsrName = ((EditText)findViewById(R.id.TreeLoginId)).getText().toString();
        String UsrPWD = ((EditText)findViewById(R.id.TreeLoginPwd)).getText().toString();

        String cmd;
        Client client = new Client();
        Vector<String> feedback;

        cmd = String.format("%d$%s$%s", 1, UsrName, UsrPWD);
        feedback = client.startConnect(cmd);

        String str = feedback.elementAt(0);
        String toShow = "";

        switch (str)
        {
            case "TooLong": toShow = "用户名或密码过长"; break;
            case "AlreadyExist": toShow = "用户名已存在"; break;
            case "Success": toShow = "注册成功";
                Global.setUsr(this, UsrName, UsrPWD);
                break;
            default:break;
        }

        Toast.makeText(TreeHoleLogin.this, toShow, Toast.LENGTH_LONG).show();
    }
}
