package com.laacompany.travelplanner.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.laacompany.travelplanner.DestinationDetailActivity;
import com.laacompany.travelplanner.ExploreActivity;
import com.laacompany.travelplanner.Handle.Handle;
import com.laacompany.travelplanner.ModelClass.Destination;
import com.laacompany.travelplanner.PlanActivity;
import com.laacompany.travelplanner.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.DestinationViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<Destination> mDestinations, mDestinationsFull;
    private boolean searching;
    private String countryFilter;
    private double ratingFilter;
    private int visitorFilter;
    private String textFilter = "";

    //Sort
    private String categorySort;
    private String sortBy;

    public ExploreAdapter(Context context, ArrayList<Destination> destinations, boolean searching){
        mContext = context;
        mDestinations  = destinations;
        mDestinationsFull = new ArrayList<>();
        mDestinationsFull.addAll(destinations);
        this.searching = searching;
        countryFilter = null;
        ratingFilter = 0;
        visitorFilter = 0;
    }

    @NonNull
    @Override
    public DestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DestinationViewHolder(LayoutInflater.from(mContext),parent);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationViewHolder holder, int position) {
        holder.bind(mDestinations.get(position), position);
    }

    public void setDestinations(ArrayList<Destination> destinations){
        mDestinationsFull = destinations;
    }

    private Comparator<Destination> comparator;
    public void getCustomSort(String category, String sortBy) {
        this.categorySort = category;
        this.sortBy = sortBy;
        comparator = new Comparator<Destination>() {
            @Override
            public int compare(Destination o1, Destination o2) {
                switch (categorySort) {
                    case "ByName" :
                        return(sortBy.equals("Ascending"))?o1.getName().compareTo(o2.getName()):o2.getName().compareTo(o1.getName());
                    case "ByRating" : {
                        int r1 =(int) (o1.getRating()*100);
                        int r2 =(int) (o2.getRating()*100);
                        return (sortBy.equals("Ascending"))? r1-r2 : r2-r1;
                    }
                    case "ByVisitor" :
                        return (sortBy.equals("Ascending"))?o1.getTotalVisitor() - o2.getTotalVisitor():o2.getTotalVisitor() - o1.getTotalVisitor();
                }
                return 0;
            }
        };
        Collections.sort(mDestinations, comparator );
        Collections.sort(mDestinationsFull, comparator );

         notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDestinations.size();
    }

    public void getCustomFilter(String country, double rating, int visitor){

        if(country.equals("None")){
            this.countryFilter = null;
        } else {
            this.countryFilter = country;
        }
        this.ratingFilter = rating;
        this.visitorFilter = visitor;

        ArrayList<Destination> filteredList = new ArrayList<>();

        for (Destination destination : mDestinationsFull) {
            if (destination.getName().toLowerCase().contains(textFilter) &&
                    ((countryFilter == null) || destination.getCountry().toLowerCase().equals(countryFilter.toLowerCase())) &&
                    destination.getRating() >= ratingFilter && destination.getTotalVisitor() >= visitorFilter) {
                filteredList.add(destination);
            }
        }
        mDestinations.clear();
        mDestinations.addAll(filteredList);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Destination> filteredList = new ArrayList<>();
            textFilter="";

            if (constraint == null || constraint.length() == 0) {
                for (Destination destination : mDestinationsFull) {
                    if (((countryFilter == null) || destination.getCountry().toLowerCase().equals(countryFilter.toLowerCase())) &&
                            destination.getRating() >= ratingFilter && destination.getTotalVisitor() >= visitorFilter){
                        filteredList.add(destination);
                    }
                }
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                textFilter = filterPattern;
                for (Destination destination : mDestinationsFull) {
                    if (destination.getName().toLowerCase().contains(filterPattern) &&
                            ((countryFilter == null) || destination.getCountry().toLowerCase().equals(countryFilter.toLowerCase())) &&
                            destination.getRating() >= ratingFilter && destination.getTotalVisitor() >= visitorFilter) {
                        filteredList.add(destination);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mDestinations.clear();
            mDestinations.addAll((List) results.values );
            notifyDataSetChanged();
        }
    };


    public class DestinationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTVName, mTVAddress, mTVCountry, mTVRating, mTVVisitor, mTVOpenTime;
        private ImageView mIVPreview, mIVFlag;
        private String mDestinationID;

        public DestinationViewHolder(LayoutInflater inflater, @NonNull ViewGroup parent) {
            super(inflater.inflate(R.layout.item_explore, parent, false));

            mTVName = itemView.findViewById(R.id.id_item_explore_tv_name);
            mTVAddress = itemView.findViewById(R.id.id_item_explore_tv_address);
            mTVCountry =  itemView.findViewById(R.id.id_item_explore_tv_country);
            mTVRating = itemView.findViewById(R.id.id_item_explore_tv_rating);
            mTVVisitor = itemView.findViewById(R.id.id_item_explore_tv_visitor);
            mTVOpenTime = itemView.findViewById(R.id.id_item_explore_tv_open_time);
            mIVPreview = itemView.findViewById(R.id.id_item_explore_iv_preview);
            mIVFlag = itemView.findViewById(R.id.id_item_explore_iv_flag);

            itemView.setOnClickListener(this);
        }

        public void bind(Destination destination, int position){
            mDestinationID = destination.getDestinationId();
            String rating = destination.getRating()+" / 5";
            String openTime = Handle.getHourFormat(destination.getOpenTime()) + " - " + Handle.getHourFormat(destination.getCloseTime());
            String bestTime = Handle.getHourFormat(destination.getBestTimeStart()) + " - " + Handle.getHourFormat(destination.getBestTimeEnd());
            String time = openTime + " , " + bestTime;

            mTVName.setText(destination.getName());
            mTVAddress.setText(destination.getAddress());
            mTVCountry.setText(destination.getCountry());
            mTVRating.setText(rating);
            mTVVisitor.setText(String.valueOf(destination.getTotalVisitor()));
            mTVOpenTime.setText(time);

            Glide.with(mContext)
                    .load(destination.getPreviewUrl())
                    .into(mIVPreview);

            Glide.with(mContext)
                    .load(destination.getFlagUrl())
                    .into(mIVFlag);

        }

        @Override
        public void onClick(View v) {
            if (searching){
                Intent returnIntent = new Intent();
                returnIntent.putExtra(PlanActivity.EXTRA_RESULT_DESTINATION_ID,mDestinationID);
                ((ExploreActivity)mContext).setResult(Activity.RESULT_OK,returnIntent);
                ((ExploreActivity)mContext).finish();
            } else {
                mContext.startActivity(DestinationDetailActivity.newIntent(mContext, mDestinationID));
            }
        }
    }
}
