package com.example.it3176_smartnote.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

public class TouchImageView extends ImageView {
 protected Matrix mBaseMatrix = new Matrix();

 protected Matrix mSuppMatrix = new Matrix();
 private final Matrix mDisplayMatrix = new Matrix();
 final protected RotateBitmap mBitmapDisplayed = new RotateBitmap(null);

 private OnTapListener mOnTapListener;

 public static interface OnTapListener {
  public void onTapped(Matrix matrix);
 }

 public static Matrix matrix = new Matrix();

 // We can be in one of these 3 states
 static final int NONE = 0;
 static final int DRAG = 1;
 static final int ZOOM = 2;
 int mode = NONE;

 // Remember some things for zooming
 PointF last = new PointF();
 PointF start = new PointF();
 float minScale = 1f;
 float maxScale = 3f;
 float[] m;

 float redundantXSpace, redundantYSpace;

 float width, height;
 static final int CLICK = 3;
 float saveScale = 1f;
 float right, bottom, origWidth, origHeight, bmWidth, bmHeight;
 int mThisWidth = -1, mThisHeight = -1;

 float mMaxZoom;

 int mScrollY;

 int mScrollX;

 int mLeft;

 int mRight;

 int mTop;

 int mBottom;

 int mPaddingTop;

 int mPaddingBottom;

 int mPaddingLeft;

 int mPaddingRight;

 ScaleGestureDetector mScaleDetector;

 Context context;

 public OnTouchListener mOnTouchListener;

 public TouchImageView(Context context) {
  super(context);
  sharedConstructing(context);
 }

 public TouchImageView(Context context, AttributeSet attrs) {
  super(context, attrs);
  sharedConstructing(context);
 }

 public void setOnTapListener(OnTapListener onTapListener) {
  mOnTapListener = onTapListener;
 }

 public interface Recycler {
  public void recycle(Bitmap b);
 }

 public void setRecycler(Recycler r) {
  mRecycler = r;
 }

 private Recycler mRecycler;
 private Runnable mOnLayoutRunnable = null;

 @Override
 protected void onLayout(boolean changed, int left, int top, int right,
   int bottom) {

  Log.e(VIEW_LOG_TAG, "ON_LAYOUT");
  // mOnTapListener.onTapped();
  mLeft = left;
  mRight = right;
  mTop = top;
  mBottom = bottom;
  mThisWidth = right - left;
  mThisHeight = bottom - top;
  Runnable r = mOnLayoutRunnable;
  if (r != null) {
   mOnLayoutRunnable = null;
   r.run();
  }

  if (mBitmapDisplayed.getBitmap() != null) {
   getProperBaseMatrix(mBitmapDisplayed, mBaseMatrix);
   setImageMatrix(getImageViewMatrix());
   Log.e(VIEW_LOG_TAG, getImageViewMatrix().toShortString());
  }
  invalidate();
  super.onLayout(changed, left, top, right, bottom);
 }

 // Setup the base matrix so that the image is centered and scaled properly.
 private void getProperBaseMatrix(RotateBitmap bitmap, Matrix matrix) {
  float viewWidth = getWidth();
  float viewHeight = getHeight();

  float w = bitmap.getWidth();
  float h = bitmap.getHeight();
  int rotation = bitmap.getRotation();
  matrix.reset();

  // We limit up-scaling to 2x otherwise the result may look bad if it's
  // a small icon.
  float widthScale = Math.min(viewWidth / w, 2.0f);
  float heightScale = Math.min(viewHeight / h, 2.0f);
  float scale = Math.min(widthScale, heightScale);

  matrix.postConcat(bitmap.getRotateMatrix());
  matrix.postScale(scale, scale);

  matrix.postTranslate((viewWidth - w * scale) / 2F, (viewHeight - h
    * scale) / 2F);
  Log.e(VIEW_LOG_TAG, matrix.toString());
 }

 // This function changes bitmap, reset base matrix according to the size
 // of the bitmap, and optionally reset the supplementary matrix.
 public void setImageBitmapResetBase(final Bitmap bitmap,
   final boolean resetSupp) {
  setImageRotateBitmapResetBase(new RotateBitmap(bitmap), resetSupp);
 }

 final Handler mHandler = new Handler();
 // Create runnable for posting
 final Runnable mAdjustUI = new Runnable() {
  @Override
public void run() {
  }
 };

