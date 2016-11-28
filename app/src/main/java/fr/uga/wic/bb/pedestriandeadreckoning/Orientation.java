package fr.uga.wic.bb.pedestriandeadreckoning;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;

/**
 * Created by rosparsb on 28/11/16.
 */

public class Orientation implements SensorEventListener {

    /*
        * 0 : Yaw, 1 : Pitch, 2 : Roll
        */
    private float [] mOrientationVals = new float[3];

    private float [] mRotationMatrixMagnetic = new float[16];
    private float [] mRotationMatrixMagneticToTrue = new float[16];
    private float [] mRotationMatrix = new float[16];

    private SensorManager senSensorManager;
    private Sensor senRotation;
    private long lastUpdate = 0;

    public Orientation(SensorManager sensorManager){

        senSensorManager = sensorManager;
        senRotation = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        senSensorManager.registerListener(this, senRotation , SensorManager.SENSOR_DELAY_GAME);
    }

    public float[] getmOrientationVals() {
        return mOrientationVals;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR){
            return;
        }

        long curTime = System.currentTimeMillis();

        if ((curTime - lastUpdate) > 100) {
            lastUpdate = curTime;

            // Transforme le rotation vector en matrice de rotation
            SensorManager.getRotationMatrixFromVector(mRotationMatrixMagnetic, event.values);

            // Creation de la matrice de passage du repère magnétique au repère classique
            Matrix.setRotateM(mRotationMatrixMagneticToTrue, 0, -1.83f, 0 , 0, 1);

            // Change la matrice d'orientation du repère magnétique au repère classique
            Matrix.multiplyMM(mRotationMatrix, 0, mRotationMatrixMagnetic, 0, mRotationMatrixMagneticToTrue, 0);

            // Transforme la matrice de rotation en une successsion de rotations autour de z,y,x
            SensorManager.getOrientation(mRotationMatrix, mOrientationVals);

            orientationListener.onOrientationChange(this.mOrientationVals);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onPause(){
        senSensorManager.unregisterListener(this);
    }

    public void onResume(){
        senSensorManager.registerListener(this, senRotation, SensorManager.SENSOR_DELAY_NORMAL);
    }


    private OrientationListener orientationListener;

    public void setOrientationChangeListener(OrientationListener listener){
        orientationListener = listener;
    }

    public interface OrientationListener{
        public void onOrientationChange(float [] orientation);
    }
}
