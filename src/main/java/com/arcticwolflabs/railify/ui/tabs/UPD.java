package com.arcticwolflabs.railify.ui.tabs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arcticwolflabs.railify.R;
import com.arcticwolflabs.railify.base.Database;
import com.arcticwolflabs.railify.ui.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;


public class UPD extends Fragment {

    private Context context;
    private MainActivity main_activity;
    private DatabaseDownloadTask mDatabaseDownloadTask = null;
    private DatabaseOpenTask mDatabaseOpenTask = null;
    private ProgressDialog mProgressDialog = null;
    private OnFragmentInteractionListener mListener;


    @Nullable
    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public UPD() {
    }

    public static UPD newInstance(Context _context) {
        UPD fragment = new UPD();
        fragment.setContext(_context);
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upd, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        Toast.makeText(context, getString(R.string.sd_card_not_found), Toast.LENGTH_LONG).show();
    }
    mDatabaseOpenTask = new DatabaseOpenTask();
        mDatabaseOpenTask.execute(new Context[] { this.context });
        }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class DatabaseDownloadTask extends AsyncTask<Context, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setTitle(getString(R.string.please_wait));
            mProgressDialog.setMessage(getString(R.string.downloading_database));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
           // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        @Override
        protected Boolean doInBackground(Context... params) {
            try {
                InputStream content = null;
                File dbDownloadPath = new File(Database.getDatabaseFolder());
                if (!dbDownloadPath.exists()) {
                    dbDownloadPath.mkdirs();
                }
                HttpURLConnection connection = null;
                try {
                 //   URL url = new URL(DB_DOWNLOAD_PATH);
                 //   connection = (HttpURLConnection) url.openConnection();
                //    connection.connect();
                 //   if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)  { return null; }
                 //   content = connection.getInputStream();
                    long downloadSize = connection.getContentLength();
                    FileOutputStream fos = new FileOutputStream(Database.getDatabaseFolder()+Database.DATABASE_NAME+".sqlite");
                    byte[] buffer = new byte[256];
                    int read;
                    long downloadedAlready = 0;
                    while ((read = content.read(buffer)) != -1) {
                        fos.write(buffer, 0, read);
                        downloadedAlready += read;
                        publishProgress((int) (downloadedAlready*100/downloadSize));
                    }
                    fos.flush();
                    fos.close();
                    content.close();
                    return true;
                }
                catch (Exception e) {
                    if (content != null) {
                        try {
                            content.close();
                        }
                        catch (IOException e1) {}
                    }
                    return false;
                }
            }
            catch (Exception e) {
                return false;
            }
        }

        protected void onProgressUpdate(Integer... values) {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.setProgress(values[0]);
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            if (result.equals(Boolean.TRUE)) {
              //  Toast.makeText(UpdateActivity.this, getString(R.string.database_download_success), Toast.LENGTH_LONG).show();
              //  mDatabaseOpenTask = new UpdateActivity.DatabaseOpenTask();
              //  mDatabaseOpenTask.execute(new Context[] { UpdateActivity.this });
            }
            else {
                //Toast.makeText(getApplicationContext(), getString(R.string.database_download_fail), Toast.LENGTH_LONG).show();
                //finish();
            }
        }
    }

    private class DatabaseOpenTask extends AsyncTask<Context, Void, Database> {

        @Override
        protected Database doInBackground(Context ... ctx) {
            try {
                String externalBaseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                // DELETE OLD DATABASE ANFANG
               // File oldFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+SD_CARD_FOLDER);
              //  File oldFile = new File(oldFolder, "dictionary.sqlite");
              //  if (oldFile.exists()) {
              //      oldFile.delete();
              //  }
             //   if (oldFolder.exists()) {
             //       oldFolder.delete();
              //  }
                // DELETE OLD DATABASE ENDE
                File newDB = new File(Database.getDatabaseFolder()+"dictionary.sqlite");
                if (newDB.exists()) {
                    return new Database(ctx[0]);
                }
                else {
                    return null;
                }
            }
            catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
          //  mProgressDialog = ProgressDialog.show(UpdateActivity.this, getString(R.string.please_wait), "Loading the database! This may take some time ...", true);
        }

        @Override
        protected void onPostExecute(Database newDB) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            if (newDB == null) {
          //      mDB = null;
                AlertDialog.Builder downloadDatabase = new AlertDialog.Builder(context);
                downloadDatabase.setTitle(getString(R.string.downloadDatabase));
                downloadDatabase.setCancelable(false);
                downloadDatabase.setMessage(getString(R.string.wantToDownloadDatabaseNow));
                downloadDatabase.setPositiveButton(getString(R.string.download), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
             //           mDatabaseDownloadTask = new UpdateActivity.DatabaseDownloadTask();
                        mDatabaseDownloadTask.execute();
                    }
                });
                downloadDatabase.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
              //          finish();
                    }
                });
                downloadDatabase.show();
            }
            else {
           //     mDB = newDB;
            }
        }
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
