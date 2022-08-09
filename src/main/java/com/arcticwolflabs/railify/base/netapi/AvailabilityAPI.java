package com.arcticwolflabs.railify.base.netapi;

import android.util.Log;

import com.arcticwolflabs.railify.base.dynamics.Availability;
import com.arcticwolflabs.railify.base.dynamics.Journey;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AvailabilityAPI {
    private final String baseurl = "https://d.erail.in/AVL_Request?Key=";
    private final String endurl = "&callback="; /* may be add at the beginning: &Cache=1 */
    private HTTPRequester hr;
    private ArrayList<Availability> availabilityArrayList;
    private boolean date_status = false;

    public AvailabilityAPI() {
        this.hr = new HTTPRequester();
    }

    private String build_url(Journey.JourneyMinimal jrny, String quotatype, Calendar cal, int date_add) {
        int month=cal.get(Calendar.MONTH) +1;
        StringBuilder str = new StringBuilder(baseurl);
        String cmmn_prt1 = String.format("%05d", jrny.getTrain_num()) + "_" + jrny.getFrom_id() + "_" + jrny.getTo_id() + "_";
        String cmmn_prt2 = "_" + quotatype + "_" + cal.get(Calendar.DAY_OF_MONTH) + "-" + month + "~";
        for (int j = 0; j < Availability.NUM_CLASSTYPE; ++j) {
            str.append(cmmn_prt1).append(Availability.CLASSTYPES[j]).append(cmmn_prt2);
        }
        int i;
        for (i = 0; i < date_add; i++) {
            cal.add(Calendar.DATE, 1);
            month=cal.get(Calendar.MONTH)+1;
            /* <tr_num>_<from_id>_<to_id>_<class_id>_<quota_id>_<day>_<month>~ */
            cmmn_prt1 = String.format("%05d", jrny.getTrain_num()) + "_" + jrny.getFrom_id() + "_" + jrny.getTo_id() + "_";
            cmmn_prt2 = "_" + quotatype + "_" + cal.get(Calendar.DAY_OF_MONTH) + "-" + month + "~";
            for (int j = 0; j < Availability.NUM_CLASSTYPE; ++j) {
                str.append(cmmn_prt1).append(Availability.CLASSTYPES[j]).append(cmmn_prt2);
            }
        }
        str.append(endurl);
        Log.d("build_url:", "build_url: "+String.valueOf(str));
        return String.valueOf(str);
    }

    private ArrayList<Availability> parse_result(String str, String quotatype) {
        availabilityArrayList = new ArrayList<Availability>();
        int pos = -1;
        if (str.equalsIgnoreCase("Empty response from server")) {
            return availabilityArrayList;
        } else {
            String date_val = "";
            String[] separated = str.split("\\n");

            HashMap<String, Integer> indices = new HashMap<>();

            int i = 0;
            for (i = 0; i < separated.length; i++) {
                Availability avl;
                // check date if avl present in res list, else create new avl
                String[] query_strings = separated[i].split("\\t", 3);
                if(!indices.containsKey(query_strings[0])) {
                    pos = pos+1;
                    indices.put(query_strings[0], pos);
                    availabilityArrayList.add(pos, new Availability());
                    avl = availabilityArrayList.get(pos);
                    avl.setDate(query_strings[0]);
                    avl.setQuota(quotatype);
                }
                else {
                    avl = availabilityArrayList.get(indices.get(query_strings[0]));
                }
                if ((separated[i].contains("AVAILABLE"))||(separated[i].contains("CURR_AVBL"))) {

                    String string_integer = query_strings[2].replaceAll("[^\\d]", "");
                    if (string_integer.equalsIgnoreCase("")){
                        string_integer=Integer.toString(0);
                    }
                    Integer[] intValue = {Integer.valueOf(string_integer), -1, -1, -1, -1};
                    switch (query_strings[1]) {
                        case "1A":
                            avl.setAvailability_1A(intValue);
                            break;
                        case "2A":
                            avl.setAvailability_2A(intValue);
                            break;
                        case "3A":
                            avl.setAvailability_3A(intValue);
                            break;
                        case "CC":
                            avl.setAvailability_CC(intValue);
                            break;
                        case "FC":
                            avl.setAvailability_FC(intValue);
                            break;
                        case "SL":
                            avl.setAvailability_SL(intValue);
                            break;
                        case "2S":
                            avl.setAvailability_2S(intValue);
                            break;
                        case "3E":
                            avl.setAvailability_3E(intValue);
                            break;
                        case "EC":
                            avl.setAvailability_EC(intValue);
                            break;
                    }

                }
                if (separated[i].contains("RAC")) {
                    //change to incorporate RAC
                    String[] string_one=query_strings[2].split("/");
                    //String string_integer = query_strings[2].replaceAll("[^\\d]", "");
                    String string_integer = string_one[1].replaceAll("[^\\d]", "");
                    if (string_integer.equalsIgnoreCase("")){
                        string_integer=Integer.toString(0);
                    }
                    Integer[] intValue = {-1, Integer.valueOf(string_integer), -1, -1, -1};
                    switch (query_strings[1]) {
                        case "1A":
                            avl.setAvailability_1A(intValue);
                            break;
                        case "2A":
                            avl.setAvailability_2A(intValue);
                            break;
                        case "3A":
                            avl.setAvailability_3A(intValue);
                            break;
                        case "CC":
                            avl.setAvailability_CC(intValue);
                            break;
                        case "FC":
                            avl.setAvailability_FC(intValue);
                            break;
                        case "SL":
                            avl.setAvailability_SL(intValue);
                            break;
                        case "2S":
                            avl.setAvailability_2S(intValue);
                            break;
                        case "3E":
                            avl.setAvailability_3E(intValue);
                            break;
                        case "EC":
                            avl.setAvailability_EC(intValue);
                            break;
                    }
                }
                if (separated[i].contains("WL")) {
                    String[] string_one = query_strings[2].split("/");
                    String string_two = string_one[0];
                    string_two = string_two.replaceAll("[\\d]", "");
                    String string_three = string_one[0];
                    string_three = string_three.replaceAll("[^\\d]", "");
                    String string_four = string_one[1];
                    string_four = string_four.replaceAll("[^\\d]", "");
                    int string_five = -1;
                    switch (string_two) {
                        case "GNWL":
                            string_five = 0;
                            break;
                        case "RLWL":
                            string_five = 1;
                            break;
                        case "PQWL":
                            string_five = 2;
                            break;
                        case "RLGN":
                            string_five = 3;
                            break;
                        case "RSWL":
                            string_five = 4;
                            break;
                        case "RQWL":
                            string_five = 5;
                            break;
                        case "CKWL":
                            string_five = 6;
                            break;
                    }
                    if(string_three.equals("")) {
                        string_three = "0";
                    }
                    if(string_four.equals("")) {
                        string_four = "0";
                    }
                    Integer[] intValue = {-1, -1, Integer.valueOf(string_four), Integer.valueOf(string_three), Integer.valueOf(string_five)};
                    switch (query_strings[1]) {
                        case "1A":
                            avl.setAvailability_1A(intValue);
                            break;
                        case "2A":
                            avl.setAvailability_2A(intValue);
                            break;
                        case "3A":
                            avl.setAvailability_3A(intValue);
                            break;
                        case "CC":
                            avl.setAvailability_CC(intValue);
                            break;
                        case "FC":
                            avl.setAvailability_FC(intValue);
                            break;
                        case "SL":
                            avl.setAvailability_SL(intValue);
                            break;
                        case "2S":
                            avl.setAvailability_2S(intValue);
                            break;
                        case "3E":
                            avl.setAvailability_3E(intValue);
                            break;
                        case "EC":
                            avl.setAvailability_EC(intValue);
                            break;
                    }
                }

            }

        }
        return availabilityArrayList;
    }

    private String parse_result_to_string(String str) {
        StringBuilder out = new StringBuilder();
        String[] separated = str.split("~");
        if (separated.length > 2) {
            if (separated[0].contains("AVL_Response")) {
                int max_num = separated.length - 1;
                Log.d("AvlAPI", "Max_num: " + max_num);
                for (int i = 1; i < max_num; ++i) {
                    /* format ...^GNWL1/WL1^n1_n2 (need to extract) */
                    String[] threeparts = separated[i].split("\\^");
                    String date_val = threeparts[0].substring(threeparts[0].lastIndexOf("_") + 1);
                    // get class
                    String[] firstsix = threeparts[0].split("_");
                    Log.d("AvlAPI", "firstsix size: " + firstsix.length);
                    Log.d("AvlAPI", "firstsix  before split: " + threeparts[0]);
                    for (int j = 0; j < Availability.NUM_CLASSTYPE; ++j) {
                        if (firstsix[3].equals(Availability.CLASSTYPES[j])) {
                            // get avl
                            String[] secone = threeparts[0].split("_");
                            // switch (cls)
                            out.append(date_val + "\t" + Availability.CLASSTYPES[j] + "\t" + threeparts[1] + "\n");
                        }
                    }
                }
            }
        } else {
            return "Empty response from server";
        }
        return out.toString();
    }

    public ArrayList<Availability> query(Journey.JourneyMinimal jrny, String quotatype, Calendar cal, int date_add) {
        return parse_result(parse_result_to_string(hr.requestGETString(build_url(jrny, quotatype, cal, date_add))), quotatype);
    }

    public String queryRaw(Journey.JourneyMinimal jrny, String quotatype, Calendar cal) {
        return hr.requestGETString(build_url(jrny, quotatype, cal, 6));
    }

    public String queryRawProcessed(Journey.JourneyMinimal jrny, String quotatype, Calendar cal) {
        String ss = hr.requestGETString(build_url(jrny, quotatype, cal, 6));
        Log.d("HTTPR", ss);
        return parse_result_to_string(ss);
    }


}

