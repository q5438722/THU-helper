package com.tnnowu.android.googlenavigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Vector;

public class ViewHoleActivity extends BaseActivity {

    private static final String TAG = "ViewHoleActivity";

    private static Deque<ViewHoleActivity.CommentButton> buttons;

    LinearLayout linearLayout;

    RelativeLayout.LayoutParams layoutParams;

    int msgId;

    Client client;
    Vector<String> feedback;
    String cmd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_hole);
        initializeToolbar();

        Bundle bundle = this.getIntent().getExtras();
        String msg = bundle.getString("Msg");
        msgId = bundle.getInt("MsgId");

        Button button = findViewById(R.id.FatherMsg);

        String ID = ((Integer)msgId).toString();
        button.setText("["+ID+"]: "+msg);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(ViewHoleActivity.this, NewHoleActivity.class);

                Bundle bundle = new Bundle();
                bundle.putInt("To", msgId);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        linearLayout = findViewById(R.id.linearLayoutInScrollView);
        layoutParams = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(50,15,50,15);
        buttons = new ArrayDeque<>();

        //buttons.add(new CommentButton(this, "Alice", msgId, "这是一条评论"));

        try {
            ReLoadComment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void FlashComment(View view) throws Exception
    {
        ReLoadComment();
    }

    void ReLoadComment() throws Exception
    {
        client = new Client();
        cmd = String.format("%d$%s$%s$%d", 5, Global.USRID, Global.USRPWD, msgId);
        feedback = client.startConnect(cmd);
        int size = feedback.size()/2;

        buttons.clear();
        for(int i=0; i<size;i++)
        {
            int id = Integer.parseInt(feedback.elementAt(2*i));
            String usr;
            try {
                usr = Global.NICK_NAME[id];
            }catch (Exception e){ usr = feedback.elementAt(2*i); }

            String comment = "[" + usr + "]: " + feedback.elementAt(2*i+1).replaceAll(Global.Replace, "\n");
            buttons.add(new CommentButton(this, comment));
        }

        linearLayout.removeAllViews();
        for(CommentButton b : buttons)
            linearLayout.addView(b);
    }

    class CommentButton extends android.support.v7.widget.AppCompatButton
    {
        @SuppressLint({"SetTextI18n", "RtlHardcoded"})
        CommentButton(Context context, final String msg)
        {
            super(context);
            setText(msg);
            setAllCaps(false);
            setLayoutParams(layoutParams);
            setBackgroundResource(R.drawable.comment_button_shape);
            setGravity(Gravity.LEFT);
        }
    }
}