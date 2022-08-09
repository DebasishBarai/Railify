package com.arcticwolflabs.railify.base.netapi;

import android.util.Log;


import com.arcticwolflabs.railify.base.dynamics.PNRPassenger;
import com.arcticwolflabs.railify.base.dynamics.PNRStatus;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.FormBody;


public class PNRStatusAPI {

    private final String baseurlfirst="https://erail.in/pnr-status/";
    private final String baseurl = "https://data.tripmgt.com/Data.aspx?Action=PNR_STATUS_RR&Data1=";
    private HTTPRequester hr;

    OkHttpClient okHttpClient=new OkHttpClient();
    // https://data.tripmgt.com/Data.aspx?Action=PNR_STATUS_RR&Data1=<PNRNUM>&t=<TIME>
    // PNRNUM - pnr number e.g. 6124983758
    // TIME - number of milliseconds since 1970/01/01 e.g. 1549968340997 at 4.16PM, Feb 12, 2019


    private static final String SERVICE_NAME = "TrainPnrStatusService";
    private static final String PARAM_PNR = "lccp_pnrno1";
    private static final String SERVICE_URL = "https://www.trainspnrstatus.com/pnrformcheck.php";
    private static final String PARAM_REFERER = "referer";
    private static final String PARAM_ORIGIN = "origin";
    private static final String REFERRER_URL = "https://www.trainspnrstatus.com/";
    private static final String ORIGIN_URL = "https://www.trainspnrstatus.com";
    private static final String SEPARATOR_SLASH = "/";
    private static final String PARAM_DNT = "dnt";
    private static final String PARAM_SCHEME = "scheme";
    private static final String PARAM_CONTENT_TYPE = "content-type";
    private static final String VALUE_ONE = "1";
    private static final String VALUE_HTTPS = "https";
    private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String	TD_END	= "</td>";
    private static final String	TD_START	= "<td";
    private static final String TABLE_START = "<table";
    private static final String TABLE_END = "</table>";



    public PNRStatusAPI() {
        this.hr = new HTTPRequester();
    }

    private String build_url(long pnr_num) {
        StringBuilder str = new StringBuilder(baseurl);
        str.append(String.valueOf(pnr_num));
        return String.valueOf(str);
    }

    private String build_url_first(long pnr_num) {
        StringBuilder str = new StringBuilder(baseurlfirst);
        str.append(String.valueOf(pnr_num));
        return String.valueOf(str);
    }

    private PNRStatus parse_result(long pnr_num,String str) {
            PNRStatus pnrStatus = new PNRStatus();
            ArrayList<String> elements=new ArrayList<>();
            try {
                elements = ParseTrainPnrStatusResponse(str);
            } catch (Exception e) {

            }

            Log.d("pnr_status", "elements in parsed response : " + elements.size());

            pnrStatus.setPnr_num(pnr_num);
            int infoDataCount = 8;
            if (elements.size() > infoDataCount) {
                // Seems to be a valid ticket data
                String ticketClass = elements.get(3).trim();
                int index = elements.get(0).indexOf('*');
                if (index > -1) {
                    pnrStatus.setTrainNo(elements.get(0).substring(index + 1));
                }else {
                pnrStatus.setTrainNo(elements.get(0));
                }
                pnrStatus.setTrainName(elements.get(1));
                pnrStatus.setTrainJourneyDate(elements.get(2));
                // FromStation, ToStation, ReservedUpTo,BoardingPoint

                pnrStatus.setDestination(elements.get(5));
                pnrStatus.setEmbarkPoint(elements.get(6));
                pnrStatus.setBoardingPoint(elements.get(7));
                pnrStatus.setTicketClass(ticketClass);

                int passengersCount = (elements.size() - infoDataCount - 1) / 3;
                Log.d("pnr_status", "passengersCount : " + passengersCount);

                int passengerDataIndex = infoDataCount;
                ArrayList<PNRPassenger> passengersList = new ArrayList();
                for (int i = 1; i <= passengersCount; i++) {
                    String currentStatus = elements.get(passengerDataIndex + 2);
                    String bookingBerth = elements.get(passengerDataIndex + 1);

                    // Create the PassengerDataVo
                    PNRPassenger passengerData = new PNRPassenger();
                    passengerData.setPassengerIndex(elements.get(passengerDataIndex));
                    passengerData.setBookingBerth(bookingBerth);
                    passengerData.setCurrentStatus(currentStatus);


                    passengersList.add(passengerData);
                    passengerDataIndex += 3;
                }
                String chartStatus = elements.get(elements.size() - 1);
                pnrStatus.setChartStatus(chartStatus);
                pnrStatus.setPassengers(passengersList);
            } else {
            }
            return pnrStatus;
        }

    public PNRStatus query(long pnr_num) {
        return parse_result(pnr_num,get_response(pnr_num));
    }

    public String queryRaw(long pnr_num) {
        return hr.requestGETString(build_url(pnr_num));
    }

