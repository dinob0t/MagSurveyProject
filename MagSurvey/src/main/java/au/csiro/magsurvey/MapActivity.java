package au.csiro.magsurvey;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ToggleButton;
import android.view.View;
import android.widget.Toast;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.File;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.io.FileNotFoundException;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;


import android.util.Log;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.view.LayoutInflater;
import android.os.Environment;
import java.io.PrintWriter;



import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;



/**
 * This shows how to create a simple activity with a map and a marker on the map.
 * <p>
 * Notice how we deal with the possibility that the Google Play services APK is not
 * installed/enabled/updated on a user's device.
 */
public class MapActivity extends FragmentActivity
        implements ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        OnMyLocationButtonClickListener,
        SensorEventListener {
    /**
     * Note that this may be null if the Google Play services APK is not available.
     */

    private static final int ZOOM_LOCATION_START = 0;
    private static final String TAG = "MEDIA";
    private static final int ZOOM_LEVEL = 18;
    private static final int POLY_WIDTH = 5;
    private static final int SAMPLE_INTERVAL = 100000;


    private Boolean runningSurvey;
    private GoogleMap mMap;
    private LocationClient mLocationClient;
    private Location currentLocation;
    private LatLng currentLatLng;
    private int firstLoadZoom;
    private Polyline surveyLine;
    private PolylineOptions surveyLineOptions;
    private List<SurveyPoint> surveyPoints;
    private List<LatLng> surveyLatLngs;
    private Integer pointNum;
    private Context context;
    private SensorManager sm;
    private Sensor mag;
    private float[] mMag;
    private float[] mMagTotal;
    private int magPoints;




    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        firstLoadZoom = ZOOM_LOCATION_START;
        runningSurvey = false;
        surveyPoints = new ArrayList<SurveyPoint>();
        surveyLatLngs = new ArrayList<LatLng>();
        pointNum = 0;
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mag = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mMagTotal = new float[3];
        setContentView(R.layout.map);
        setUpMapIfNeeded();
        Toast.makeText(this, "Acquiring location - please wait", Toast.LENGTH_LONG).show();

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
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
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
        if(firstLoadZoom == 0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,ZOOM_LEVEL));
            Toast.makeText(this, "Location acquired", Toast.LENGTH_SHORT).show();
            firstLoadZoom++;
        }

        //if(firstLoadZoom == ZOOM_LOCATION_DELAY) {

        //    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,ZOOM_LEVEL));
        //    Toast.makeText(this, "Location acquired", Toast.LENGTH_SHORT).show();
        //}
        //if(firstLoadZoom == ZOOM_LOCATION_START) {
        //    Toast.makeText(this, "Acquiring location - please wait", Toast.LENGTH_SHORT).show();
        //}

        //if(firstLoadZoom <= ZOOM_LOCATION_DELAY) {
        //    firstLoadZoom++;
        //}

        if(runningSurvey == true) {

            SurveyPoint currentPoint = new SurveyPoint(pointNum, currentLocation.getLatitude(), currentLocation.getLongitude(),  mMagTotal[0]/magPoints, mMagTotal[1]/magPoints, mMagTotal[2]/magPoints);
            zeroMagTotal();
            if(pointNum==0) {
                surveyLineOptions = new PolylineOptions().width(POLY_WIDTH).color(Color.RED);
                surveyLine = mMap.addPolyline(surveyLineOptions);
                surveyLatLngs.add(currentLatLng);
            }

            surveyLatLngs.add(currentLatLng);
            surveyPoints.add(currentPoint);
            pointNum++;

            surveyLine.setPoints(surveyLatLngs);
        }

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

    public void onToggleSurvey(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            runningSurvey = true;
            sm.registerListener(this, mag, SAMPLE_INTERVAL);
            zeroMagTotal();
        } else {
            runningSurvey = false;
            sm.unregisterListener(this);
        }
    }

    public static boolean isSDCARDAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public void finishSurvey(View view) {
        runningSurvey = false;

        if(isSDCARDAvailable()==false) {
            Toast.makeText(this, "External storage unavailable", Toast.LENGTH_SHORT).show();
            return;
        }

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.textprompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // get user input and set it to result
                                // edit text
                                if (userInput.getText() != null) {
                                    File root = android.os.Environment.getExternalStorageDirectory();
                                    File dir = new File(root.getAbsolutePath() + "/magSurvey");
                                    if (!dir.exists()) {
                                        dir.mkdirs();
                                    }
                                    File file = new File(dir, userInput.getText().toString());
                                    FileOutputStream f = null;

                                    try {
                                        f = new FileOutputStream(file);
                                        PrintWriter pw = new PrintWriter(f);
                                        for (Integer i=0; i < pointNum; i++) {
                                            SurveyPoint currentPoint = surveyPoints.get(i);
                                            pw.println(i.toString() + " " + currentPoint.getpointLat().toString()  + " " + currentPoint.getpointLon().toString() + " "
                                                    + currentPoint.getTotalMag().toString());
                                            pw.write("\n");
                                        }
                                        pw.flush();
                                        pw.close();
                                        f.flush();
                                        f.close();
                                        new SingleMediaScanner(context, file);

                                    } catch (FileNotFoundException e) {
                                    } catch (IOException e) {
                                    } catch (Exception e) {
                                    } finally {
                                        if (f != null) {
                                            f = null;
                                        }
                                    }
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) mMag = event.values.clone();
        if (mMag != null) {
            mMagTotal[0] = mMag[0] + mMagTotal[0];
            mMagTotal[1] = mMag[1] + mMagTotal[1];
            mMagTotal[2] = mMag[2] + mMagTotal[2];
            magPoints++;
        }

    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

    public void zeroMagTotal() {
        mMagTotal[0] = 0;
        mMagTotal[1] = 0;
        mMagTotal[2] = 0;
        magPoints = 0;
    }

}