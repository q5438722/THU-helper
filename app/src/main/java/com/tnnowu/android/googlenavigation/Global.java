package com.tnnowu.android.googlenavigation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.file.attribute.UserDefinedFileAttributeView;

import static android.content.Context.MODE_PRIVATE;

public class Global {
    public static String Replace = "<here is replace>";
    public static String USRID = "有问题";
    public static String USRPWD = "有问题";
    //region Nick Name
    public static String[] NICK_NAME =
            {
                    "Alpha",
                    "Bravo",
                    "China",
                    "Delta",
                    "Echo",
                    "Foxtrot",
                    "Golf",
                    "Hotel",
                    "India",
                    "Juliet",
                    "Kilo",
                    "Lima",
                    "Mary",
                    "November",
                    "Oscar",
                    "Paper",
                    "Quebec",
                    "Research",
                    "Sierra",
                    "Tango",
                    "Uniform",
                    "Victor",
                    "Whisky",
                    "X-ray",
                    "Yankee",
                    "Zulu"
            };
    //endregion

    public static void setUsr(Context context, String id, String pwd)
    {
        USRID = id;
        USRPWD = pwd;

        try {
            FileOutputStream foutId = context.openFileOutput("USRID", MODE_PRIVATE);
            foutId.write((id+"\n").getBytes());

            FileOutputStream foutPwd = context.openFileOutput("USRPWD", MODE_PRIVATE);
            foutPwd.write((pwd+"\n").getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void loadUsr(Context context)
    {
        try {
            FileInputStream finId = context.openFileInput("USRID");
            int len = finId.available();
            byte[] buf = new byte[len];
            finId.read(buf);
            USRID = new String(buf);
            USRID = USRID.substring(0, USRID.indexOf("\n"));

            FileInputStream finPwd = context.openFileInput("USRPWD");
            len = finPwd.available();
            buf = new byte[len];
            finPwd.read(buf);
            USRPWD = new String(buf);
            USRPWD = USRPWD.substring(0, USRPWD.indexOf("\n"));
        } catch (Exception e) {
            USRID = "有问题";
            USRPWD = "有问题";
            e.printStackTrace();
        }
    }

    //将字符串转化成Drawable
    public synchronized static Drawable StringToDrawable(String icon)
    {
        if (icon == null || icon.length() < 10)
            return null;

        byte[] img = Base64.decode(icon.getBytes(), Base64.DEFAULT);
        Bitmap bitmap;
        if (img != null) {
            bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            @SuppressWarnings("deprecation")
            Drawable drawable = new BitmapDrawable(bitmap);

            return drawable;
        }
        return null;
    }

    //将drawable转化成字符串
    public synchronized static String DrawableToString(Drawable drawable)
    {
        if (drawable != null)
        {
            Bitmap bitmap = Bitmap.createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            int size = bitmap.getWidth() * bitmap.getHeight() * 4;

            // 创建一个字节数组输出流,流的大小为size
            ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
            // 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            // 将字节数组输出流转化为字节数组byte[]
            byte[] imagedata = baos.toByteArray();

            String ret = Base64.encodeToString(imagedata, Base64.DEFAULT);
            return ret;
        }
        return " ";
    }
}
