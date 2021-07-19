package com.jumbo.trus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;
    private int leftPadding;

    public SimpleDividerItemDecoration(Context context) {
        mDivider = context.getDrawable(R.drawable.recycler_horizontal_divider);
        this.leftPadding = 90;
    }

    public SimpleDividerItemDecoration(Context context, int leftPadding) {
        mDivider = context.getDrawable(R.drawable.recycler_horizontal_divider);
        this.leftPadding = 90;
        this.leftPadding = leftPadding;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft()+leftPadding;
        int right = parent.getWidth() - parent.getPaddingRight()-90;

        int childCount = parent.getChildCount();
        for (int i = 0; i <= childCount-2; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
