package fr.uga.wic.bb.pedestriandeadreckoning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity {

    private SensorManager senSensorManager;
    private SharedPreferences sharedPref;

    private MapView mapView;
    private static MapboxMap mapboxMap;
    private static MarkerView marker;

    private PDR pdr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button settingsButton = (Button) findViewById(R.id.settings);
        settingsButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });


        final float latStart = Float.parseFloat(getString(R.string.lat_start));
        final float lonStart = Float.parseFloat(getString(R.string.lon_start));

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        pdr = new PDR(senSensorManager, sharedPref, latStart, lonStart);
        pdr.setPasListener(pdrListener);

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
        mapView.onPause();
    }

    protected void onResume() {
        super.onResume();
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

    private PDR.PDRListener pdrListener = new PDR.PDRListener() {
        @Override
        public void onPasDetected(float[] newLocation) {
            LatLng newPos = new LatLng(newLocation[0],newLocation[1]);
            marker.setPosition(newPos);
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
            marker.setVisible(true);
            marker.setPosition(point);
            pdr.setmCurrentLocation(new float[]{(float) point.getLatitude(), (float) point.getLongitude()});
            System.out.println("update marker position");
        }
    }


}
