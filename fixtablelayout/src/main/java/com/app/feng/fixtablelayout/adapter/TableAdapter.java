package com.app.feng.fixtablelayout.adapter;


import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.app.feng.fixtablelayout.R;
import com.app.feng.fixtablelayout.inter.IDataAdapter;
import com.app.feng.fixtablelayout.widget.SingleLineLinearLayout;
import com.app.feng.fixtablelayout.widget.TextViewUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class TableAdapter extends RecyclerView.Adapter<TableViewHolder> {

    private HorizontalScrollView titleView;
    private int[] widthColumns = {};

    private ParametersHolder parametersHolder;

    private IDataAdapter dataAdapter;
    private float mDensity;

    private SparseBooleanArray selectedItems;
    private int positionSelected = -1;

    private TableAdapter(
            HorizontalScrollView titleView,
            ParametersHolder parametersHolder, IDataAdapter dataAdapter,
            float density) {
        super();
        this.titleView = titleView;
        this.parametersHolder = parametersHolder;
        this.dataAdapter = dataAdapter;
        this.mDensity = density;

        this.selectedItems = new SparseBooleanArray();

        initViews();
    }

    private int calculatePixels(int dps) {
        int pixels = (int) (dps * this.mDensity + 0.5f);

        return pixels;
    }

    private void initViews() {
        SingleLineLinearLayout titleChild = ((SingleLineLinearLayout) titleView.getChildAt(0));
        widthColumns = new int[dataAdapter.getTitleCount()];

        for (int i = 0; i < dataAdapter.getTitleCount(); i++) {
            int width = this.calculatePixels(dataAdapter.getHighestWidthText(i));

            widthColumns[i] = width;

            TextView textView = TextViewUtils.generateTextView(titleChild.getContext(),
                    dataAdapter.getTitleAt(i),
                    parametersHolder.item_gravity,
                    width,
                    10);

            textView.setTextColor(Color.BLACK);

            titleChild.addView(textView, i);
        }
        titleChild.setBackgroundColor(parametersHolder.title_color);
    }

    @Override
    public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SingleLineLinearLayout singleLineLinearLayout = new SingleLineLinearLayout(parent.getContext());

        for (int i = 0; i < dataAdapter.getTitleCount(); i++) {
            TextView textView = TextViewUtils.generateTextView(parent.getContext(), " ",
                    parametersHolder.item_gravity,
                    widthColumns[i],
                    20);

            singleLineLinearLayout.addView(textView, i);
        }

        return new TableViewHolder(singleLineLinearLayout);
    }

    @Override
    public void onBindViewHolder(TableViewHolder holder, final int position) {
        final SingleLineLinearLayout ll_content = (SingleLineLinearLayout) holder.itemView;
        List<TextView> bindViews = new ArrayList<>();

        for (int i = 0; i < dataAdapter.getTitleCount(); i++) {
            TextView textView = (TextView) ll_content.getChildAt(i);
            bindViews.add(textView);
        }

        setBackgrandForItem(position, ll_content);

        if (parametersHolder.enable_selection) {
            ll_content.setBackgroundResource(R.drawable.statelist_item_background);
            ll_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleSelection(position, ll_content);
                }
            });
        } else {
            int[] attrs = new int[]{R.attr.selectableItemBackground};
            TypedArray typedArray = ll_content.getContext().obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            ll_content.setBackgroundResource(backgroundResource);

            ll_content.setOnClickListener(dataAdapter.getOnClickListener());
        }

        dataAdapter.convertData(position, bindViews);
    }

    private void toggleSelection(int pos, SingleLineLinearLayout view) {
        if (pos != RecyclerView.NO_POSITION) {
            if (selectedItems.get(pos, false)) {
                positionSelected = -1;
                selectedItems.delete(pos);
                view.setActivated(selectedItems.get(pos, false));
            } else if (selectedItems.size() == 0) {
                positionSelected = pos;
                selectedItems.put(pos, true);
                view.setActivated(selectedItems.get(pos, true));
            }

            notifyItemChanged(pos);
        } else {
            positionSelected = -1;
        }
    }

    public int getPositionSelected() {
        return positionSelected;
    }

    private void setBackgrandForItem(int position, SingleLineLinearLayout ll_content) {
        if (position % 2 != 0) {
            ll_content.setBackgroundColor(parametersHolder.col_1_color);
        } else {
            ll_content.setBackgroundColor(parametersHolder.col_2_color);
        }
    }

    @Override
    public int getItemCount() {
        return dataAdapter.getItemCount();
    }

    public static class ParametersHolder {
        int col_1_color;
        int col_2_color;
        int title_color;
        int item_padding;
        int item_gravity;
        boolean enable_selection;

        public ParametersHolder(int s_color, int b_color, int title_color,
                                int item_padding, int item_gravity, boolean enable_selection) {
            this.col_1_color = s_color;
            this.col_2_color = b_color;
            this.title_color = title_color;
            this.item_padding = item_padding;
            this.item_gravity = item_gravity;
            this.enable_selection = enable_selection;
        }
    }

    public static class Builder {
        HorizontalScrollView titleView;

        ParametersHolder parametersHolder;
        IDataAdapter dataAdapter;
        float density;

        public Builder setTitleView(HorizontalScrollView titleView) {
            this.titleView = titleView;
            return this;
        }

        public Builder setParametersHolder(
                ParametersHolder parametersHolder) {
            this.parametersHolder = parametersHolder;
            return this;
        }

        public Builder setDataAdapter(IDataAdapter dataAdapter) {
            this.dataAdapter = dataAdapter;
            return this;
        }

        public Builder setDensity(float density) {
            this.density = density;
            return this;
        }

        public TableAdapter create() {
            return new TableAdapter(titleView, parametersHolder, dataAdapter, density);
        }
    }

    public void notifyLoadData() {
        notifyDataSetChanged();
    }
}
