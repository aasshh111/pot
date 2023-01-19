package com.example.test000111;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Listview extends Fragment {
    private View view;


    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelCategory> categoryArrayList;
    private AdapterCategory adapterCategory;
    private ProgressDialog progressDialog;
    TextView title_tv,sub_title_tv,goodt,goodh,goodm,meterview;
    ImageButton logOut_Btn;
    RecyclerView plant_List;
    Button addcategoryBtn;
    LinearLayout plantviewer;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_user_dashboard, container, false);
        logOut_Btn=view.findViewById(R.id.logOut_Btn);
        plant_List=view.findViewById(R.id.plant_List);
        sub_title_tv=view.findViewById(R.id.sub_title_tv);
        plantviewer=view.findViewById(R.id.plantviewer);
        goodh=view.findViewById(R.id.goodh);
        goodt=view.findViewById(R.id.goodt);
        goodm=view.findViewById(R.id.goodm);
        meterview=view.findViewById(R.id.meterview);

//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding =  ActivityUserDashboardBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("잠시 기다려 주세요 ...");
        progressDialog.setCanceledOnTouchOutside(false);


        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadCategory();





        logOut_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });
        addcategoryBtn=view.findViewById(R.id.addcategoryBtn);

        addcategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addnull();
                startActivity(new Intent(getActivity(), PotAddActivity.class));

            }
        });
        return view;

    }

    private void addnull() {
        progressDialog.setMessage("화분을 생성중입니다.");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("potimage","");
        hashMap.put("potname","");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Pot").child("potid")
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();

                        getActivity().finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadCategory() {

        categoryArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Pot").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                categoryArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    ModelCategory model = ds.getValue(ModelCategory.class);
                    categoryArrayList.add(model);

                }

                adapterCategory = new AdapterCategory(getActivity(),categoryArrayList);

                plant_List.setAdapter(adapterCategory);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUser() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        } else {
            String email = firebaseUser.getEmail();
            sub_title_tv.setText(email);
        }

    }

}
