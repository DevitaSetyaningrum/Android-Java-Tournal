package com.laacompany.travelplanner.Algorithm;

import android.util.Log;

import java.util.ArrayList;

public class TSP {

    private static int[][] to;
    private static double[][] memo,dist;
    private static int n;
    private static ArrayList<Integer> index;

    public static ArrayList<Integer> simulate(double[][] distI, int nI){
        dist = distI;
        n = nI;
        to = new int[n][1<<n];
        memo = new double[n][1<<n];
        for(int i = 0; i < n; i++){
            for(int j = 0; j < (1<<n); j++){
                memo[i][j] = -1;
            }
        }
        dp(0,1);
        int xi = 0;
        int mask = 1;
        index = new ArrayList<>();
        index.add(xi);
        while(mask != ((1<<n)-1)){
            xi = to[xi][mask];
            index.add(xi);
            mask |= (1<<xi);
        }
        return index;
    }

    private static double dp(int cur, int bitmask){
        if (bitmask == ((1<<n)-1)) return dist[cur][0];
        if (memo[cur][bitmask] != -1) return memo[cur][bitmask];

        double mins = 1e9;
        for(int i=0;i<n;i++){
            if ((bitmask & (1<<i)) > 0) continue;
            double tot = dp(i,bitmask | (1<<i)) + dist[cur][i];
            if (mins > tot){
                mins = tot;
                to[cur][bitmask]=i;
            }
        }
        return memo[cur][bitmask] = mins;
    }

}
