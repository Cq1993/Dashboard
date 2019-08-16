package cq.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Chenqi
 * <p>
 * date 2019-08-16 11:03
 * description DashBoardView
 */
public class DashBoardView extends View {
    /**
     * 屏幕密度
     */
    private float mDensity;

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 需要绘制的总数
     */
    int mTotalDegrees = 0;

    /**
     * 仪表盘标题
     */
    String mTitle = "当前数值";

    /**
     * 仪表盘最大数值
     */
    int mMaxValue = 10000;

    /**
     * 按钮文字
     */
    String mBtnText = "测试";

    /**
     * 按钮绘制范围
     */
    RectF mBtnRectf;

    /**
     * 绘制进度
     */
    private int mDrawDegrees = 0;

    public DashBoardView(Context context) {
        super(context);
        init();
    }

    public DashBoardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public DashBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int deltaX, deltaY;
        int min = Math.min(width, height);
        deltaX = (width - min) / 2 + 6;
        deltaY = (height - min) / 2 + 6;
        mPaint.setStyle(Paint.Style.STROKE);

        //绘制表盘底色
        float deltaX0 = deltaX + min / 50 * mDensity + 0.5f;
        float deltaY0 = deltaY + min / 50 * mDensity + 0.5f;
        mPaint.setStrokeWidth(height > width ? 2 * (deltaX0 - 6) : 2 * (deltaY0 - 6));
        mPaint.setColor(Color.rgb(50, 166, 210));
        RectF rectF3 = new RectF(deltaX0, deltaY0, width - deltaX0, height - deltaY0);
        canvas.drawArc(rectF3, -210f, 240f, false, mPaint);

        //绘制最外面一条弧线
        mPaint.setStrokeWidth(3);
        RectF rectF1 = new RectF(deltaX - 3f, deltaY - 3f, width - deltaX + 3f, height - deltaY + 3f);
        canvas.drawArc(rectF1, -210f, 240f, false, mPaint);

        //绘制最里面一条弧线
        deltaX += min / 25 * mDensity + 0.5f;
        deltaY += min / 25 * mDensity + 0.5f;
        RectF rectF2 = new RectF(deltaX, deltaY, width - deltaX, height - deltaY);
        canvas.drawArc(rectF2, -210f, 240f, false, mPaint);

        /*
        绘制进度由于颜色差值，总共240个刻度，所以分240步完成（当值最大时绘制充满240个刻度）
        一个刻度＝2.4度
        初始颜色：r=76,g=110,b=248
        最后颜色：r=209,g=142,b=252
         */
        int startR = 76, startG = 110, startB = 248;
        int endR = 209, endG = 142, endB = 252;
        int tempR = endR - startR;
        int tempG = endG - startG;
        int tempB = endB - startB;
        for (int i = 0; i < mDrawDegrees; i++) {
            int r = startR + tempR * i / mTotalDegrees;
            int g = startG + tempG * i / mTotalDegrees;
            int b = startB + tempB * i / mTotalDegrees;
            //绘制进度条
            mPaint.setStrokeWidth(Math.min(deltaX, deltaY) - 3f);
            mPaint.setColor(Color.rgb(r, g, b));
            canvas.drawArc(rectF3, -210f + i * 2.4f, 2.4f, false, mPaint);
            //绘制进度白线
            mPaint.setStrokeWidth(5f);
            mPaint.setColor(Color.WHITE);
            canvas.drawArc(rectF2, -210f + i * 2.4f, 2.4f, false, mPaint);
        }

