package com.app.feng.tablerecyclerview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.TextView;

import com.app.feng.fixtablelayout.inter.IDataAdapter;
import com.app.feng.tablerecyclerview.bean.DataBean;

import java.util.List;

/**
 * Created by feng on 2017/4/4.
 */

public class FixTableAdapter implements IDataAdapter {

    public String[] titles;

    public List<DataBean> data;

    private Context mContext;

    public FixTableAdapter(String[] titles,List<DataBean> data, Context mContext) {
        this.titles = titles;
        this.data = data;
        this.mContext = mContext;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    @Override
    public String getTitleAt(int pos) {
        return titles[pos];
    }

    @Override
    public int getTitleCount() {
        return titles.length;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void convertData(int position,List<TextView> bindViews) {
        DataBean dataBean = data.get(position);

        bindViews.get(0)
                .setText(dataBean.id);
        bindViews.get(1)
                .setText(dataBean.data1);
        bindViews.get(2)
                .setText(dataBean.data2);
        bindViews.get(3)
                .setText(dataBean.data3);
        bindViews.get(4)
                .setText(dataBean.data4);
        bindViews.get(5)
                .setText(dataBean.data5);
        bindViews.get(6)
                .setText(dataBean.data6);
        bindViews.get(7)
                .setText(dataBean.data7);
        bindViews.get(8)
                .setText(dataBean.data8);
    }

    @Override
    public void convertLeftData(int position,TextView bindView) {
        bindView.setText(data.get(position).id);
    }

    private String getHighestText(String highestText, String field, int pos){
        if (highestText.length() < field.length() && titles[pos].length() < field.length()){
            highestText = field;
        } else if (highestText.length() < titles[pos].length()) {
            highestText = titles[pos];
        }

        return highestText;
    }

    @Override
    public int getHighestWidthText(int pos) {
        String highestText = "";
        /*
        * Para pegar o maior valor daquela coluna eu devo varrer todas as linhas
        * e ver o maior texto, a partir desse valor eu devo calcular o width para ser padrão
        * para todos os itens daquela coluna
        *
        * */

        for (DataBean item: data) {
            switch (pos){
                case 0:
                    highestText = getHighestText(highestText, item.id, pos);
                    break;
                case 1:
                    highestText = getHighestText(highestText, item.data1, pos);
                    break;
                case 2:
                    highestText = getHighestText(highestText, item.data2, pos);
                    break;
                case 3:
                    highestText = getHighestText(highestText, item.data3, pos);
                    break;
                case 4:
                    highestText = getHighestText(highestText, item.data4, pos);
                    break;
                case 5:
                    highestText = getHighestText(highestText, item.data5, pos);
                    break;
                case 6:
                    highestText = getHighestText(highestText, item.data6, pos);
                    break;
                case 7:
                    highestText = getHighestText(highestText, item.data7, pos);
                    break;
                case 8:
                    highestText = getHighestText(highestText, item.data8, pos);
                    break;
            }
        }

        Rect bounds = new Rect();
        Paint mTextPaint = new Paint();
        mTextPaint.getTextBounds(highestText, 0, highestText.length(), bounds);
        int height = bounds.height();
        int width = bounds.width() + 30;

        return width;
    }

    @Override
    public View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, DetailBookActivity.class);
                mIntent.putExtra("icon", "test");

                mContext.startActivity(mIntent);
            }
        };
    }
}