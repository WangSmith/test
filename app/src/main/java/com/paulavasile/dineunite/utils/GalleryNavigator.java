package com.paulavasile.dineunite.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.paulavasile.dineunite.R;


/**
 * Created by Administrator on 10/30/2015.
 */
public class GalleryNavigator extends View {
    private static final int SPACING = 10;
    private static final int RADIUS = 10;
    private int mSize = 10;
    private int mPosition = 0;
    private static final Paint mOnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);;
    private static final Paint mOffPaint = new Paint(Paint.ANTI_ALIAS_FLAG);;

    public GalleryNavigator(Context context) {
        super(context);
        mOnPaint.setColor(getResources().getColor(R.color.primary));
        mOffPaint.setColor(getResources().getColor(R.color.right_grey));
    }

    public GalleryNavigator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mOnPaint.setColor(getResources().getColor(R.color.primary));
        mOffPaint.setColor(getResources().getColor(R.color.right_grey));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mSize; ++i) {
            if (i == mPosition) {
                canvas.drawCircle(i * (2 * RADIUS + SPACING) + RADIUS, RADIUS, RADIUS, mOnPaint);
            } else {
                canvas.drawCircle(i * (2 * RADIUS + SPACING) + RADIUS, RADIUS, RADIUS, mOffPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mSize * (2 * RADIUS + SPACING) - SPACING, 2 * RADIUS);
    }

    public void setPosition(int id) {
        mPosition = id;
    }

    public void setSize(int size) {
        mSize = size;
    }


}
