package com.app.feng.fixtablelayout.inter;

import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.List;

public interface IDataAdapter {

    String getTitleAt(int pos);

    int getTitleCount();

    int getItemCount();

    void convertData(int position,List<TextView> bindViews);

    String getHighestText(String highestText, String field, int pos);

    int getHighestWidthText(int pos);

    OnClickListener getOnClickListener();

}
