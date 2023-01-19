package com.example.test000111;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.test000111.databinding.ActivityPoteditBinding;
import com.example.test000111.databinding.RowPlantBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory> {

    private Context context;
    private ArrayList<ModelCategory> categoryArrayList;

    private RowPlantBinding binding;
    private FirebaseUser firebaseAuth;



    public AdapterCategory(Context context, ArrayList<ModelCategory> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
    }

    @NonNull
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = RowPlantBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderCategory(binding.getRoot());

    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {

        ModelCategory model = categoryArrayList.get(position);

        String id = model.getId();
        String potname = model.getPotname();
        String potid = model.getPotid();
        String uid = model.getUid();
        long timestamp = model.getTimestamp();
        String potimage = model.getPotimage();
        String formattedDate = MyApplication.formatTimestamp(timestamp);

        holder.category_tv.setText(potname);
        holder.pot_if.setText(formattedDate);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("화분 삭제")
                        .setMessage("정말로 이 화분을 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteCategory(model, holder);
                                updatePotidFirebase2(model);
                                Toast.makeText(context, "삭제중입니다 ...", Toast.LENGTH_SHORT).show();
                                context.startActivity(new Intent(context,Listview.class));
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        holder.setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(context, PotEditActivity.class);
                newIntent.putExtra("potid", model.getPotid());
                context.startActivity(newIntent);
            }
        });

        holder.layBtn.setOnClickListener(new View.OnClickListener() { //화면누르면 다음화면넘어간다.
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(context, Listview1.class);
                newIntent.putExtra("potid", model.getPotid()); //potid로 넘어감
                context.startActivity(newIntent);
            }
        });
        Glide.with(holder.itemView)
                .load(potimage)
                .placeholder(R.drawable.ic_circle_24)
                .error(R.drawable.ic_email_24)
                .centerCrop()
                .placeholder(R.drawable.ic_florist_24)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.circleIv);
    }


    private void deleteCategory(ModelCategory model, HolderCategory holder) {

        String uid = model.getUid();
        String potname = model.getPotname();
        String potid = model.getPotid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).child("Pot").child(potid)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "성공적으로 삭제하였습니다 ...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }



    private void editpage(ModelCategory model, HolderCategory holder){

        String potid = model.getPotid(); // 위치에따른 값 받기
        String uid = model.getUid(); //위치에 따른 값 받기


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users"); //파이어베이스
        ref.child(uid).child("Pot").child(potid).addListenerForSingleValueEvent(new ValueEventListener() { //파이어베이스 위치
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) { //데이터 받기

                String potid = "" + datasnapshot.child("potid").getValue();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Intent myIntent = new Intent(context,PotEditActivity.class);
        //myIntent.putExtra(ModelCategory. potname);
        context.startActivity(myIntent);//페이지넘기기(넘기기전에 값 다 받아서 넘겨야함)



    }



    private void updatePotidFirebase2(ModelCategory model) {
        String potid = model.getPotid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("able","");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PotidCheck");
        ref.child("serial").child(potid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }


    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    class HolderCategory extends RecyclerView.ViewHolder{

        TextView pot_if;
        TextView category_tv ;
        ImageButton deleteBtn;
        ImageButton setBtn;
        LinearLayout layBtn;
        CircleImageView circlev;

        public HolderCategory(@NonNull View itemView) {
            super(itemView);
            layBtn = binding.plantviewer;

            pot_if = binding.potInfo;
            category_tv  = binding.categoryTitle;
            deleteBtn = binding.deleteIcon;
            setBtn = binding.setIcon;
            circlev = binding.circleIv;

        }
    }
}