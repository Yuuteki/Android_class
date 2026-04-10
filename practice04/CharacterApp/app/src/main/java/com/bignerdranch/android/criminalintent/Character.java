package com.bignerdranch.android.criminalintent;

import java.util.UUID;

public class Character {

    private UUID mId;
    private int mCharacterPicId;
    private String mName;
    private String mNickName;
    private int mProgramPicId;
    private String mProgramName;

    public Character(int characterPicId, String name, String nickName,
                     int programPicId, String programName) {
        mId = UUID.randomUUID();
        mCharacterPicId = characterPicId;
        mName = name;
        mNickName = nickName;
        mProgramPicId = programPicId;
        mProgramName = programName;
    }

    public UUID getId() {
        return mId;
    }

    public int getCharacterPicId() {
        return mCharacterPicId;
    }

    public String getName() {
        return mName;
    }

    public String getNickName() {
        return mNickName;
    }

    public int getProgramPicId() {
        return mProgramPicId;
    }

    public String getProgramName() {
        return mProgramName;
    }
}
