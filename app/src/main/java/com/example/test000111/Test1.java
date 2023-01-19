package com.example.test000111;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;

public class Test1 extends AppCompatActivity {
    HomeFragment homeFragment;
    Listview listview;
    Cameraview cameraview;
    Userview userview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.btnview);
        homeFragment = new HomeFragment();
        listview = new Listview();
        cameraview = new Cameraview();
        userview = new Userview();

        getSupportFragmentManager().beginTransaction().replace(R.id.home_ly, homeFragment).commit();

        NavigationBarView navigationBarView = findViewById(R.id.bottomNavigationView);

        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.btnHome://홈화면 가는 버튼
                        getSupportFragmentManager().beginTransaction().replace(R.id.home_ly, homeFragment).commit();
                        break;
                    case R.id.btnList://2번째 메뉴 가는 버튼
                        getSupportFragmentManager().beginTransaction().replace(R.id.home_ly, listview).commit();// listview 부분에 필요한.java 이름 넣으면됨
                        break;

                    case R.id.btnCamera://3번째 카메라 메뉴 가는 버튼
                        getSupportFragmentManager().beginTransaction().replace(R.id.home_ly, cameraview).commit();// cameraview 부분에 이름.java 필요한 자바 이름 넣으면됨
                        break;
                    case R.id.btnUser:// 4번째 유저설정 메뉴 가는 버튼
                        getSupportFragmentManager().beginTransaction().replace(R.id.home_ly, userview).commit();
                        break;
                }
                return true;
            }
        });
    }
}