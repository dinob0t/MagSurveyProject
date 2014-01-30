package au.csiro.magsurvey;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

import android.app.Activity;
import android.graphics.Color;

import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * This shows how to create a simple activity with a map and a marker on the map.
 * <p>
 * Notice how we deal with the possibility that the Google Play services APK is not
 * installed/enabled/updated on a user's device.
 */
public class DisplayMapActivity extends FragmentActivity
        implements ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        OnMyLocationButtonClickListener {
    /**
     * Note that this may be null if the Google Play services APK is not available.
     */


    private static final int NUM_COLOURS = 10;
    private static final int MAX_COLOURS = 240;
    private static final int MIN_COLOURS = 0;
    private static final int LOC_SAMPLE_INTERVAL = 5000;
    private static final int STATIC_INTEGER_VALUE = 10;



    private String fileName;
    private GoogleMap mMap;
    private LocationClient mLocationClient;
    private Location currentLocation;
    private LatLng currentLatLng;





    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(LOC_SAMPLE_INTERVAL)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.displaymap);
        setUpMapIfNeeded();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        mLocationClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.displaymap))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                    getApplicationContext(),
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);

    }

    /**
     * Button to get current Location. This demonstrates how to get the current Location as required
     * without needing to register a LocationListener.
     */
    //public void showMyLocation(View view) {
     //   if (mLocationClient != null && mLocationClient.isConnected()) {
     //       String msg = "Location = " + mLocationClient.getLastLocation();
     //       Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
     //   }
   // }

    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
    }

    /**
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(
                REQUEST,
                this);  // LocationListener
    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onDisconnected() {
        // Do nothing
    }

    /**
     * Implementation of {@link OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }

    @Override
    public boolean onMyLocationButtonClick() {

        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    public static boolean isSDCARDAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void loadSurvey(View view) {

        //fileName = null;
       // List<SurveyPoint> surveyPoints = new ArrayList<SurveyPoint>();
        //Integer pointNum = 0;
        if(isSDCARDAvailable()==false) {
            Toast.makeText(this, "External storage unavailable", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent i = new Intent(this,FileExplorer.class);

        startActivityForResult(i, STATIC_INTEGER_VALUE);

       // LoadSurvey loadSurvey = new LoadSurvey(fileName);

        //if(loadSurvey.loadPoints()== true) {

        //    surveyPoints = loadSurvey.getSurveyPoints();

        //    pointNum = loadSurvey.getPointNum();
        //}
        //Toast.makeText(this, "Number of points loaded : " + pointNum.toString(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (STATIC_INTEGER_VALUE) : {
                if (resultCode == RESULT_OK) {
                    mMap.clear();
                    fileName = data.getStringExtra("fileName");
                    Toast.makeText(this, "Survey loaded from: " + fileName , Toast.LENGTH_SHORT).show();
                    LoadSurvey loadSurvey = new LoadSurvey(fileName);
                    loadSurvey.loadPoints();
                    List<SurveyPoint> surveyPoints = loadSurvey.getSurveyPoints();
                    Integer totalPoints = surveyPoints.size();
                    Toast.makeText(this, totalPoints.toString() + " points loaded", Toast.LENGTH_SHORT).show();

                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(plotPoints(surveyPoints, totalPoints).build(), 50));

                }
                break;
            }
        }
    }

    public LatLngBounds.Builder plotPoints(List<SurveyPoint> surveyPoints, Integer totalPoints){
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        SurveyPoint currentPoint = surveyPoints.get(0);
        Double minMag = minMag = currentPoint.getTotalMag();
        Double maxMag = maxMag = currentPoint.getTotalMag();

        for (Integer i=1; i < totalPoints; i++) {
            currentPoint = surveyPoints.get(i);
             if(currentPoint.getTotalMag()<minMag) {
                minMag = currentPoint.getTotalMag();
            }
            if(currentPoint.getTotalMag()>maxMag) {
                maxMag = currentPoint.getTotalMag();
            }
        }

        for (Integer i=0; i < totalPoints; i++) {
            currentPoint = surveyPoints.get(i);
            LatLng currentLatLng = new LatLng(currentPoint.getpointLat(), currentPoint.getpointLon());
            bounds.include(currentLatLng);
            DecimalFormat df = new DecimalFormat("#.#");
            Integer currentPointNum = currentPoint.getpointNumber() + 1;
            Double colorchooser = MAX_COLOURS - ((MAX_COLOURS - MIN_COLOURS) * (currentPoint.getTotalMag()-minMag) / (maxMag-minMag));

            mMap.addMarker(new MarkerOptions()
                    .position(currentLatLng)
                    .title("Point " + currentPointNum.toString())
                    .snippet(df.format(currentPoint.getTotalMag()) + "\u00B5"  +"T")
                    .icon(BitmapDescriptorFactory.defaultMarker(colorchooser.intValue())));
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow))
                    //.infoWindowAnchor(0.5f, 0.5f));

        }



        return bounds;
    }



}