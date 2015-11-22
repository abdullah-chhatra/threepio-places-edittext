package com.abdullah.threepio.placesview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;


class PlacesAutoCompleteAdapter extends ArrayAdapter<PlacesAutoCompleteAdapter.Place>
        implements Filterable {

    private GoogleApiClient client;
    private LatLngBounds bounds;
    private AutocompleteFilter filter;
    private List<Place> placesResult;

    public PlacesAutoCompleteAdapter(Context context, int layoutRes, int textViewResourceId) {
        super(context, layoutRes, textViewResourceId);
        this.placesResult = new ArrayList<Place>();
    }

    public void setBounds(LatLngBounds bounds) {
        this.bounds = bounds;
    }

    public LatLngBounds getBounds() {
        return bounds;
    }

    public void setClient(GoogleApiClient client) {
        this.client = client;
    }

    public void setFilter(AutocompleteFilter filter) {
        this.filter = filter;
    }

    public AutocompleteFilter getAutoCompleteFilter() {
        return filter;
    }

    @Override
    public int getCount() {
        return placesResult.size();
    }

    @Override
    public Place getItem(int position) {
        return placesResult.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if(constraint != null) {
                    placesResult = getPlacesResult(constraint);
                    results.values = placesResult;
                    results.count = placesResult.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    private List<Place> getPlacesResult(CharSequence constraint) {
        List<Place> results = new ArrayList<Place>();
        if(client != null && client.isConnected()) {

            PendingResult<AutocompletePredictionBuffer> pres =
                    Places.GeoDataApi.getAutocompletePredictions(
                            client,
                            constraint.toString(),
                            bounds,
                            filter);
            AutocompletePredictionBuffer autocompletePredictions =
                    pres.await(60, TimeUnit.SECONDS);

            final Status status = autocompletePredictions.getStatus();
            if (status.isSuccess()) {
                Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
                while (iterator.hasNext()) {
                    results.add(new Place(iterator.next()));
                }
            }
            autocompletePredictions.release();

        }
        return results;
    }


    static class Place {

        public CharSequence placeId;
        public CharSequence description;

        Place(AutocompletePrediction prediction) {
            this.placeId = prediction.getPlaceId();
            this.description = prediction.getDescription();
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }
}
