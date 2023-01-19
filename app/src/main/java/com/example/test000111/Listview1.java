package com.example.test000111;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Listview1 extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private String potid = "";
    private String Moi = "";
    private String Gsoil = "";
    private String humi = "";
    private String meter = "";
    private String temper = "";
    private String Soil = "";



    TextView health,temperature,humidity,lamp,meterview,tank,goodh,goodt,goodm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.readinfor);

        health=findViewById(R.id.healthview);
        temperature=findViewById(R.id.tempratureview);
        humidity=findViewById(R.id.humidityview);
        lamp=findViewById(R.id.lampview);
        meterview=findViewById(R.id.meterview);
        tank=findViewById(R.id.tankview);
        goodh=findViewById(R.id.goodh);
        goodm=findViewById(R.id.goodm);
        goodt=findViewById(R.id.goodt);


        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference light = database.getReference("/PlantClass/Rose/humi");
        light.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                goodh.setText(value);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                goodh.setText("error");
            }
        });

        DatabaseReference temtem = database.getReference("/PlantClass/Rose/temper");
        temtem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                goodt.setText(value);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                goodt.setText("error");
            }
        });
        DatabaseReference metmet = database.getReference("/PlantClass/Rose/meter");
        metmet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                goodm.setText(value);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                goodm.setText("error");
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        final Intent intent = getIntent();
        potid = intent.getStringExtra("potid");


        DatabaseReference plplpl = database.getReference("/PotidCheck/serial/"+potid+"/Moi/Soil");
        plplpl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                meterview.setText(value);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                meterview.setText("error");
            }
        });

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//
//        DatabaseReference temp = database.getReference("flower/temp");//현재온도
//        temp.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                int value = dataSnapshot.getValue(int.class);
//                temperature.setText(value + "C°");
//            }
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                temperature.setText("error");
//            }
//        });
//        DatabaseReference goodtemp = database.getReference("flower/temp");//적정온도
//        goodtemp.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                goodt.setText("적정온도"+value);
//            }
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                temperature.setText("error");
//            }
//        });
//        DatabaseReference humi = database.getReference("flower/humi");//현재습도
//        humi.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                int value = dataSnapshot.getValue(int.class);
//                humidity.setText(value + "%");
//            }
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                humidity.setText("error");
//            }
//        });
//        DatabaseReference goodhumi = database.getReference("flower/humi");//적정습도
//        goodhumi.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                humidity.setText("적정습도"+value);
//            }
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                humidity.setText("error");
//            }
//        });
//        DatabaseReference met = database.getReference("flower/meter");//현재토양습도
//        met.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                int value = dataSnapshot.getValue(int.class);
//                meter.setText(value + "%");
//            }
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                meter.setText("error");
//            }
//        });
//        DatabaseReference goodmet = database.getReference("flower/meter");//적정토양습도
//        goodmet.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                meter.setText("적정습도"+value);
//            }
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                meter.setText("error");
//            }
//        });
//        DatabaseReference tan = database.getReference("flower/tank");
//        tan.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                int value = dataSnapshot.getValue(int.class);
//                tank.setText(value + "%");
//            }
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                tank.setText("error");
//            }
//        });
//        DatabaseReference heal = database.getReference("flower/health");
//        heal.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                health.setText(value);
//            }
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                health.setText("error");
//            }
//        });
//        DatabaseReference light = database.getReference("flower/light");
//        light.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                lamp.setText(value);
//            }
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                lamp.setText("error");
//            }
//        });
//
//
//
//
    }
    private void getinfo() {



    }

    private void Classinfo() {




    }



}
