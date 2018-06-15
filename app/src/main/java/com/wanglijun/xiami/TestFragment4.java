package com.wanglijun.xiami;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TestFragment4 extends Fragment {

    private NestedScrollView scrollView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view4, null);

        scrollView = (NestedScrollView) view;

        return view;
    }


}
