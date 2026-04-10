package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CharacterListFragment extends Fragment {

    private RecyclerView mCharacterRecyclerView;
    private CharacterAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_character_list, container, false);

        mCharacterRecyclerView = (RecyclerView) view
                .findViewById(R.id.character_recycler_view);
        mCharacterRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        CharacterLab characterLab = CharacterLab.get(getActivity());
        List<Character> characters = characterLab.getCharacters();

        if (mAdapter == null) {
            mAdapter = new CharacterAdapter(characters);
            mCharacterRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class CharacterHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ImageView mProgramImageView;
        private TextView mNameTextView;
        private TextView mProgramNameTextView;

        private Character mCharacter;

        public CharacterHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mProgramImageView = (ImageView) itemView.findViewById(R.id.list_item_program_image);
            mNameTextView = (TextView) itemView.findViewById(R.id.list_item_character_name);
            mProgramNameTextView = (TextView) itemView.findViewById(R.id.list_item_program_name);
        }

        public void bindCharacter(Character character) {
            mCharacter = character;
            mProgramImageView.setImageResource(mCharacter.getProgramPicId());
            mNameTextView.setText(mCharacter.getName());
            mProgramNameTextView.setText(mCharacter.getProgramName());
        }

        @Override
        public void onClick(View v) {
            Intent intent = CharacterActivity.newIntent(getActivity(), mCharacter.getId());
            startActivity(intent);
        }
    }

    private class CharacterAdapter extends RecyclerView.Adapter<CharacterHolder> {

        private List<Character> mCharacters;

        public CharacterAdapter(List<Character> characters) {
            mCharacters = characters;
        }

        @Override
        public CharacterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_character, parent, false);
            return new CharacterHolder(view);
        }

        @Override
        public void onBindViewHolder(CharacterHolder holder, int position) {
            Character character = mCharacters.get(position);
            holder.bindCharacter(character);
        }

        @Override
        public int getItemCount() {
            return mCharacters.size();
        }
    }
}
