package com.example.test000111;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.test000111.databinding.ActivityPotAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Locale;


public class PotAddActivity extends AppCompatActivity {
    private Context context;
    private ActivityPotAddBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private String potname = "";
    private String potid = "";
    private Uri imageUri = null;
    private String able,unable ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPotAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        Glide.with(PotAddActivity.this)
                .load(imageUri)
                .placeholder(R.drawable.ic_circle_24)
                .error(R.drawable.ic_email_24)
                .centerCrop()
                .placeholder(R.drawable.ic_florist_24)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.potimg1Icon);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("잠시 기다려 주세요 ...");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.potimg1Icon.setOnClickListener(new View.OnClickListener() { //화분이미지버튼
            @Override
            public void onClick(View view) {
                showImageAttachMenu(); //메뉴
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() { //생성하기
            @Override
            public void onClick(View view) {
                validateData();

            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() { //뒤로가기 버튼튼
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PotAddActivity.this,Test1.class));
            }
        });
    }


    private void uploadImage() { //사진 파베에 등록

        progressDialog.setMessage("updating profile image...");
        progressDialog.show();

        String filePathAndName = ("flower"); //프로필이미지를 Storage에 업로드
        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName).child("flower/"); //업로드하는 경로

        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                        while (!task.isSuccessful());
                        String uploadedImageUri = ""+task.getResult();
                        addCategoryFirebase(uploadedImageUri);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(PotAddActivity.this, "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

    }



    private void showImageAttachMenu(){ // 갤러리, 카메라 메뉴 보여주는거

        PopupMenu popupMenu = new PopupMenu(this, binding.potimg1Icon);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Gallery");
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                progressDialog.dismiss();

                int which = item.getItemId();
                if (which==0) {
                    pickImageCamera();
                } else if (which == 1){
                    pickImageGallery();
                }
                return false;
            }
        });
    }

    private void pickImageCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample image description");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private void pickImageGallery() { //갤러리에서 사진가져오는거
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);

    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        binding.potimg1Icon.setImageURI(imageUri);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(PotAddActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        binding.potimg1Icon.setImageURI(imageUri);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(PotAddActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private void loadUserInfo1() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Pot").child(potid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String potname = "" + snapshot.child("potname").getValue();
                String potimage = "" + snapshot.child("potimage").getValue();// 이미지 경로에서 이미지를 가져온다.
                binding.categoryEt.setText(potname);

                Activity activity = PotAddActivity.this;
                if (activity.isFinishing())
                    return;

                Glide.with(PotAddActivity.this)
                        .load(potimage)
                        .centerCrop()
                        .placeholder(R.drawable.ic_florist_24)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.potimg1Icon);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void validateData () {


        potname = binding.categoryEt.getText().toString().trim();
        potid = binding.categoryEt2.getText().toString().trim().toUpperCase(Locale.ROOT);;

        if (TextUtils.isEmpty(potname)) {
            Toast.makeText(this, "화분 이름을 입력하여 주세요 ...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(potid)) {
            Toast.makeText(this, "제품의 화분ID를 입력하여 주세요", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PotidCheck");
            ref.child("serial").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(potid.toString()).exists()==false) {
                        Toast.makeText(getApplicationContext(), "잘못된 코드 입니다.", Toast.LENGTH_SHORT).show();//토스메세지 출력 //potid 없거나 다를때
                    } else if(dataSnapshot.child(potid.toString()).exists()==true){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PotidCheck");
                        ref.child("serial").child(potid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child("able".toString()).exists()==true) {
                                    if(imageUri == null){
                                        Toast.makeText(getApplicationContext(), "화분이 생성되었습니다.", Toast.LENGTH_SHORT).show();//토스메세지 출력 //없을때
                                        addCategoryFirebase("");
                                        updatePotidFirebase();//able을 unable로
                                    }else {
                                        uploadImage();
                                        updatePotidFirebase();//able을 unable로
                                    }
                                }else if (dataSnapshot.child("unable".toString()).exists()==true) {
                                    Toast.makeText(getApplicationContext(), "이미 사용하고 있는 ID입니다.", Toast.LENGTH_SHORT).show();//토스메세지 출력 //없을때
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
        private void addCategoryFirebase(String imageUri) {

            progressDialog.setMessage("화분을 생성중입니다.");
            progressDialog.show();

            long timestamp = (System.currentTimeMillis());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("potid", "" + potid);
            hashMap.put("potname", "" + potname);
            hashMap.put("timestamp", timestamp);
            hashMap.put("uid", "" + firebaseAuth.getUid());
            if (imageUri != null) {
                hashMap.put("potimage", "" + imageUri);//이미지 빈칸 아니면 이미지 주소 넣음
            }

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Pot").child(potid)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(PotAddActivity.this, "화분이 성공적으로 생성되었습니다.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PotAddActivity.this,Test1.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(PotAddActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        private void updatePotidFirebase() {

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("unable","");
            hashMap.put("uid", "" + firebaseAuth.getUid());

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


    private void updatePlantinfo(){}

}