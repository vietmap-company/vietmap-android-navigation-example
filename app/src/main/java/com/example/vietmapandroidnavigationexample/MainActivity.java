package com.example.vietmapandroidnavigationexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mapbox.mapboxsdk.location.permissions.PermissionsListener;
import com.mapbox.mapboxsdk.location.permissions.PermissionsManager;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PermissionsListener {

    private PermissionsManager permissionsManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button =  findViewById(R.id.pushToNavigationScreen);
        Intent it = new Intent(this, VietMapNavigationActivity.class);
        button.setOnClickListener(view -> startActivity(it));

        permissionsManager = new PermissionsManager(this);
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager.requestLocationPermissions(this);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "This app needs location permissions in order to show its functionality.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
        } else {
            Toast.makeText(this, "You didn't grant location permissions.",
                    Toast.LENGTH_LONG).show();
        }
    }
}