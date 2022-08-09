package com.arcticwolflabs.railify.core.services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DbDownloader extends Worker {
    int res;
    public DbDownloader(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        res=downloadDb();
        if (res==0){
            return Result.success();
        }else {
            if (res==1){
                return Result.failure();
            }else{
                return Result.retry();
            }
        }
    }

    private int downloadDb(){
        return 0;
    }
}
