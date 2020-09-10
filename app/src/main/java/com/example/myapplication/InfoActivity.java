package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.myapplication.MainActivity.isConnected;

public class InfoActivity extends AppCompatActivity {

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        btn = (Button) findViewById(R.id.try_again);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetConnection();

                if(isConnected){
                    Intent intent = new Intent(InfoActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(InfoActivity.this,"No Internet",Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    public void checkInternetConnection(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if(activeNetwork != null){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                //Toast.makeText(MainActivity.this,"WiFi " + isConnected,Toast.LENGTH_LONG).show();
            }
            else if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                //Toast.makeText(MainActivity.this,"Mobile Internet",Toast.LENGTH_LONG).show();
            }
        }
        else {
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            //Toast.makeText(MainActivity.this,"Not internet " + isConnected,Toast.LENGTH_LONG).show();
        }

    }
}
