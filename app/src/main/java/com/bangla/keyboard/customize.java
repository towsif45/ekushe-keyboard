package com.bangla.keyboard;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class customize extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    ToggleButton soundButton,vibrateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);


        soundButton=(ToggleButton)findViewById(R.id.button_sound);
        vibrateButton=(ToggleButton)findViewById(R.id.button_vibrate);

        SharedPreferences prefs = getSharedPreferences("test", MODE_PRIVATE);
        MainActivity.tag = prefs.getInt("theme",0);

        final Button darkButton = findViewById(R.id.button);
        final Button whiteButton = findViewById(R.id.button3);

        if(MainActivity.tag==0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                darkButton.setBackground(getDrawable(R.drawable.black_theme_selected));
            }
        }else if(MainActivity.tag==1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                whiteButton.setBackground(getDrawable(R.drawable.white_theme_selected));
            }
        }

       // soundButton.setChecked(true);

        int sound = prefs.getInt("Sound on Tap",0);
        int vibrate = prefs.getInt("Vibrate on Tap",0);

        System.out.println("sound>>>>>>>>>>>> in custom  "+sound);
        if(sound==1)
            soundButton.setChecked(true);
        else
            soundButton.setChecked(false);

        if(vibrate==1)
            vibrateButton.setChecked(true);
        else
            vibrateButton.setChecked(false);


        soundButton.setOnCheckedChangeListener(this);
        vibrateButton.setOnCheckedChangeListener(this);

        darkButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("test",MODE_PRIVATE).edit();
                editor.putInt("theme",0);
                editor.commit();
                CustomKeyboard.selectedTheme=0;
                CandidateView.themeColor=R.color.kalo;
                MainActivity.tag=0;
                darkButton.setBackground(getDrawable(R.drawable.black_theme_selected));
                whiteButton.setBackground(getDrawable(R.drawable.white_theme));
            }
        });

        whiteButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("test",MODE_PRIVATE).edit();
                editor.putInt("theme",1);
                editor.commit();
                CustomKeyboard.selectedTheme=1;
                CandidateView.themeColor=R.color.shada;
                MainActivity.tag=1;
                darkButton.setBackground(getDrawable(R.drawable.black_theme));
                whiteButton.setBackground(getDrawable(R.drawable.white_theme_selected));
            }
        });

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(soundButton.isChecked() == true)
        {
            SharedPreferences.Editor editor = getSharedPreferences("test",MODE_PRIVATE).edit();
            editor.putInt("Sound on Tap",1);
            editor.commit();
        }
        else
        {
            SharedPreferences.Editor editor = getSharedPreferences("test",MODE_PRIVATE).edit();
            editor.putInt("Sound on Tap",0);
            editor.commit();
        }

        if(vibrateButton.isChecked() == true)
        {
            SharedPreferences.Editor editor = getSharedPreferences("test",MODE_PRIVATE).edit();
            editor.putInt("Vibrate on Tap",1);
            editor.commit();
        }
        else
        {
            SharedPreferences.Editor editor = getSharedPreferences("test",MODE_PRIVATE).edit();
            editor.putInt("Vibrate on Tap",0);
            editor.commit();
        }
    }
}
