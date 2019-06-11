package com.example.advancedprogramming4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.MotionEventCompat;

import java.lang.Math;

public class Joystick extends View {
    private boolean firstTime;
    private Paint outerCirclePaint;
    private Paint innerCirclePaint;
    private boolean isPlayerMoving;
    private Point origin;
    private float outerRadius;
    private float innerRadius;
    private Point joystickPos;
    private Paint backgroundPaint;
    private TcpClient mTcpClient;
    public Joystick(Context context,TcpClient client) {
        super(context);
        outerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerCirclePaint.setColor(Color.GRAY);
        outerCirclePaint.setStyle(Paint.Style.FILL);
        innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerCirclePaint.setColor(Color.GREEN);
        innerCirclePaint.setStyle(Paint.Style.FILL);
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.BLUE);
        backgroundPaint.setStyle(Paint.Style.FILL);
        firstTime=false;
        mTcpClient = client;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
// Account for padding

    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPaint(backgroundPaint);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        outerRadius = (float) 0.45* Math.min(width,height);
        innerRadius = (float) 0.1* Math.min(width,height);
        origin = new Point(width/2,height/2);
        if(!firstTime){
            joystickPos = new Point(width/2,height/2);
            firstTime=true;
        }
        canvas.drawCircle(width/2,height/2,outerRadius,outerCirclePaint);
        canvas.drawCircle(joystickPos.x,joystickPos.y,innerRadius,innerCirclePaint);
    }
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                int pointerIndex = MotionEventCompat.getActionIndex(event);
                float x = event.getX();
                float y = event.getY();
                Point pos = new Point((int)x, (int)y);
                if (touchPosInsideCircle(pos,(int)innerRadius))
                    isPlayerMoving = true;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!isPlayerMoving)
                    return true;
                float x = event.getX();
                float y = event.getY();
                Point pos = new Point((int)x, (int)y);
                if(touchPosInsideCircle(pos,(int)outerRadius)){
                    joystickPos = pos;
                    invalidate();
                }
                else
                    isPlayerMoving = false;

                break;
            }
            case MotionEvent.ACTION_UP:{
                joystickPos = origin;
                invalidate();
                isPlayerMoving = false;
                break;
            }
            case MotionEvent.ACTION_CANCEL:
                isPlayerMoving = false;
                break;
        }
        if(isPlayerMoving){
            if (mTcpClient != null) {
                float aileron =( joystickPos.x - origin.x)/outerRadius;
                float elevator =-( joystickPos.y - origin.y)/outerRadius;
                mTcpClient.sendMessage("set aileron " + aileron);
                mTcpClient.sendMessage("set elevator " + elevator);
            }
        }
        return true;
}
        private boolean touchPosInsideCircle(Point pos,int radius){
            return distance(origin,pos)<radius;
        }
        private double distance(Point p1,Point p2){
            return Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y));
        }



}



