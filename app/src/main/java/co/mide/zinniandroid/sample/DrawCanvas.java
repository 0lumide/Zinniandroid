package co.mide.zinniandroid.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;


public class DrawCanvas extends View {
    private Paint paint;
    private ArrayList<Path> strokePaths = new ArrayList<>();
    private ArrayList<Stroke> strokes = new ArrayList<>();
    private Path path;
    private int strokeCount = -1;
    private Bitmap viewCache;
    private final float STROKE_WIDTH = 17;
    private StrokeCallback strokeCallback;
    private int count = 1;

    private void init() {
        paint = new Paint();
        paint.setColor(Color.parseColor("#EE010101"));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
    }

    public DrawCanvas(Context context) {
        super(context);
        init();
    }

    public DrawCanvas(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DrawCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void onMeasure(int measuredWidth, int measuredHeight) {
        super.onMeasure(measuredWidth, measuredHeight);
        int dimension;
        Log.e("onMeasure"+count++, String.format("Width: %s: %d: %d\tHeight: %s: %d: %d",
                        MeasureSpec.toString(MeasureSpec.getMode(measuredWidth)), getMeasuredWidth(), getWidth(),
                        MeasureSpec.toString(MeasureSpec.getMode(measuredHeight)), getMeasuredHeight(), getHeight())
        );
        if ((getWidth() == 0) && (getHeight() == 0)){
            dimension = MeasureSpec.makeMeasureSpec(Math.min(getMeasuredWidth(), getMeasuredHeight()), MeasureSpec.EXACTLY);
            setMeasuredDimension(dimension, dimension);
        }else{
            dimension = MeasureSpec.makeMeasureSpec(Math.min(getWidth(), getHeight()), MeasureSpec.EXACTLY);
            setMeasuredDimension(dimension, dimension);
        }
    }

    public Stroke getStroke(int strokeNum){
        return strokes.get(strokeNum);
    }

    public void undoStroke(){
        if(strokeCount >= 0) {
            strokeCount--;
            strokePaths.remove(strokePaths.size() - 1);
            strokes.remove(strokes.size() - 1);
            viewCache = null;
            invalidate();
            if(strokeCallback != null)
                strokeCallback.onStrokeCountChange(strokeCount+1);
        }
    }

    public void resetCanvas(){
        strokePaths = new ArrayList<>();
        strokes = new ArrayList<>();
        strokeCount = -1;
        viewCache = null;
        invalidate();
    }

    public void registerStrokeCallback(StrokeCallback s){
        if(strokeCallback == null)
            this.strokeCallback = s;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(viewCache != null)
            canvas.drawBitmap(viewCache, 0, 0, null);
        //draw strokePath
        if(strokeCount >= 0) {
            for(int i = 0; i< strokePaths.size(); i++) {
                if(viewCache != null)
                    i = strokeCount;
                path = strokePaths.get(i);
                canvas.drawPath(path, paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean returnValue = false;
        if ((event.getAction() == MotionEvent.ACTION_MOVE) || (event.getAction() == MotionEvent.ACTION_DOWN)) {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                setDrawingCacheEnabled(true);
                buildDrawingCache(true);
                viewCache = Bitmap.createBitmap(getDrawingCache());
                setDrawingCacheEnabled(false);
                strokeCount++;
                strokePaths.add(new Path());
                strokes.add(new Stroke());
            }
            Stroke stroke = strokes.get(strokes.size() - 1);
            Path strokePath = strokePaths.get(strokes.size() - 1);
            if(event.getAction() == MotionEvent.ACTION_MOVE){
                final int historySize = event.getHistorySize();
                for (int h = 0; h < historySize; h++) {
                    strokePath.lineTo(event.getHistoricalX(h), event.getHistoricalY(h));
                    stroke.addPoint((int) event.getHistoricalX(h),
                            (int) event.getHistoricalY(h));
                }
                strokePath.lineTo(event.getX(), event.getY());
                stroke.addPoint((int) event.getX(), (int) event.getY());
            } else {
                if(stroke.getSize() > 0) {
                    strokePath.lineTo(event.getX(), event.getY());
                    stroke.addPoint((int) event.getX(), (int) event.getY());
                }else{
                    stroke.addPoint((int)event.getX(), (int)event.getY());
                    strokePath.moveTo(event.getX(), event.getY());
                }
            }
            invalidate();
            returnValue = true;
        }else{
            if((event.getAction() == MotionEvent.ACTION_UP)||(event.getAction() == MotionEvent.ACTION_CANCEL)){
                if (strokeCallback != null)
                    strokeCallback.onStrokeCountChange(strokeCount + 1);
            }
        }
        return returnValue;
    }


    public class Stroke{
        private ArrayList<Point> strokes = new ArrayList<>();

        public void addPoint(int x, int y){
            strokes.add(new Point(x, y));
        }

        public Point getPoint(int index){
            return strokes.get(index);
        }

        public int getSize(){
            return strokes.size();
        }
    }

    public interface StrokeCallback {
        void onStrokeCountChange(int strokeCount);
    }
}