package com.example.cg.custompwd.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.cg.custompwd.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 先从一个简单的连框画起
 * 作者：cg
 * 时间：2017/8/16 0016 上午 9:55
 */
public class simpleCustomPwd extends View {

    //键盘
    private InputMethodManager input;

    //用来记录输入的密码
    private List<String> mPwd;

    private int pwdNumber = 6;                             //密码的个数

    private int mpwdWidth;                                 //每个字被平分框
    private int mpwdMargin;                                //每个框的边距
    private int pwdFrameColorOfBegin = Color.BLACK;        //框内没有数字的颜色
    private int pwdFrameColorOfEnd = Color.GRAY;           //框内有数据的颜色
    private int pwdFrameStrokeWidth = 5;                   //框的宽
    private int pwdTextColor = Color.RED;                  //数字的颜色
    private int pwdTextSize = 50;                          //数字的大小，默认是画出框的一半大小
    private int pwdStyleType = 1;                          //边框的样式，默认为全边框1, 2:下划线 3:待定
    private int pwdBgBitmap = 0;                               //用户传入的背景图id

    Paint mPaint;                    //初始连框画笔
    Paint tPaint;                    //初始文字画笔
    Paint aPaint;                    //显示数字后边框画笔
    Paint atPaint;                   //数字变圆点之后画笔
    Paint bPaint;                    //背景是图的画笔

    private int number = 0;          //当前已经按了几个数


    /**
     * 定义当用户输入完数字之后，点击键盘的确定按钮事件
     */
    public interface onInputEnterLitener {
        void onInputEnter(String password);
    }

    private onInputEnterLitener monInputEnterLitener;

    public void setOnInputEnterLitener(onInputEnterLitener monInputEnterLitener)
    {
        this.monInputEnterLitener = monInputEnterLitener;
    }


    public simpleCustomPwd(Context context) {
        this(context,null);
    }

