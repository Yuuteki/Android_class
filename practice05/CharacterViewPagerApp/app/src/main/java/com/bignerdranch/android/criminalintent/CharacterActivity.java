package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.UUID;

public class CharacterActivity extends SingleFragmentActivity {

    private static final String EXTRA_CHARACTER_ID =
            "com.bignerdranch.android.criminalintent.character_id";

    public static Intent newIntent(Context packageContext, UUID characterId) {
        Intent intent = new Intent(packageContext, CharacterActivity.class);
        intent.putExtra(EXTRA_CHARACTER_ID, characterId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID characterId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CHARACTER_ID);
        return CharacterFragment.newInstance(characterId);
    }
}
