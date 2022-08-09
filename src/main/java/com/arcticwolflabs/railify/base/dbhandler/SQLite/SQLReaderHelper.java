package com.arcticwolflabs.railify.base.dbhandler.SQLite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.arcticwolflabs.railify.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SQLReaderHelper extends SQLiteOpenHelper {
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    public final String DATABASE_NAME;
    public final String DATABASE_PATH;
    public final int dbtype;

    public static final int DATABASE_VERSION = 1;
    public static final int DBTYPE_STATION = 1;
    public static final int DBTYPE_TRAIN = 2;
    public static final int DBTYPE_STATION2TRAINS = 3;
    public static final int DBTYPE_PHYSIOGRAPHY = 4;
    public static final int DBTYPE_CGRAPH = 5;

    private static String getDBLocation(Context context, int _dbtype) {
        if (_dbtype == DBTYPE_STATION) {
            return context.getResources().getString(R.string.stationsdbfname);
        } else if (_dbtype == DBTYPE_TRAIN) {
            return context.getResources().getString(R.string.trainsdbfname);
        } else if (_dbtype == DBTYPE_STATION2TRAINS) {
            return context.getResources().getString(R.string.station2trainsdbfname);
        } else if (_dbtype == DBTYPE_PHYSIOGRAPHY) {
            return context.getResources().getString(R.string.physiographydbfname);
        }
        return context.getResources().getString(R.string.cgraphdbfname);
    }

    public SQLReaderHelper(Context context, int _dbtype) {
        super(context, getDBLocation(context, _dbtype), null, DATABASE_VERSION);
        this.DATABASE_NAME = getDBLocation(context, _dbtype);
        this.dbtype = _dbtype;
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DATABASE_PATH = context.getFilesDir().getPath() + context.getPackageName() + "/databases/";
        }
        this.myContext = context;
    }

    //Create a empty database on the system
    public void createDatabase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
            Log.v("DB Exists", "db exists");
            // By calling this method here onUpgrade will be called on a
            // writeable database, but only if the version number has been
            // bumped
            //onUpgrade(myDataBase, DATABASE_VERSION_old, DATABASE_VERSION);
        }

        boolean dbExist1 = checkDataBase();
        if (!dbExist1) {
            this.getReadableDatabase();
            try {
                this.close();
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }

    //Check database already exist or not
    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
            String myPath = DATABASE_PATH + DATABASE_NAME;
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        } catch (SQLiteException e) {
        }
        return checkDB;
    }

    //Copies your database from your local assets-folder to the just created empty database in the system folder
    private void copyDataBase() throws IOException {

        InputStream mInput = myContext.getAssets().open(DATABASE_NAME);
        String outFileName = DATABASE_PATH + DATABASE_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[2024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    //delete database
    public void db_delete() {
        File file = new File(DATABASE_PATH + DATABASE_NAME);
        if (file.exists()) {
            file.delete();
            System.out.println("delete database file.");
        }
    }

    //Open database
    public void openDatabase() throws SQLException {
        String myPath = DATABASE_PATH + DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void closeDataBase() throws SQLException {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            Log.v("Database Upgrade", "Database version higher than old.");
            db_delete();
        }

    }

}
