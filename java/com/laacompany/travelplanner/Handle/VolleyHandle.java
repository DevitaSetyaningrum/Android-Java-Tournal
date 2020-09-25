package com.laacompany.travelplanner.Handle;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.laacompany.travelplanner.ModelClass.Destination;
import com.laacompany.travelplanner.ModelClass.Plan;
import com.laacompany.travelplanner.ModelClass.PlanMaster;
import com.laacompany.travelplanner.ModelClass.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;

public class VolleyHandle {

    public static RequestQueue mRequestQueue;
    public static ArrayList<String> sPlanMasterIds;
    public static VolleyResponseListener listener;
    public static PlanMaster curQueryPlanMaster;

    public static void init(Context context){
        mRequestQueue = Volley.newRequestQueue(context);
        sPlanMasterIds = new ArrayList<>();
    }

    public interface VolleyResponseListener {
        void onResponse(String functionResp);
        void onError(String functionResp);
    }

    public static void addNewUser(String user_id, String gender, long date_of_birth, String phone){
        String url = "https://us-central1-fir-crud-restapi-a8904.cloudfunctions.net/app/api/regis";

        JSONObject obj = new JSONObject();
        try {
            obj.put("user_id", user_id);
            obj.put("date_of_birth", date_of_birth);
            obj.put("gender", gender);
            obj.put("phone", phone);
            JSONArray pmId = new JSONArray();
            obj.put("planmasters", pmId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("12345", "addNewUser success");
                if (listener!=null) listener.onResponse("addNewUser");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("12345", "addNewUser" + error.toString());
                if (listener!=null) listener.onError("addNewUser");
            }
        });

