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

import android.app.AlertDialog;
import android.app.FragmentContainer;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/**
 * Created by Tnno Wu on 2017/5/6.
 */

public class SecondActivity extends BaseActivity {

    private static final String TAG = "SecondActivity";

    private static final int ColorPool[] = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.WHITE};
    //SQLite Helper类
    private DatabaseHelper databaseHelper = new DatabaseHelper
            (this, "database.db", null, 1);

    int currentCoursesNumber = 0;
    int maxCoursesNumber = 0;
    public boolean cty_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        //工具条
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        initializeToolbar();

        loadData();

        createLeftView(new Course("", "zl", "aaa",
                Integer.valueOf("4"), Integer.valueOf("1"), Integer.valueOf("10")));

    }

    //从数据库加载数据
    private void loadData() {
        ArrayList<Course> courseList = new ArrayList<>(); //课程列表
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from courses", null);
        if (cursor.moveToFirst()) {
            do {
                courseList.add(new Course(
                        cursor.getString(cursor.getColumnIndex("course_name")),
                        cursor.getString(cursor.getColumnIndex("teacher")),
                        cursor.getString(cursor.getColumnIndex("class_room")),
                        cursor.getInt(cursor.getColumnIndex("day")),
                        cursor.getInt(cursor.getColumnIndex("class_start")),
                        cursor.getInt(cursor.getColumnIndex("class_end"))));
            } while(cursor.moveToNext());
        }
        cursor.close();

        //使用从数据库读取出来的课程信息来加载课程表视图
        for (Course course : courseList) {
            createLeftView(course);
            createCourseView(course);
        }
    }

    //保存数据到数据库
    private void saveData(Course course) {
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into courses(course_name, teacher, class_room, day, class_start, class_end) " +
                "values(?, ?, ?, ?, ?, ?)", new String[] {course.getCourseName(), course.getTeacher(),
                course.getClassRoom(), course.getDay()+"", course.getStart()+"", course.getEnd()+""});
    }

    //创建课程节数视图
    private void createLeftView(Course course) {
        int len = course.getEnd();
        if (len > maxCoursesNumber) {
            for (int i = 0; i < len-maxCoursesNumber; i++) {
                View view = LayoutInflater.from(this).inflate(R.layout.left_view, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(110,180);
                view.setLayoutParams(params);

                TextView text = view.findViewById(R.id.class_number_text);
                text.setText(String.valueOf(++currentCoursesNumber));

                LinearLayout leftViewLayout = findViewById(R.id.left_view_layout);
                leftViewLayout.addView(view);
            }
        }
        maxCoursesNumber = len;
    }

    //创建课程视图
    private void createCourseView(final Course course) {
        int height = 180;
        int integer = course.getDay();

        final RelativeLayout day;

        if (cty_flag && ((integer < 1 || integer > 7) || course.getStart() > course.getEnd()))
            Toast.makeText(this, "星期几没写对,或课程结束时间比开始时间还早~~", Toast.LENGTH_LONG).show();
        else {
            switch (integer) {
                case 1: day = findViewById(R.id.monday);break;
                case 2: day = findViewById(R.id.tuesday);break;
                case 3: day = findViewById(R.id.wednesday);break;
                case 4: day = findViewById(R.id.thursday);break;
                case 5: day = findViewById(R.id.friday);break;
                case 6: day = findViewById(R.id.saturday);break;
                default: day = findViewById(R.id.weekday);break;
            }


            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, (course.getEnd()-course.getStart()+1)*height - 8);


            final TextView tv=new TextView(this);
            tv.setText(course.getCourseName());
            tv.setY(height * (course.getStart()-1));

            Random rand = new Random();
            int temp = rand.nextInt(5);
            tv.setBackgroundColor(ColorPool[temp]);
            tv.getBackground().setAlpha(160);
            tv.setTextSize(16);

            tv.setLayoutParams(lp);//设置布局参数
            day.addView(tv);

//            view.setY(height * (course.getStart()-1)); //设置开始高度,即第几节课开始
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
//                    (ViewGroup.LayoutParams.MATCH_PARENT,(course.getEnd()-course.getStart()+1)*height - 8); //设置布局高度,即跨多少节课
//            view.setLayoutParams(params);
//            TextView text = view.findViewById(R.id.text_view);
//            text.setText(course.getCourseName() + "\n" + course.getTeacher() + "\n" + course.getClassRoom()); //显示课程名
//            day.addView(view);
            //长按删除课程

            tv.setOnClickListener(new View.OnClickListener()
                  {

                      String id[] = {"王子龙" , "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
                      @Override
                      public void onClick(View v)
                      {
                          final AlertDialog.Builder normalDialog =
                                  new AlertDialog.Builder(SecondActivity.this);
                          normalDialog.setIcon(R.drawable.zilong_table);
                          normalDialog.setTitle(course.getCourseName());
                          normalDialog.setMessage("课程教师：" + course.getTeacher() + "\n课程地点：" + course.getClassRoom() + "\n课程日期：" + id[Integer.valueOf(course.getDay())] + "\n课程时间：" + course.getStart() + "节到" + course.getEnd() + "节");


                          normalDialog.setPositiveButton("确定",
                                  new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which) {
                                          //...To-do
                                      }
                                  });
                          normalDialog.show();

                      }
                  }

            );

            tv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    tv.setVisibility(View.GONE);//先隐藏
                    day.removeView(tv);//再移除课程视图
                    SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
                    sqLiteDatabase.execSQL("delete from courses where course_name = ?", new String[] {course.getCourseName()});
                    return true;
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == 0 && data != null) {
            Course course = (Course) data.getSerializableExtra("course");
            //创建课程表左边视图(节数)
//            createLeftView(course);
            //创建课程表视图
            createCourseView(course);
            //存储数据到数据库
            if ((course.getDay() >= 1 && course.getDay() < 8) && course.getStart() < course.getEnd())
             saveData(course);
        }
    }

    public void SecondAdd(View view)
    {
        cty_flag = true;
        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(SecondActivity.this);
        final View dialogView = LayoutInflater.from(SecondActivity.this)
                .inflate(R.layout.activity_add_course,null);
        customizeDialog.setTitle("添加一门新的课程");
        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final EditText inputCourseName = (EditText) dialogView.findViewById(R.id.course_name);
                        final EditText inputTeacher = (EditText) dialogView.findViewById(R.id.teacher_name);
                        final EditText inputClassRoom = (EditText) dialogView.findViewById(R.id.class_room);
                        final EditText inputDay = (EditText) dialogView.findViewById(R.id.week);
                        final EditText inputStart = (EditText) dialogView.findViewById(R.id.classes_begin);
                        final EditText inputEnd = (EditText) dialogView.findViewById(R.id.classes_ends);

                        String courseName = inputCourseName.getText().toString();
                        String teacher = inputTeacher.getText().toString();
                        String classRoom = inputClassRoom.getText().toString();
                        String day = inputDay.getText().toString();
                        String start = inputStart.getText().toString();
                        String end = inputEnd.getText().toString();

                        if (courseName.equals("") || day.equals("") || start.equals("") || end.equals(""))
                        {
                            Toast.makeText(SecondActivity.this, "课程名称未填写",Toast.LENGTH_SHORT).show();
                        }
                        else if(day.equals(""))
                        {
                            Toast.makeText(SecondActivity.this, "课程日期未填写",Toast.LENGTH_SHORT).show();
                        }
                        else if(start.equals("") || end.equals(""))
                        {
                            Toast.makeText(SecondActivity.this, "课程时段未填写",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Course course = new Course(courseName, teacher, classRoom,
                                    Integer.valueOf(day), Integer.valueOf(start), Integer.valueOf(end));
                            createCourseView(course);
                            if ((course.getDay() >= 1 && course.getDay() < 8) && course.getStart() < course.getEnd())
                                saveData(course);
                        }
                    };

                });
        customizeDialog.show();

    }

    public void SecondLoad(View view) throws Exception
    {
        Client client = new Client();
        String cmd = String.format("%d$%s$%s", 32, "hyx", "666");
        Vector<String> feedBack = client.startConnect(cmd);
        int size = feedBack.size() / 6;
        for(int i=0; i<size; i++)
        {
            Course course = new Course(feedBack.get(6*i), feedBack.get(6*i+1), feedBack.get(6*i+2),
                    Integer.valueOf(feedBack.get(6*i+3)),
                    Integer.valueOf(feedBack.get(6*i+4)),
                    Integer.valueOf(feedBack.get(6*i+5)) - 1);

            createCourseView(course);
            saveData(course);

        }
    }


}