    private String get_response(long pnr_num){
        // Create the headers and params for request
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();

        // Creating the headers
        headers.put(PARAM_REFERER, REFERRER_URL);
        headers.put(PARAM_ORIGIN, ORIGIN_URL);
        headers.put(PARAM_DNT, VALUE_ONE);
        headers.put(PARAM_SCHEME, VALUE_HTTPS);
        headers.put(PARAM_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);

        // Form Data for the request
        params.put(PARAM_PNR, Long.toString(pnr_num));

        // invoke the post method and get the response
        String webResponse="";
        try {
            webResponse = doPostRequest(SERVICE_URL, headers, params);
            if (webResponse.equals("")) {
                Log.d("pnr_status", "get_response: null response");
            }
            Log.d("pnr_status", "get_response: "+webResponse);;
        } catch (Exception e) {

        }

        return webResponse;
    }

    private String doPostRequest(final String targetUrl,
                                 final HashMap<String, String> headers,
                                 final HashMap<String, String> params){
        //RequestBody requestBody = RequestBody.create(WEB_FORM, "");
        String res="";
        try {
            Request.Builder requestBuilder = new Request.Builder()
                    .url(targetUrl);
            if (null != headers) {
                for (String key : headers.keySet()) {
                    requestBuilder.addHeader(key, String.valueOf(headers.get(key)));
                }
            }
            //--
            FormBody.Builder formEncodingBuilder = new FormBody.Builder();
            if (null != params) {
                for (String key : params.keySet()) {
                    formEncodingBuilder.add(key, String.valueOf(params.get(key)));
                }
            }

            RequestBody formBody = formEncodingBuilder.build();
            Request request = requestBuilder
                    .url(targetUrl)
                    .post(formBody)
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            res = response.body().string();
            return res;
        } catch (IOException ex) {
        }
        return res;
    }

    public ArrayList<String> ParseTrainPnrStatusResponse(final String html){
        final String DATA_BLOCK_STYLE = "table table-striped table-bordered";
        ArrayList<String> elements = new ArrayList();
        if(null != html && html.length() > 0) {
            int startIndex = html.indexOf(DATA_BLOCK_STYLE);
            int endIndex = html.indexOf(TABLE_END, startIndex);

            String journeyDetails = html.substring(startIndex, endIndex);
            ArrayList<String> journeyDetailsList = getCellDataAsItems(journeyDetails);
            if(journeyDetailsList.isEmpty() || journeyDetailsList.size() != 17){
                return elements;
            }
            // TrainNo, TrainName, TravelDate, TicketClass
            elements.add(getInnerValue( journeyDetailsList.get(5), "a"));
            elements.add(getInnerValue( journeyDetailsList.get(6), "a"));
            elements.add(journeyDetailsList.get(7));
            elements.add(journeyDetailsList.get(8));
            // FromStation, ToStation, ReservedUpTo,BoardingPoint
            elements.add(journeyDetailsList.get(13));
            elements.add(journeyDetailsList.get(14));
            elements.add(journeyDetailsList.get(15));
            elements.add(journeyDetailsList.get(16));

            // Process passengers table
            startIndex = html.indexOf(DATA_BLOCK_STYLE, endIndex);
            endIndex = html.indexOf(TABLE_END, startIndex);

            String passengerDetails = html.substring(startIndex, endIndex);
            ArrayList<String> passengerDetailsList = getCellDataAsItems(passengerDetails);
            int NUM_HEADER_ROWS = 3;
            int NUM_TRAILER_ROWS = 2;
            int NUM_ROWS_PER_PASSENGER = 3;
            int numPassengers = (passengerDetailsList.size() - NUM_HEADER_ROWS - NUM_TRAILER_ROWS) / NUM_ROWS_PER_PASSENGER;
            int offset;
            for(int i = 0; i < numPassengers; i++) {
                offset = NUM_HEADER_ROWS + (i * NUM_ROWS_PER_PASSENGER);
                elements.add(getInnerValue(passengerDetailsList.get( offset ), "strong"));
                elements.add(getInnerValue(passengerDetailsList.get( offset + 1),"strong"));
                elements.add(passengerDetailsList.get( offset + 2));
            }
            // ChartPrepareStatus
            int last = NUM_HEADER_ROWS + numPassengers * NUM_ROWS_PER_PASSENGER + 1;
            elements.add( passengerDetailsList.get(last) );
        }
        return elements;
    }

    private static String getInnerValue(final String formatted, final String element){
        int startIndex = formatted.indexOf("<" + element);
        if(startIndex > -1){
            startIndex = formatted.indexOf(">");
            int endIndex = formatted.indexOf("</" + element + ">");
            return formatted.substring(startIndex + 1, endIndex);
        }
        return formatted;
    }
    private ArrayList<String> getCellDataAsItems(String journeyDetails) {
        ArrayList<String> journeyDetailsList = new ArrayList<>();
        int itemStartIndex = journeyDetails.indexOf(TD_START);
        int itemEndIndex;
        String data;
        while(itemStartIndex > -1){
            itemStartIndex = journeyDetails.indexOf(">", itemStartIndex);
            itemEndIndex = journeyDetails.indexOf(TD_END, itemStartIndex);
            data = journeyDetails.substring(itemStartIndex + 1, itemEndIndex);
            journeyDetailsList.add(data);
            itemStartIndex = journeyDetails.indexOf(TD_START, itemEndIndex);
        }
        return journeyDetailsList;
    }


}
