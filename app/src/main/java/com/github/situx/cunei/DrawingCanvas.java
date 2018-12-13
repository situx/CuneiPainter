package com.github.situx.cunei;

    import android.content.Context;
    import android.graphics.Bitmap;
    import android.graphics.Canvas;
    import android.graphics.Color;
    import android.graphics.Paint;
    import android.graphics.Path;
    import android.util.AttributeSet;
    import android.view.MotionEvent;
    import android.view.View;

/**
 * Canvas for drawing lines that indicate cuneiform characters.
 */
public class DrawingCanvas extends View {
        Integer a=0,b=0,c=0,d=0,s=0;
        public int width;
        public int height;
        private float startX;
        private float startY;
    private Path mPath;
        private CuneiPainter context;
        private Paint mPaint;
        private float mX, mY;
        private static final float TOLERANCE = 5;

    /**
     * Constructor for this class
     * @param c the context of this canvas
     * @param attrs the attributes to consider
     */
        public DrawingCanvas(Context c, AttributeSet attrs) {
            super(c, attrs);
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
        }

        // override onSizeChanged
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            // your Canvas will draw onto the defined Bitmap
            Bitmap mBitmap = Bitmap.createBitmap(w <= 0 ? 1 : w, h <= 0 ? 1 : h, Bitmap.Config.ARGB_8888);
            Canvas mCanvas = new Canvas(mBitmap);
        }

        // override onDraw
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // draw the mPath with the mPaint on the canvas when onDraw
            canvas.drawPath(mPath, mPaint);
        }

        // when ACTION_DOWN start touch according to the x,y values
        private void startTouch(float x, float y) {
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
            startX=x;
            startY=y;
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
            mPath.reset();
            invalidate();
        }

        // when ACTION_UP stop touch
        private void upTouch() {
            mPath.lineTo(mX, mY);
            double delta_x = mX - startX;
            double delta_y = mY - startY;
            double m=delta_y/delta_x;
            double radius = Math.atan(m)*100;
            System.out.println("Angle: "+radius);
            ++s;
            if(radius>140 && radius<200){
                System.out.println("Detected: A");
                ++a;
                this.context.setStatusText();
            }else if(radius>-30 && radius<30){
                System.out.println("Detected: B");
                ++b;
                this.context.setStatusText();
            }else if(radius<-30 && radius>-170){
                System.out.println("Detected: D");
                ++d;
                this.context.setStatusText();
            }else if(radius>30 && radius<150){
                System.out.println("Detected: C");
                ++c;
                this.context.setStatusText();
            }
            this.context.lookUp();
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
