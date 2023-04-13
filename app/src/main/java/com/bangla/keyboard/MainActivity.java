package com.bangla.keyboard;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    public static int tag=1;
    static boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


//        textView = (TextView) findViewById(R.id.textView1);
//
//        textView.setMovementMethod(new ScrollingMovementMethod());

        String data = "";

        StringBuffer sBuffer = new StringBuffer();
        InputStream is = this.getResources().openRawResource(R.raw.test1);

        BufferedReader bufferedReader =  new BufferedReader(new InputStreamReader(is));

        if(is != null) {
            try {
//                while( (data = bufferedReader.readLine() ) != null ) {
//                    String[] sArr = data.split(" ");
//                    for (String sa: sArr ) {
////                        System.out.println(sa);
//                        sBuffer.append(sa + " ");
//                    }
//                    sBuffer.append(data + "\n");
//                }
//                textView.setText(sBuffer);
//                textView.setText("ggwp");
                is.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> settings<<<<<<<<<<<<<<");
//            startActivityForResult(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    public void tensorflowTest(View view){
        startActivity(new Intent(this, TensorflowTestActivity.class));
    }

    public void enableKeyboard(View view){
        startActivityForResult(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
    }

    public void chooseKeyboard(View view) {
//       startActivityForResult(new Intent(Settings.ENABLED_INPUT_METHODS               ), 0);
        InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
        imeManager.showInputMethodPicker();
    }
    public void edittextKeyboard(View view){

        Intent intent = new Intent(this, textActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void tutorialKeyboard(View view){

        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void themeChange(View view){

        Intent intent = new Intent(this, customize.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        SharedPreferences.Editor editor = getSharedPreferences("test",MODE_PRIVATE).edit();
//
//        SharedPreferences prefs = getSharedPreferences("test", MODE_PRIVATE);
//        tag = prefs.getInt("theme",0);
//
//        final Dialog dialog=new Dialog(this);
////        final Dialog  dialog = new Dialog(this,R.style.Dialog);
//
////        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
//
//        Window window = dialog.getWindow();
//        //window.setLayout(300,300);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setTitle("থিম নির্বাচন");
//        dialog.setContentView(R.layout.themedialog);
//
//        //  dialog.setCancelable(true);
//        dialog.show();
//
//
//        Button button = (Button) dialog.findViewById(R.id.ok);
//        final RadioButton drakButton,lightButton;
//        drakButton=(RadioButton)dialog.findViewById(R.id.drakButton);
//        lightButton=(RadioButton)dialog.findViewById(R.id.lightButton);
//        if(tag == 0) {
//            drakButton.setChecked(true);
//            CustomKeyboard.selectedTheme=0;
//        }
//        else  {
//            lightButton.setChecked(true);
//            CustomKeyboard.selectedTheme=1;
//        }
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(drakButton.isChecked())
//                {
//                    SharedPreferences.Editor editor = getSharedPreferences("test",MODE_PRIVATE).edit();
//                    editor.putInt("theme",0);
//                    editor.commit();
//                    CustomKeyboard.selectedTheme=0;
//                    CandidateView.themeColor=R.color.kalo;
//                    tag=0;
//                }
//                else if(lightButton.isChecked())
//                {
//                    SharedPreferences.Editor editor = getSharedPreferences("test",MODE_PRIVATE).edit();
//                    editor.putInt("theme",1);
//                    editor.commit();
//                    CustomKeyboard.selectedTheme=1;
//                    CandidateView.themeColor=R.color.shada;
//                    tag=1;
//                }
//                dialog.dismiss();
//            }
//        });
//
//
//        Button cancel = (Button) dialog.findViewById(R.id.cancel);
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                dialog.dismiss();
//            }
//        });


    }

}
