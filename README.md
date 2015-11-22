# Threepio Places View

A simple places auto complete text view with suggestions from Google places api.

##Usage##

###Permissions required###
You need to add `INTERNET` permission in `AndroidManifest.xml` for the view to work. Put following code under your `manifest` tag:

```XML
<uses-permission android:name="android.permission.INTERNET" />
```

###Google Places API Key###

In order to use the view you need to have a valid Google Places API key. Place that key as follows under `application` tag in your `AndroidManifest.xml`:

```XML
<meta-data
      android:name="com.google.android.geo.API_KEY"
      android:value="YOUR-API-KEY"/>
```

Please check out Google Places API documentation on how to get the API Key.

###Import the library###
Add following into your project level build.gradle file:

```Gradle
compile 'com.abdullah.threepio:places-view:1.0.0'
```

###Placing the view in your layout XML###

```XML
<com.abdullah.threepio.placesview.PlacesAutoCompleteEditText
        android:id="@+id/places_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:south_bound="8"
        app:west_bound="65"
        app:north_bound="37"
        app:east_bound="100"/>
```

###Initializing in code###

This is the last step before you are ready to use the places auto complete view. You need to pass a valid instance of `GoogleApiClient` to the view as follows:

```Java
placesView.setGoogleApiClient(googleApiClient);
```

If you want to listen to when user selects one of suggestion you may do so by setting a listener. Implement `PlaceSelectedListener` and pass it to the view:

```Java
class MyPlaceSelectedListener implements PlaceSelectedListener {
    
    @Override
    public void onPlaceSelected(Place place) {
        //Do some awesome thing with place.
        //This place is 'com.google.android.gms.location.places.Place'
    }
}

placesView.setPlaceSelectedListener(myListener);
```

Optionally you can specify lat lng bounds for places api, so that Google will try to give you suggestions with in those bounds. The bounds are specified using these attributes `south_bound`, `west_bound`, `north_bound` and `east_bound`. You must specify all four of them, excluding any one of them will not set the bonds for Places API.

###Customizing###

`PlacesAutoCompleteEditText` is a subclass of `AppCompatAutoCompleteTextView` so all the usual attributes can be used to customize the appearance and behaviour that applies to `AppCompatAutoCompleteTextView`.

By default `PlacesAutoCompleteEditText` uses `android.R.layout.simple_list_item_1` to show individual auto complete options. If you want to use custom layout for individual items you could do so by specifying `item_layout` attribute. Create your own custom layout and specify it against `item_layout` as follows:

```XML
<com.abdullah.threepio.placesview.PlacesAutoCompleteEditText
      android:id="@+id/places_view"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      app:item_layout="@layout/autocomplete_item"
      app:item_text_id="@+id/place_text_view"/>
```

If the custom layout is a TextView or its decedent then no need to specify `item_text_id`. If it is complex layout with containers and place description is to be shown on a `TextView` inside a container then you must specify its id. As of now it is not possible to refer to id in some other layout xml file so you need to specify it as a new id with same name as in item layout file i.e specify as `@+id/place_text_view` instead of `@id/place_text_view`. Due to the way Android handles ids this works!.
