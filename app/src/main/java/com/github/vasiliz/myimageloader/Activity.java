package com.github.vasiliz.myimageloader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

public class Activity extends AppCompatActivity {

    private static final String TAG = "Activivty";
    private ImageView mImageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_load_image);
        mImageView = findViewById(R.id.image);

        ImageLoader.getInstance().with(getApplication()).load("https:\\/\\/pp.userapi.com\\/c846320\\/v846320640\\/b6a89\\/cGXI8gLBy8U.jpg?ava=1").into(mImageView);
         //  ImageLoader.getInstance().with(this).load("https:\\/\\/vk.com\\/doc110088_461401201").into(mVideoView);
    }
}
