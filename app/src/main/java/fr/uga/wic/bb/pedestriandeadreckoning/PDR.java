package fr.uga.wic.bb.pedestriandeadreckoning;

import android.hardware.SensorManager;

/**
 * Created by rosparsb on 28/11/16.
 */

public class PDR {

    private float [] mCurrentLocation = new float[2];

    private SensorManager senSensorManager;
    private Podometre podometre;
    private Orientation orientation;

    /*
    *   Constructor new PDR set original location and pass sensor manager
    */
    public PDR(SensorManager sensorManager, float lat, float lon) {
        this.senSensorManager = sensorManager;
        this.mCurrentLocation[0] = lat;
        this.mCurrentLocation[1] = lon;

        podometre = new Podometre(senSensorManager);
        orientation = new Orientation(senSensorManager);

        podometre.setPasListener(pasListener);
        orientation.setOrientationChangeListener(orientationListener);
    }

    public float [] getmCurrentLocation() {
        return mCurrentLocation;
    }

    public void setmCurrentLocation(float [] mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
    }

    public float [] computeNextStep(float stepSize, float bearing){
        float R = 6371000;
        float lat2 = (float) Math.asin( Math.sin(mCurrentLocation[0])*Math.cos(stepSize/R) +
                Math.cos(mCurrentLocation[0])*Math.sin(stepSize/R)*Math.cos(bearing) );
        float lon2 = (float) (mCurrentLocation[1] +
                Math.atan2(Math.sin(bearing)*Math.sin(stepSize/R)*Math.cos(mCurrentLocation[0]),
                        Math.cos(stepSize/R)-Math.sin(mCurrentLocation[0])*Math.sin(lat2)));

        this.mCurrentLocation[0] = lat2;
        this.mCurrentLocation[1] = lon2;

        return this.mCurrentLocation;
    }

    public void start(){

    }

    public void stop(){

    }

    private Podometre.PasListener pasListener = new Podometre.PasListener() {
        @Override
        public void onPasDetected(int nbPas) {
            float [] newLocation = computeNextStep((float)1000000,orientation.getmOrientationVals()[0]);
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