 public void setImageRotateBitmapResetBase(final RotateBitmap bitmap,
   final boolean resetSupp) {
  final int viewWidth = getWidth();

  if (viewWidth <= 0) {
   mOnLayoutRunnable = new Runnable() {
    @Override
	public void run() {
     setImageRotateBitmapResetBase(bitmap, resetSupp);
    }
   };
   return;
  }

  if (bitmap.getBitmap() != null) {
   getProperBaseMatrix(bitmap, mBaseMatrix);
   setImageBitmap(bitmap.getBitmap(), bitmap.getRotation());
  } else {
   mBaseMatrix.reset();
   setImageBitmap(null);
  }

  if (resetSupp) {
   mSuppMatrix.reset();
  }
  setImageMatrix(getImageViewMatrix());
  mMaxZoom = maxZoom();
 }

 private void sharedConstructing(Context context) {
  super.setClickable(true);
  this.context = context;
  mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
  matrix.setTranslate(1f, 1f);
  m = new float[9];
  setImageMatrix(matrix);
  setScaleType(ScaleType.MATRIX);

  setOnTouchListener(new OnTouchListener() {

   @Override
   public boolean onTouch(View v, MotionEvent event) {
    mScaleDetector.onTouchEvent(event);

    matrix.getValues(m);
    float x = m[Matrix.MTRANS_X];
    float y = m[Matrix.MTRANS_Y];
    PointF curr = new PointF(event.getX(), event.getY());

    switch (event.getAction()) {
    case MotionEvent.ACTION_DOWN:
     last.set(event.getX(), event.getY());
     start.set(last);
     mode = DRAG;
     break;
    case MotionEvent.ACTION_MOVE:
     if (mode == DRAG) {
      float deltaX = curr.x - last.x;
      float deltaY = curr.y - last.y;
      float scaleWidth = Math.round(origWidth * saveScale);
      float scaleHeight = Math.round(origHeight * saveScale);
      if (scaleWidth < width) {
       deltaX = 0;
       if (y + deltaY > 0)
        deltaY = -y;
       else if (y + deltaY < -bottom)
        deltaY = -(y + bottom);
      } else if (scaleHeight < height) {
       deltaY = 0;
       if (x + deltaX > 0)
        deltaX = -x;
       else if (x + deltaX < -right)
        deltaX = -(x + right);
      } else {
       if (x + deltaX > 0)
        deltaX = -x;
       else if (x + deltaX < -right)
        deltaX = -(x + right);

       if (y + deltaY > 0)
        deltaY = -y;
       else if (y + deltaY < -bottom)
        deltaY = -(y + bottom);
      }
      matrix.postTranslate(deltaX, deltaY);
      last.set(curr.x, curr.y);
     }
     break;

    case MotionEvent.ACTION_UP:
     mode = NONE;
     int xDiff = (int) Math.abs(curr.x - start.x);
     int yDiff = (int) Math.abs(curr.y - start.y);
     Log.e(VIEW_LOG_TAG, matrix.toShortString() + "^^^^^^");
     if (xDiff < CLICK && yDiff < CLICK) {
      mHandler.post(mAdjustUI);
      // performClick();
      Log.e(VIEW_LOG_TAG, "ACTOIN_UP");
      // invalidate();
      // mOnTapListener.onTapped(matrix);
      return true;

     }
     Log.e(VIEW_LOG_TAG, matrix.toShortString() + "^^^^^^");
     break;

    case MotionEvent.ACTION_POINTER_UP:
     mode = NONE;
     break;
    }
    setImageMatrix(matrix);
    invalidate();
    return true; // indicate event was handled
   }

  });
 }

 // Combine the base matrix and the supp matrix to make the final matrix.
 protected Matrix getImageViewMatrix() {
  // The final matrix is computed as the concatentation of the base matrix
  // and the supplementary matrix.
  mDisplayMatrix.set(mBaseMatrix);
  mDisplayMatrix.postConcat(mSuppMatrix);
  return mDisplayMatrix;

 }

 /*
  * @Override public void setImageBitmap(Bitmap bm) {
  * super.setImageBitmap(bm); if (bm != null) { bmWidth = bm.getWidth();
  * bmHeight = bm.getHeight(); } }
  */

 @Override
 public void setImageBitmap(Bitmap bitmap) {
  setImageBitmap(bitmap, 0);
 }

