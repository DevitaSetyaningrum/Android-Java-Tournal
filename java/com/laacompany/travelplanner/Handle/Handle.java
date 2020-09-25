package com.laacompany.travelplanner.Handle;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.laacompany.travelplanner.ModelClass.Destination;
import com.laacompany.travelplanner.ModelClass.PlanMaster;
import com.laacompany.travelplanner.ModelClass.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Handle {

    public static boolean isExit;
    public static User sCurrentUser;
    public static ArrayList<PlanMaster> sPlanMasters;
    public static ArrayList<Destination> sDestinations;
    public static ArrayList<Pair<Double,Double>> sCurrentRoutes;
    public static ArrayList<String> sCurrrenRouteDestinations;
    public static FirebaseAuth mAuth;
    private static Comparator<Destination> sDestinationComparator = (o1, o2) -> o1.getDestinationId().compareTo(o2.getDestinationId());

    public static void init(Context context){
        isExit = false;
        sCurrentUser = new User();
        sPlanMasters = new ArrayList<>();
        sDestinations = new ArrayList<>();
        sCurrentRoutes = new ArrayList<>();
        sCurrrenRouteDestinations = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        VolleyHandle.init(context);
    }

    public static String getHourFormat(int minutes){
        int hour = minutes/60;
        int minute = minutes%60;
        return ((hour>9)? hour : "0"+hour) + ":" + ((minute>9)? minute : "0"+minute);
    }

    public static String getDateToString(Date date){
        return  new SimpleDateFormat("EEEE, dd MMM YYYY").format(date);
    }

    public static Date convLongToDate(long time){
        Date date = new Date();
        date.setTime(time);
        return date;
    }

    public static long convDateToLong(Date date){
        return date.getTime();
    }

    public static ArrayList<PlanMaster> getCurrentDatePlanMasters(Date date){

        ArrayList<PlanMaster> currentPlanMasters = new ArrayList<>();
        Calendar cPM = Calendar.getInstance();
        cPM.setTime(date);
        for(PlanMaster planMaster : sPlanMasters){
            Calendar c = Calendar.getInstance();
            c.setTime(planMaster.getEventDate());
            if(cPM.get(Calendar.YEAR) != c.get(Calendar.YEAR)) continue;
            if(cPM.get(Calendar.MONTH) != c.get(Calendar.MONTH)) continue;
            if(cPM.get(Calendar.DATE) != c.get(Calendar.DATE)) continue;

            currentPlanMasters.add(planMaster);
        }
        return currentPlanMasters;
    }


    public static Destination getDestination(String destinationID){
        int index = Collections.binarySearch(sDestinations, new Destination(destinationID), sDestinationComparator);
        return sDestinations.get(index);
    }

    public static Destination getDestination(int index){
        return sDestinations.get(index);
    }

    public static double range(Pair<Double,Double> latLng1, Pair<Double,Double> latLng2){
        double x = latLng1.first - latLng2.first;
        double y = latLng1.second - latLng2.second;
        return Math.sqrt(x*x + y*y);
    }



}
