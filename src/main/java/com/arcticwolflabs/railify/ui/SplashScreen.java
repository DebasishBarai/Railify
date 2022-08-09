package com.arcticwolflabs.railify.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.arcticwolflabs.railify.R;
import com.arcticwolflabs.railify.Railify;
import com.arcticwolflabs.railify.core.CoreSystem;
import com.arcticwolflabs.railify.core.services.ServiceSystem;

import java.util.Calendar;

public class SplashScreen extends AppCompatActivity {

    CoreSystem csystem;
    ServiceSystem serviceSystem;
    LoadCoreServiceSystemAsync loadCoreServiceSystemAsync;
    LottieAnimationView coreSystemLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        coreSystemLoading = (LottieAnimationView) findViewById(R.id.core_system_loading_animation);
        loadCoreServiceSystemAsync = new LoadCoreServiceSystemAsync();
        loadCoreServiceSystemAsync.execute(getApplicationContext());
        //new Handler().postDelayed(new Runnable() {
        //@Override
        //public void run() {
        //csystem = new CoreSystem(getApplicationContext());

        //}
        //},10000);
        ((Railify) getApplication()).setT1(Calendar.getInstance().getTimeInMillis());
    }

    public class LoadCoreServiceSystemAsync extends AsyncTask<Context, LottieAnimationView, Void> {
        CoreSystem _coreSystem;
        ServiceSystem _serviceSystem;


        @Override
        protected void onPreExecute() {
            coreSystemLoading.playAnimation();

        }

        @Override
        protected Void doInBackground(Context... contexts) {

            _coreSystem = new CoreSystem(contexts[0]);
            _serviceSystem=new ServiceSystem(contexts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            UpdateToUI(_coreSystem, _serviceSystem);
        }
    }

    private void UpdateToUI(CoreSystem coreSystem,ServiceSystem serviceSystem) {
        ((Railify) getApplication()).setCoreSystem(coreSystem);
        ((Railify) getApplication()).setServiceSystem(serviceSystem);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();

    }

}
