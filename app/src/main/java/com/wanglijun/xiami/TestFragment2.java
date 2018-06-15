package com.wanglijun.xiami;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class TestFragment2 extends Fragment {
    RecyclerView recyclerView;

    List<String> datas;
    TestAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view2, null);

        recyclerView = view.findViewById(R.id.recyclerView);


        datas = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            datas.add("item " + i);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter=new TestAdapter(datas));

        return view;
    }



    public static class TestViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public TestViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    public static class TestAdapter extends RecyclerView.Adapter<TestViewHolder> {


        List<String> strings;

        public TestAdapter(List<String> list) {
            strings = list;
        }

        @Override
        public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TestViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
        }

        @Override
        public void onBindViewHolder(TestViewHolder holder, int position) {
            holder.textView.setText(strings.get(position));

            holder.itemView.setBackgroundColor(position % 2 == 0 ? Color.YELLOW : Color.BLUE);
        }

        @Override
        public int getItemCount() {
            return strings.size();
        }
    }

}