 public void setImageBitmap(Bitmap bitmap, int rotation) {
  super.setImageBitmap(bitmap);
  Drawable d = getDrawable();
  if (d != null) {
   d.setDither(true);
  }

  Bitmap old = mBitmapDisplayed.getBitmap();
  mBitmapDisplayed.setBitmap(bitmap);
  mBitmapDisplayed.setRotation(rotation);

  if (old != null && old != bitmap && mRecycler != null) {
   mRecycler.recycle(old);
  }
  if (bitmap != null) {
   bmWidth = bitmap.getWidth();
   bmHeight = bitmap.getHeight();
  }
 }

 public void setMaxZoom(float x) {
  maxScale = x;
 }

 private class ScaleListener extends
   ScaleGestureDetector.SimpleOnScaleGestureListener {
  @Override
  public boolean onScaleBegin(ScaleGestureDetector detector) {
   mode = ZOOM;
   return true;
  }

  @Override
  public boolean onScale(ScaleGestureDetector detector) {
   float mScaleFactor = (float) Math.min(
     Math.max(.95f, detector.getScaleFactor()), 1.05);
   float origScale = saveScale;
   saveScale *= mScaleFactor;
   if (saveScale > maxScale) {
    saveScale = maxScale;
    mScaleFactor = maxScale / origScale;
   } else if (saveScale < minScale) {
    saveScale = minScale;
    mScaleFactor = minScale / origScale;
   }
   right = width * saveScale - width
     - (2 * redundantXSpace * saveScale);
   bottom = height * saveScale - height
     - (2 * redundantYSpace * saveScale);
   if (origWidth * saveScale <= width
     || origHeight * saveScale <= height) {
    matrix.postScale(mScaleFactor, mScaleFactor, width / 2,
      height / 2);
    if (mScaleFactor < 1) {
     matrix.getValues(m);
     float x = m[Matrix.MTRANS_X];
     float y = m[Matrix.MTRANS_Y];
     if (mScaleFactor < 1) {
      if (Math.round(origWidth * saveScale) < width) {
       if (y < -bottom)
        matrix.postTranslate(0, -(y + bottom));
       else if (y > 0)
        matrix.postTranslate(0, -y);
      } else {
       if (x < -right)
        matrix.postTranslate(-(x + right), 0);
       else if (x > 0)
        matrix.postTranslate(-x, 0);
      }
     }
    }
   } else {
    matrix.postScale(mScaleFactor, mScaleFactor,
      detector.getFocusX(), detector.getFocusY());
    matrix.getValues(m);
    float x = m[Matrix.MTRANS_X];
    float y = m[Matrix.MTRANS_Y];
    if (mScaleFactor < 1) {
     if (x < -right)
      matrix.postTranslate(-(x + right), 0);
     else if (x > 0)
      matrix.postTranslate(-x, 0);
     if (y < -bottom)
      matrix.postTranslate(0, -(y + bottom));
     else if (y > 0)
      matrix.postTranslate(0, -y);
    }
   }
   return true;

  }
 }

 // Sets the maximum zoom, which is a scale relative to the base matrix. It
 // is calculated to show the image at 400% zoom regardless of screen or
 // image orientation. If in the future we decode the full 3 megapixel image,
 // rather than the current 1024x768, this should be changed down to 200%.
 protected float maxZoom() {
  if (mBitmapDisplayed.getBitmap() == null) {
   return 1F;
  }

  float fw = (float) mBitmapDisplayed.getWidth() / (float) mThisWidth;
  float fh = (float) mBitmapDisplayed.getHeight() / (float) mThisHeight;
  float max = Math.max(fw, fh) * 4;
  return max;
 }

 @Override
 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
  super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  width = MeasureSpec.getSize(widthMeasureSpec);
  height = MeasureSpec.getSize(heightMeasureSpec);
  // Fit to screen.
  float scale;
  float scaleX = width / bmWidth;
  float scaleY = height / bmHeight;
  scale = Math.min(scaleX, scaleY);
  matrix.setScale(scale, scale);
  setImageMatrix(matrix);
  saveScale = 1f;

  // Center the image
  redundantYSpace = height - (scale * bmHeight);
  redundantXSpace = width - (scale * bmWidth);
  redundantYSpace /= 2;
  redundantXSpace /= 2;

  matrix.postTranslate(redundantXSpace, redundantYSpace);

  origWidth = width - 2 * redundantXSpace;
  origHeight = height - 2 * redundantYSpace;
  right = width * saveScale - width - (2 * redundantXSpace * saveScale);
  bottom = height * saveScale - height
    - (2 * redundantYSpace * saveScale);
  setImageMatrix(matrix);
 }

}