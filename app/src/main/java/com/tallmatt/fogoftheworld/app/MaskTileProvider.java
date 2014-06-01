package com.tallmatt.fogoftheworld.app;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by michaudm3 on 5/12/2014.
 */
public class MaskTileProvider implements TileProvider {
    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
    Canvas resultCanvas;
    Bitmap result;
    ByteArrayOutputStream stream;
    byte[] byteArray;

    Paint keepPaint;
    Paint clearAwayPaint;
    Paint maskPaint;
    Paint fogPaint;
    ArrayList<PointLatLng> points;
    Bitmap mask;
    Canvas maskCanvas;

    boolean lock  = false;

    GoogleMap map;

    int bitmapWidth = 256;
    int bitmapHeight = 256;
    public static final double radiusConstant = 0.0004;//423902130126953;

//    int width = 180/4;
//    int height = 180/4;

    class Point {
        float x, y;
        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public MaskTileProvider (GoogleMap map) {
        this.map = map;

        // this paint is transparent, anything in the mask drawn with this paint will not affect the fog
        keepPaint = new Paint();
        keepPaint.setStyle(Paint.Style.FILL);
        keepPaint.setColor(Color.WHITE);
        keepPaint.setAlpha(0);

        // this paint is black, anything in black will clear the fog
        clearAwayPaint = new Paint();
        clearAwayPaint.setStyle(Paint.Style.FILL);
        clearAwayPaint.setColor(Color.BLACK);
        clearAwayPaint.setAlpha(255);

        // this paint is used to draw the mask onto the fog
        maskPaint = new Paint();
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        // this paint is the fog
        fogPaint = new Paint();
        fogPaint.setColor(Color.argb(238, 200, 200, 200));

        // the mask/result canvas and bitmap are global variables to save on some memory, thus we have
        // to lock out some tiles so that only one is being created at a time
        points = new ArrayList<PointLatLng>();

        //points.add(new LatLng(42.342969f, -71.100892f));
        //points.add(new LatLng(42.342832f, -71.100920f));
        //points.add(new LatLng(42.342645f, -71.100785f));

    }
    public void setPoints(ArrayList<PointLatLng> points) {
        this.points = points;
    }
    @Override
    public Tile getTile(int x, int y, int zoom) {
        // we only want to do one tile at a time, if we don't we will mess up the Bitmap creation stuff
        if(!lock) {
            // no one is making a tile so we should start and lock everyone out
            lock = true;
            try {
                LatLngBounds bounds = boundsOfTile(x,y,zoom);
                // generate the mask
                mask = Bitmap.createBitmap(bitmapWidth, bitmapHeight, conf);
                maskCanvas = new Canvas(mask);
                // draw the points on the mask as black circles
                int pointsOnTile = 0;
                for(PointLatLng ll : points) {
                    if(!ll.source.equals(FogConstants.SOURCE_NETWORK)) {
                        if (ll.latLng.longitude < bounds.northeast.longitude + 0.01 && ll.latLng.longitude > bounds.southwest.longitude - 0.01 &&
                                ll.latLng.latitude < bounds.northeast.latitude + 0.01 && ll.latLng.latitude > bounds.southwest.latitude - 0.01) {//bounds.contains(ll)) {
                            double thisTileWidth = bounds.northeast.longitude - bounds.southwest.longitude;
                            double thisTileHeight = bounds.northeast.latitude - bounds.southwest.latitude;
                            //Log.d("TM", "zoom/bitwidth = "+((float)zoom/bitmapWidth));
                            //Log.d("TM", "thisTileWidth*(zoom/bitwidth) = "+(thisTileWidth*(float)zoom/bitmapWidth));
                            float radius = (float) (radiusConstant * (float) bitmapWidth / thisTileWidth);
                            clearAwayPaint.setShader(new RadialGradient(
                                    (float) ((((ll.latLng.longitude - bounds.southwest.longitude) / thisTileWidth) * bitmapWidth)),
                                    (float) (bitmapHeight - (((ll.latLng.latitude - bounds.southwest.latitude) / thisTileHeight) * bitmapHeight)),
                                    radius, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.MIRROR));
                            maskCanvas.drawCircle(
                                    (float) ((((ll.latLng.longitude - bounds.southwest.longitude) / thisTileWidth) * bitmapWidth)),
                                    (float) (bitmapHeight - (((ll.latLng.latitude - bounds.southwest.latitude) / thisTileHeight) * bitmapHeight)),
                                    radius, clearAwayPaint);
                            pointsOnTile++;
                        }
                    }
                }

                // create the bitmap that will eventually become the tile
                result = Bitmap.createBitmap(bitmapWidth, bitmapHeight, conf);

                resultCanvas = new Canvas(result);
                // draw the fog onto the result
                resultCanvas.drawRect(0, 0, bitmapWidth, bitmapHeight, fogPaint);
                // apply the mask, wherever there is a black circle the fog will be cleared
                if(pointsOnTile>0) {
                    resultCanvas.drawBitmap(mask, 0, 0, maskPaint);
                }

                // stream the bitmap to a byte array
                stream = new ByteArrayOutputStream();
                result.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
                // use the bytearray to create the tile
                Tile tile = new Tile(bitmapWidth, bitmapHeight, byteArray);
//                Log.d("TM", "tile returned: (" + x + ", " + y + ", " + zoom + ")");
                // release the lock
                lock = false;
                // return the tile
                return tile;
            } catch(Exception e) {
                lock = false;
                e.printStackTrace();
                return TileProvider.NO_TILE;
            }
        }
        // it seems like if we return null here then the map will try again later to make the tile. This works nicely so far
        return null;
    }
    public static double toLatitude(double y) {
        double radians = Math.atan(Math.exp(Math.toRadians(y)));
        return Math.toDegrees(2 * radians)-90;
    }
    private LatLngBounds boundsOfTile(int x, int y, int zoom) {
        int noTiles = (1 << zoom);
        double longitudeSpan = 360.0 / noTiles;
        double longitudeMin = -180.0 + x * longitudeSpan;

        double mercatorMax = 180 - (((double) y) / noTiles) * 360;
        double mercatorMin = 180 - (((double) y + 1) / noTiles) * 360;
        double latitudeMax = toLatitude(mercatorMax);
        double latitudeMin = toLatitude(mercatorMin);

        LatLngBounds bounds = new LatLngBounds(new LatLng(latitudeMin, longitudeMin), new LatLng(latitudeMax, longitudeMin + longitudeSpan));
        return bounds;
    }
}
