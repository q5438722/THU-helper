package com.tnnowu.android.googlenavigation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Vector;

public class NewHoleActivity extends BaseActivity {

    private static final String TAG = "NewHoleActivity";

    int TO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_hole);
        initializeToolbar();

        Bundle bundle = getIntent().getExtras();
        TO = bundle.getInt("To");
    }

    @SuppressLint("DefaultLocale")
    public void sendMsg(View view) throws Exception
    {
        EditText txt = findViewById(R.id.content);
        String msg = txt.getText().toString();

        msg = msg.replaceAll("\n", Global.Replace);

        String cmd;
        Client client = new Client();
        Vector<String> feedback;

        if(TO==-1)
        {
            cmd = String.format("%d$%s$%s$%s", 2, Global.USRID, Global.USRPWD, msg);
            feedback = client.startConnect(cmd);
        }
        else
        {
            cmd = String.format("%d$%s$%s$%s$%d", 3, Global.USRID, Global.USRPWD, msg, TO);
            feedback = client.startConnect(cmd);
        }

        Toast.makeText(NewHoleActivity.this, feedback.elementAt(0), Toast.LENGTH_LONG).show();
        this.finish();

    }

}