package br.pucminas.damd;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;


public class MainActivity extends AppCompatActivity {
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute
    CognitoCachingCredentialsProvider cognitoProvider;
    MyInterface myInterface;
    RequestClass request = new RequestClass("GPS", "shulambs");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button) findViewById(R.id.btnShow);

        // Initialize the Amazon Cognito credentials provider
        cognitoProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-2:f70ecaff-057c-4009-b72f-d1ba29623b44", // Identity pool ID
                Regions.US_EAST_2 // Region
        );

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }

        // Create LambdaInvokerFactory, to be used to instantiate the Lambda proxy.
        LambdaInvokerFactory factory = new LambdaInvokerFactory(this.getApplicationContext(),
                Regions.US_EAST_2, cognitoProvider);


        // Create the Lambda proxy object with a default Json data binder.
        // You can provide your own data binder by implementing
        // LambdaDataBinder.
        myInterface = factory.build(MyInterface.class);

        RequestClass request = new RequestClass("GPS", "1234");
        // The Lambda function invocation results in a network call.
        // Make sure it is not called from the main thread.
        new AsyncTask<RequestClass, Void, ResponseClass>() {
            @Override
            protected ResponseClass doInBackground(RequestClass... params) {
                // invoke "echo" method. In case it fails, it will throw a
                // LambdaFunctionException.
                try {
                    return myInterface.AndroidAWSlambda(params[0]);
                } catch (LambdaFunctionException lfe) {
                    Log.e("Tag", "Failed to invoke echo", lfe);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ResponseClass result) {
                if (result == null) {
                    return;
                }

                // Do a toast
                Toast.makeText(MainActivity.this, result.getResponseString(), Toast.LENGTH_LONG).show();
            }
        }.execute(request);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensagem(v);
            }
        });

    }

    public void mostrarMensagem(View view) {

        Location location; // location
        double latitude; // latitude
        double longitude; // longitude
        String sensorName = "NOT AVAILABLE";
        String sensorValue = "NONE";


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 21 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // getting GPS status
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.v("isGPSEnabled", "=" + isGPSEnabled);
        // getting network status
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

        if (isNetworkEnabled) {
            location = null;
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                sensorName = "NETWORK";
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    sensorValue = "lat: " + latitude + ", long: " + longitude;
                }
            }
        }

        if (isGPSEnabled) {
            location = null;
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                sensorName = "GPS";
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    sensorValue = "lat: " + latitude + ", long: " + longitude;
                }
            }
        }

        RequestClass request = new RequestClass(sensorName, sensorValue);

        new AsyncTask<RequestClass, Void, ResponseClass>() {
            @Override
            protected ResponseClass doInBackground(RequestClass... params) {
                try {
                    return myInterface.AndroidAWSlambda(params[0]);
                } catch (LambdaFunctionException lfe) {
                    Log.e("Tag", "Failed to invoke echo", lfe);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ResponseClass result) {
                if (result == null) {
                    return;
                }

                // Do a toast
                Toast.makeText(MainActivity.this, result.getResponseString(), Toast.LENGTH_LONG).show();
            }
        }.execute(request);
    }

}
