package coma.example.salih.googlemapdempapp;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Activity1 extends AppCompatActivity {

    private TextView imeiview, ipview, latitudeview, longitudeview;
    private Button bvalid,bmap;
    static final Integer PHONESTATS = 0x1;
    protected String imei;
    String ipAddress;
    private LocationManager locationManager;
    private LocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout1);

        imeiview = (TextView)findViewById(R.id.imei);
        ipview = (TextView)findViewById(R.id.ip);
        latitudeview = (TextView)findViewById(R.id.lattitude);
        longitudeview = (TextView)findViewById(R.id.longitude);
        bvalid = (Button)findViewById(R.id.button);
        bmap = (Button)findViewById(R.id.mapbouton);


        // Pour prendre l'@IP du wifi
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ActivityCompat.requestPermissions(Activity1.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);

        // Ecouteur pour afficher les information
        bvalid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                askForPermission(Manifest.permission.READ_PHONE_STATE, PHONESTATS);
                imeiview.setText(imei);
                ipview.setText(ipAddress);

                GPSLoc loc = new GPSLoc(getApplicationContext());
                Location l =loc.getLocation();
                if (l!=null){

                    double lat = l.getLatitude();
                    double lon = l.getLongitude();
                    latitudeview.setText(String.valueOf(lat));
                    longitudeview.setText(String.valueOf(lon));
                }

            }
        });

        // Ecouteur pour afficher les coordonnées dans la map
        bmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent a = new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(a);
            }
        });
    }

    // Recupération de l'IMEI

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(Activity1.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should show an explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale(Activity1.this, permission)) {

                ActivityCompat.requestPermissions(Activity1.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(Activity1.this, new String[]{permission}, requestCode);
            }
        } else {
            imei = getImeiNumber();
            Toast.makeText(this,permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    imei = getImeiNumber();

                } else {

                    Toast.makeText(Activity1.this, "You have Denied the Permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private String getImeiNumber() {
        final TelephonyManager telephonyManager= (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //getDeviceId() is Deprecated so for android O we can use getImei() method
            return telephonyManager.getImei();
        }
        else {
            return telephonyManager.getDeviceId();
        }

    }

}
