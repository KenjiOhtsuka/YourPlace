package com.improve_future.yourplace.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Created by k_110_000 on 10/14/2014.
 */
public class TokyoMetroAPI {
    public static Bitmap generateStationMark(
            final int color, final String stationCode, final Typeface typeface,
            final int sideLength) {
        final float floatSideLength = sideLength * 1.0f;
        final Bitmap bitmap = Bitmap.createBitmap(sideLength, sideLength, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);

        final float circleBorderWidth = floatSideLength * 3/ 40;
        final float circleCenterPosition = floatSideLength / 2;
        final float circleRadius = floatSideLength * 17 / 40;
        final Paint circlePaint = new Paint();
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setStrokeWidth(circleBorderWidth);
        circlePaint.setAntiAlias(true);
        canvas.drawCircle(circleCenterPosition, circleCenterPosition, circleRadius,
                circlePaint);
        circlePaint.setColor(color);
        circlePaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(circleCenterPosition, circleCenterPosition, circleRadius,
                circlePaint);

        final Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(typeface);

        final String lineCode = stationCode.substring(0, 1);
        final String stationNumber = stationCode.substring(1);

        textPaint.setTextSize(floatSideLength * 3 / 8);
        canvas.drawText(lineCode, floatSideLength / 2, floatSideLength * 17 / 40, textPaint);
        textPaint.setTextSize(floatSideLength * 19 / 40);
        canvas.drawText(stationNumber, floatSideLength / 2, floatSideLength * 4 / 5, textPaint);
        return bitmap;
    }
}
