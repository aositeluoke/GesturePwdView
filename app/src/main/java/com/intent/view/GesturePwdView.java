package com.intent.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.intent.gesturepwdview.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 类描述:手势密码
 * 作者:xues
 * 时间:2019年02月21日
 */

public class GesturePwdView extends View {
    private static final String TAG = "GesturePwdView";
    private int mNomalColor = Color.parseColor("#d7d7d7");
    private int mSelectColor = Color.parseColor("#8bb8d3");
    private int mErrorColor = Color.parseColor("#ff0000");
    private int mSuccessColor = Color.parseColor("#00ff00");
    private float mOutRingWidth = 14;
    private float mInnerRingWidth = 10;
    private float mLineWidth = 10;
    private Path mArrowPath;
    /*最小长度*/
    private int mMinLength = 4;
    /*
    * 当前状态
    * */
    private int mCurStatus = NOMAL;
    private float mOutRingRadius, mInnerRingRadius;
    private CircleItem[][] circleArray = new CircleItem[3][3];
    private List<CircleItem> mSelectRing = new ArrayList<>();

    private ValueAnimator mAnimator;
    private Paint mOutRingPaint, mInnerRingPaint, mLinePaint, mArrowPaint;

    public GesturePwdView(Context context) {
        this(context, null);
    }

