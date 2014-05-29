package com.tallmatt.fogoftheworld.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by michaudm3 on 5/11/2014.
 */
public class FogMask extends View {
    public GoogleMap getMap() {
        return map;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    GoogleMap map;
    GroundOverlay imageOverlay;

    int width = 0;
    int height = 0;
    Paint greyPaint;
    Paint drawPaint;
    Paint maskPaint;
    ArrayList<Point> points;
    Bitmap mask;
    Canvas maskCanvas;
    Bitmap result;
    Canvas resultCanvas;

    public FogMask(Context context) {
        super(context);
        init();
    }

    public FogMask(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FogMask(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        greyPaint = new Paint();
        greyPaint.setStyle(Paint.Style.FILL);
        greyPaint.setColor(Color.rgb(128,128,128));
        greyPaint.setAlpha(128);

        drawPaint = new Paint();
        drawPaint.setStyle(Paint.Style.FILL);
        drawPaint.setColor(Color.BLACK);

        maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        points = new ArrayList<Point>();


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        mask = Bitmap.createBitmap(width, height, conf); // this creates a MUTABLE bitmap
        maskCanvas = new Canvas(mask);
        maskCanvas.drawRect(0,0,width,height,greyPaint);
        result = Bitmap.createBitmap(width, height, conf); // this creates a MUTABLE bitmap
        resultCanvas = new Canvas(result);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            points.add(new Point((int)event.getX(), (int)event.getY()));
            invalidate();
            //maskCanvas.drawCircle(event.getX(), event.getY(), 48, drawPaint);
        }
        return false;
    }

    public void drawMask() {
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        //canvas.drawRect(0, 0, width, height, keepPaint);
        resultCanvas.drawBitmap(mask, 0, 0, maskPaint);
        maskPaint.setXfermode(null);
        imageOverlay.setImage(BitmapDescriptorFactory.fromBitmap(result));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        maskCanvas.drawRect(0,0,width,height,greyPaint);
        for(Point point : points) {
            maskCanvas.drawCircle(point.x, point.y, 48,drawPaint );
        }
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        //canvas.drawRect(0, 0, width, height, keepPaint);
        canvas.drawBitmap(mask, 0, 0, maskPaint);
        maskPaint.setXfermode(null);
    }
}
