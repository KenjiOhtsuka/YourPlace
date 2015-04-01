package com.improve_future.yourplace.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.improve_future.yourplace.R;

/**
 * Created by k_110_000 on 11/13/2014.
 */
public class TriangleView extends View {
    // direction
    public final static int DIRECTION_RIGHT = 0;
    public final static int DIRECTION_LEFT = 1;
    public final static int DIRECTION_TOP = 2;
    public final static int DIRECTION_BOTTOM = 3;

    private Paint mPaint;
    private int mColor;
    private int mDirection;

    public TriangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        TypedArray a =
                context.obtainStyledAttributes(attrs, R.styleable.Triangle);
        mColor = a.getColor(R.styleable.Triangle_color, 0xFF888888);
        mDirection = a.getInt(
                R.styleable.Triangle_direction, DIRECTION_RIGHT);
    }

    public void setColor(final String color) {
        setColor(Color.parseColor(color));
    }

    public void setColor(final int color) {
        mColor = color;
        requestLayout();
        invalidate();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(mColor);

        Path triangle = new Path();
        switch (mDirection) {
            case DIRECTION_RIGHT:
                triangle.moveTo(0, 0);
                triangle.lineTo(0, getMeasuredHeight());
                triangle.lineTo(getMeasuredWidth(), (getMeasuredHeight() / 2));
                break;
            case DIRECTION_LEFT:
                triangle.moveTo(getMeasuredWidth(), 0);
                triangle.lineTo(getMeasuredWidth(), getMeasuredHeight());
                triangle.lineTo(0, (getMeasuredHeight() / 2));
                break;
            case DIRECTION_TOP:
                triangle.moveTo(0, getMeasuredHeight());
                triangle.lineTo(getMeasuredWidth(), getMeasuredHeight());
                triangle.lineTo((getMeasuredWidth() / 2), 0);
                break;
            case DIRECTION_BOTTOM:
                triangle.moveTo(0, 0);
                triangle.lineTo(getMeasuredWidth(), 0);
                triangle.lineTo((getMeasuredWidth() / 2), getMeasuredHeight());
                break;
        }
        canvas.drawPath(triangle, mPaint);
    }
}
