package com.example.it3176_smartnote.util;

import java.io.ByteArrayOutputStream;
  

import com.example.it3176_smartnote.R;
import com.example.it3176_smartnote.util.TouchImageView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
  
public class ImageFullScreenActivity extends Activity{
    String uri;
      
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view_full_screen);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	uri = extras.getString("uri");
        }
        ImageView image = (ImageView) findViewById(R.id.imageReviewFullScreen);
        getActionBar().setTitle(uri.substring(uri.lastIndexOf("/") + 1,uri.length()));
         
        image.setImageURI(Uri.parse(uri));
        TouchImageView img = (TouchImageView) findViewById(R.id.imageReviewFullScreen);
        img.setMaxZoom(4);
    }
      
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }   
} 