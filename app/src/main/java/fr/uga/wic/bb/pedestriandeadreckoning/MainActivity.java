package fr.uga.wic.bb.pedestriandeadreckoning;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SensorManager senSensorManager;
    private TextView pasText;

    private Podometre podometre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pasText = (TextView)findViewById(R.id.pasText);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        podometre = new Podometre(senSensorManager);

        podometre.setPasListener(pasListener);
    }

    protected void onPause() {
        super.onPause();
        podometre.onPause();
    }

    protected void onResume() {
        super.onResume();
        podometre.onResume();
    }

    private Podometre.PasListener pasListener = new Podometre.PasListener() {
        @Override
        public void onPasDetected(int nbPas) {
            pasText.setText("Nombre pas : "+nbPas);
        }
    };


}