    public GesturePwdView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GesturePwdView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GesturePwdView, 0, 0);
        mNomalColor = a.getColor(R.styleable.GesturePwdView_nomal_color, mNomalColor);
        mSelectColor = a.getColor(R.styleable.GesturePwdView_select_color, mSelectColor);
        mErrorColor = a.getColor(R.styleable.GesturePwdView_error_color, mErrorColor);
        mSuccessColor = a.getColor(R.styleable.GesturePwdView_success_color, mSuccessColor);
        mOutRingWidth = a.getDimension(R.styleable.GesturePwdView_out_ring_width, mOutRingWidth);
        mInnerRingWidth = a.getDimension(R.styleable.GesturePwdView_inner_ring_width, mInnerRingWidth);
        mLineWidth = a.getDimension(R.styleable.GesturePwdView_line_width, mLineWidth);
        mMinLength = a.getInteger(R.styleable.GesturePwdView_min_length, mMinLength);
        a.recycle();
        initPaint();
        initAnimator();
    }

    private void initPaint() {
    /*外圆环画笔*/
        mOutRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutRingPaint.setStrokeWidth(mOutRingWidth);
        mOutRingPaint.setColor(mNomalColor);
        mOutRingPaint.setStyle(Paint.Style.STROKE);

        /*内圆环画笔*/
        mInnerRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerRingPaint.setStrokeWidth(mInnerRingWidth);
        mInnerRingPaint.setColor(mNomalColor);
        mInnerRingPaint.setStyle(Paint.Style.STROKE);

        /*线条画笔*/
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(mLineWidth);
        mLinePaint.setColor(mNomalColor);
        mLinePaint.setStyle(Paint.Style.STROKE);

        /*箭头画笔*/
        mArrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArrowPaint.setColor(mNomalColor);
        mArrowPaint.setStrokeWidth(10);
    }


    private float mTriangleLength = 30;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        mOutRingRadius = getMeasuredWidth() / 12f;
        mInnerRingRadius = mOutRingRadius / 6f;
        int value = 0;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                value++;
                float x = (column * 2 + 1) * getMeasuredWidth() / 6f;
                float y = (row * 2 + 1) * getMeasuredWidth() / 6f;
                PointF pointF = new PointF(x, y);
                CircleItem circleItem = new CircleItem(value, pointF);
                circleArray[row][column] = circleItem;
            }
        }

        initArrowPath();

    }

    /**
     * 初始化箭头路径
     */
    private void initArrowPath() {
        mArrowPath = new Path();
        float startX = -mOutRingRadius - mOutRingWidth;
        float yOff = (float) (Math.sin(Math.toRadians(30)) * mTriangleLength);
        float xOff = (float) (Math.cos(Math.toRadians(30)) * mTriangleLength);
        mArrowPath.moveTo(startX, 0);
        mArrowPath.lineTo(startX - xOff, -yOff);
        mArrowPath.lineTo(startX - xOff, yOff);
        mArrowPath.close();
    }

    private float x, y;
    private StringBuilder mGesturePwd = new StringBuilder();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLinePaint.setColor(mSelectColor);
                mArrowPaint.setColor(mSelectColor);
                x = event.getX();
                y = event.getY();
                mGesturePwd.delete(0, mGesturePwd.length());
                for (CircleItem[] circleItems : circleArray) {
                    for (CircleItem circleItem : circleItems) {
                        if (isInCircle(x, y, circleItem.getCenterPoint())) {
                            mCurStatus = CHECK;
                            mGesturePwd.append(circleItem.value);
                            mSelectRing.add(circleItem);
                            break;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();
                if (mSelectRing.size() > 0) {
                    for (CircleItem[] circleItems : circleArray) {
                        for (CircleItem circleItem : circleItems) {
                            if (isInCircle(x, y, circleItem.getCenterPoint()) && !mSelectRing.contains(circleItem)) {
                                mCurStatus = CHECK;
                                mGesturePwd.append(circleItem.value);
                                mSelectRing.add(circleItem);
                                break;
                            }
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                /*松手的时候不绘制多余的线*/
                x = 0;
                y = 0;

                /*密码有误*/
                if ((!TextUtils.isEmpty(pwd)) && !pwd.equals(mGesturePwd.toString()) || mGesturePwd.length() < mMinLength) {
                    mLinePaint.setColor(mErrorColor);
                    mArrowPaint.setColor(mErrorColor);
                    mCurStatus = ERROR;
                    if (callBack != null && !TextUtils.isEmpty(mGesturePwd)) {
                        if (mGesturePwd.length() >= mMinLength) {
                            callBack.onError(mGesturePwd + "");
                        } else {
                            callBack.onLengthError();
                        }
                    }
                } else {
                    mLinePaint.setColor(mSuccessColor);
                    mArrowPaint.setColor(mSuccessColor);
                    mCurStatus = SUCCESS;
                    if (callBack != null && !TextUtils.isEmpty(mGesturePwd)) {
                        callBack.onSuccess(mGesturePwd + "");
                    }
                }
                mAnimator.start();
                break;
        }
        invalidate();
        return true;
    }


    /**
     * 初始化倒计时动画
     */
    private void initAnimator() {
        mAnimator = ValueAnimator.ofFloat(0, 1)
                .setDuration(500);
//        mAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                mCurStatus = NOMAL;
//                mSelectRing.clear();
//                invalidate();
//            }
//        });

        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*绘制圆环*/
        drawRing(canvas);
        /*绘制线条*/
        drawConnectLine(canvas);
        /*绘制小箭头*/
        drawArrow(canvas);
//
//        /*绘制未连接的线条*/
//        drawUnConnectLine(canvas);
    }


    /**
     * 绘制九个圆环
     *
     * @param canvas
     */
    private void drawRing(Canvas canvas) {
        for (CircleItem[] circleItem : circleArray) {
            for (CircleItem item : circleItem) {
                if (mSelectRing.contains(item)) {
                    switch (mCurStatus) {
                        case CHECK:
                            mOutRingPaint.setColor(mSelectColor);
                            mInnerRingPaint.setColor(mSelectColor);
                            break;
                        case ERROR:
                            mOutRingPaint.setColor(mErrorColor);
                            mInnerRingPaint.setColor(mErrorColor);
                            break;
                        case SUCCESS:
                            mOutRingPaint.setColor(mSuccessColor);
                            mInnerRingPaint.setColor(mSuccessColor);
                            break;
                    }
                } else {
                    mOutRingPaint.setColor(mNomalColor);
                    mInnerRingPaint.setColor(mNomalColor);
                }

                canvas.drawCircle(item.centerPoint.x, item.centerPoint.y, mOutRingRadius, mOutRingPaint);
                canvas.drawCircle(item.centerPoint.x, item.centerPoint.y, mInnerRingRadius, mInnerRingPaint);
            }
        }
    }

    /**
     * 绘制已连接的线（两圆）
     *
     * @param canvas
     */
    private void drawConnectLine(Canvas canvas) {
        for (int i = 1; i < mSelectRing.size(); i++) {
            PointF startPoint = mSelectRing.get(i - 1).getCenterPoint();
            PointF nextPointF = mSelectRing.get(i).getCenterPoint();

            float distance = getDistance(startPoint, nextPointF);

            float offx1 = mInnerRingRadius / 2f * (nextPointF.x - startPoint.x) / (distance);
            float offy1 = mInnerRingRadius / 2f * (nextPointF.y - startPoint.y) / (distance);
            float startX = startPoint.x + offx1;
            float startY = startPoint.y + offy1;

            float endX = nextPointF.x - offx1;
            float endY = nextPointF.y - offy1;
            canvas.drawLine(startX, startY, endX, endY, mLinePaint);
        }
    }

    /**
     * 绘制小箭头
     *
     * @param canvas
     */
    private void drawArrow(Canvas canvas) {
        for (int i = 1; i < mSelectRing.size(); i++) {
            PointF startPointF = mSelectRing.get(i - 1).getCenterPoint();
            PointF endPointF = mSelectRing.get(i).getCenterPoint();
            float tana = (endPointF.y - startPointF.y) / (endPointF.x - startPointF.x);
            float degreeA = (float) Math.atan(tana);
            float B = (float) (Math.toDegrees(degreeA));

            /*终点在起点左侧时，加180*/
            if (endPointF.x < startPointF.x) {
//                B += 180;
            }

            canvas.save();
            canvas.translate(endPointF.x, endPointF.y);
            canvas.rotate(B);
            canvas.drawPath(mArrowPath, mArrowPaint);
            canvas.restore();
        }
    }

    /**
     * 绘制未连接的线条（只有起点圆环）
     *
     * @param canvas
     */
    private void drawUnConnectLine(Canvas canvas) {
        if (mSelectRing.size() > 0 && x != 0) {
            PointF endPointF = new PointF(x, y);
            PointF startPointF = mSelectRing.get(mSelectRing.size() - 1).getCenterPoint();
            float distance = getDistance(startPointF, endPointF);
            float offx = mInnerRingRadius / 2f * (endPointF.x - startPointF.x) / (distance);
            float offy = mInnerRingRadius / 2f * (endPointF.y - startPointF.y) / (distance);
            float startX = startPointF.x + offx;
            float startY = startPointF.y + offy;
            float endX = endPointF.x;
            float endY = endPointF.y;
            canvas.drawLine(startX, startY, endX, endY, mLinePaint);
        }
    }


    public static final int NOMAL = 0;
    public static final int CHECK = 1;
    public static final int ERROR = 2;
    public static final int SUCCESS = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NOMAL, CHECK, ERROR, SUCCESS})
    public @interface Status {
    }

    /**
     * 圆环实例
     */
    private class CircleItem {
        private int value;
        private PointF centerPoint;


        public CircleItem(int value, PointF centerPoint) {
            this.value = value;
            this.centerPoint = centerPoint;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public PointF getCenterPoint() {
            return centerPoint;
        }

        public void setCenterPoint(PointF centerPoint) {
            this.centerPoint = centerPoint;
        }

    }


    /**
     * @param x      检测测点x值
     * @param y      检测点y值
     * @param mPoinF 圆的圆心坐标
     * @return
     */
    public boolean isInCircle(float x, float y, PointF mPoinF) {
        double distance = Math.sqrt(Math.pow(mPoinF.x - x, 2) + Math.pow(mPoinF.y - y, 2));
        return distance <= mOutRingRadius;
    }

    /**
     * 两点间的距离
     *
     * @param startPoint
     * @param endPoint
     * @return
     */
    public float getDistance(PointF startPoint, PointF endPoint) {
        return (float) Math.sqrt(Math.pow(endPoint.x - startPoint.x, 2) + Math.pow(endPoint.y - startPoint.y, 2));
    }


    private CallBack callBack;
    private String pwd = "1234";

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        void onSuccess(String pwd);

        void onError(String pwd);

        void onLengthError();
    }


}
