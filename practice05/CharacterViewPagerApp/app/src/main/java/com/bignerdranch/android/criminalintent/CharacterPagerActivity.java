package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.UUID;

public class CharacterPagerActivity extends FragmentActivity {

    private static final String EXTRA_CHARACTER_ID =
            "com.bignerdranch.android.criminalintent.character_id";

    private ViewPager mViewPager;
    private List<Character> mCharacters;
    private MediaPlayer mMediaPlayer;

    public static Intent newIntent(Context packageContext, UUID characterId) {
        Intent intent = new Intent(packageContext, CharacterPagerActivity.class);
        intent.putExtra(EXTRA_CHARACTER_ID, characterId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_pager);

        UUID characterId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CHARACTER_ID);

        mViewPager = (ViewPager) findViewById(R.id.character_view_pager);
        mCharacters = CharacterLab.get(this).getCharacters();

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(
                fragmentManager,
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                Character character = mCharacters.get(position);
                return CharacterFragment.newInstance(character.getId());
            }

            @Override
            public int getCount() {
                return mCharacters.size();
            }
        });

        for (int i = 0; i < mCharacters.size(); i++) {
            if (mCharacters.get(i).getId().equals(characterId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.tmnt_theme);
            mMediaPlayer.setLooping(true);
        }
        mMediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            mMediaPlayer.seekTo(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
