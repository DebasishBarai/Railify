package com.arcticwolflabs.railify.ui.utils;

import com.arcticwolflabs.railify.base.netapi.PlaceAPI;
import com.arcticwolflabs.railify.base.netapi.WeatherAPI;
import com.arcticwolflabs.railify.core.CacheSystem;
import com.arcticwolflabs.railify.core.CoreSystem;


public interface AsyncResponse {
    void processFinish(boolean isUserFirstTime, CoreSystem coreSystem, WeatherAPI wapi, PlaceAPI papi, CacheSystem.CacheWeather cw_tr, CacheSystem.CachePlace cp_tr);
}
