package com.bangla.keyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.bangla.keyboard.R;

import java.util.List;

/**
 * Created by Gautam on 11/26/2017.
 */

public class CustomKeyboardView extends KeyboardView {

    Context mcontext;
    private Paint paint;
    private Path path;
    boolean flag;
    public boolean ff = true;
    public boolean gesture_color = true;
    private boolean isGesture_color = true;


    public CustomKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mcontext = context;

        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        if(gesture_color)
            this.paint.setColor(getResources().getColor(R.color.gesture));
        else
            this.paint.setColor(getResources().getColor(R.color.transparant));

        isGesture_color = gesture_color;
        this.paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(dpToPixel(10,mcontext));
        flag = false;

        this.path = new Path();
    }

    int dpToPixel(float dp,Context context){

        float scale = context.getResources().getDisplayMetrics().density;
        int pixel = (int) (dp * scale + 0.5f);
        return pixel;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(gesture_color != isGesture_color){
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>on draw");
            if(gesture_color)
                this.paint.setColor(getResources().getColor(R.color.gesture));
            else
                this.paint.setColor(getResources().getColor(R.color.transparant));

            isGesture_color = gesture_color;
        }


        Paint paint1 = new Paint();
        paint1.setTextSize(dpToPixel(10,mcontext));
        paint1.setColor(Color.GRAY);

        Paint paint2 = new Paint();
        paint2.setTextSize(dpToPixel(15,mcontext));
        paint2.setColor(Color.GRAY);


        List<Keyboard.Key> keys = getKeyboard().getKeys();

        int flagg = 0;
        for(Keyboard.Key key : keys){
            if(key.codes[0] == 46){
                flagg = 2;
                break;
            }
            if(key.codes[0] == 2470){
                flagg = 3;
                break;
            }
            if(key.codes[0] == 2466){
                flagg = 4;
                break;
            }
            if(key.codes[0] == 2551){
                flagg = 1;
                break;
            }
        }

        int padding_x=5,padding_y=15;

        if(flagg == 2) {
            for (Keyboard.Key key : keys) {
                if (key.codes[0] == 113) {
                    canvas.drawText("1", key.x + (key.width / 2) + dpToPixel(padding_x, mcontext), key.y + dpToPixel(padding_y, mcontext), paint1);
                }else if (key.codes[0] == 119)
                    canvas.drawText("2", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 101)
                    canvas.drawText("3", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 114)
                    canvas.drawText("4", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 116)
                    canvas.drawText("5", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 121)
                    canvas.drawText("6", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 117)
                    canvas.drawText("7", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 105)
                    canvas.drawText("8", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 111)
                    canvas.drawText("9", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 112)
                    canvas.drawText("0", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 46)
                    canvas.drawText(" ", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint2);
                else if (key.codes[0] == 49)
                    canvas.drawText("১", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 50)
                    canvas.drawText("২", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 51)
                    canvas.drawText("৩", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 52)
                    canvas.drawText("৪", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 53)
                    canvas.drawText("৫", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 54)
                    canvas.drawText("৬", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 55)
                    canvas.drawText("৭", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 56)
                    canvas.drawText("৮", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 57)
                    canvas.drawText("৯", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 48)
                    canvas.drawText("০", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2535)
                    canvas.drawText("1", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2536)
                    canvas.drawText("2", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2537)
                    canvas.drawText("3", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2538)
                    canvas.drawText("4", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2539)
                    canvas.drawText("5", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2540)
                    canvas.drawText("6", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2541)
                    canvas.drawText("7", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2542)
                    canvas.drawText("8", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2543)
                    canvas.drawText("9", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2534)
                    canvas.drawText("0", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == -105)
                    canvas.drawText("প্রভাত", key.x + (key.width/6), key.y + dpToPixel(padding_y,mcontext), paint1);
            }
        }
        else if(flagg == 3){
            for (Keyboard.Key key : keys) {
                if (key.codes[0] == -105)
                    canvas.drawText("EN", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2551)
                    canvas.drawText(" ", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint2);
            }
        }
        else if(flagg == 4){
            for (Keyboard.Key key : keys) {
                if (key.codes[0] == -105)
                    canvas.drawText("EN", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2551)
                    canvas.drawText(" ", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint2);
            }
        }
        else if(flagg == 1) {
            for (Keyboard.Key key : keys) {
                if (key.codes[0] == 49)
                    canvas.drawText("১", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 50)
                    canvas.drawText("২", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 51)
                    canvas.drawText("৩", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 52)
                    canvas.drawText("৪", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 53)
                    canvas.drawText("৫", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 54)
                    canvas.drawText("৬", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 55)
                    canvas.drawText("৭", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 56)
                    canvas.drawText("৮", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 57)
                    canvas.drawText("৯", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 48)
                    canvas.drawText("০", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 113)
                    canvas.drawText("১", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 119)
                    canvas.drawText("২", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 101)
                    canvas.drawText("৩", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 114)
                    canvas.drawText("৪", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 116)
                    canvas.drawText("৫", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 121)
                    canvas.drawText("৬", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 117)
                    canvas.drawText("৭", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 105)
                    canvas.drawText("৮", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 111)
                    canvas.drawText("৯", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 112)
                    canvas.drawText("০", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2551)
                    canvas.drawText(" ", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint2);
                else if (key.codes[0] == 2535)
                    canvas.drawText("1", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2536)
                    canvas.drawText("2", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2537)
                    canvas.drawText("3", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2538)
                    canvas.drawText("4", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2539)
                    canvas.drawText("5", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2540)
                    canvas.drawText("6", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2541)
                    canvas.drawText("7", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2542)
                    canvas.drawText("8", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2543)
                    canvas.drawText("9", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == 2534)
                    canvas.drawText("0", key.x + (key.width / 2) + dpToPixel(padding_x,mcontext), key.y + dpToPixel(padding_y,mcontext), paint1);
                else if (key.codes[0] == -105)
                    canvas.drawText("প্রভাত", key.x + (key.width / 6), key.y + dpToPixel(padding_y,mcontext), paint1);
            }
        }

        canvas.drawPath(path, paint);

        if(flag==true){
            path.reset();
            invalidate();
//            path.moveTo(100,100);
//            path.lineTo(1,1);
//            this.paint.setColor(Color.RED);
//            canvas.drawPath(path, paint);
            System.out.println(">>>zzzzzzzzzzz >>>>>>>>");
            flag=false;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        super.onTouchEvent(me);



        float eventX = me.getX();
        float eventY = me.getY();

        switch (me.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.reset();
                path.moveTo(eventX, eventY);
                System.out.println(">>>down >>>>>>>> "+eventX + ">>> "+eventY);
                flag = false;
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(eventX, eventY);
                if(ff == false){
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                path.lineTo(eventX, eventY);
                System.out.println(">>> UP >>>>>>>>");
                flag = true;
                ff = true;
                break;
            default:
                return false;
        }

        // Schedules a repaint.
        invalidate();
        return true;
    }

    @Override
    protected boolean onLongPress(Keyboard.Key popupKey) {
        return super.onLongPress(popupKey);
    }
}
