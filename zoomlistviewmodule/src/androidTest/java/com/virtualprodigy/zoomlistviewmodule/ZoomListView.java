package com.virtualprodigy.zoomlistviewmodule;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

/**
 * Created by virtualprodigyllc on 8/9/15.
 *
 * Well I original created this custom view class in 2013 or 2014 during the beginning of the summer around July.
 * I remember I was a few weeks away from starting an internship. I wish I had the original creation dates
 * but the original computer hard drive needed to be replaced in my old laptop. :/ I went through hell
 * to retrieve all of source code & my file creation dates are wrong new... I know no one else cares but I
 * did want those dates to see how I've improved over the years.
 * I'm pulling this ListView from my original project and putting it into a module called ZoomListView.
 */
public class ZoomListView extends ListView {

    private final String TAG = this.getClass().getSimpleName();
    private Camera mCamera = new Camera();
    private Matrix mMatrix = new Matrix();

    public ZoomListView(Context context) {
        super(context);
        inflateCustomView();
    }

    public ZoomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateCustomView();
    }

    public ZoomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateCustomView();
    }

    private void inflateCustomView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.zoom_listview_layout, this, true);
    }

    /**
     * During the on draw the center of the child view is calculated for the center of the listview
     * based on the distance from the center of the listview a scale is appealed to view to make it
     * appear closer or further away. Using the camera a rotation/tranform is applied to make the
     * view appear closer to the screen in the y axis
     * rotation transformed
     * @param canvas
     * @param child
     * @param drawingTime
     * @return
     */
    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        Bitmap bitmap = child.getDrawingCache();
        if(bitmap == null){
            child.setDrawingCacheEnabled(true);
            child.buildDrawingCache();
            bitmap = child.getDrawingCache();
        }
        int left = child.getLeft();
        int top = child.getTop();

        // get offset to center
        int centerX = child.getWidth() / 2;
        int centerY = child.getHeight() / 2;

        // get absolute center of child
        float pivotX = left + centerX;
        float pivotY = top + centerY;

        // calculate distance from center
        float centerScreen = getHeight() / 2;
        float distFromCenter = (pivotY - centerScreen) / centerScreen;

        // calculate scale and rotation
        float scale = (float)(1 - 2 * (1 - Math.cos(distFromCenter)));
        if(scale < 0.5f)
            scale = 0.5f; //i dont want them too small
        if(child == getChildAt(0));
        Log.i(TAG, "Tweaking the 3d list view scale is" + scale);//use this to set a minimum scale down
        float rotation = distFromCenter;

        canvas.save();
        canvas.scale(scale, scale, pivotX, pivotY);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);


        if (mCamera == null) {
            mCamera = new Camera();
        }
        mCamera.save();
        mCamera.rotateY(rotation);

        if (mMatrix == null) {
            mMatrix = new Matrix();
        }
        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        mMatrix.preTranslate(-centerX, -centerY);
        mMatrix.postScale(scale, scale);
        mMatrix.postTranslate(pivotX, pivotY);
        canvas.drawBitmap(bitmap, left, top, paint);

        canvas.restore();
        return false;
    }

}