        mRequestQueue.add(jsonObjectRequest);
    }

    public static void getCurrentUser(String user_id, boolean withPlan){
        String url="https://us-central1-fir-crud-restapi-a8904.cloudfunctions.net/app/api/read/user/" + user_id;
        if (withPlan) getAllPlanMasters(user_id);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONArray jsonArray = new JSONArray(response);

                    JSONObject obj = (JSONObject) jsonArray.get(0);
                    long date_of_birth = obj.getLong("dob");
                    String gender = obj.getString("gender");
                    String phone = obj.getString("phone");
                    JSONArray array = obj.getJSONArray("planmasters");
                    for(int i = 0; i < array.length(); i++){
                        sPlanMasterIds.add((String) array.get(i));
                    }

                    Handle.sCurrentUser = new User(user_id, Handle.mAuth.getCurrentUser().getEmail(), phone, gender, Handle.convLongToDate(date_of_birth));
                    Log.d("12345", "getCurrentUser success");
                    if (listener != null) listener.onResponse("getCurrentUser");
                } catch (JSONException e) {
                    if (listener != null) listener.onError("getCurrentUserNotFound");
                    Log.d("12345", "getCurrentUser " + e.getMessage());
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (listener != null) listener.onError("getCurrentUser");
            }
        });

        mRequestQueue.add(stringRequest);
    }

    public static void getAllDestinations(){
        String url = "https://us-central1-fir-crud-restapi-a8904.cloudfunctions.net/app/api/read/destinations";
        Handle.sDestinations.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject obj = (JSONObject) jsonArray.get(i);

                        String destinationId = obj.getString("id");
                        String name = obj.getString("name");
                        String address = obj.getString("address");
                        String country = obj.getString("country");
                        String previewUrl = obj.getString("preview_url");
                        String flagUrl = obj.getString("flag_url");
                        double rating = obj.getDouble("rating");
                        double latitude = obj.getDouble("latitude");
                        double longitude = obj.getDouble("longitude");
                        int totalVisitor = obj.getInt("total_visitor");
                        int visitorEachDay = obj.getInt("visitor_each_day");
                        int bestTimeStart = obj.getInt("best_time_start");
                        int bestTimeEnd = obj.getInt("best_time_end");
                        int openTime = obj.getInt("open_time");
                        int closeTime = obj.getInt("close_time");

                        Destination destination = new Destination(destinationId,name,address,country,previewUrl,flagUrl,rating,latitude,longitude,totalVisitor,visitorEachDay,bestTimeStart,bestTimeEnd,openTime,closeTime);
                        Handle.sDestinations.add(destination);
                    }
                    Log.d("12345", "getAllDestinations success");
                    if (listener!=null) listener.onResponse("getAllDestinations");
                } catch (JSONException e) {
                    Log.d("12345", "getAllDestinations "+  e.toString());
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("12345", "getAllDestinations" + error.toString());
                if (listener!=null) listener.onError("getAllDestinations");
            }
        });

        mRequestQueue.add(stringRequest);
    }

    public static void getAllPlanMasters(String user_id){
        String url = "https://us-central1-fir-crud-restapi-a8904.cloudfunctions.net/app/api/read/planmasters/" + user_id;
        Handle.sPlanMasters.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray pmArray = new JSONArray(response);

                    for(int i = 0; i < pmArray.length(); i++){
                        JSONObject obj = pmArray.getJSONObject(i);
                        String id = obj.getString("id");
                        String event_title = obj.getString("event_title");
                        long event_date = obj.getLong("event_date");
                        int time_start = obj.getInt("time_start");
                        String origin_id = obj.getString("origin_id");
                        String origin_name = obj.getString("origin_name");
                        String origin_address = obj.getString("origin_address");
                        String origin_preview_url = obj.getString("origin_preview_url");
                        Plan origin = new Plan(origin_id, origin_name, origin_address, origin_preview_url,0,0);

                        PlanMaster planMaster = new PlanMaster(id,event_title,Handle.convLongToDate(event_date),time_start,origin);

                        JSONArray jsonArray = obj.getJSONArray("plans");
                        for(int j = 0; j < jsonArray.length(); j++){
                            JSONObject objplan = (JSONObject) jsonArray.get(j);
                            String name = objplan.getString("name");
                            String destination_id = objplan.getString("destination_id");
                            String address = objplan.getString("address");
                            String preview_url = objplan.getString("preview_url");
                            int duration = objplan.getInt("duration");
                            int arrived_time = objplan.getInt("arrived_time");
                            planMaster.getPlans().add(new Plan(destination_id,name,address,preview_url,arrived_time,duration));
                        }

                        Handle.sPlanMasters.add(planMaster);
                    }

                    Timber.tag("12345").d("getPlanMasters Success %s", Handle.sPlanMasters.size());
                    if (listener != null) listener.onResponse("getAllPlanMasters");
                } catch (JSONException e) {
                    Timber.tag("12345").d("getPlanMasters %s", e.getMessage());
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("12345", "getPlanMaster " + error.toString());
                if (listener != null) listener.onError("getAllPlanMasters");
            }
        });

        mRequestQueue.add(stringRequest);
    }

    public static void getPlanMaster(String id){
        Log.d("12345", "planmaster " + id);
        String url = "https://us-central1-fir-crud-restapi-a8904.cloudfunctions.net/app/api/read/planmaster/" + id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String id = obj.getString("id");
                    String event_title = obj.getString("event_title");
                    long event_date = obj.getLong("event_date");
                    int time_start = obj.getInt("time_start");
                    String origin_id = obj.getString("origin_id");
                    String origin_name = obj.getString("origin_name");
                    String origin_address = obj.getString("origin_address");
                    String origin_preview_url = obj.getString("origin_preview_url");
                    Plan origin = new Plan(origin_id, origin_name, origin_address, origin_preview_url,0,0);

                    curQueryPlanMaster = new PlanMaster(id,event_title,Handle.convLongToDate(event_date),time_start,origin);

                    JSONArray jsonArray = obj.getJSONArray("plans");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject objplan = (JSONObject) jsonArray.get(i);
                        String name = objplan.getString("name");
                        String destination_id = objplan.getString("destination_id");
                        String address = objplan.getString("address");
                        String preview_url = objplan.getString("preview_url");
                        int duration = objplan.getInt("duration");
                        int arrived_time = objplan.getInt("arrived_time");
                        curQueryPlanMaster.getPlans().add(new Plan(destination_id,name,address,preview_url,arrived_time,duration));
                    }

                    if (listener != null) listener.onResponse("getPlanMaster");
                } catch (JSONException e) {
                    Log.d("12345", "getPlanMaster " + e.getMessage());
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("12345", "getPlanMaster " + error.toString());
                if (listener != null) listener.onError("getPlanMaster");
            }
        });

        mRequestQueue.add(stringRequest);
    }

    public static void updateUserPlanMaster(){
        String url = "https://us-central1-fir-crud-restapi-a8904.cloudfunctions.net/app/api/update/user/" + Handle.sCurrentUser.getUserId();
        JSONObject obj = new JSONObject();

        try {
            JSONArray planMasterIds = new JSONArray();
            for(String string : sPlanMasterIds){ planMasterIds.put(string); }
            obj.put("planmasters", planMasterIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("12345", "update user's plan master success");
                if (listener != null)  listener.onResponse("updateUserPlanMaster");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("12345", "update user" + error.toString());
                if (listener != null)  listener.onError("updateUserPlanMaster");
            }
        });

        mRequestQueue.add(jsonObjectRequest);
    }

    public static void updateUser(){
        String url = "https://us-central1-fir-crud-restapi-a8904.cloudfunctions.net/app/api/update/user/" + Handle.sCurrentUser.getUserId();
        JSONObject obj = new JSONObject();

        try {
            JSONArray planMasterIds = new JSONArray();
            for(PlanMaster planMaster : Handle.sPlanMasters){ planMasterIds.put(planMaster.getPlanMasterId()); }
            obj.put("planmasters", planMasterIds);
            obj.put("date_of_birth", Handle.convDateToLong(Handle.sCurrentUser.getDateOfBirth()));
            obj.put("gender", Handle.sCurrentUser.getGender());
            obj.put("phone", Handle.sCurrentUser.getPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("12345", "update user success");
                if (listener!=null) listener.onResponse("updateUser");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("12345", "update user" + error.toString());
                if (listener!=null) listener.onError("updateUser");
            }
        });

        mRequestQueue.add(jsonObjectRequest);
    }

    public static void addPlanMaster(int index){
        String url = "https://us-central1-fir-crud-restapi-a8904.cloudfunctions.net/app/api/add/planmaster";

        JSONObject obj = new JSONObject();
        try {
            obj.put("event_title", Handle.sPlanMasters.get(index).getEventTitle());
            obj.put("event_date", Handle.convDateToLong(Handle.sPlanMasters.get(index).getEventDate()));
            obj.put("time_start", Handle.sPlanMasters.get(index).getTimeStart());
//            obj.put("latitude", Handle.sPlanMasters.get(index).getLatitude());
//            obj.put("longitude", Handle.sPlanMasters.get(index).getLongitude());
            Plan origin = Handle.sPlanMasters.get(index).getOrigin();
            obj.put("origin_id", origin.getDestinationId());
            obj.put("origin_name", origin.getName());
            obj.put("origin_address", origin.getAddress());
            obj.put("origin_preview_url", origin.getPreviewUrl());

            JSONArray jsonArray = new JSONArray();
            for(Plan plan : Handle.sPlanMasters.get(index).getPlans()){
                JSONObject objplan = new JSONObject();
                objplan.put("destination_id", plan.getDestinationId());
                objplan.put("name", plan.getName());
                objplan.put("preview_url", plan.getPreviewUrl());
                objplan.put("address", plan.getAddress());
                objplan.put("arrived_time", plan.getArrivedTime());
                objplan.put("duration", plan.getDuration());
                jsonArray.put(objplan);
            }
            obj.put("plans",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("12345", response.toString());
                try {
                    Handle.sPlanMasters.get(index).setPlanMasterId(response.getString("id"));
                    sPlanMasterIds.add(response.getString("id"));
                    updateUser();
                    if (listener!=null) listener.onResponse("addPlanMaster");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (listener!=null) listener.onError("addPlanMaster");
                Log.d("12345", "addplanmaster" + error.toString());
            }
        });

        mRequestQueue.add(jsonObjectRequest);
    }

    public static void updatePlanMaster(int index){
        String url = "https://us-central1-fir-crud-restapi-a8904.cloudfunctions.net/app/api/update/planmaster/"+ Handle.sPlanMasters.get(index).getPlanMasterId();

        JSONObject obj = new JSONObject();

        try {
            obj.put("event_title", Handle.sPlanMasters.get(index).getEventTitle());
            obj.put("event_date", Handle.convDateToLong(Handle.sPlanMasters.get(index).getEventDate()));
            obj.put("time_start", Handle.sPlanMasters.get(index).getTimeStart());

            Plan origin = Handle.sPlanMasters.get(index).getOrigin();
            obj.put("origin_id", origin.getDestinationId());
            obj.put("origin_name", origin.getName());
            obj.put("origin_address", origin.getAddress());
            obj.put("origin_preview_url", origin.getPreviewUrl());

            JSONArray jsonArray = new JSONArray();
            for(Plan plan : Handle.sPlanMasters.get(index).getPlans()){
                JSONObject objplan = new JSONObject();
                objplan.put("destination_id", plan.getDestinationId());
                objplan.put("name", plan.getName());
                objplan.put("preview_url", plan.getPreviewUrl());
                objplan.put("address", plan.getAddress());
                objplan.put("arrived_time", plan.getArrivedTime());
                objplan.put("duration", plan.getDuration());
                jsonArray.put(objplan);
            }
            obj.put("plans",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Timber.tag("12345").d("updatePlanMaster success");
                if (listener!=null) listener.onResponse("updatePlanMaster");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Timber.tag("12345").d("updatePlanMaster %s", error.toString());
                if (listener!=null) listener.onError("updatePlanMaster");
            }
        });

        mRequestQueue.add(jsonObjectRequest);
    }
}
