package fr.uga.wic.bb.pedestriandeadreckoning;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by rosparsb on 14/11/16.
 */

public class Podometre implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private long lastPas = 0;
    private int nbPas;
    private double a;


    public Podometre(SensorManager sensorManager){

        senSensorManager = sensorManager;
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_GAME);
    }

    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                lastUpdate = curTime;

                a = Math.sqrt(x*x+y*y+z*z) - 9.88;

                if ((curTime - lastPas) > 500 && a > 3) {
                    nbPas++;
                    lastPas = curTime;
                    pasListener.onPasDetected(nbPas);
                }
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onPause(){
        senSensorManager.unregisterListener(this);
    }

    public void onResume(){
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }


    private PasListener pasListener;

    public void setPasListener(PasListener listener){
        pasListener = listener;
    }

    public interface PasListener{
        public void onPasDetected(int nbPas);
    }
}
