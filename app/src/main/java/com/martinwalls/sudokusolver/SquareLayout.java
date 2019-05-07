package com.martinwalls.sudokusolver;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * A subclass of FrameLayout to ensure the contained view(s) are square. The height and width
 * of the layout are both set to the specified width of the view, and the given height
 * is ignored.
 */
public class SquareLayout extends FrameLayout {

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
//        super.onMeasure(Math.min(widthMeasureSpec, heightMeasureSpec), Math.min(widthMeasureSpec, heightMeasureSpec));
    }
}
