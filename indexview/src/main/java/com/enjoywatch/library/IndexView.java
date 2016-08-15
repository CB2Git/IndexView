package com.enjoywatch.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 自定义索引菜单，类似于通讯录右侧索引
 */
public class IndexView extends TextView {
    //索引项文字
    private String mIndex;
    //默认索引文字颜色
    private int mDefaultTextColor;
    //按下索引文字颜色
    private int mTouchTextColor;
    //索引文字大小,自动根据控件大小计算
    private float mIndexTextSize;
    //索引文字占用的高度
    private float mIndexTextHeight;
    //索引控件默认背景
    private Drawable mIndexNormalBg;
    //索引控件按下背景
    private Drawable mIndexTouchBg;
    //弹出框背景
    private Drawable mPopupBackground;
    //弹出框文字颜色
    private int mPopupTextColor;
    //弹出框文字大小
    private float mPopupTextSize;
    //当前选中位置
    private int mSelPos = 0;
    // 绘制索引项的画笔
    private Paint mIndexPaint = new Paint();
    // 索引项的默认宽度 30dp，默认高度为父布局传递进来的高度
    private int INDEX_WIDTH = dp2px(25);
    //默认延时时长 1秒
    private int DELAY_TIME = 1000;
    // 显示的索引悬浮窗
    private TextView mOverlayWindow;
    // 悬浮框内边距
    private int OverlayWindowPadding = dp2px(14);
    // 窗口管理类
    private WindowManager windowManager;
    // 悬浮窗是否存在
    private boolean isVisibility = false;
    // 是否显示悬浮窗
    private boolean mShow = true;
    //回调接口
    private OnIndexChangeListener mOnIndexChangeListener;
    //延时取消悬浮窗
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (isVisibility) {
                isVisibility = false;
                mOverlayWindow.setVisibility(INVISIBLE);
            }
        }
    };

    public IndexView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public IndexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public IndexView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void init(Context context, AttributeSet attrs, int defStyle) {
        // 加载布局属性
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.IndexView, defStyle, 0);
        mIndex = a.getString(R.styleable.IndexView_index);
        mIndexNormalBg = a.getDrawable(R.styleable.IndexView_indexBackground);
        mIndexTouchBg = a.getDrawable(R.styleable.IndexView_indexTouchBackground);
        mDefaultTextColor = a.getColor(R.styleable.IndexView_defaultTextColor, 0xff898989);
        mTouchTextColor = a.getColor(R.styleable.IndexView_touchTextColor, 0xff3399FF);
        mPopupBackground = a.getDrawable(R.styleable.IndexView_popupBackground);
        mPopupTextColor = a.getColor(R.styleable.IndexView_popupTextColor, mTouchTextColor);
        mPopupTextSize = a.getDimension(R.styleable.IndexView_popupTextSize, dp2px(30));
        mSelPos = a.getInteger(R.styleable.IndexView_defaultSelectPosition, 0);
        mSelPos = mSelPos > mIndex.length() - 1 ? 0 : mSelPos;
        DELAY_TIME = a.getInt(R.styleable.IndexView_delayTime, DELAY_TIME);
        mShow = a.getBoolean(R.styleable.IndexView_showPopup, mShow);
        a.recycle();
        initPopWindow(context);
    }

    // 初始化悬浮窗口
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initPopWindow(Context context) {
        //  mOverlayWindow 是一个TextView
        mOverlayWindow = new TextView(context);
        mOverlayWindow.setLayoutParams(new ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        mOverlayWindow.setTextSize(mPopupTextSize);
        mOverlayWindow.setTextColor(mPopupTextColor);
        //宽高必须设置，不然悬浮窗刷新出现问题，而且文字会左右移动
        mOverlayWindow.setWidth(sp2px((int) mPopupTextSize) + OverlayWindowPadding);
        //mOverlayWindow.setHeight(sp2px((int) mPopupTextSize));
        mOverlayWindow.setGravity(Gravity.CENTER);
        mOverlayWindow.setBackground(mPopupBackground);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        mOverlayWindow.setVisibility(INVISIBLE);
        windowManager.addView(mOverlayWindow, lp);
    }

    /**
     * 测量索引控件的大小，默认宽度为30dp，默认高度为充满父布局
     * 根据测量结果自动计算索引文字的大小
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int resultWidth;
        // 如果是wrap_content,选取默认宽度和默认宽度的较小值，否则使用父布局指定的大小
        if (widthMode == MeasureSpec.AT_MOST) {
            resultWidth = Math.min(widthSize, INDEX_WIDTH);
        } else {
            resultWidth = widthSize;
        }

        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        //减去上下内边距
        float textHeight = heightSize - paddingTop - paddingBottom;
        float textWidth = widthSize - paddingLeft - paddingRight;
        //根据高度计算出每一个文字占用的高度
        mIndexTextHeight = textHeight / mIndex.length();
        //文字尺寸为文字高度和控件宽度的较小值
        mIndexTextSize = Math.min(mIndexTextHeight, textWidth);
        setMeasuredDimension(
                MeasureSpec.makeMeasureSpec(resultWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 设置画笔
        mIndexPaint.setTextSize(mIndexTextSize);
        mIndexPaint.setAntiAlias(true);
        mIndexPaint.setStyle(Paint.Style.STROKE);
        // 绘制索引项
        for (int i = 0; i < mIndex.length(); i++) {
            if (i != mSelPos) {
                mIndexPaint.setColor(mDefaultTextColor);
            } else {
                mIndexPaint.setColor(mTouchTextColor);
            }
            // 如果设置了左右边距，则按照边距绘制文字，如果没有设置，那么自动水平居中
            float left;
            if (getPaddingLeft() == 0 && getPaddingRight() == 0) {
                left = (getMeasuredWidth() - mIndexPaint.measureText(mIndex, i, i + 1)) / 2;
            } else {
                left = getPaddingLeft();
            }

            // 垂直居中
            Paint.FontMetrics metrics = mIndexPaint.getFontMetrics();
            float textHeight = metrics.bottom - metrics.top;
            float top = i * mIndexTextHeight + (mIndexTextHeight - textHeight) / 2;
            // 因为绘制文字是基于基线位置的，所以需要转换一下
            top = top - metrics.ascent;

            canvas.drawText(String.valueOf(mIndex.charAt(i)), left, top + getPaddingTop(), mIndexPaint);
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 如果悬浮窗还存在，那么取消延时
                if (isVisibility) {
                    this.removeCallbacks(mRunnable);
                }
                setBackground(mIndexTouchBg);
                break;
            case MotionEvent.ACTION_UP:
                // 开始延时效果
                this.postDelayed(mRunnable, DELAY_TIME);
                setBackground(mIndexNormalBg);
                break;
        }
        // 判断当前选中是否改变
        boolean changed = false;
        if (mSelPos != getTouchPos(y)) {
            changed = true;
            mSelPos = getTouchPos(y);
        }
        // 调用回调接口
        if (mOnIndexChangeListener != null && changed && mSelPos >= 0 && mSelPos < mIndex.length()) {
            mOnIndexChangeListener.OnIndexChange(mIndex, mSelPos);
        }
        // 设置悬浮窗可见并设置悬浮窗文字
        if (mSelPos >= 0 && changed && mSelPos < mIndex.length() && mShow) {
            mOverlayWindow.setGravity(Gravity.CENTER);
            mOverlayWindow.setText(String.valueOf(mIndex.charAt(mSelPos)));
            if (!isVisibility) {
                isVisibility = true;
                mOverlayWindow.setVisibility(VISIBLE);
            }
        }
        invalidate();
        return true;
    }

    /**
     * 根据点击的Y，计算点击的项
     */
    private int getTouchPos(float y) {
        return (int) (Math.ceil((y - getPaddingTop()) / mIndexTextHeight) - 1);
    }

    /**
     * dp 转 px
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    /**
     * sp 转 px
     */
    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }


    /**
     * 当拥有此控件的Activity被销毁的时候，如果悬浮窗还存在，则删除悬浮窗
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //如果悬浮窗显示，则销毁
        if (isVisibility) {
            isVisibility = false;
            windowManager.removeViewImmediate(mOverlayWindow);
        }
    }

    /**
     * 索引切换监听
     */
    public interface OnIndexChangeListener {
        /**
         * 索引切换回调
         *
         * @param index   索引文字
         * @param postion 当前选中位置
         */
        void OnIndexChange(String index, int postion);
    }

    public void setOnIndexChangeListener(OnIndexChangeListener onIndexChangeListener) {
        this.mOnIndexChangeListener = onIndexChangeListener;
    }

    /**
     * 设置当前选中位置
     *
     * @param position 选中的位置0~mIndex.length()-1，如果position大于字符串长度，那么选中第一个
     */
    public void setSelectIndex(int position) {
        mSelPos = position > mIndex.length() - 1 ? 0 : position;
        invalidate();
    }

    /**
     * 设置是否显示悬浮窗
     *
     * @param show true显示/false不显示
     */
    public void setShowPopup(boolean show) {
        mShow = show;
    }
}
