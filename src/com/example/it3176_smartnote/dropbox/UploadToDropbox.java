package com.example.it3176_smartnote.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

/**
 * This class is to upload a existing note into dropbox.
 * @author Lee Zhuo Xun
 *
 */
public class UploadToDropbox extends AsyncTask<Void, Long, Boolean> {

    private DropboxAPI<?> mApi;
    private String mPath;
    private File mFile;

    private long mFileLen;
    private UploadRequest mRequest;
    private Context mContext;
    private ProgressDialog mDialog;

    private String mErrorMsg;

    /**
     * This is the default constructor to upload file into dropbox
     * @param context
     * @param api
     * @param dropboxPath
     * @param file
     */
    public UploadToDropbox(Context context, DropboxAPI<?> api, String dropboxPath,
            File file) {
        // We set the context this way so we don't accidentally leak activities
        mContext = context;

        mFileLen = file.length();
        mApi = api;
        mPath = dropboxPath;
        mFile = file;
        
        mDialog = new ProgressDialog(mContext);
        mDialog.setMax(100);
        mDialog.setMessage("Uploading " + mFile.getName());
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setProgress(0);
        mDialog.setButton("Cancel", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // This will cancel the putFile operation
                mRequest.abort();
            }
        });
        mDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
        	mFile.setExecutable(false);
        	mFile.setWritable(false);
        	mFile.setReadable(true);
            // By creating a request, we get a handle to the putFile operation,
            // so we can cancel it later if we want to
            FileInputStream fis = new FileInputStream(mFile);
            String path = mPath + mFile.getName();
            //This is to detect whether there is any such folder in Dropbox
            Entry existingEntry = mApi.metadata("/Smart_note", 1, null, false, null);
            Log.i("DbExampleLog", "The file's rev is now: " + existingEntry.rev);
            if((existingEntry.rev == null) || (existingEntry.rev.length() == 0)){
                mApi.createFolder("/Smart_note");	
            }
            //Process the file and upload it into dropbox
            mRequest = mApi.putFileOverwriteRequest(path, fis, mFile.length(),
                    new ProgressListener() {
                @Override
                public long progressInterval() {
                    // Update the progress bar every half-second or so
                    return 500;
                }

                @Override
                public void onProgress(long bytes, long total) {
                    publishProgress(bytes);
                }
            });

            if (mRequest != null) {
                mRequest.upload();
                return true;
            }

        } catch (DropboxUnlinkedException e) {
            // This session wasn't authenticated properly or user unlinked
            mErrorMsg = "This app wasn't authenticated properly.";
            e.printStackTrace();
        } catch (DropboxFileSizeException e) {
            // File size too big to upload via the API
            mErrorMsg = "This file is too big to upload";
            e.printStackTrace();
        } catch (DropboxPartialFileException e) {
            // We canceled the operation
            mErrorMsg = "Upload canceled";
            e.printStackTrace();
        } catch (DropboxServerException e) {
            // Server-side exception.  These are examples of what could happen,
            // but we don't do anything special with them here.
            if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                // Unauthorized, so we should unlink them.  You may want to
                // automatically log the user out in this case.
            	e.printStackTrace();
            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                // Not allowed to access this
            	e.printStackTrace();
            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                // path not found (or if it was the thumbnail, can't be
                // thumbnailed)
            	e.printStackTrace();
            } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
                // user is over quota
            	e.printStackTrace();
            } else {
                // Something else
            	e.printStackTrace();
            }
            // This gets the Dropbox error, translated into the user's language
            mErrorMsg = e.body.userError;
            if (mErrorMsg == null) {
                mErrorMsg = e.body.error;
            }
        } catch (DropboxIOException e) {
            // Happens all the time, probably want to retry automatically.
            mErrorMsg = "Network error.  Try again.";
            e.printStackTrace();
        } catch (DropboxParseException e) {
            // Probably due to Dropbox server restarting, should retry
            mErrorMsg = "Dropbox error.  Try again.";
            e.printStackTrace();
        } catch (DropboxException e) {
            // Unknown error
            mErrorMsg = "Unknown error.  Try again.";
            e.printStackTrace();
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
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
        //Check whether the note has uploaded into dropbox successfully
        if (result) {
            showToast("Note uploaded successfully");
            mFile.delete();
        } else {
            showToast(mErrorMsg);
        }
    }

    /**
     * Easier way to show the toast
     * @param msg
     */
    private void showToast(String msg) {
        Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        error.show();
    }
}