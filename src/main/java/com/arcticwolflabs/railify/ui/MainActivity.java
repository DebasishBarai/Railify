package com.arcticwolflabs.railify.ui;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.arcticwolflabs.railify.BuildConfig;
import com.arcticwolflabs.railify.base.statics.Station;
import com.arcticwolflabs.railify.core.services.ServiceSystem;
import com.arcticwolflabs.railify.ui.utils.AsyncResponse;
import com.arcticwolflabs.railify.ui.utils.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.arcticwolflabs.railify.R;
import com.arcticwolflabs.railify.Railify;
import com.arcticwolflabs.railify.base.Settings;
import com.arcticwolflabs.railify.base.dynamics.Journey;
import com.arcticwolflabs.railify.base.dynamics.Place;
import com.arcticwolflabs.railify.base.dynamics.Weather;
import com.arcticwolflabs.railify.base.netapi.PlaceAPI;
import com.arcticwolflabs.railify.base.netapi.WeatherAPI;
import com.arcticwolflabs.railify.core.CacheSystem;
import com.arcticwolflabs.railify.core.CoreSystem;
import com.arcticwolflabs.railify.ui.tabs.AVL;
import com.arcticwolflabs.railify.ui.tabs.PLC;
import com.arcticwolflabs.railify.ui.tabs.PNRS;
import com.arcticwolflabs.railify.ui.tabs.S2S;
import com.arcticwolflabs.railify.ui.tabs.TR;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kobakei.ratethisapp.RateThisApp;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements S2S.OnFIListener, TR.OnFIListener, PLC.OnFIListener {


    private static final int PERMISSION_REQUEST_CODE = 200;
    //For Onboarding
    SharedPreferences sharedPreferences;
    boolean isUserFirstTime;
    private static final String PREFERENCES_FILE = "materialsample_settings";


    Calendar calendar;
    long t1;
    long t2;
    long t3;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Settings settings;
    Boolean placeFragmentOn;
    AppBarLayout appbar;
    /* APIs */
    private WeatherAPI wapi;
    private PlaceAPI papi;


    /* Caches */
    private CacheSystem.CachePlace cp_tr;
    private CacheSystem.CacheWeather cw_tr;

    private InterstitialAd interstitialad;

    private boolean isLocationStarted;

    private boolean isAppStarting;
    private AsyncTask<Void, Void, Void> asyc;
    private Station station;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        sharedPreferences = getSharedPreferences("preferencesFile", MODE_PRIVATE);
        isUserFirstTime = sharedPreferences.getBoolean("firstRun", true);
        sharedPreferences.edit().putBoolean("firstRun", false).apply();

        t1 = Calendar.getInstance().getTimeInMillis();

        MobileAds.initialize(this, "ca-app-pub-7267102133462645~9007518563");

        if (isUserFirstTime) {

            if (!Tools.checkLocationPermissions(this)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE);
            }
        }

        interstitialad = new InterstitialAd(this);
        interstitialad.setAdUnitId("ca-app-pub-7267102133462645/9491670077");
        AdRequest intertitialadrequest = new AdRequest.Builder().build();
        interstitialad.loadAd(intertitialadrequest);

        interstitialad.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                interstitialad.loadAd(new AdRequest.Builder().build());
            }
        });

        //App rating feature initialization
        RateThisApp.onCreate(this);
        RateThisApp.showRateDialogIfNeeded(this, R.style.TimePickerTheme);
        RateThisApp.Config config = new RateThisApp.Config(15, 15);
        RateThisApp.init(config);

        Log.d("main activity", "main activity resumed");
        appbar = findViewById(R.id.appbar);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());
        isLocationStarted = false;
        isAppStarting = true;

        if (isAppStarting) {

            Log.d("main activity", "main activity csystem to be started");

            Log.d("Loading time", "loading time csystem started");


            Log.d("main activity", "main activity csystem started");
            placeFragmentOn = false;

            /*Caches */
            settings = new Settings(this);

            ((Railify) getApplication()).setSharedPreferences(this);

            ((Railify) getApplication()).setT2(Calendar.getInstance().getTimeInMillis());

            //firebase cloud messeging configuration start
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create channel to show notifications.
                String channelId  = "FCM Default channel";
                String channelName = "FCM Messege";
                NotificationManager notificationManager =
                        getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                        channelName, NotificationManager.IMPORTANCE_LOW));
            }

            if (getIntent().getExtras() != null) {
                for (String key : getIntent().getExtras().keySet()) {
                    Object value = getIntent().getExtras().get(key);
                    Log.d("main_activity_firebase", "Key: " + key + " Value: " + value);
                }
            }

            // [START subscribe_topics]
            FirebaseMessaging.getInstance().subscribeToTopic("weather")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "msg_subscribed";
                            if (!task.isSuccessful()) {
                                msg = "msg_subscribe_failed";
                            }
                            Log.d("main_activity_firebase", msg);
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
            // [END subscribe_topics]

            // Get token
            // [START log_reg_token]
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("main_activity_firebase", "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            String token = task.getResult();

                            // Log and toast
                            String msg = "FCM registration Token: "+token;
                            Log.d("main_activity_firebase", msg);
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
            // [END log_reg_token]

            //firebase cloud messeging configuration end
            Log.d("Loading time", "total loading time is " + (((Railify) getApplication()).getT2() - ((Railify) getApplication()).getT1()) + " ms");
            asyc = new DataLoaderASync(new AsyncResponse() {
                @Override
                public void processFinish(boolean _isUserFirstTime, CoreSystem _coreSystem, WeatherAPI _wapi, PlaceAPI _papi, CacheSystem.CacheWeather _cw_tr, CacheSystem.CachePlace _cp_tr) {
                    isUserFirstTime = _isUserFirstTime;
                    Log.d("Loading time", "total loading time is ms processfinish started");
                    ((Railify) getApplication()).setCoreSystem(_coreSystem);
                    wapi = _wapi;
                    papi = _papi;
                    cw_tr = _cw_tr;
                    cp_tr = _cp_tr;
                    Log.d("Loading time", "total loading time is ms processfinish completed");
                }
            }, isUserFirstTime, getApplicationContext(), this, getCoreSystem(), wapi, papi, cw_tr, cp_tr, settings);
            asyc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
        isAppStarting = false;


    }


    public void startLocationWorker() {
        if (!isLocationStarted) {
            getServiceSystem().startLocationWorker();
            isLocationStarted = true;
            Log.d("Location Service", "Location Tracking started");
        }
    }

    public void stopLocationWorker() {
        getServiceSystem().stopLocationWorker();
    }


    public Weather GetWeather(String station_id) {
        Weather res = cw_tr.get(station_id);
        if (res == null) {
            res = wapi.query(station_id);
            if (res != null) {
                cw_tr.set(station_id, res);
                Log.w("WAPICache", "REMOTE FETCHED: " + cw_tr.get(station_id).toString());
            } else {
                Log.w("WAPICache", "Remote weather is null");
            }
            // result.clear() may require deepcopy of ArrayList, inside cache implemented
        } else {
            Log.w("WAPICache", "LOCAL FETCHED: " + res.toString());
        }
        return res;
    }

    public ArrayList<Place> GetPlace(String station_id) {
        ArrayList<Place> res = papi.query(station_id);
        if (res == null) {
            res = papi.query(station_id);
            if (res != null) {
                cp_tr.set(station_id, res);
                Log.w("PAPICache", "REMOTE FETCHED: " + cp_tr.get(station_id).toString());
            } else {
                Log.w("PAPICache", "Remote weather is null");
            }
            // result.clear() may require deepcopy of ArrayList, inside cache implemented
        } else {
            Log.w("PAPICache", "LOCAL FETCHED: " + res.toString());
        }
        return res;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_share) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                String shareMessage = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                //e.toString();
            }
            return true;
        }
        if (id == R.id.action_rate_app) {
            RateThisApp.showRateDialog(this, R.style.TimePickerTheme);
            return true;
        }
        if (id == R.id.action_feedback) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            String[] addresses = {"debasishbaraiju@gmail.com"};
            intent.putExtra(Intent.EXTRA_EMAIL, addresses);
            intent.putExtra(Intent.EXTRA_SUBJECT, "My feedback for the Railify app");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Journey.JourneyMinimal selected_jrny, Date run_date, int tab_num) {
        if (tab_num == 1) {
            TR tr_s2s = (TR) getSupportFragmentManager().getFragments().get(1);
            this.mViewPager.setCurrentItem(1);
            calendar=Calendar.getInstance();
            if(!(run_date==null)){
                calendar.setTime(run_date);
                calendar.add(Calendar.DATE, (-(selected_jrny.getFrom_day()-1)));
            }
            tr_s2s.get_arguments_from_s2s(selected_jrny, calendar.getTime(), tr_s2s.getView().findViewById(
                    R.id.train_route_train_no), tr_s2s.getView().findViewById(R.id.childLinearLayoutContainer));
            ((AutoCompleteTextView) (tr_s2s.getView().findViewById(R.id.train_route_train_no))).dismissDropDown();
        } else {
            AVL avl_s2s = (AVL) getSupportFragmentManager().getFragments().get(3);
            this.mViewPager.setCurrentItem(3);
            avl_s2s.get_arguments_from_s2s(selected_jrny, run_date);
        }
    }

    public void setCurrentTab(int item) {
        if (item >= 0 && item < this.mSectionsPagerAdapter.getCount()) {
            this.mViewPager.setCurrentItem(item);
        }
    }

    public Fragment getFragment(int item) {
        return this.mSectionsPagerAdapter.getItem(item);
    }

    public CoreSystem getCoreSystem() {
        return ((Railify) getApplication()).getCoreSystem();
    }

    public ServiceSystem getServiceSystem() {
        return ((Railify) getApplication()).getServiceSystem();
    }

    @Override
    public void onFragmentInteraction(String station_id, String station_name) {
        PLC pl_frag = PLC.newInstance(this);
        pl_frag.get_arguments_from_tr(station_id, station_name);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_content, pl_frag, "pl_frag");
        fragmentTransaction.addToBackStack(null);
        appbar.setVisibility(View.GONE);
        placeFragmentOn = true;
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(String station_id, String destinationTitle, Location location) {
        //station=getCoreSystem().getStationDetail(station_id);
        Uri uri = Uri.parse("geo:" + Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude()) + "?q=" + Uri.encode(destinationTitle));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (interstitialad.isLoaded()) {
            interstitialad.show();
        }
        if (placeFragmentOn) {
            appbar.setVisibility(View.VISIBLE);
            placeFragmentOn = false;
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("main activity", "onDestroy: onDestroy called");
        super.onDestroy();
        //getServiceSystem().stopLocationService();
        stopLocationWorker();
        getCoreSystem().unloadGraph();
    }

    public WeatherAPI getWapi() {
        return wapi;
    }

    public PlaceAPI getPapi() {
        return papi;
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        Context context;

        public SectionsPagerAdapter(FragmentManager fm, Context _context) {
            super(fm);
            context = _context;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return S2S.newInstance(this.context);
            } else if (position == 1) {
                return TR.newInstance(this.context);
            } else if (position == 2) {
                return PNRS.newInstance(this.context);
            } else if (position == 3) {
                return AVL.newInstance(this.context);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

    }

    private class DataLoaderASync extends AsyncTask<Void, Void, Void> {
        private boolean isUserFirstTime;
        private Context context;
        private WeakReference<MainActivity> mainActivity;
        private CoreSystem coreSystem;
        private WeatherAPI wapi;
        private PlaceAPI papi;
        private CacheSystem.CacheWeather cw_tr;
        private CacheSystem.CachePlace cp_tr;
        private WeakReference<Settings> settings;
        public AsyncResponse delegate = null;

        DataLoaderASync(AsyncResponse delegate, boolean isUserFirstTime, Context context, MainActivity mainActivity, CoreSystem coreSystem, WeatherAPI wapi, PlaceAPI papi, CacheSystem.CacheWeather cw_tr, CacheSystem.CachePlace cp_tr, Settings settings) {
            this.delegate = delegate;
            this.isUserFirstTime = isUserFirstTime;
            this.context = context;
            this.mainActivity = new WeakReference<MainActivity>(mainActivity);
            this.coreSystem = coreSystem;
            this.wapi = wapi;
            this.papi = papi;
            this.cw_tr = cw_tr;
            this.cp_tr = cp_tr;
            this.settings = new WeakReference<Settings>(settings);
        }

        @Override
        protected void onPreExecute() {
            Log.d("Loading time", "total loading time is ms onpreexecute started");

            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.d("Loading time", "total loading time is ms doinbackground started");


            if (isUserFirstTime) {

                //startActivity(introIntent);
                coreSystem.copyAssets();

            }

            Log.d("Loading time", "total loading time is ms loadgraph started");
            coreSystem.loadGraph();
            isUserFirstTime = false;
            //t1=Calendar.getInstance().getTimeInMillis();

            Log.d("main activity", "main activity resumed");


            Log.d("main activity", "main activity csystem to be started");

            Log.d("Loading time", "loading time csystem started");


            Log.d("main activity", "main activity csystem started");
            //startService(new Intent(this, RailifyService.class));


            Log.d("Loading time", "total loading time is ms wapi papi started");
            /* APIs */
            wapi = new WeatherAPI(coreSystem.getDBReader());
            papi = new PlaceAPI(coreSystem.getDBReader());

            Log.d("Loading time", "total loading time is ms cachesystem started");
            /* Caches */
            cw_tr = new CacheSystem.CacheWeather(
                    Double.valueOf(settings.get().getList_store_time_secs().get(settings.get().getCacheStoreTime())));
            cp_tr = new CacheSystem.CachePlace();

            Log.d("Loading time", "total loading time is ms locationservice started");

            mainActivity.get().startLocationWorker();

            Log.d("Loading time", "total loading time is ms doinbackground completed");

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            super.onPostExecute(param);

            Log.d("Loading time", "total loading time is ms delegate started");
            delegate.processFinish(isUserFirstTime, coreSystem, wapi, papi, cw_tr, cp_tr);

            Log.d("Loading time", "total loading time is ms delegate completed");
        }

    }

}



