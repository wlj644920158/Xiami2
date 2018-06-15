package com.wanglijun.xiami;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;


import com.wanglijun.xiami.xiami.XiamiLayout;
import com.wanglijun.xiami.xiami.XiamiPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private XiamiLayout xiamiLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xiamiLayout = findViewById(R.id.xiamilayout);



        List<Fragment> views = new ArrayList<>();


        Fragment viwe1 = new TestFragment1();
        Fragment viwe2 = new TestFragment2();
        Fragment viwe3 = new TestFragment3();
        Fragment viwe4 = new TestFragment4();

        views.add(viwe1);
        views.add(viwe2);
        views.add(viwe3);
        views.add(viwe4);

        List<String> titles = new ArrayList<>();

        titles.add("乐库");
        titles.add("推荐");
        titles.add("趴间");
        titles.add("看点");


        XiamiPagerAdapter xiamiPagerAdapter = new XiamiPagerAdapter(getSupportFragmentManager(),views, titles);

        xiamiLayout.setAdapter(xiamiPagerAdapter,0);

    }
}
