package com.example.test000111;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class Cameraview extends Fragment {
    private View view;
    private ProgressDialog progressDialog;
    private Uri imageUri = null;
    private FirebaseAuth firebaseAuth;
    private String potname = "";
    private String potid = "";
    private String potimage = "";
    Button btn_photo, gallay, savephoto;
    ImageView iv_photo;

    TextView imageText;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.testaicamera, container, false);

        final Bundle intent = getArguments();

        btn_photo=view.findViewById(R.id.btn_photo);
        gallay=view.findViewById(R.id.gallay);
        savephoto=view.findViewById(R.id.savephoto);
        iv_photo=view.findViewById(R.id.iv_photo);
        imageText=view.findViewById(R.id.imageText);


        firebaseAuth = FirebaseAuth.getInstance(); // Auth에등록된계정에서 값을 가져온다//세미콜론있어서 짜피 이거랑은 상관이없네 보니까
        if(imageUri != null) {
            Glide.with(Cameraview.this)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_circle_24)
                    .error(R.drawable.ic_email_24)
                    .centerCrop()
                    .placeholder(R.drawable.ic_florist_24)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(iv_photo);
        }
     //유저 정보 서버에서 불러오는거//이걸 여기에 쓴 이유를 알아보자

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("잠시 기다려 주세요 ...");
        progressDialog.setCanceledOnTouchOutside(false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference temp12 = database.getReference("flowername/");//꽃종류
        temp12.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                imageText.setText(value);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                imageText.setText("error");
            }
        });


        btn_photo.setOnClickListener(new View.OnClickListener() { //갤러리버튼
            @Override
            public void onClick(View view) {
                pickImageGallery();
            }
        });

        gallay.setOnClickListener(new View.OnClickListener() { //카메라버튼
            @Override
            public void onClick(View view) {
                pickImageCamera();

            }
        });


        savephoto.setOnClickListener(new View.OnClickListener() {//분석버튼  이거그냥 이미지 데베에 올리기용\
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        return view;

    }
    private void loadView() {//왜 사진이 있을때만 값을 가져오지? //중요한건 이미 이미지가 있으면 사진가져와도 수정전까지는 저장된값 유지

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Identify").child("potimage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String potimage = "" + snapshot.child("potimage").getValue();// 이미지 경로에서 이미지를 가져온다.

                Activity activity = getActivity();
                if (activity.isFinishing())
                    return;
//이미지 가져온거를 glide에 불러온다. 그럼 서버에 저장을하고 그 저장한거를 가져온다는 이야긴데,,?

                Glide.with(Cameraview.this)
                        .load(potimage)
                        .placeholder(R.drawable.ic_circle_24)
                        .error(R.drawable.ic_email_24)
                        .centerCrop()
                        .placeholder(R.drawable.ic_florist_24)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(iv_photo);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void updateProfile(String imageUri){ //프로필 업데이트 , 사진수정,
        progressDialog.setMessage("Updating user Profile...");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("potimage","" + imageUri);//이미지 빈칸 아니면 이미지 주소 넣음

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users"); //경로는 화분추가 경로 그대로 받아오기
        ref.child(firebaseAuth.getUid()).child("Identify").child("potimage")
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "성공적으로 업로드했습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadImage() { //사진 파베에 등록

        progressDialog.setMessage("updating profile image...");
        progressDialog.show();

        String filePathAndName = ("flower"+(firebaseAuth.getUid())); //프로필이미지를 Storage에 업로드
        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName).child("Identify/");

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
                        Toast.makeText(getActivity(), "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void pickImageCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample image description");
        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

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
                        iv_photo.setImageURI(imageUri);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
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
                        iv_photo.setImageURI(imageUri);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );
}
