package com.app.feng.fixtablelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import com.app.feng.fixtablelayout.adapter.TableAdapter;
import com.app.feng.fixtablelayout.inter.IDataAdapter;
import com.app.feng.fixtablelayout.inter.ILoadMoreListener;
import com.app.feng.fixtablelayout.widget.FixedGridLayoutManager;
import com.app.feng.fixtablelayout.widget.SingleLineItemDecoration;
import com.app.feng.fixtablelayout.widget.TableLayoutManager;

import java.lang.ref.WeakReference;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class FixTableLayout extends FrameLayout {
    public static final int MESSAGE_FIX_TABLE_LOAD_COMPLETE = 1001;

    RecyclerView recyclerView;
    HorizontalScrollView titleView;
    FrameLayout fl_load_mask;

    float mDensity;

    int divider_height;
    int divider_color;
    int col_1_color;
    int col_2_color;
    int title_color;
    int item_padding;
    int item_gravity;
    boolean enable_selection;

    private IDataAdapter dataAdapter;

    private boolean isLoading = false;
    private ILoadMoreListener loadMoreListener;
    private boolean hasMoreData = true;

    public FixTableLayout(Context context) {
        this(context, null);
    }

    public FixTableLayout(
            Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FixTableLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDensity = context.getResources().getDisplayMetrics().density;

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FixTableLayout);

        divider_height = array.getDimensionPixelOffset(R.styleable.FixTableLayout_fixtable_divider_height, getResources().getDimensionPixelOffset(R.dimen.divider_default_value));
        divider_color = array.getColor(R.styleable.FixTableLayout_fixtable_divider_color, Color.BLACK);
        col_1_color = array.getColor(R.styleable.FixTableLayout_fixtable_column_1_color, Color.WHITE);
        col_2_color = array.getColor(R.styleable.FixTableLayout_fixtable_column_2_color, Color.WHITE);
        title_color = array.getColor(R.styleable.FixTableLayout_fixtable_title_color, Color.GRAY);

        item_gravity = array.getInteger(R.styleable.FixTableLayout_fixtable_item_gravity, 0);
        enable_selection = array.getBoolean(R.styleable.FixTableLayout_fixtable_enable_selection, false);

        switch (item_gravity) {
            case 0:
                item_gravity = Gravity.CENTER;
                break;
            case 1:
                item_gravity = Gravity.START | Gravity.CENTER_VERTICAL;
                break;
            case 2:
                item_gravity = Gravity.END | Gravity.CENTER_VERTICAL;
                break;
        }

        array.recycle();

        View view = inflate(context, R.layout.table_view, null);
        init(view);
        addView(view);
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        titleView = view.findViewById(R.id.titleView);
        fl_load_mask = view.findViewById(R.id.load_mask);

        TableLayoutManager t1 = new TableLayoutManager();

        FixedGridLayoutManager t2 = new FixedGridLayoutManager();

        recyclerView.setLayoutManager(t2);

        titleView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                recyclerView.onTouchEvent(event);
                return true;
            }
        });

        SingleLineItemDecoration itemDecoration = new SingleLineItemDecoration(divider_height, divider_color);

        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                titleView.scrollBy(dx, 0);
            }
        });
    }

    public void setAdapter(IDataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
        initRecyclerViewAdapter();
    }

    int lastVisablePos = -1;
    FixTableHandler fixTableHandler;

    public int getPositionSelected(){
        TableAdapter tableAdapter = (TableAdapter) recyclerView.getAdapter();
        return tableAdapter.getPositionSelected();
    }

    public void enableLoadMoreData() {
        fixTableHandler = new FixTableHandler(FixTableLayout.this, recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!isLoading && hasMoreData &&
                        newState == RecyclerView.SCROLL_STATE_IDLE &&
                        lastVisablePos == recyclerView.getAdapter().getItemCount() - 1) {

                    isLoading = true;
                    fl_load_mask.setVisibility(VISIBLE);

                    if (loadMoreListener != null) {
                        loadMoreListener.loadMoreData(
                                fixTableHandler.obtainMessage(FixTableLayout.MESSAGE_FIX_TABLE_LOAD_COMPLETE));
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View bottomView = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                lastVisablePos = recyclerView.getChildAdapterPosition(bottomView);
            }
        });
    }

    public void setLoadMoreListener(ILoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    private void initRecyclerViewAdapter() {
        TableAdapter.Builder builder = new TableAdapter.Builder();
        TableAdapter tableAdapter = builder
                .setTitleView(titleView)
                .setParametersHolder(
                        new TableAdapter.ParametersHolder(col_1_color, col_2_color, title_color,
                                item_padding, item_gravity, enable_selection))
                .setDataAdapter(dataAdapter)
                .setDensity(mDensity)
                .create();

        recyclerView.setAdapter(tableAdapter);
    }

    public void dataUpdate() {
        TableAdapter tableAdapter = (TableAdapter) recyclerView.getAdapter();
        tableAdapter.notifyLoadData();
    }

    private static class FixTableHandler extends Handler {
        WeakReference<RecyclerView> recyclerViewWeakReference;
        WeakReference<FixTableLayout> fixTableLayoutWeakReference;

        FixTableHandler(FixTableLayout fixTableLayout, RecyclerView recyclerView) {
            recyclerViewWeakReference = new WeakReference<>(recyclerView);
            fixTableLayoutWeakReference = new WeakReference<>(fixTableLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_FIX_TABLE_LOAD_COMPLETE) {
                RecyclerView recyclerView = recyclerViewWeakReference.get();
                FixTableLayout fixTableLayout = fixTableLayoutWeakReference.get();

                TableAdapter tableAdapter = (TableAdapter) recyclerView.getAdapter();
                int startPos = tableAdapter.getItemCount() - 1;
                int loadNum = msg.arg1;
                if (loadNum > 0) {
                    tableAdapter.notifyLoadData();
                } else {
                    fixTableLayout.hasMoreData = false;
                }

                fixTableLayout.fl_load_mask.setVisibility(GONE);
                fixTableLayout.isLoading = false;
            }
        }
    }
}
