package fr.uga.wic.bb.pedestriandeadreckoning;

import android.content.Context;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SensorManager senSensorManager;
    private TextView pasText;
    private TextView yawText;
    private TextView pitchText;
    private TextView rollText;
    private TextView latText;
    private TextView lonText;

    private Podometre podometre;
    private Orientation orientation;
    private PDR pdr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pasText = (TextView)findViewById(R.id.pasText);
        yawText = (TextView)findViewById(R.id.yawText);
        pitchText = (TextView)findViewById(R.id.pitchText);
        rollText = (TextView)findViewById(R.id.rollText);
        latText = (TextView)findViewById(R.id.latText);
        lonText = (TextView)findViewById(R.id.lonText);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        podometre = new Podometre(senSensorManager);
        orientation = new Orientation(senSensorManager);
        pdr = new PDR(senSensorManager, (float) 45.19270982152921, (float) 5.773703679442406);

        podometre.setPasListener(pasListener);
        orientation.setOrientationChangeListener(orientationListener);
        pdr.setPasListener(pdrListener);

        latText.setText("Lat : "+pdr.getmCurrentLocation()[0]);
        lonText.setText("Lon : "+pdr.getmCurrentLocation()[1]);
    }

    protected void onPause() {
        super.onPause();
        podometre.onPause();
        orientation.onPause();
    }

    protected void onResume() {
        super.onResume();
        podometre.onResume();
        orientation.onResume();
    }

    private Podometre.PasListener pasListener = new Podometre.PasListener() {
        @Override
        public void onPasDetected(int nbPas) {
            pasText.setText("Nombre pas : "+nbPas);
        }
    };

    private PDR.PDRListener pdrListener = new PDR.PDRListener() {
        @Override
        public void onPasDetected(float[] newLocation) {
            latText.setText("Lat : "+newLocation[0]);
            lonText.setText("Lon : "+newLocation[1]);
        }
    };

    private Orientation.OrientationListener orientationListener = new Orientation.OrientationListener() {

        @Override
        public void onOrientationChange(float[] orientation) {
            yawText.setText("Yaw : "+round(Math.toDegrees(orientation[0]),1)+"°");
            pitchText.setText("Pitch : "+round(Math.toDegrees(orientation[1]),1)+"°");
            rollText.setText("Roll : "+round(Math.toDegrees(orientation[2]),1)+"°");
        }
    };

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }


}