        //绘制进度圆点
        float x, y;
        float angle = mDrawDegrees * 2.4f - 210;
        if (width > height) {
            x = width / 2 + (min / 2 - deltaY) * (float) Math.cos(angle * Math.PI / 180);
            y = min / 2 + (min / 2 - deltaY) * (float) Math.sin(angle * Math.PI / 180);
        } else {
            x = min / 2 + (min / 2 - deltaX) * (float) Math.cos(angle * Math.PI / 180);
            y = height / 2 + (min / 2 - deltaX) * (float) Math.sin(angle * Math.PI / 180);
        }
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, 8, mPaint);

        //绘制刻度
        mPaint.setColor(Color.rgb(255, 255, 255));
        int count = -1;
        int value = 0;
        int graduationLength;
        for (float i = -210f; i < 30f; i += 2.4f) {
            count++;
            if (count % 10 == 0) {
                mPaint.setStrokeWidth(4);
                graduationLength = min / 10;
            } else {
                mPaint.setStrokeWidth(2);
                graduationLength = min / 15;
            }
            if (width >= height) {
                float x1 = width / 2 + (min / 2 - deltaY) * (float) Math.cos(i * Math.PI / 180);
                float y1 = min / 2 + (min / 2 - deltaY) * (float) Math.sin(i * Math.PI / 180);
                float x2 = width / 2 + (min / 2 - deltaY + graduationLength) * (float) (Math.cos(i * Math.PI / 180));
                float y2 = min / 2 + (min / 2 - deltaY + graduationLength) * (float) (Math.sin(i * Math.PI / 180));
                canvas.drawLine(x1, y1, x2, y2, mPaint);
                //绘制刻度值
                if (count % 20 == 0) {
                    Rect textBounds = new Rect();
                    mPaint.getTextBounds(value + "", 0, (value + "").length(), textBounds);
                    mPaint.setTextSize(20);
                    canvas.rotate(i + 90, x1, y1);
                    canvas.drawText(value == 0 ? "\t\t\t" + value : value + "", x1 - 25, y1 + 25, mPaint);
                    canvas.rotate(-i - 90, x1, y1);
                    value += 2000;
                }
            } else {
                float x1 = min / 2 + (min / 2 - deltaX) * (float) Math.cos(i * Math.PI / 180);
                float y1 = height / 2 + (min / 2 - deltaX) * (float) Math.sin(i * Math.PI / 180);
                float x2 = min / 2 + (min / 2 - deltaX + graduationLength) * (float) (Math.cos(i * Math.PI / 180));
                float y2 = height / 2 + (min / 2 - deltaX + graduationLength) * (float) (Math.sin(i * Math.PI / 180));
                canvas.drawLine(x1, y1, x2, y2, mPaint);
                //绘制刻度值
                if (count % 20 == 0) {
                    Rect textBounds = new Rect();
                    mPaint.getTextBounds(value + "", 0, (value + "").length(), textBounds);
                    mPaint.setTextSize(20);
                    canvas.rotate(i + 90, x1, y1);
                    canvas.drawText(value == 0 ? "\t\t\t" + value : value + "", x1 - 25, y1 + 25, mPaint);
                    canvas.rotate(-i - 90, x1, y1);
                    value += 2000;
                }
            }
        }

        //绘制标题
        mPaint.setTextSize(min / 15);
        Rect titleBounds = new Rect();
        mPaint.getTextBounds(mTitle, 0, mTitle.length(), titleBounds);
        canvas.drawText(mTitle, 0, mTitle.length(), width / 2 - titleBounds.width() / 2, getHeight() / 2 - titleBounds.height() * 2.5f, mPaint);

        //绘制当前值
        String currentValue = mDrawDegrees * mMaxValue / 100 + "";
        mPaint.setTextSize(min / 7);
        Rect valueBounds = new Rect();
        mPaint.getTextBounds(currentValue, 0, currentValue.length(), valueBounds);
        canvas.drawText(currentValue, 0, currentValue.length(), width / 2 - valueBounds.width() / 2, getHeight() / 2, mPaint);

        /*
         绘制按钮
         1.绘制文字
         2.绘制边框
         */
        mPaint.setTextSize(min / 15);
        Rect btnBounds = new Rect();
        mPaint.getTextBounds(mBtnText, 0, mBtnText.length(), btnBounds);
        canvas.drawText(mBtnText, 0, mBtnText.length(), width / 2 - btnBounds.width() / 2, getHeight() / 2 + 2 * btnBounds.height(), mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        float left = width / 2 - btnBounds.width() / 2 - btnBounds.width() / 8;
        float right = width / 2 + btnBounds.width() / 2 + btnBounds.width() / 8;
        float top = height / 2 + btnBounds.height() / 1.3f;
        float bottom = height / 2 + btnBounds.height() * 2.4f;
        mBtnRectf = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(mBtnRectf, 30, 20, mPaint);

        if (mDrawDegrees < mTotalDegrees) {
            mDrawDegrees++;
            invalidate();
        }
    }

    /**
     * 初始化
     */
    protected void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mDensity = getContext().getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_UP:
                Log.e("TEST", "手指点击位置:" + "x=" + event.getX() + ",y=" + event.getY());
                Log.e("TEST", "范围:" + "top=" + mBtnRectf.top +
                        ",left=" + mBtnRectf.left + ",right=" + mBtnRectf.right +
                        ",bottom=" + mBtnRectf.bottom);
                if (event.getX() >= mBtnRectf.left && event.getX() <= mBtnRectf.right
                        && event.getY() >= mBtnRectf.top && event.getY() <= mBtnRectf.bottom) {
                    if (mOnBtnClick != null)
                        mOnBtnClick.onClick();
                }
                break;
        }
        return true;
    }

    private OnBtnClick mOnBtnClick;

    public void setBtnClickListener(OnBtnClick onBtnClick) {
        this.mOnBtnClick = onBtnClick;
    }

    public interface OnBtnClick {
        void onClick();
    }

    /**
     * @param degree >100,<10000
     */
    public void setData(int degree) {
        if (degree < 100)
            degree = 100;
        if (degree > 10000)
            degree = 10000;
        mTotalDegrees = degree / 100;
        mDrawDegrees = 0;
        invalidate();
    }
}