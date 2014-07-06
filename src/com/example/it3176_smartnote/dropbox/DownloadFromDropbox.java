package com.example.it3176_smartnote.dropbox;
/*
 * Copyright (c) 2010-11 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.bouncycastle.asn1.ocsp.Request;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.SQLiteController.it3176.SQLiteController;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.DropboxAPI.ThumbFormat;
import com.dropbox.client2.DropboxAPI.ThumbSize;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.example.it3176_smartnote.CreateActivity;
import com.example.it3176_smartnote.MainActivity;
import com.example.it3176_smartnote.model.Note;

/**
 * Here we show getting metadata for a directory and downloading a file in a
 * background thread, trying to show typical exception handling and flow of
 * control for an app that downloads a file from Dropbox.
 * @author Lee Zhuo Xun
 *
 */
public class DownloadFromDropbox extends AsyncTask<Void, Long, Boolean> {


    private Context mContext;
    private final ProgressDialog mDialog;
    private DropboxAPI<?> mApi;
    private String mPath;
    private String mUrl;
    private String localFilePath;
    private Drawable mDrawable;

    private FileOutputStream mFos;

    private boolean mCanceled;
    private Long mFileLen;
    private String mErrorMsg;

    public DownloadFromDropbox(Context context, DropboxAPI<?> api,String dropboxPath, String url) {
        // We set the context this way so we don't accidentally leak activities
        mContext = context;

        mApi = api;
        mPath = dropboxPath;
        mUrl = url;

        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("Downloading Note");
        mDialog.setButton("Cancel", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mCanceled = true;
                mErrorMsg = "Canceled";

                // This will cancel the getThumbnail operation by closing
                // its stream
                if (mFos != null) {
                    try {
                        mFos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });

        mDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (mCanceled) {
                return false;
            }

            // Get the metadata for a directory
            Entry dirent = mApi.metadata(mPath, 1000, null, true, null);
            System.out.println(mPath);
            if (!dirent.isDir || dirent.contents == null) {
                // It's not a directory, or there's nothing in it
                mErrorMsg = "File or empty directory";
                return false;
            }

            // Now pick a random one
            /*Entry ent = thumbs.get(index);
            String path = ent.path;
            mFileLen = ent.bytes;*/


            localFilePath = Environment.getExternalStorageDirectory() + "/Documents/" + mUrl.substring(mUrl.lastIndexOf("/") + 1, mUrl.length());
            System.out.println("Cache Path: " + localFilePath);
            try {
                mFos = new FileOutputStream(localFilePath);
            } catch (FileNotFoundException e) {
                mErrorMsg = "Couldn't create a local file to store the note";
                return false;
            }

            DropboxFileInfo info = mApi.getFile("/Smart_note/" + mUrl.substring(mUrl.lastIndexOf("/") + 1, mUrl.length()), null, mFos, null);
            Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
            
            if (mCanceled) {
                return false;
            }
            // We must have a legitimate picture
            return true;

        } catch (DropboxUnlinkedException e) {
            // The AuthSession wasn't properly authenticated or user unlinked.
        } catch (DropboxPartialFileException e) {
            // We canceled the operation
            mErrorMsg = "Download canceled";
        } catch (DropboxServerException e) {
            // Server-side exception.  These are examples of what could happen,
            // but we don't do anything special with them here.
            if (e.error == DropboxServerException._304_NOT_MODIFIED) {
                // won't happen since we don't pass in revision with metadata
            } else if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                // Unauthorized, so we should unlink them.  You may want to
                // automatically log the user out in this case.
            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                // Not allowed to access this
            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                // path not found (or if it was the thumbnail, can't be
                // thumbnailed)
            } else if (e.error == DropboxServerException._406_NOT_ACCEPTABLE) {
                // too many entries to return
            } else if (e.error == DropboxServerException._415_UNSUPPORTED_MEDIA) {
                // can't be thumbnailed
            } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
                // user is over quota
            } else {
                // Something else
            }
            // This gets the Dropbox error, translated into the user's language
            mErrorMsg = e.body.userError;
            if (mErrorMsg == null) {
                mErrorMsg = e.body.error;
            }
        } catch (DropboxIOException e) {
            // Happens all the time, probably want to retry automatically.
            mErrorMsg = "Network error.  Try again.";
        } catch (DropboxParseException e) {
            // Probably due to Dropbox server restarting, should retry
            mErrorMsg = "Dropbox error.  Try again.";
        } catch (DropboxException e) {
            // Unknown error
            mErrorMsg = "Unknown error.  Try again.";
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Long... progress) {
        int percent = (int)(100.0*(double)progress[0]/mFileLen + 0.5);
        mDialog.setProgress(percent);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mDialog.dismiss();
        if (result) {
            // Set the image now that we have it
            //mView.setImageDrawable(mDrawable);
        	Log.d("URL: ",localFilePath);
        	//Get the text file
        	File file = new File(localFilePath);
        	//Read text from file
        	ArrayList<String> textArrList = new ArrayList<String>();
        	try {
        	    BufferedReader br = new BufferedReader(new FileReader(file));
        	    String line;
        	    while ((line = br.readLine()) != null) {
        	    	textArrList.add(line.substring(line.indexOf(":", 1) + 1));
        	    }
        	}
        	catch (IOException e) {
        	    //You'll need to add proper error handling here
        		e.printStackTrace();
        	}
        	//
        	String noteTitle = textArrList.get(0);
        	String category = textArrList.get(1);
        	String content = "";
        	for(int i=2;i<textArrList.size();i++){
        		content += textArrList.get(i);
        		content += "\n";
        	}
    	    System.out.println(content);
    	    try{
    	    	Note note = null;
    	    	if(category.equals("Personal") || (category.equals("Meeting Notes") || (category.equals("Client")))){    	    		
    	    		note = new Note(noteTitle, content, category, "", "", "");
    	    	}
    	    	else{
    	    		note = new Note(noteTitle, content, "Personal", "", "", "");
    	    	}
				SQLiteController entry = new SQLiteController(mContext);
				entry.open();
				entry.insertNote(note);
				entry.close();
    	    }
    	    catch(Exception e){
    	    	mErrorMsg = "Something went wrong in processing the downloaded file.";
    	    	showToast(mErrorMsg);	
    	    }
    	    finally{
				Toast.makeText(mContext, "Note Saved", Toast.LENGTH_LONG).show();
				file.delete();
				((Activity) mContext).finish();
				Intent intent = new Intent(mContext, MainActivity.class);
				mContext.startActivity(intent);		
    	    }
        } else {
            // Couldn't download it, so show an error
            showToast(mErrorMsg);
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        error.show();
    }


}