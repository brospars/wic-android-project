package fr.uga.wic.bb.pedestriandeadreckoning;

import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;

/**
 * Created by rosparsb on 28/11/16.
 */

public class PDR {

    private float [] mCurrentLocation = new float[2];

    private SensorManager senSensorManager;
    private SharedPreferences sharedPref;
    private Podometre podometre;
    private Orientation orientation;

    /*
    *   Constructor new PDR set original location and pass sensor manager
    */
    public PDR(SensorManager sensorManager, SharedPreferences sharedPref, float lat, float lon) {
        this.senSensorManager = sensorManager;
        this.sharedPref = sharedPref;
        this.mCurrentLocation[0] = lat;
        this.mCurrentLocation[1] = lon;

        podometre = new Podometre(senSensorManager,sharedPref);
        orientation = new Orientation(senSensorManager);

        podometre.setPasListener(pasListener);
        orientation.setOrientationChangeListener(orientationListener);
    }

    public float [] getmCurrentLocation() {
        return mCurrentLocation;
    }

    /*
    * set the current location of PDR user
    */
    public void setmCurrentLocation(float [] mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
        pdrListener.onPasDetected(mCurrentLocation);
    }

    /*
    * compute the next location given the starting point, the stepSize and the orientation
    */
    public float [] computeNextStep(float stepSize, float bearing){

        double orientation = bearing;
        double lat = Math.toRadians(mCurrentLocation[0]);
        double lon = Math.toRadians(mCurrentLocation[1]);



        double R = 6371000;
        double lat2 = Math.asin( Math.sin(lat)*Math.cos(stepSize/R) + Math.cos(lat)*Math.sin(stepSize/R)*Math.cos(orientation));
        double lon2 = lon + Math.atan2(Math.sin(orientation)*Math.sin(stepSize/R)*Math.cos(lat), Math.cos(stepSize/R)-Math.sin(lat)*Math.sin(lat2));

        this.mCurrentLocation[0] = (float) Math.toDegrees(lat2);
        this.mCurrentLocation[1] = (float) Math.toDegrees(lon2);

        return this.mCurrentLocation;
    }

    public void start(){

    }

    public void stop(){

    }
    private Podometre.PasListener pasListener = new Podometre.PasListener() {
        @Override
        public void onPasDetected(int nbPas) {
            // compute next step given the step size in shared property
            float stepSize = Float.parseFloat(sharedPref.getString("stepSize","0.7"));
            float [] newLocation = computeNextStep(stepSize,orientation.getmOrientationVals()[0]);
            pdrListener.onPasDetected(newLocation);
        }
    };


    private Orientation.OrientationListener orientationListener = new Orientation.OrientationListener() {

        @Override
        public void onOrientationChange(float[] orientation) {
        }
    };

    private PDR.PDRListener pdrListener;

    public void setPasListener(PDR.PDRListener listener){
        pdrListener = listener;
    }

    public interface PDRListener{
        public void onPasDetected(float [] newLocation);
    }
}
