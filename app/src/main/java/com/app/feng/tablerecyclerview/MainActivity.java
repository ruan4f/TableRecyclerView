package com.app.feng.tablerecyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.app.feng.fixtablelayout.FixTableLayout;
import com.app.feng.fixtablelayout.inter.ILoadMoreListener;
import com.app.feng.tablerecyclerview.bean.DataBean;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public String[] title = {"Id", "title1","title grande para o texto","title3","title4","title5","title6","title7",
                             "titulo mais grande de texto para teste"};

    public List<DataBean> data = new ArrayList<>();

    int currentPage = 1;
    int totalPage = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < 40; i++) {
            data.add(new DataBean("id__","Eu estou testando texto grande","data2","data3","Texto grande será que funciona","data5","data6","data7",
                                  "data8"));
        }

        final FixTableLayout fixTableLayout = findViewById(R.id.fixTableLayout);

        final FixTableAdapter fixTableAdapter = new FixTableAdapter(title,data, MainActivity.this);

        fixTableLayout.setAdapter(fixTableAdapter);

        fixTableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(MainActivity.this, DetailBookActivity.class);
                mIntent.putExtra("icon", "test");

                startActivity(mIntent);
            }
        });

        fixTableLayout.enableLoadMoreData();

        fixTableLayout.setLoadMoreListener(new ILoadMoreListener() {
            @Override
            public void loadMoreData(final Message message) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (currentPage <= totalPage) {
                            for (int i = 0; i < 50; i++) {
                                data.add(new DataBean("update_id","update_data","data2","data3","data4","data5",
                                                      "data6","data7","data8"));
                            }
                            currentPage++;
                            message.arg1 = 50; // 更新了50条数据
                        } else {
                            message.arg1 = 0;
                        }
                        message.sendToTarget();
                    }
                }).start();
            }
        });
    }

}
