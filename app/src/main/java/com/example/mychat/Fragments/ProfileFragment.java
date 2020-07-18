package com.example.mychat.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mychat.Model.User;
import com.example.mychat.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    private  CircleImageView mProfileImage;
    private TextView mUsername;

    private FirebaseUser fUser;
    private DatabaseReference mRef;
    private StorageTask storageTask;

    private  Uri mImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mProfileImage = view.findViewById(R.id.profile_image);
        mUsername = view.findViewById(R.id.username);

        fUser  = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isAdded()) {
                    User user = dataSnapshot.getValue(User.class);
                    mUsername.setText(user.getUsername());
                    if (user.getImageUrl().equals("default")) {
                        Glide.with(getContext()).load(R.mipmap.ic_launcher).into(mProfileImage);
                    } else {
                        Glide.with(getContext()).load(user.getImageUrl()).into(mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = CropImage.activity()
//                        .setAspectRatio(1,1)
//                        .getIntent(getContext());
//
//                startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
             //   CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(getActivity(),ProfileFragment.this);
                CropImage.activity().setAspectRatio(1,1).start(getActivity(),ProfileFragment.this);
                Log.d("theartistpro", "onClick: "+getActivity().toString());
                Log.d("theartistpro", "onClick: "+getContext().toString());
//                com.example.mychat.MainActivity
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("theartist", "onActivityResult:  request code "+String.valueOf(requestCode)+" and result code"+String.valueOf(resultCode));
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            uploadImage();

        } else {
            Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Updating profile image");
        progressDialog.show();

        Log.d("theartist", "uploadImage: "+mImageUri.toString());

        if(mImageUri!=null){
           final StorageReference mFileRef = FirebaseStorage.getInstance().getReference()
                    .child("Uploads").child(System.currentTimeMillis()+"jpeg");

            storageTask = mFileRef.putFile(mImageUri);

            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return mFileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String url = downloadUri.toString();

                        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).child("imageUrl").setValue(url);
                        progressDialog.dismiss();
//                        startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
//                        finish();
                    } else {
                        Toast.makeText(getContext(), "Upload failed!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });





        }else{
            Toast.makeText(getContext(), "No image was selected!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }


    }




}