    public simpleCustomPwd(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public simpleCustomPwd(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.cgPwd,defStyleAttr,0);
        int n = array.getIndexCount();
        for(int i=0;i<n;i++)
        {
            int arr = array.getIndex(i);
            switch (arr)
            {
                case R.styleable.cgPwd_pwdNumber:
                    pwdNumber = array.getInt(arr,6);
                    break;
                case R.styleable.cgPwd_pwdMargin:
                    mpwdMargin = array.getDimensionPixelSize(arr,(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,getResources().getDisplayMetrics()));
                    break;
                case R.styleable.cgPwd_pwdFrameColorOfBegin:
                    pwdFrameColorOfBegin = array.getColor(arr,Color.BLACK);
                    break;
                case R.styleable.cgPwd_pwdFrameColorOfEnd:
                    pwdFrameColorOfEnd = array.getColor(arr,Color.GRAY);
                    break;
                case R.styleable.cgPwd_pwdFrameStrokeWidth:
                    pwdFrameStrokeWidth = array.getInt(arr,5);
                    break;
                case R.styleable.cgPwd_pwdTextColor:
                    pwdTextColor = array.getInt(arr,Color.RED);
                    break;
                case R.styleable.cgPwd_pwdTextSize:
                    pwdTextSize = array.getDimensionPixelSize(arr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.cgPwd_pwdStyleType:
                    pwdStyleType = array.getInt(arr,1);
                    break;
                case R.styleable.cgPwd_pwdBgBitmap:
                    pwdBgBitmap = array.getResourceId(arr,0);
                    break;
            }
        }

        array.recycle();


        init(context);

    }

    /**
     * 初始化变量
     */
    private void init(Context context) {

        //初始化Margin也就是距离边的距离
        mpwdMargin = dip2px(context,5);

        mPwd = new ArrayList<>();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(pwdFrameColorOfBegin);
        mPaint.setStrokeWidth(pwdFrameStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        aPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        aPaint.setColor(pwdFrameColorOfEnd);
        aPaint.setStrokeWidth(pwdFrameStrokeWidth);
        aPaint.setStyle(Paint.Style.STROKE);

        tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tPaint.setColor(pwdTextColor);
        tPaint.setTextSize(pwdTextSize);
        tPaint.setStyle(Paint.Style.FILL);

        atPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        atPaint.setColor(Color.RED);
        atPaint.setStyle(Paint.Style.FILL);

        bPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bPaint.setStyle(Paint.Style.FILL);

        this.setOnKeyListener(new NumKeyListener());
        //初始化键盘
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        input = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthModel = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightModel = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height ;

        width = widthSize;
        height = heightSize;

        //将宽度去掉左右padding
        widthSize = widthSize - getPaddingLeft() - getPaddingRight();

        //取每个框最大应该占多少，就是在没有设置mpwdMargin时
        mpwdWidth = widthSize/pwdNumber;

        //将高度去掉上下padding
        height = heightSize - getPaddingTop() - getPaddingBottom();

        //因数为框是一个正方形，所以宽，高取小值
        if(mpwdWidth > height)
        {
            mpwdWidth = height;
        }

        height = mpwdWidth;

        //这里重新画了一下宽、高。这主要的作用就是如果你不设置具体的高，它的高也不会是全屏
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(pwdStyleType==1) {
            drawAllFrame(canvas);
        }else if(pwdStyleType==2) {
            drawBottomLine(canvas);
        }else if(pwdStyleType==3){

            //如果选择了图片做为背景，但是没有给出图片id，则让其画方全背景
            if(pwdBgBitmap!=0) {
                drawBgBitmap(canvas);
            }else{
                drawAllFrame(canvas);
            }
        }

        if(mPwd.size()>0)
        {
            drawMyText(canvas);
        }

    }

    /**
     * 画全的边框，区别于只有一个下划线的框
     * @param canvas                  画板
     */
    private void drawAllFrame(Canvas canvas)
    {
        for(int i=0;i<pwdNumber;i++)
        {

            int left = mpwdMargin + i * mpwdWidth + getPaddingLeft();

            int top = mpwdMargin;
            int right = (i + 1) * mpwdWidth - mpwdMargin + getPaddingLeft();
            int bottom = mpwdWidth - mpwdMargin;

            RectF mRectf = new RectF(left,top,right,bottom);

            Paint nPaint = null;
            if(i <= number-1)
            {
                nPaint = aPaint;
            }else{
                nPaint = mPaint;
            }

            canvas.drawRoundRect(mRectf,10,10,nPaint);

        }
    }

    /**
     * 画只有一个下边线的框。
     * @param canvas　　　　　画板
     */
    private void drawBottomLine(Canvas canvas)
    {
        for(int i=0;i<pwdNumber;i++)
        {

            int startX = mpwdMargin + i * mpwdWidth + getPaddingLeft();
            int startY = mpwdWidth - mpwdMargin;
            int stopX = (i + 1) * mpwdWidth - mpwdMargin + getPaddingLeft();
            int stopY = startY;


            Paint nPaint = null;
            if(i <= number-1)
            {
                nPaint = aPaint;
            }else{
                nPaint = mPaint;
            }

            canvas.drawLine(startX,startY,stopX,stopY,nPaint);

        }
    }

    /**
     * 将用户传入的背景图片，放到背景处
     * @param canvas
     */
    private void drawBgBitmap(Canvas canvas)
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), pwdBgBitmap);
        for(int i= 0;i<pwdNumber;i++)
        {

            int left = mpwdMargin + i * mpwdWidth + getPaddingLeft();

            int top = mpwdMargin;
            int right = (i + 1) * mpwdWidth - mpwdMargin + getPaddingLeft();
            int bottom = mpwdWidth - mpwdMargin;

            Rect mSrcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            Rect mDestRect = new Rect(left, top, right, bottom);

            canvas.drawBitmap(bitmap,mSrcRect,mDestRect,bPaint);


        }
        bitmap.recycle();
    }



    /**
     * 写数字
     * @param canvas　　画板
     */
    private void drawMyText(Canvas canvas) {



        for (int i = 0; i < number; i++) {

            String mNumber = mPwd.get(i);

            Rect bounds = new Rect();
            //tPaint.setTextSize(getMeasuredHeight()/2);
            tPaint.getTextBounds(mNumber, 0, mNumber.length(), bounds);


            int centerX = (i + 1) * mpwdWidth - mpwdWidth / 2;
            int centerY = mpwdWidth / 2;

            canvas.drawText(mNumber, centerX - bounds.width() / 2, centerY + bounds.height() / 2, tPaint);
        }

        //isDrawNumber = false;
    }


    /**
     * 点击事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                requestFocus();
                input.showSoftInput(this, InputMethodManager.SHOW_FORCED);

                return true;

        }

        return super.onTouchEvent(event);
    }


    /**
     * 得到写入的数据
     * @return         返回数据
     */
    public String getNumber()
    {
        String str = "";
        for(String s : mPwd)
        {
            str += s;
        }
        return str;
    }

    /**
     * 设置数据，引处可以一次将数字全部得到
     * @param mNumber              数字
     */
    public void setNumber(String mNumber)
    {
        if(mNumber.length()!=pwdNumber)
        {
            Toast.makeText(getContext(),"密码位数不对!",Toast.LENGTH_LONG).show();
            return;
        }else{
            number = 0;
            mPwd.clear();
            //isDrawNumber = true;
            for(int i=0;i<pwdNumber;i++)
            {
                mPwd.add(mNumber.substring(i,i+1));
                number++;
                invalidate();
            }
        }

    }

    /**
     * 清空数字
     */
    public void cancelNumber()
    {
        mPwd.clear();
        number = 0;
        invalidate();
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 键盘弹出
     */
    class NumKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (event.isShiftPressed()) {//Handle * # and other keys
                    return false;
                }
                //如果是数字，就向画板中画数字
                if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {//Only deal with numbers

                    if(number < pwdNumber) {

                        mPwd.add(String.valueOf(keyCode - 7));
                        number++;

                        invalidate();
                    }

                    return true;
                }
                //点击删除键时，将数字删除
                if (keyCode == KeyEvent.KEYCODE_DEL) {

                    if(number > 0)
                    {
                        mPwd.remove(mPwd.size() - 1);
                        number--;
                        //isDrawNumber = true;
                        invalidate();
                    }
                    return true;
                }
                //点击确定按钮，回调点击事件，前端页面可以取得相应的数字
                if (keyCode == KeyEvent.KEYCODE_ENTER) {

                    if(monInputEnterLitener!=null)
                    {
                        monInputEnterLitener.onInputEnter(getNumber());
                    }
                    return true;
                }
            }
            return false;
        }

    }
}
