package com.example.it3176_smartnote.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
  

import com.example.it3176_smartnote.R;
import com.example.it3176_smartnote.UpdateActivity;
import com.example.it3176_smartnote.util.TouchImageView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout; 
import android.widget.ImageView.ScaleType;
  
public class ImageFullScreenActivity extends Activity{
	byte[] imageDecodedString;
    String uri; 
    int rotateImage = 0;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view_full_screen);
        Bundle extras = getIntent().getExtras();
        
        if (extras != null) {
        	imageDecodedString = extras.getByteArray("byteArray");
        	uri = extras.getString("uri");
        	rotateImage = extras.getInt("rotateImage");
        }
        ImageView image = (ImageView) findViewById(R.id.imageReviewFullScreen);
        getActionBar().setTitle(uri.substring(uri.lastIndexOf("/") + 1,uri.length()));
        Bitmap decodedByte = BitmapFactory.decodeByteArray(imageDecodedString,0, imageDecodedString.length);
        image.setImageBitmap(decodedByte);
        if(rotateImage == 90){
        	System.out.println("Rotation: " + rotateImage);
        	Matrix matrix=new Matrix();
        	image.setScaleType(ScaleType.MATRIX);   //required
        	matrix.preRotate((float) 180, image.getWidth()/2, image.getHeight()/2);
        	image.setImageMatrix(matrix);
        }
        TouchImageView img = (TouchImageView) findViewById(R.id.imageReviewFullScreen);
        img.setMaxZoom(4);
        img.setRotation(rotateImage);

    }
      
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }   
    
    public int getCameraPhotoOrientation(Context context, Uri imageUri,
			String imagePath) {
		int rotate = 0;
		try {
			context.getContentResolver().notifyChange(imageUri, null);
			File imageFile = new File(imagePath);

			ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			default:
				rotate = 0;
				break;
			}

			Log.i("RotateImage", "Exif orientation: " + orientation);
			Log.i("RotateImage", "Rotate value: " + rotate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rotate;
	}
} 