package com.abdullah.threepio.placesedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class PlacesAutoCompleteEditText extends AppCompatAutoCompleteTextView {

    private PlacesAutoCompleteAdapter adapter;
    private GoogleApiClient googleApiClient;
    private Place currentPlace;
    private PlaceSelectedListener listener;

    private LatLngBounds latLngBounds;
    private int itemLayout;
    private int itemTextId;

    public PlacesAutoCompleteEditText(Context context) {
        super(context);
        init(null, 0);
    }

    public PlacesAutoCompleteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PlacesAutoCompleteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public void setPlaceSelectedListener(PlaceSelectedListener listener) {
        this.listener = listener;
    }

    public Place getCurrentPlace() {
        return currentPlace;
    }

    public boolean isPlaceSelected() {
        return currentPlace != null;
    }

    public void setGoogleApiClient(GoogleApiClient client) {
        googleApiClient = client;
        adapter.setClient(client);
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void setLatLngBounds(double south, double west, double north, double east) {
        adapter.setBounds(new LatLngBounds(new LatLng(south, west), new LatLng(north, west)));
    }

    public void setLatLngBounds(LatLngBounds latLngBounds) {
        adapter.setBounds(latLngBounds);
    }

    public LatLngBounds getLatLngBounds() {
        return adapter.getBounds();
    }

    public void setAutoCompleteFilter(AutocompleteFilter filter) {
        adapter.setFilter(filter);
    }

    public AutocompleteFilter getAutoCompleteFilter() {
        return adapter.getAutoCompleteFilter();
    }

    // <editor-fold desc="Private Methods" >
    private void init(AttributeSet attrs, int defStyleAttr) {
        if(attrs == null) {
            return;
        }

        TypedArray ta = getContext().getTheme()
                                    .obtainStyledAttributes(attrs,
                                            R.styleable.PlacesAutoCompleteEditText,
                                            defStyleAttr, 0);

        if(ta.hasValue(R.styleable.PlacesAutoCompleteEditText_south_bound) &&
                ta.hasValue(R.styleable.PlacesAutoCompleteEditText_west_bound) &&
                ta.hasValue(R.styleable.PlacesAutoCompleteEditText_north_bound) &&
                ta.hasValue(R.styleable.PlacesAutoCompleteEditText_east_bound)) {
            double south = ta.getFloat(R.styleable.PlacesAutoCompleteEditText_south_bound, 0);
            double west = ta.getFloat(R.styleable.PlacesAutoCompleteEditText_west_bound, 0);
            double north = ta.getFloat(R.styleable.PlacesAutoCompleteEditText_north_bound, 0);
            double east = ta.getFloat(R.styleable.PlacesAutoCompleteEditText_east_bound, 0);

            latLngBounds = new LatLngBounds(new LatLng(south, west), new LatLng(north, east));
        }

        itemLayout = ta.getResourceId(R.styleable.PlacesAutoCompleteEditText_item_layout, android.R.layout.simple_list_item_1);
        itemTextId = ta.getResourceId(R.styleable.PlacesAutoCompleteEditText_item_text_id, 0);

        adapter = new PlacesAutoCompleteAdapter(getContext(), itemLayout, itemTextId);
        adapter.setBounds(latLngBounds);
        setAdapter(adapter);
        setOnItemClickListener(itemClickListener);
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(googleApiClient == null) {
                return;
            }

            currentPlace = null;
            PlacesAutoCompleteAdapter.Place place = adapter.getItem(position);
            String placeId = String.valueOf(place.placeId);

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(googleApiClient, placeId);
            placeResult.setResultCallback(resultCallback);
        }
    };

    private ResultCallback<PlaceBuffer> resultCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if(!places.getStatus().isSuccess()) {
                return;
            }
            currentPlace = places.get(0);
            if(listener != null) {
                listener.onPlaceSelected(currentPlace);
            }
        }
    };
    // <editor-fold>
}
