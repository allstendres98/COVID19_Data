package com.al375502.covid_19data;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceView;
import android.view.View;

public class CustomView extends View {

    Paint paint;
    Rect bounds;
    public CustomView(Context context) {
        super(context);
        paint = new Paint();
        bounds = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.getClipBounds(bounds);
        float centerX = (bounds.left + bounds.right) / 2f;
        float centerY = (bounds.top + bounds.bottom) / 2f;
        float radius = Math.min(bounds.width(), bounds.height()) / 4f;
        float sq3r = radius * (float) Math.sqrt(3); paint.setARGB(128, 255, 0, 0);
        canvas.drawCircle(centerX, centerY + sq3r / 3, radius, paint);
        paint.setARGB(128, 0, 255, 0); canvas.drawCircle(centerX + radius / 2, centerY - sq3r / 6, radius, paint);
        paint.setARGB(128, 0, 0, 255);
        canvas.drawCircle(centerX - radius / 2, centerY - sq3r / 6, radius, paint);
    }
}
