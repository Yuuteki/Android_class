package com.bignerdranch.android.photogallery;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;


public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
