package com.example.test000111;

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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.test000111.databinding.ActivityPoteditBinding;
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
import com.bumptech.glide.Glide;


import java.util.HashMap;

public class PotEditActivity extends AppCompatActivity {

    private ActivityPoteditBinding binding;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Uri imageUri = null;
    private String potname = "";
    private String potid = "";
    private String potimage = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPoteditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        final Intent intent = getIntent();
        potid = intent.getStringExtra("potid");
        potimage = intent.getStringExtra("potimage");
        potname = intent.getStringExtra("potname");// Auth에등록된계정에서 값을 가져온다//세미콜론있어서 짜피 이거랑은 상관이없네 보니까
        if(potimage != null){
            loadUserInfo();
        }else{
            binding.categoryEt.setText(potname);
            Glide.with(PotEditActivity.this)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_circle_24)
                    .error(R.drawable.ic_florist_24)
                    .centerCrop()
                    .placeholder(R.drawable.ic_circle_24)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.potimgIcon);

        } //유저 정보 서버에서 불러오는거//이걸 여기에 쓴 이유를 알아보자

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("잠시 기다려 주세요 ...");
        progressDialog.setCanceledOnTouchOutside(false);


        binding.potimgIcon.setOnClickListener(new View.OnClickListener() { //화분이미지버튼
            @Override
            public void onClick(View view) {
                showImageAttachMenu(); //메뉴
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener(){ //수정완료 버튼
            @Override
            public void onClick(View view) {
                validateData();
            }
        });


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadUserInfo() {//왜 사진이 있을때만 값을 가져오지? //중요한건 이미 이미지가 있으면 사진가져와도 수정전까지는 저장된값 유지

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Pot").child(potid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String potname = "" + snapshot.child("potname").getValue();//potname 에 있는 값을 potname에 가져온다.
                String potimage = "" + snapshot.child("potimage").getValue();// 이미지 경로에서 이미지를 가져온다.
                binding.categoryEt.setText(potname); // 이름쓰는곳에 potname 의 가져온 값이 들어간다.

                Activity activity = PotEditActivity.this;
                if (activity.isFinishing())
                    return;
//이미지 가져온거를 glide에 불러온다. 그럼 서버에 저장을하고 그 저장한거를 가져온다는 이야긴데,,?

                Glide.with(PotEditActivity.this)
                        .load(potimage)
                        .placeholder(R.drawable.ic_circle_24)
                        .error(R.drawable.ic_email_24)
                        .centerCrop()
                        .placeholder(R.drawable.ic_florist_24)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.potimgIcon);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void validateData() { //화분정보 수정

        final Intent intent = getIntent();
        potid = intent.getStringExtra("potid");


        potname = binding.categoryEt.getText().toString().trim();

        if(TextUtils.isEmpty((potname))) {//이름빈칸이면 이름쳐라
            progressDialog.dismiss();
            Toast.makeText(this,"이름을 입력해 주세요...", Toast.LENGTH_SHORT).show(); //이름쳐라 메세지노출
        } else { //이름빈칸아니면
            if (imageUri == null) { //이름 빈칸 아닌데 이미지 빈칸이면
                updateProfile("");
                //이름만 서버에 올리고 이미지는 빈칸으로 그냥올림
            } else {//이미지 빈칸아니면--->이미지 들어있으면
                uploadImage();
                                //서버에 이미지올림
            }
        }
    }

    private void updateProfile(String imageUri){ //프로필 업데이트 , 사진수정,
        progressDialog.setMessage("Updating user Profile...");
        progressDialog.show();

        final Intent intent = getIntent();
        potid = intent.getStringExtra("potid");


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("potname", "" + potname); //이름 서버에 ㄱ
        if (imageUri != null) {
            hashMap.put("potimage","" + imageUri);//이미지 빈칸 아니면 이미지 주소 넣음
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users"); //경로는 화분추가 경로 그대로 받아오기
        ref.child(firebaseAuth.getUid()).child("Pot").child(potid)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(PotEditActivity.this, "성공적으로 업로드했습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(PotEditActivity.this, "업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadImage() { //사진 파베에 등록

        final Intent intent = getIntent();
        potid = intent.getStringExtra("potid");

        progressDialog.setMessage("updating profile image...");
        progressDialog.show();

        String filePathAndName = ("flower"+(firebaseAuth.getUid())); //프로필이미지를 Storage에 업로드
        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName).child("flower/");

        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                        while (!task.isSuccessful());
                        String uploadedImageUri = ""+task.getResult();
                        updateProfile(uploadedImageUri);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(PotEditActivity.this, "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void showImageAttachMenu(){ // 갤러리, 카메라 메뉴 보여주는거

        PopupMenu popupMenu = new PopupMenu(this, binding.potimgIcon);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Gallery");
        popupMenu.show(); //팝업 메뉴들이 나옴 카메라, 갤러리

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                progressDialog.dismiss();
                //카메라와 갤러리 선택하는곳
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
                        binding.potimgIcon.setImageURI(imageUri);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(PotEditActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
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
                        binding.potimgIcon.setImageURI(imageUri);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(PotEditActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

}