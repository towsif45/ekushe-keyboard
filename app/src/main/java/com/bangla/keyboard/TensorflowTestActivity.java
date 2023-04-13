package com.bangla.keyboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TensorflowTestActivity extends AppCompatActivity {

    TextView textView;
    Button button;
    EditText editText;

    Interpreter interpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tensorflow_test);

        try {
            interpreter = new Interpreter(loadModelFile(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        textView = findViewById(R.id.show_result);
        button = findViewById(R.id.predict);
        editText = findViewById(R.id.input);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editText.getText().toString();
                float x = doInference(input);

                textView.setText(Float.toString(x));
            }
        });
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("ModelLSTM.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = assetFileDescriptor.getStartOffset();
        long length = assetFileDescriptor.getLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length);
    }

    public float doInference(String input){
        float[][] input1=new float[1][8];
        input1[0][0]= (float) 0.0;
        input1[0][1]= (float) 0.0;
        input1[0][2]= (float) 0.0;
        input1[0][3]= (float) 0.0;
        input1[0][4]= (float) 0.0;
        input1[0][5]= (float) 0.0;
        input1[0][6]= (float) 0.0;
        input1[0][7]= (float) 1.0;
        System.out.println(input1[0][0]);
        System.out.println(input1[0][7]);

        float[][] output=new float[1][3];
        interpreter.run(input1, output);

        System.out.println(output[0][0]);
        System.out.println(output[0][1]);
        System.out.println(output[0][2]);
        return output[0][0];

    }
}