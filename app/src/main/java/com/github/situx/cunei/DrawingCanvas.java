package com.github.situx.cunei;

import android.content.Context;
import android.graphics.*;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Canvas for drawing lines that indicate cuneiform characters.
 */
public class DrawingCanvas extends View {
        Integer a=0,b=0,c=0,d=0,s=0,left=0,right=0,up=0,down=0;
        public List<LineParameters> lines= new LinkedList<>();
        public List<String> strokes= new LinkedList<>();
        private Canvas mCanvas;
        String paleocodage="";
        public int width;
        public int height;
        private float startX;
        private float startY;
    private Path mPath;
        private CuneiPainter context;
        private Paint mPaint;
        private float mX, mY;
        private static final float TOLERANCE = 5;
        private List<Path> paths;
        private List<Path> undonepaths;

    /**
     * Constructor for this class
     * @param c the context of this canvas
     * @param attrs the attributes to consider
     */
        public DrawingCanvas(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.paths=new ArrayList<>();
            this.undonepaths=new ArrayList<>();
            context = (CuneiPainter)c;

            // we set a new Path
            mPath = new Path();

            // and we set a new Paint with the desired attributes
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.RED);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(20);
            this.mCanvas=new Canvas();
        }

        // override onSizeChanged
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            // your Canvas will draw onto the defined Bitmap
            Bitmap mBitmap = Bitmap.createBitmap(w <= 0 ? 1 : w, h <= 0 ? 1 : h, Bitmap.Config.ARGB_8888);
            this.mCanvas = new Canvas(mBitmap);
        }

        // override onDraw
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // draw the mPath with the mPaint on the canvas when onDraw
            for(Path p:this.paths){
                canvas.drawPath(p,mPaint);
            }
        }

        // when ACTION_DOWN start touch according to the x,y values
        private void startTouch(float x, float y) {
            undonepaths.clear();
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
            startX=x;
            startY=y;
        }

        public void undoPath(){
            if(!this.paths.isEmpty()) {
                undonepaths.add(this.paths.remove(this.paths.size() - 1));
                invalidate();
            }
            s--;
            switch(strokes.get(strokes.size()-1)){
                case "a":--a;break;
                case "b":--b;break;
                case "c":--c;break;
                case "d":--d;break;
            }
            strokes.remove(strokes.size()-1);
            this.context.setStatusText();
            this.context.lookUp();
        }

        public void redoPath(){
            if(!this.undonepaths.isEmpty()) {
                paths.add(this.paths.remove(this.paths.size() - 1));
                invalidate();
            }
            s++;
        }

        // when ACTION_MOVE move touch according to the x,y values
        private void moveTouch(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOLERANCE || dy >= TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }

    /**
     * Clears the canvas.
     */
    void clearCanvas() {
            for(Path p:this.paths){
                p.reset();
            }
            this.paths.clear();
            invalidate();
        }

        // when ACTION_UP stop touch
        private void upTouch() {
            mPath.lineTo(mX, mY);
            //mPath.moveTo(startX, startY);
            float delta_x = mX - startX;
            float delta_y = mY - startY;

            //

            double m=delta_y/delta_x;
            double radius = Math.atan(m)*100;
            System.out.println("Angle: "+radius);
            ++s;
            if(radius>140 && radius<200){
                System.out.println("Detected: A");
                ++a;
                strokes.add("a");
                if(startY>mY){
                    up++;
                    paleocodage+="!a-";
                    this.lines.add(new LineParameters(startX,startY,mX,mY,delta_x,delta_y,StrokeType.INV_A));
                }else{
                    down++;
                    paleocodage+="a-";
                    this.lines.add(new LineParameters(startX,startY,mX,mY,delta_x,delta_y,StrokeType.A));
                }
                this.context.setStatusText();
            }else if(radius>-30 && radius<30){
                System.out.println("Detected: B");
                ++b;
                strokes.add("b");
                if(startX>mX){
                    left++;
                    paleocodage+="!b-";
                    /*mPath.moveTo(startX+40, startY+50);
                    mPath.lineTo(startX+40, startY+50);
                    mPath.lineTo(startX, startY+45);
                    mPath.lineTo(startX+40, startY+50);*/
                    this.lines.add(new LineParameters(startX,startY,mX,mY,delta_x,delta_y,StrokeType.INV_B));
                }else{
                    paleocodage+="b-";
                    right++;
                    /*mPath.moveTo(startX-40, startY+50);
                    mPath.lineTo(startX-40, startY+45);
                    mPath.lineTo(startX, startY+45);
                    mPath.lineTo(startX+40, startY+40);*/
                    this.lines.add(new LineParameters(startX,startY,mX,mY,delta_x,delta_y,StrokeType.B));
                }
                this.context.setStatusText();
            }else if(radius<-30 && radius>-170){
                System.out.println("Detected: D");
                ++d;
                strokes.add("d");
                if(startY>mY){
                    up++;
                    paleocodage+="c-";
                    /*mPath.moveTo(startX+50, startY+50);
                    mPath.lineTo(startX+50, startY);
                    mPath.lineTo(startX, startY+50);
                    mPath.lineTo(startX+50, startY+50);*/
                    this.lines.add(new LineParameters(startX,startY,mX,mY,delta_x,delta_y,StrokeType.C));
                }else{
                    down++;
                    paleocodage+="d-";
                    this.lines.add(new LineParameters(startX,startY,mX,mY,delta_x,delta_y,StrokeType.D));
                    /*mPath.moveTo(startX, startY);
                    mPath.lineTo(startX, startY+50);
                    mPath.lineTo(startX-50, startY);
                    mPath.lineTo(startX, startY);*/
                }
                this.context.setStatusText();
            }else if(radius>30 && radius<150){
                System.out.println("Detected: C");
                ++c;
                strokes.add("c");
                if(startY>mY){
                    paleocodage+="e-";
                    this.lines.add(new LineParameters(startX,startY,mX,mY,delta_x,delta_y,StrokeType.E));
                    up++;
                }else{
                    down++;
                    paleocodage+="f-";
                    this.lines.add(new LineParameters(startX,startY,mX,mY,delta_x,delta_y,StrokeType.F));
                }
                this.context.setStatusText();

            }
            System.out.println(LocationCalc.calculateRelation(lines,lines.get(lines.size()-1)));
            this.context.lookUp();
            this.mCanvas.drawPath(mPath,mPaint);
            paths.add(mPath);
            mPath=new Path();
        }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        super.setOnLongClickListener(l);
        this.clearCanvas();
    }

    //override the onTouchEvent
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startTouch(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveTouch(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    upTouch();
                    invalidate();
                    break;
            }
            return true;
        }
}
