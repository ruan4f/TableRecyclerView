package com.app.feng.fixtablelayout.widget;

import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class TableLayoutManager extends RecyclerView.LayoutManager {

    private int verticalOffset;
    private int horizontalOffset;

    private int firstVisPos;
    private int lastVisPos;

    private SparseArray<Rect> mItemAnchorMap = new SparseArray<>();

    private int oldChildCount = 1;

    public TableLayoutManager() {
        super();
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }

        Log.i("Ruan total", "" + state.getItemCount());
        Log.i("Ruan filhos", "" + getChildCount());

        if (getChildCount() == 0 && state.isPreLayout()) {//state.isPreLayout()是支持动画的
            return;
        }

        if (getChildCount() > 0 && state.didStructureChange()) {
            oldChildCount = getChildCount();
            fill(recycler, state, 0);
            return;
        } else if (getChildCount() - oldChildCount > 0 && !state.didStructureChange()) {
            fill(recycler, state, 0);
            return;
        }

        detachAndScrapAttachedViews(recycler);

        verticalOffset = 0;
        firstVisPos = 0;
        lastVisPos = state.getItemCount();
        fill(recycler, state, 0);
    }

    private int fill(RecyclerView.Recycler recycler, RecyclerView.State state, int dy) {
        int offsetTop = 0;

        if (getChildCount() > 0) {
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View child = getChildAt(i);

                if (dy > 0) {
                    if (getDecoratedBottom(child) < 0) {
                        removeAndRecycleView(child, recycler);
                        firstVisPos++;
                    }
                } else if (dy < 0) {
                    if (getDecoratedTop(child) > getHeight() - getPaddingBottom()) {
                        removeAndRecycleView(child, recycler);
                        lastVisPos--;
                    }
                }
            }
        }

        if (dy >= 0) {
            int minPos = firstVisPos;
            lastVisPos = getItemCount() - 1;

            if (getChildCount() > 0) {
                View lastView = getChildAt(getChildCount() - 1);
                minPos = getPosition(lastView) + 1;
                offsetTop = getDecoratedBottom(lastView);
            }

            for (int i = minPos; i <= lastVisPos; i++) {
                View child = recycler.getViewForPosition(i);
                addView(child);

                measureChild(child, 0, 0);

                if (offsetTop - dy > getHeight()) {
                    removeAndRecycleView(child, recycler);
                    lastVisPos = i - 1;
                } else {
                    int w = getDecoratedMeasuredWidth(child);
                    int h = getDecoratedMeasuredHeight(child);

                    Rect aRect = mItemAnchorMap.get(i);
                    if (aRect == null) {
                        aRect = new Rect();
                    }
                    aRect.set(0, offsetTop + verticalOffset, w, offsetTop + h + verticalOffset);
                    mItemAnchorMap.put(i, aRect);

                    layoutDecorated(child, -horizontalOffset, offsetTop, -horizontalOffset + w,
                            offsetTop + h);
                    offsetTop += h;

                }
            }

            View lastChild = getChildAt(getChildCount() - 1);
            if (getPosition(lastChild) == getItemCount() - 1) {
                int gap = getHeight() - getDecoratedBottom(lastChild);
                if (gap > 0) {
                    dy -= gap;
                }
            }
        } else {

            int maxPos = getItemCount() - 1;
            firstVisPos = 0;
            if (getChildCount() > 0) {
                View firstView = getChildAt(0);
                maxPos = getPosition(firstView) - 1;
            }

            for (int i = maxPos; i >= firstVisPos; i--) {
                Rect aRect = mItemAnchorMap.get(i);

                if (aRect != null) {
                    if (aRect.bottom - verticalOffset - dy < 0) {
                        firstVisPos = i + 1;
                        break;
                    } else {
                        View child = recycler.getViewForPosition(i);
                        addView(child, 0);
                        measureChild(child, 0, 0);

                        layoutDecorated(child, aRect.left - horizontalOffset,
                                aRect.top - verticalOffset, aRect.right - horizontalOffset,
                                aRect.bottom - verticalOffset);
                    }
                }
            }
        }

        return dy;
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        //Completely scrap the existing layout
        removeAllViews();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(
            int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        View firstView = getChildAt(0);

        if (firstView != null) {
            int firstViewWidth = firstView.getMeasuredWidth();
            if (firstViewWidth <= getWidth()) {
                return 0;
            }

            if (horizontalOffset + dx > firstViewWidth - getWidth()) {
                dx = 0;
            } else if (horizontalOffset + dx <= 0) {
                dx = 0;
            }

            horizontalOffset += dx;
            offsetChildrenHorizontal(-dx);
            return dx;
        } else {
            return 0;
        }
    }

    @Override
    public int scrollVerticallyBy(
            int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (dy == 0 || getChildCount() == 0) {
            return 0;
        }
        int realOffset = dy;

        View firstView = getChildAt(0);
        View lastView = getChildAt(getChildCount() - 1);

        //Optimize the case where the entire data set is too small to scroll
        int viewSpan = getDecoratedBottom(lastView) - getDecoratedTop(firstView);
        if (viewSpan < getVerticalSpace()) {
            return 0;
        }

        if (verticalOffset + realOffset < 0) {
            realOffset = -verticalOffset;
        } else if (realOffset > 0) {
            if (getPosition(lastView) == getItemCount() - 1) {
                int gap = getHeight() - getPaddingBottom() - getDecoratedBottom(lastView);
                if (gap > 0) {
                    realOffset = -gap;
                } else if (gap == 0) {
                    realOffset = 0;
                } else {
                    realOffset = Math.min(realOffset, -gap);
                }
            }
        }

        realOffset = fill(recycler, state, realOffset);
        verticalOffset += realOffset;
        offsetChildrenVertical(-realOffset);

        return realOffset;
    }

    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    private int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

}
