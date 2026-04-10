package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.UUID;

public class CharacterFragment extends Fragment {

    private static final String ARG_CHARACTER_ID = "character_id";

    private Character mCharacter;
    private TextView mProgramNameTextView;
    private ImageView mProgramImageView;
    private TextView mNickNameTextView;
    private ImageButton mCharacterImageButton;
    private TextView mFullNameTextView;

    public static CharacterFragment newInstance(UUID characterId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CHARACTER_ID, characterId);

        CharacterFragment fragment = new CharacterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID characterId = (UUID) getArguments().getSerializable(ARG_CHARACTER_ID);
        mCharacter = CharacterLab.get(getActivity()).getCharacter(characterId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_character, container, false);

        mProgramNameTextView = (TextView) v.findViewById(R.id.character_program_name);
        mProgramImageView = (ImageView) v.findViewById(R.id.character_program_image);
        mNickNameTextView = (TextView) v.findViewById(R.id.character_nick_name);
        mCharacterImageButton = (ImageButton) v.findViewById(R.id.character_image);
        mFullNameTextView = (TextView) v.findViewById(R.id.character_full_name);

        mProgramNameTextView.setText(mCharacter.getProgramName());
        mProgramImageView.setImageResource(mCharacter.getProgramPicId());
        mNickNameTextView.setText(mCharacter.getNickName());
        mCharacterImageButton.setImageResource(mCharacter.getCharacterPicId());
        mFullNameTextView.setText(R.string.tap_character_hint);

        mCharacterImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFullNameTextView.setText(mCharacter.getName());
            }
        });

        return v;
    }
}
