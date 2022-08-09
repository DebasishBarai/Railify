package com.arcticwolflabs.railify.base;

import android.content.Context;

import com.arcticwolflabs.railify.base.netapi.Crypto;
import com.arcticwolflabs.railify.base.netapi.HTTPRequester;

import java.util.LinkedList;
import java.util.Queue;

public class DataStats {
    private Context context;
    private HTTPRequester hr;
    private Queue<Stat> stats;

    public DataStats(Context context) {
        this.context = context;
        this.stats = new LinkedList<Stat>();
        this.hr = new HTTPRequester();
    }

    public static class Stat {
        private String data;

        @Override
        public String toString() {
            return "Stat{" +
                    "data='" + data + '\'' +
                    '}';
        }
    }

    public void sendStats() {
        String secretKey = "0123456789012345";
        for (Stat cstat : this.stats) {
            Crypto encrypted = Crypto.encrypt(secretKey, cstat.toString());
            String estr = encrypted.getData();
            // upload stat
        }
    }

    public void addStats(Stat stat) {
        this.stats.add(stat);

    }

}
