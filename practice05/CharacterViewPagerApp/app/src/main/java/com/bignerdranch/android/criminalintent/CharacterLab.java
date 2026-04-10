package com.bignerdranch.android.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CharacterLab {
    private static CharacterLab sCharacterLab;

    private ArrayList<Character> mCharacters;

    public static CharacterLab get(Context context) {
        if (sCharacterLab == null) {
            sCharacterLab = new CharacterLab(context);
        }
        return sCharacterLab;
    }

    private CharacterLab(Context context) {
        mCharacters = new ArrayList<>();
        addCharacter(R.drawable.tmntleo, "Leonardo", "Leo");
        addCharacter(R.drawable.tmntdon, "Donatello", "Don");
        addCharacter(R.drawable.tmntraph, "Raphael", "Raph");
        addCharacter(R.drawable.tmntmike, "Michelangelo", "Mike");
    }

    private void addCharacter(int characterPicId, String name, String nickName) {
        mCharacters.add(new Character(
                characterPicId,
                name,
                nickName,
                R.mipmap.ic_launcher,
                "Teenage Mutant Ninja Turtles"));
    }

    public List<Character> getCharacters() {
        return mCharacters;
    }

    public Character getCharacter(UUID id) {
        for (Character character : mCharacters) {
            if (character.getId().equals(id)) {
                return character;
            }
        }
        return null;
    }
}
