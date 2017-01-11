package fr.uga.wic.bb.pedestriandeadreckoning;

import android.content.Context;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity {

    private SensorManager senSensorManager;
    private TextView pasText;
    private TextView yawText;
    private TextView pitchText;
    private TextView rollText;
    private TextView latText;
    private TextView lonText;

    private MapView mapView;
    private static MapboxMap mapboxMap;
    private static MarkerView marker;

    private Podometre podometre;
    private Orientation orientation;
    private PDR pdr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final float latStart = Float.parseFloat(getString(R.string.lat_start));
        final float lonStart = Float.parseFloat(getString(R.string.lon_start));

        pasText = (TextView)findViewById(R.id.pasText);
        yawText = (TextView)findViewById(R.id.yawText);
        pitchText = (TextView)findViewById(R.id.pitchText);
        rollText = (TextView)findViewById(R.id.rollText);
        latText = (TextView)findViewById(R.id.latText);
        lonText = (TextView)findViewById(R.id.lonText);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        podometre = new Podometre(senSensorManager);
        orientation = new Orientation(senSensorManager);
        pdr = new PDR(senSensorManager, latStart, lonStart);

        podometre.setPasListener(pasListener);
        orientation.setOrientationChangeListener(orientationListener);
        pdr.setPasListener(pdrListener);

        latText.setText("Lat : "+pdr.getmCurrentLocation()[0]);
        lonText.setText("Lon : "+pdr.getmCurrentLocation()[1]);

        // connect to Mapbox with access token
        MapboxAccountManager.start(this, getString(R.string.access_token));

        // Create a mapView
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        // Add a MapboxMap
        mapView.getMapAsync(new OnMapReadyCallback() {

            public void onMapReady(MapboxMap mapboxMap) {
                MainActivity.mapboxMap = mapboxMap;

                LatLng latLng = new LatLng(latStart, lonStart);

                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(19)
                    .build());

                //HotFix : Adding marker here invisible because marker wasn't showing otherwise
                marker = mapboxMap.addMarker(new MarkerViewOptions().position(latLng).title("position"));
                marker.setVisible(false);

                mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    public void onMapClick(LatLng point) {
                        updateMarker(point);
                    }
                });
            }
        });
    }

    protected void onPause() {
        super.onPause();
        podometre.onPause();
        orientation.onPause();
        mapView.onPause();
    }

    protected void onResume() {
        super.onResume();
        podometre.onResume();
        orientation.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
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

    public void updateMarker(LatLng point){
        if(marker == null){
            marker = mapboxMap.addMarker(new MarkerViewOptions().position(point).title("position"));
            System.out.println("create marker");

        }else {
            marker.setPosition(point);
            marker.setVisible(true);
            System.out.println("update marker position");
        }
    }


}
