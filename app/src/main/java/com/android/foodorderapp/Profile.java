package com.android.foodorderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Profile extends AppCompatActivity {

            ImageView ProfileImage;
        TextView Name,Gmail;
        FirebaseUser user;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_profile);
            Bundle data = getIntent().getExtras();
            user=data.getParcelable("account");
            Toast.makeText(this, user.getPhoneNumber(), Toast.LENGTH_SHORT).show();
            Log.d("USERid",user.getUid());
            Log.d("USERno", Objects.requireNonNull(user.getPhoneNumber()));
            Log.d("USERPid",user.getProviderId());
            Log.d("USER",user.getMetadata().toString());
//            Log.d("USER",user.getUid());
//            Log.d("USER",user.getUid());
            ProfileImage=findViewById(R.id.userImage);
            Name=findViewById(R.id.Name);
            Gmail=findViewById(R.id.gmail);
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(ProfileImage);
            Name.setText(user.getDisplayName());
            Gmail.setText(user.getEmail());

        }

    public void signInPage(View view) {
        startActivity(new Intent(Profile.this,Login.class));
        finish();
    }
}
