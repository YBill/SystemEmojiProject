package com.example.bill.systememojiproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView emojiText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emojiText = (TextView) findViewById(R.id.text);

        int[] codes = getResources().getIntArray(R.array.emoji_code);
        for (int code : codes) {
            emojiText.append(getEmojiStringByUnicode(code));
        }
    }

    private String getEmojiStringByUnicode(int unicodeJoy) {
        return new String(Character.toChars(unicodeJoy));
    }

}
