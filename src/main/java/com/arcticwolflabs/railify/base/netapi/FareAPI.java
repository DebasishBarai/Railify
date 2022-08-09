package com.arcticwolflabs.railify.base.netapi;

import android.util.Log;

import com.arcticwolflabs.railify.base.dynamics.Fare;
import com.arcticwolflabs.railify.base.dynamics.Journey;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class FareAPI {
    private final String baseurl = "https://erail.in/train-fare/";
    private final String endurl = "&callback="; /* may be add at the beginning: &Cache=1 */
    private HTTPRequester hr;
    /* get https://erail.in/train-fare/12345?from=HWH&to=BWN&adult=1&child=0&sfemale=0&smale=0 */
//    form id="form1" method="post" " +
//    "action-xhr="/data.aspx?action=AMPRedirect&amp;train=12345&amp;lan=en&amp;Data1=train-fare"" +
//
    public FareAPI() {
        this.hr = new HTTPRequester();
    }

    private String build_url(Journey.JourneyMinimal jrny) {
        StringBuilder str = new StringBuilder(baseurl);
        /* <tr_num>_<from_id>_<to_id>_<class_id>_<quota_id>_<day>_<month>~ */
        String cmmn_prt1 = String.format("%05d", jrny.getTrain_num());
        String cmmn_prt2 = "?from=" + jrny.getFrom_id() + "&to=" + jrny.getTo_id();
        str.append(cmmn_prt1).append(cmmn_prt2);

        return String.valueOf(str);
    }

    private Fare parse_result(Document document) {
        Fare fare = new Fare();
        if (!(document==null)){


        Elements table_fares = document.getElementsByClass("tableSingleFare table table-bordered table-condensed");
        Elements row_fares = table_fares.get(0).getElementsByTag("tr");
        Elements class_fares = row_fares.get(0).getElementsByTag("th");
        Elements general_fares = row_fares.get(1).getElementsByTag("td");
        Elements tatkal_fares = row_fares.get(2).getElementsByTag("td");
        int i = 0;
        for (i = 1; i < class_fares.size(); i++) {
            if (general_fares.get(i).text().equalsIgnoreCase("-")){
                general_fares.get(i).text("0");
        }
            if (tatkal_fares.get(i).text().equalsIgnoreCase("-")){
                tatkal_fares.get(i).text("0");
            }

            int[] fare_amount = {(Integer.parseInt(general_fares.get(i).text().replace(",",""))), (Integer.parseInt(tatkal_fares.get(i).text().replace(",","")))};
            if (class_fares.get(i).text().equalsIgnoreCase("1A")) {
                fare.setClasstypeAc1(true);
                fare.setFareAc1(fare_amount);
            }
            if (class_fares.get(i).text().equalsIgnoreCase("2A")) {
                fare.setClasstypeAc2(true);
                fare.setFareAc2(fare_amount);
            }
            if (class_fares.get(i).text().equalsIgnoreCase("3A")) {
                fare.setClasstypeAc3(true);
                fare.setFareAc3(fare_amount);
            }
            if (class_fares.get(i).text().equalsIgnoreCase("CC")) {
                fare.setClasstypeCc(true);
                fare.setFareCc(fare_amount);
            }
            if (class_fares.get(i).text().equalsIgnoreCase("FC")) {
                fare.setClasstypeFc(true);
                fare.setFareFc(fare_amount);
            }
            if (class_fares.get(i).text().equalsIgnoreCase("SL")) {
                fare.setClasstypeSl(true);
                fare.setFareSl(fare_amount);
            }
            if (class_fares.get(i).text().equalsIgnoreCase("2S")) {
                fare.setClasstype2s(true);
                fare.setFare2s(fare_amount);
            }
            if (class_fares.get(i).text().equalsIgnoreCase("3E")){
                fare.setClasstypeEcon3(true);
                fare.setFareEcon3(fare_amount);
            }
            if (class_fares.get(i).text().equalsIgnoreCase("EC")) {
                fare.setClasstypeEc(true);
                fare.setFareEc(fare_amount);
            }
            if (class_fares.get(i).text().equalsIgnoreCase("GN")) {
                fare.setClasstypeUr(true);
                fare.setFareUr(fare_amount);
            }

        }
        }
        return fare;
    }

    public Fare query(Journey.JourneyMinimal jrny) {
        return parse_result(requestGetDocument(build_url(jrny)));
    }

    public String queryRaw(Journey.JourneyMinimal jrny) {
        return requestGetDocument(build_url(jrny)).toString();
    }

    private Document requestGetDocument(String _url) {
        Document document = null;
        try {
            document = Jsoup.connect(_url).get();
        } catch (Exception e) {
            Log.d("FareAPI", e.getLocalizedMessage());
        }
        return document;
    }

}

