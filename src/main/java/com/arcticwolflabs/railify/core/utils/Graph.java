package com.arcticwolflabs.railify.core.utils;

import com.arcticwolflabs.railify.core.CacheSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import static java.lang.Math.abs;

public class Graph {

    /*public static ArrayList<CacheSystem.Pair<String, ArrayList<Integer>>> find_paths(HashMap<String, HashMap<String, ArrayList<Integer>>> graph, String start, String stop)
    {
        HashMap<String, ArrayList<Integer>> step_1 = graph.get(start);
        Comparator<CacheSystem.Pair<String, ArrayList<Integer>>> PairComparator = new Comparator<CacheSystem.Pair<String, ArrayList<Integer>>>() {
            @Override
            public int compare(CacheSystem.Pair<String, ArrayList<Integer>> e1, CacheSystem.Pair<String, ArrayList<Integer>> e2) {
                return e1.second.compareTo(e2.second);
            }
        };*/

    public static ArrayList<CacheSystem.Pair<String, ArrayList<Integer>>> find_paths(HashMap<String, HashMap<String, ArrayList<Integer>>> graph, String start, String stop, Integer dist) {
        HashMap<String, ArrayList<Integer>> step_1 = graph.get(start);
        Comparator<CacheSystem.Pair<String, ArrayList<Integer>>> PairComparator = new Comparator<CacheSystem.Pair<String, ArrayList<Integer>>>() {
            @Override
            public int compare(CacheSystem.Pair<String, ArrayList<Integer>> o1, CacheSystem.Pair<String, ArrayList<Integer>> o2) {
                int e1 = 0;
                int e2 = 0;
                int i = 0;
                for (i = 0; i < o1.second.size(); i++) {
                    e1 = e1 + o1.second.get(i);
                }
                e1 = e1 / i;
                for (i = 0; i < o2.second.size(); i++) {
                    e2 = e2 + o2.second.get(i);
                }
                e2 = e2 / i;

                return (e2 > e1) ? (-1) : 1;
            }
        };
        ArrayList<CacheSystem.Pair<String, ArrayList<Integer>>> path_1 = new ArrayList<CacheSystem.Pair<String, ArrayList<Integer>>>();
        ArrayList<CacheSystem.Pair<String, ArrayList<Integer>>> path_2 = new ArrayList<>();
        if (dist == 0) {
            for (String x : step_1.keySet()) {
                if (x.equals(stop)) {
                    path_1.add(new CacheSystem.Pair<String, ArrayList<Integer>>(null, step_1.get(x)));
                    continue;
                }
                // hacky way to enable directness in graph;
                if (graph.containsKey(x)) {
                    for (String y : graph.get(x).keySet()) {
                        if (y.equals(stop)) {
                            path_2.add(new CacheSystem.Pair<String, ArrayList<Integer>>(x, graph.get(x).get(y)));//step_1.get(x) + graph.get(x).get(y)
                        }
                    }
                }
            }
            // [path_1, Collections.sort(path_2, PairComparator)]
            Collections.sort(path_2, PairComparator);
        } else {
            ArrayList<Integer> dist_first = new ArrayList<Integer>();
            ArrayList<Integer> dist_second = new ArrayList<Integer>();
            ArrayList<Integer> diff_dist = new ArrayList<Integer>();
            int valid_dist_first = 0;
            int valid_dist_second = 0;
            int valid_diff_dist = 0;
            boolean isstart = true;
            String valid_via_station = null;
            for (String x : step_1.keySet()) {
                if (!(x.equals(stop))) {
                    dist_first = step_1.get(x);
                    for (int i = 0; i < dist_first.size(); i++) {
                        if (dist_first.get(i) < dist) {
                            if (graph.containsKey(x)) {
                                for (String y : graph.get(x).keySet()) {
                                    if (y.equals(stop)) {
                                        dist_second = graph.get(x).get(y);
                                        for (int j = 0; j < dist_second.size(); j++) {
                                            if(abs(dist - (dist_first.get(i) + dist_second.get(j)))==0){
                                                diff_dist.clear();
                                                diff_dist.add(dist_first.get(i));
                                                diff_dist.add(dist_second.get(j));
                                                path_2.clear();
                                                path_2.add(new CacheSystem.Pair<String, ArrayList<Integer>>(x, diff_dist));
                                                return path_2;
                                            }
                                            if(abs(dist - (dist_first.get(i) + dist_second.get(j))) <= 6) {
                                                if (isstart) {
                                                    isstart = false;
                                                    valid_via_station = x;
                                                    valid_dist_first = dist_first.get(i);
                                                    valid_dist_second = dist_second.get(j);
                                                    valid_diff_dist = abs(dist - (dist_first.get(i) + dist_second.get(j)));
                                                } else {
                                                    if (abs(dist - (dist_first.get(i) + dist_second.get(j))) < valid_diff_dist) {
                                                        valid_via_station = x;
                                                        valid_dist_first = dist_first.get(i);
                                                        valid_dist_second = dist_second.get(j);
                                                        valid_diff_dist = abs(dist - (dist_first.get(i) + dist_second.get(j)));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(isstart){
                return null;
            }else {
                diff_dist.clear();
                diff_dist.add(valid_dist_first);
                diff_dist.add(valid_dist_second);
                path_2.clear();
                path_2.add(new CacheSystem.Pair<String, ArrayList<Integer>>(valid_via_station, diff_dist));
            }

        }
        return path_2;

    }
}
