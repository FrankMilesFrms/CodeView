/*
 * Copyright Frms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.frms.codeview;

import java.util.ArrayList;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector;
import android.view.WindowManager;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputContentInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.OverScroller;
import android.widget.ProgressBar;
import android.support.v7.view.ActionMode;

import com.frms.codeview.tools.InputCompletion.JavaScriptCompletion;
import com.frms.codeview.tools.Kit;
import com.frms.codeview.tools.PluginUI;
import com.frms.codeview.tools.Theme;
import com.frms.codeview.tools.ThemeDark;
import com.frms.codeview.tools.ThemeLight;
import com.frms.codeview.tools.UndoStack;
import com.frms.lexer.TAG;
import com.frms.lexer.language.Java;
import com.frms.lexer.language.JavaScript;

/**
 * 因为入不敷出，所以我们不打算持续更新此项目，但我们会修复你们反映的Bugs。
 * 所以，在这里感谢你们长久以来的支持。
 * 如果你喜欢这个项目，不妨给颗星星支持我吧！
 *
 *     We do not plan to update this project continuously because we cannot make ends meet,
 * but we will fix the Bugs you have reported.
 * So thank you for your continued support.
 * If you like this project, please collect it, thanks! :)
 *
 */

/**
 * 我们也许会实现的：
 * 1."如何自定义语法高亮规则？.pdf"
 * 2."CodeView的原理.pdf"
 * 3.查找、替换功能的增加。
 * 4.过大文本无法打开问题。
 * 5."\t" 和 空格符号 的区分
 * 6.代码输入自动添补规则
 *
 * 1." How do I customize syntax highlighting rules?.pdf"
 * 2." Principles of CodeView.pdf"
 * 3. The addition of search and replace functions.
 * 4. Too big texts does not open.
 * 5. The distinction between "\t" and space symbols
 * 6. Code input automatically fill rules
 *
 * 注意（你可能认为的错误）
 *
 * 1、在这个Demo中，我们没有处理键盘弹起时顶起CodeView的操作。
 * 因此，你需要注意并及时纠正这个错误来避免文本选择范围不全。
 *
 * 2、只有在光标存在可视范围之内，他才会和编辑同步。
 *
 * 3、屏幕切换、视图重启等因为未保存导致部分数据丢失问题。
 *
 * Matters Needing Attentions (what you might think are errors)
 * 1. In this Demo, I didn't handle jacking up CODEVIEW when the keyboard is played.
 * Therefore, you need to be aware of and correct this error to avoid an incomplete selection of text.
 *
 * 2. Only when the cursor is in visible range will it synchronize with the CodeView
 *
 * 3. screens-change view-restart because not saved cause data loss problem.
 */

/**
 *开发日志（Development of Logs）：
 *
 * 仅短暂更新，修复BUGs。
 * Only a brief update to fix bugs.
 *                                                                                   2021年4月4日09:43:14
 *
 * 由于新冠病毒造成的疫情爆发，本项目暂停维护。
 *
 * 预计下次维护时间在疫情过后或者是7月中旬，在此期间不再开发新的功能。
 *
 * 如果存在可以100%复原错误，请发送邮件给我以便妥善解决。
 *
 * Due to the outbreak caused by Corona Virus, the maintenance of this project is suspended.
 *
 * It is expected that the next maintenance will be after the outbreak or in the middle of July,
 *
 * and no new functions will be developed during this period.
 *
 * If there are errors, please send me an email for proper solution.
 *                                                                                    by Frms
 *                                                                                    2020年5月5日00:07:16
 * 开发日志：
 * 2021/4/4
 * 修复多行删除撤回失败问题
 * 选择范围过小的问题
 * 部分文字没绘制的问题
 *
 * 2020/6/8
 * 修复滑动条超过屏幕错误
 * 修复无法关闭滑动条错误
 * 待定：创建onKeyDown 替代 sendKeyEvent 的部分事件，详细参照#setPasteInClipboardManager();
 * 其他：修复部分错误。
 * 可能存在的错误：无法在 ‘QQ输入法’ 实现光标移动（其他非EditText类或子类均有类似情况，其他输入法未做测试）
 * 延迟开发，后退一月。
 */

/**
 * 部分操作说明：
 * 手势说明：
 * 单指：
 *  非选择状态：
 *    点击行号触发断点标记的启用或取消。
 *    点击文本会光标定位，并显示辅助光标，点击处若无文本，则自动选取最近文本，后再启用输入法。
 *    滑动时，若点击位置在辅助光标或光标附近，则视为滑动光标，此时若允许显示放大镜，则启用之。
 *
 * 选择:
 *      长按文本处，先计算抓取一个词组（能被Java允许的字符,不包括符号），
 *      如果词组超过屏幕（不论两边或一边），屏幕位置移动到第一个光标处。
 *      此时若处于滑动时，且点击位置在辅助光标或光标附近，则视为滑动光标，此时若允许显示放大镜，则启用之。
 * 双指：
 *     放缩字体大小。
 *
 * 项目名称 ： CodeView
 * @author  ： Frms, 3505826836@qq.com
 * 创建时间 ： 2020/2/8 16:26(ydt)
 */
public class CodeView extends View implements 
    GestureDetector.OnGestureListener
{
    /**
     *  控件标识
     */
    private static final String Tag = "CodeView D";
    
    /**
     * 控件版本
     */
    private static final int version = 0x1000;
    
    private static final int UNKNOWN  = -1;
    
    /**
     * <p>左光标标识</p>
     * 
     */
    private static final int CURSOR_LEFT = 0x0005;
    
    /**
     * <p>右光标标识</p>
     * 
     */
    private static final int CURSOR_RIGHT = 0x0006;
    
    
    
    /**
     * 默认字体大小
     */
    @SuppressLint("all")
    private final int BASE_TEXT_SIZE = 50;
    
    /**
     * 非选择文本模式
     */
    public static final int SELECT_NONE = 0x1003;
    
    /**
     * 选择文本模式
     */
    public static final int SELECT_ING = 0x1004;
    
    /**
     *  针对于InputConnection的启用参数
     */
    public static final int RUN_MODE = 0xbabe;
    
    /**
     * DejaVuSansMono.ttf 字体
     */
    public static final int DEJAVUSANSMONO = 0x1005;
    
    /**
     * momospace 字体
     */
    public static final int MONOSPACE = 0x1006;
    
    /**
     * sans serif字体
     */
    public static final int SANS_SERIF = 0x1007;
    
    /**
     * serif 字体
     */
    public static final int SERIF = 0x1008;
    
    /**
     * JavaScript 语法规则
     */
    public static final int LANGUAGE_NATIVE_JAVASCRIPT = 1;
    
    /**
     * 无语法规则
     */
    public static final int LANGUAGE_NATIVE_NONE = 0;
    
    /**
     * Java 语法规则
     */
    public static final int LANGUAGE_NATIVE_JAVA = 2;
    
    private final AppCompatActivity mActivity;
    
    // 绘制区域
    private Rect mDrawClip;
    
    // 绘制
    private TextPaint mTextPaint, mLinePaint, mOtherPaint;
    
    // 基本控件内容
    protected long mWidth = 0L, mHeight = 0L;
    protected int mSelectMode;// 选择模式
    
    // 基本绘制参数（不易改变）
    private int drawRowHeight;// 行高度
    
    private Paint.FontMetrics drawFontMetrics;// 绘制距离的基本参数
    
    // 光标图片资源
    private Bitmap mBitmapCursor, mBitmapLeftCursor, mBitmapRightCursor, mBitmapScrollBar;
    private int mBitmapSize, mBitmapSelectSize;
    private Bitmap mBitmapScreen;
    private boolean mRefreshScreen = true;
    
    // 基本文字存储
    private int length = 0; // 注意，文本会自动追加一个EOF标志，这会增加一个字节。
    private char[] mChar = new char[0];// 存储字符区
    private int[] mRowStartCounts = new int[10]; // 每行开始位置引索, 这里记录的行开始时EOL后的一个字符位置，错位对应。
    private int mRowCounts = 1;// 行数
    private int mCharLitterWidth, mCharChineseWidth; // 单元宽度。
    
    
    private boolean[] mDebugLines = new boolean[2]; // 断点记录
    private boolean isShowCursor = false;// 是否显示完整光标
    private boolean isErrorTouchMode = false; // 防误触模式，他会取消部分光标跳转。
    private int[] mCursor = new int[4]; // 光标位置
    private boolean isInActionMode;//剪贴板是否显示
    public boolean setOnlyRead = false; // 设置是否只读
    
    // 加载&缓冲
    private AsyncLoader mAsyncLoader;
    private AlertDialog loadWindow;
    private CodeViewInputConnection mInputConnection;
    private InputMethodManager mInputMethodManager;
    private DisplayMetrics mDisplayMetrics;
    
    
    private OverScroller mOverScroller;// 滑动模块
    private GestureDetector mGestureDetector; // 手势
    
    
    private android.support.v7.view.ActionMode.Callback mCallback;
    private ActionMode mActionMode;
    private ClipboardManager mClipboardManager;
    private PluginUI mPluginUI;
    private Theme mTheme;
    
    private UndoStack mUndoStack;
    private Typeface typeface;
    private JavaScript mTokenJavaScript;
    private Java mTokenJava;
    private JavaScriptCompletion mjavaScriptCompletion;
    
    private boolean mScannerLock = true;
    private boolean isUseLanguage = false;
    
    private onDebugListener onDebugListener; // 断点接口。
    private onEditListener mOnEditListener; // 文本编辑接口。
    
    @SuppressLint("all")
    private int selectLanguage = 0;
    
    private boolean mVerticalScrollBar; // 是否绘制垂直条
    private boolean isShowBar; // 是否显示滚动条
    
    public CodeView(AppCompatActivity activity)
    {
        super(activity);
        mActivity = activity;
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        init(activity);
    }
    
//
//    public CodeView(Context context,  AttributeSet attrs, Activity mActivity)
//    {
//        super(context, attrs);
//        this.mActivity = null;
//        Kit.printout("无法使用布局 ： 需要调用 init(Activity) 方法");
//    }
//
//
//    public CodeView(Context context, AttributeSet attrs, int defStyleAttr)
//    {
//        super(context, attrs, defStyleAttr);
//        this.mActivity = null;
//        Kit.printout("无法使用布局 ： 需要调用 init(Activity) 方法");
//    }
//
//
//    @SuppressLint("NewApi")
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public CodeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
//    {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        this.mActivity = null;
//        Kit.printout("无法使用布局 ： 需要调用 init(Activity) 方法");
//    }
    
    /**
     * 内包含初始化数据、释放资源，在启用控件其他方法前，如果不是使用构造函数
     * 则必须调用此方法。
     * @param cx
     */
    @SuppressLint("all")
    public void init(Activity cx)
    {
        mUndoStack = new UndoStack(this);
        mTheme = new ThemeDark();
        
        mDisplayMetrics = new DisplayMetrics();
        cx.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        
        mSelectMode = SELECT_NONE;
        drawText = new char[512];
        drawCharWidth = new int[0xffff + 1];
        
        mInputConnection = new CodeViewInputConnection(this);
        mOverScroller = new OverScroller(cx);
        mGestureDetector = new GestureDetector(cx, this);
        mDrawClip = new Rect();
        mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        mjavaScriptCompletion = new JavaScriptCompletion();
        
        mClipboardManager = (ClipboardManager) cx.getSystemService(Context.CLIPBOARD_SERVICE);
        
        typeface = Typeface.createFromAsset(cx.getAssets(), "DejaVuSansMono.ttf");
        
        mLinePaint = new TextPaint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setTypeface(typeface);
        mLinePaint.setTextSize(BASE_TEXT_SIZE);
        mLinePaint.setTextAlign(Paint.Align.RIGHT);
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTypeface(typeface);
        mTextPaint.setTextSize(BASE_TEXT_SIZE);
        mOtherPaint = new TextPaint();
        mOtherPaint.setAntiAlias(true);
        mOtherPaint.setTypeface(typeface);
        mOtherPaint.setTextSize(BASE_TEXT_SIZE);
        
        mTextPaint.setColor(mTheme.getNormalColor());
        mLinePaint.setColor(mTheme.getLineCountColor());
        mOtherPaint.setColor(mTheme.getSelectColor());
        
    
        mBitmapCursor      = Kit.getBitmap(cx, R.raw.text_select_handle_middle, 0.7f);
        mBitmapLeftCursor  = Kit.getBitmap(cx, R.raw.text_select_handle_left, 0.7f);
        mBitmapRightCursor = Kit.getBitmap(cx, R.raw.text_select_handle_right, 0.7f);
        mBitmapScrollBar = Kit.getBitmap(getContext(), R.drawable.a_ve, 0.07f);
        
        mBitmapSize = mBitmapCursor.getWidth()/2;
        mBitmapSelectSize = mBitmapLeftCursor.getWidth();
        initChangeTextSize();
        
        setBackgroundColor(mTheme.getBackgroundColor());
        setDrawingCacheEnabled(true);
        setDrawingCacheQuality(DRAWING_CACHE_QUALITY_LOW);
        setLongClickable(true);
        setFocusableInTouchMode(true);
        
        setHapticFeedbackEnabled(true);
        setFocusable(true);
        
        mAsyncLoader = new AsyncLoader();
        loadWindow = new AlertDialog.Builder(getContext()).create();
        loadWindow.setTitle("数据处理中...");
        loadWindow.setView(new ProgressBar(getContext()));
        loadWindow.setCancelable(false);// 设置是否可以通过点击Back键取消
        
       
    
        // {行，位置，行，位置}, 这个位置指的是光标后的字位置，
        // 比如空文本，其默认位置是 1， 因为其后是EOF, 位置是 1.
        mCursor = new int[] {1, 1, 1, 1};
        drawCursorX = Xoffset;
        drawCursorY = (int) (drawRowHeight + drawFontMetrics.bottom);
        mHeight = drawRowHeight;
        
        mPluginUI = new PluginUI(mActivity, mDisplayMetrics.widthPixels, this);
        mPluginUI.setRowheigth(drawRowHeight);
        
        
        onDebugListener = (line, nowMode) -> {};
    }
    
    /**
     *  当需要改变视图大小时调用(不会更新控件大小)
     */
    private void initChangeTextSize()
    {
        drawFontMetrics = mTextPaint.getFontMetrics();
        drawRowHeight = (int)(drawFontMetrics.bottom - drawFontMetrics.top);// 允许（非推荐）绘制的最大高度。
        mCharChineseWidth = (int) mTextPaint.measureText("囧");
        mCharLitterWidth = (int) mTextPaint.measureText("H");
        
    }
    
    
    private long scrollX = 1000, scrollY = 1000;
    
    private float lastDist = 0,
                  lastSize = 0; // 与双指放缩文本大小有关。
    
    private boolean onClickCursor = false;// 光标是否被点击
    
    private int mClickCursor = UNKNOWN;  // 点击的光标位置
    
    private boolean isInScrollBarRange = false; // 判断触控位置是否在滑动条范围。
    
    /**
     * 这里控制滑动的范围，如果范围有大规模改变，要重新调用。
     */
    public void initScroll()
    {
        scrollX = mWidth  + Xoffset;
        scrollY = mHeight - drawRowHeight;
    }
    
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event)
    {
        onTouchZoom(event);
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            onClickCursor = false;
            mClickCursor = UNKNOWN;
            lastDist = 0;
            isInScrollBarRange = false;
            
            mPluginUI.dismissMagnifier();
        }
        
        if(isFocused())
        {
            mGestureDetector.onTouchEvent(event);
        }
        else if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP)
        {
            requestFocus();
        }
        return true;
    }
    
    /**
     * 当视图大小改变时，会控制绘制范围，比如 键盘弹出和隐藏、屏幕强行被旋转等，都会刷新一次。
     * 调用此方法不会主动更新视图。
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        mDrawClip.left = 0;
        mDrawClip.top = 0;
        mDrawClip.right = w;
        mDrawClip.bottom = h;
        
        initScroll();
    }
    
    /**
     *  这里实现（部分）作用：
     *  <p>
     *      1. 当处于 手势抛出{@link #onFling(MotionEvent, MotionEvent, float, float)}执行时，
     *      点击立刻停止抛出状态。
     *  </p>
     *  <p>
     *      2. 记录按住位置是否在光标附近。
     *  </p>
     * @param e
     * @return
     */
    @Override
    public boolean onDown(MotionEvent e)
    {
        if(e.getPointerCount() > 1)return false;
        
        isInScrollBarRange = e.getX() >= getWidth() - mBitmapScrollBar.getWidth();
        isShowBar = isInScrollBarRange;
        
        // 点击立刻停止滑动状态。
        if(!mOverScroller.isFinished()) { mOverScroller.forceFinished(true);}
        
        
        int x = (int)e.getX() + mDrawClip.left;
        int y = (int)e.getY() + mDrawClip.top + drawRowHeight;
        
        if(mSelectMode == SELECT_NONE)
        {
            onClickCursor = (Math.abs(y - drawCursorY) < drawRowHeight * 1.5f)
                          && Math.abs(x - drawCursorX)  < mCharLitterWidth/2;
        } else
        {
            boolean p;
            if(mCursor[0] == mCursor[2])
            {
                p = Math.abs(x - drawCursorX) < Math.abs(x - drawCursorX2);
            } else
            {
                p = Math.abs(y - drawCursorY) < Math.abs(y - drawCursorY2);
            }
            
            if(p)
            {
                mClickCursor = (Math.abs(y - drawCursorY - drawRowHeight) <  drawRowHeight
                             && Math.abs(x - drawCursorX)  < mBitmapSelectSize) ? CURSOR_LEFT : UNKNOWN;
            } else
            {
                mClickCursor = (Math.abs(y - drawCursorY2 - drawRowHeight) < drawRowHeight
                                && Math.abs(x - drawCursorX2)  < mBitmapSelectSize) ? CURSOR_RIGHT : UNKNOWN;
                
            }
        }
        
        return true;
    }
    
    @Override
    public void onShowPress(MotionEvent e) {}
    
    /**
     * 这里实现单指轻击松开（部分）作用：
     * <p>
     *     若处于行号内，实现断点的记录和取消，否则定位文本，注意，点击位置若不在文本附近，
     *     则视为同点击位置最近(x, y)坐标文本处。
     * </p>
     * <p>
     *    非选择状态：
     *     点击行号触发断点标记的启用或取消。
     *     点击文本会光标定位，并显示辅助光标，点击处若无文本，则自动选取最近文本，后再启用输入法。
     *  </p>
     * @param e
     * @return
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e)
    {
        if(e.getPointerCount() == 1)
        {
            // mPluginUI.dismissVerticalScrollBar();
            if(isShowBar && (isInScrollBarRange && e.getY() > 0))
            {
                scrollTo(getScrollX(), (int) (e.getY() / getHeight() * getViewHeigth()));
            }
            // 处理选择，使其取消。
            
            int x = (int)e.getX() + mDrawClip.left;
            int y = (int)e.getY() + mDrawClip.top + drawRowHeight;
            
            if(mSelectMode == SELECT_NONE)
            {
                gotoPosition(x, y);
                if(x > Xoffset) {
                    showKeyboard();
                    invalidate(mDrawClip);
                }
            } else
            {
                // 粗略估计，防止手误。
                int line = Math.max(y/drawRowHeight, 1);
                if(line < mCursor[0] || line > mCursor[2])
                {
                    hideClipboardPanel();
                    mSelectMode = SELECT_NONE;
                } else
                {
                    showKeyboard();
                }
                invalidate(mDrawClip);
            }
            
            
        }
        else
        {
            isShowBar = false;
        }
        return true;
    }
    
    /**
     * 这里实现滑动手势（部分）作用：
     * <p>
     *     1. 移动文本。
     * </p>
     * <p>
     *     2. 移动光标。
     *     滑动时，若点击位置在辅助光标或光标附近，则视为滑动光标，此时若允许显示放大镜，则启用之。
     * </p>
     * @param e1
     * @param e
     * @param distanceX
     * @param distanceY
     * @return
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e, float distanceX, float distanceY)
    {
        int x = Math.max((int)e.getX() + mDrawClip.left, Xoffset);
        int y = (int)e.getY() + mDrawClip.top;
        
        
        if(mSelectMode == SELECT_NONE)
        {
            if(isInScrollBarRange)
            {
                // 为了更好的体验，所以取消了放在上一个if内。
                if(e.getY() <= 0)return false;
                
                isShowBar = true;
                scrollTo(getScrollX(), (int) (e.getY() / getHeight() * getViewHeigth()));
            } else
            if(onClickCursor)
            {
                // 移动关标
                gotoPosition(x, y);
                gotoCursor(x, y);
                mPluginUI.showMagnifier(x -= mDrawClip.left, y -= mDrawClip.top, getScreenshot(x, y));
            }
            else {
                scrollBy((int)distanceX, (int)distanceY);
                isShowBar = true;
                // mPluginUI.setUpdateVerticalScrollBar(Math.round(scrollY));
            }
        }
        else if(mClickCursor > 0)
        {
            x = Math.max(x, Xoffset);
            
            if(mClickCursor == CURSOR_LEFT)
            {
                gotoPosition(x, y);
            } else
            {
                gotoPosition2(x, y);
            }
            
            gotoCursor(x, y);
            mPluginUI.showMagnifier(x -= mDrawClip.left, y -= mDrawClip.top, getScreenshot(x, y));
            
        } else {
            scrollBy((int)distanceX, (int)distanceY);
            isShowBar = true;
        }
        return false;
    }
    
    
    /**
     * 长按文本处，先计算抓取一个词组（能被Java允许的字符,不包括符号），
     *     如果词组超过屏幕（不论两方或一方超过），屏幕位置移动到第一个光标处，另见{@link #setSelectWordGroup(int, int)}。
     *     此时若处进行滑动时，且点击位置在辅助光标或光标附近，则视为滑动光标，此时若允许显示放大镜，则启用之。
     * @param e
     */
    @Override
    public void onLongPress(MotionEvent e)
    {
        if(e.getPointerCount() > 1)return;
        
        int x = (int)e.getX() + mDrawClip.left;
        int y = (int)e.getY() + mDrawClip.top + drawRowHeight;

		if(x >= Xoffset)
		{
			showClipboardPanel();
			setSelectWordGroup(x, y);
		}
    }
    
    /**
     * 抛出手势
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return
     */
    public boolean onFling(MotionEvent p1, MotionEvent p2, float p3, float p4)
    {
        // TODO: 不精确
        mOverScroller.fling(getScrollX(), getScrollY(), - (int)p3, -(int)p4,
            0, Math.round(scrollX), 0, Math.round(scrollY));
    
        // mPluginUI.setUpdateVerticalScrollBar(Math.round(scrollY));
        if(!isShowBar)isShowBar = true;
        
        invalidate(mDrawClip);
        return true;
    }
    
    /**
     * 抓取一个词组（能被Java允许的字符），如果词组超过屏幕（不论两方或一方），
     * 屏幕位置移动到第一个光标处，主动更新视图、选择状态和光标位置。
     * @param x
     * @param y
     */
    private void setSelectWordGroup(int x, int y)
    {
        if(length < 2)return;
        
        mSelectMode = SELECT_ING;
            y = Math.min(y, drawRowHeight * mRowCounts);
        int line = Math.max(y/drawRowHeight, 1);
    
        mCursor[0] = line;
        drawCursorY = (int) (line * drawRowHeight + drawFontMetrics.bottom);// 处理文字坐标问题
    
        int clickIndex = mRowStartCounts[line];
        int drawP = Xoffset;
    
        search :
        while(drawP < mDrawClip.right && clickIndex < length)
        {
            if(Math.abs(drawP - x) < mCharLitterWidth/2
               || mChar[clickIndex] == TAG.EOL
               || clickIndex + 1 == length)
            {
                mCursor[1] = clickIndex +1;
                drawCursorX = drawP;
                isShowCursor = true;
            
                break search;
            }
            drawP += drawCharWidth[mChar[clickIndex++]];
        }
    
        drawCursorX2 = drawCursorX;
        drawCursorY2 = drawCursorY;
        System.arraycopy(mCursor, 0, mCursor, 2, 2);
    
        int position = mCursor[1] - 1;
        int index = position;
        char c;
    
        beforeSearch :
        while (index > 0)
        {
            c = mChar[--index];
            if(Character.isJavaIdentifierPart(c))
            {
                drawCursorX -= drawCharWidth[c];
            } else
            {
                mCursor[1] =index + 2;
                break;
            }
        }
       
        if(index == 0)
            mCursor[1] = 1;
        
        index = position;
        
        afterSearch :
        while (index + 1 < length)
        {
            c = mChar[index];
            if(Character.isJavaIdentifierPart(c))
            {
                drawCursorX2 += drawCharWidth[c];
            } else
            {
                mCursor[3] = index + 1;
                break afterSearch;
            }
            index++;
        }
        
        if(index + 1 == length)
            mCursor[3] = length;
        
        if(drawCursorX < mDrawClip.left && !isErrorTouchMode)
        {
            scrollBy(drawCursorX - mDrawClip.left - Xoffset, 0);
        } else
        {
            postInvalidate(mDrawClip.left, y - drawRowHeight, mDrawClip.right, y);
        }
    
    }
    
    /**
     * 计算两点间的距离，另见{@link StrictMath#hypot(double, double)} 和 {@link #onTouchZoom(MotionEvent)}。
     * @param event
     * @return 两点间的距离。
     */
    private float getSpacing(MotionEvent event)
    {
        return (float)
                   StrictMath.hypot((event.getX(0) - event.getX(1)),
                                    (event.getY(0) - event.getY(1)));
    }
    
    /**
     * 放缩手势，用于放大或缩小文本。
     * @param event
     * @return
     */
    private boolean onTouchZoom(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_MOVE)
        {
            if(event.getPointerCount() == 2)
            {
                if(lastDist == 0)
                {
                    lastDist = getSpacing(event);
                    lastSize = mTextPaint.getTextSize();
                }
                
                if(lastDist != 0)
                {
                    setTextSize((int) (lastSize * (getSpacing(event) / lastDist)));
                }
                return true;
            }
        }
        lastDist = 0;
        return false;
    }
    
    /**
     * 在选择状态下，交换二个光标值，包括相关数值。
     */
    private void setInterchangeCursor()
    {
        int[] position = new int[2];
        System.arraycopy(mCursor, 2, position, 0, 2);
        System.arraycopy(mCursor, 0, mCursor, 2, 2);
        System.arraycopy(position, 0, mCursor, 0, 2);
        
        int cache = drawCursorX;
        drawCursorX = drawCursorX2;
        drawCursorX2 = cache;
        
        cache = drawCursorY;
        drawCursorY = drawCursorY2;
        drawCursorY2 = cache;
        
        if(mClickCursor == CURSOR_LEFT)
            mClickCursor = CURSOR_RIGHT;
        else
            mClickCursor = CURSOR_LEFT;
    }
    
    /**
     * 移动第二光标，会主动更新视图。
     * @param x 屏幕坐标x
     * @param y 屏幕坐标y
     */
    private void gotoPosition2(int x, int y)
    {
            y = Math.max(Math.min(y, Math.round(mHeight)), drawRowHeight);
        int line = Math.min(y/drawRowHeight, mRowCounts);
        
        mCursor[2] = line;
        drawCursorY2 = (int) (line * drawRowHeight + drawFontMetrics.bottom);// 处理文字坐标问题
    
        int clickIndex = mRowStartCounts[line];
        int drawP = Xoffset;
    
        
        while(drawP < mDrawClip.right && clickIndex < length)
        {
            if(Math.abs(drawP - x) < mCharLitterWidth/2
               || mChar[clickIndex] == TAG.EOL
               || clickIndex + 1 == length)
            {
                mCursor[3] = clickIndex +1;
                drawCursorX2 = drawP;
                isShowCursor = true;
            
                break;
            }
            drawP += drawCharWidth[mChar[clickIndex++]];
        }
        
        if(mCursor[1] > mCursor[3])
        {
            setInterchangeCursor();
        }
        
        if(drawCursorX2 < mDrawClip.left && !isErrorTouchMode)
        {
            scrollBy(drawCursorX2 - mDrawClip.left - Xoffset, 0);
        } else
        {
            postInvalidate(mDrawClip.left, y - drawRowHeight, mDrawClip.right, y);
        }
    }
    
    /**
     * 刷新并跳到指定位置，移动第一光标，会主动更新视图。
     *@param x 屏幕坐标x
     *@param y 屏幕坐标y
     */
    private void gotoPosition(int x, int y)
    {
                y = Math.max(Math.min(y, Math.round(mHeight)), drawRowHeight);
        int line = Math.min(y/drawRowHeight, mRowCounts);
        
        
        int click = x - Xoffset + mCharLitterWidth/2;
        
        if(mSelectMode == SELECT_NONE && click < 0 && !onClickCursor) // 点击行时触发。
        {
            onDebugListener.run(line, (mDebugLines[line] = !mDebugLines[line]));
            
            postInvalidate(0, y-drawRowHeight, mCharChineseWidth, y);
        }
        else
        {
            mCursor[0] = line;
            drawCursorY = (int) (line * drawRowHeight + drawFontMetrics.bottom);// 处理文字坐标问题

            int clickIndex = mRowStartCounts[line];
            int drawP = Xoffset;

            
            while(drawP < mDrawClip.right && clickIndex < length)
            {
                if(Math.abs(drawP - x) < mCharLitterWidth/2
                   || mChar[clickIndex] == TAG.EOL
                   || clickIndex + 1 == length)
                {
                    mCursor[1] = clickIndex + 1;
                    drawCursorX = drawP;
                    isShowCursor = true;

                    break;
                }
                drawP += drawCharWidth[mChar[clickIndex++]];
            }
            
            if(mSelectMode == SELECT_NONE)
            {
                if(drawCursorX < mDrawClip.left && !isErrorTouchMode)
                {
                    scrollBy(drawCursorX - mDrawClip.left - Xoffset, 0);
                } else
                {
                    postInvalidate(mDrawClip.left, y - drawRowHeight, mDrawClip.right, y);
                }
            } else
            {
                if(mCursor[1] > mCursor[3])
                {
                    setInterchangeCursor();
                }
                invalidate(mDrawClip);
            }
            
        }
    }
    
    /**
     *  移动屏幕，使光标存在可视范围之内(具体以可滑动范围为准)
     * @param cx 绝对坐标X
     * @param cy 绝对坐标Y
     */
    private void gotoCursor(int cx, int cy)
    {
        
        int sx = 0;
        int sy = 0;
        
        if(cx - mCharChineseWidth < mDrawClip.left)
        {
            sx = -mCharChineseWidth;
        } else if(cx + mCharChineseWidth > mDrawClip.right)
        {
            sx = mCharChineseWidth;
        }
        
        if(cy - drawRowHeight < mDrawClip.top)
        {
            sy = -drawRowHeight;
        } else if(cy + drawRowHeight > mDrawClip.bottom)
        {
            sy = drawRowHeight;
        }
        scrollBy(sx, sy);
    }
    
    @Override
    public void computeScroll()
    {
        if (mOverScroller.computeScrollOffset() )  {
            scrollTo(mOverScroller.getCurrX(), mOverScroller.getCurrY());
        }
    }
    
    @Override
    protected int computeVerticalScrollRange()
    {
        return Math.round(scrollY);
    }
    
    @Override
    protected int computeVerticalScrollOffset()
    {
        return 0;
    }
    
    @Override
    protected int computeVerticalScrollExtent()
    {
        return getHeight();
    }
    
    @Override
    public boolean canScrollHorizontally(int direction)
    {
        return super.canScrollHorizontally(direction);
    }
    
    @Override
    public boolean canScrollVertically(int direction)
    {
        return true;
    }
    
    @Override
    public void scrollTo(int x, int y)
    {
        mRefreshScreen = true;
        mScannerLock = false;
        super.scrollTo(x, y);
    }
    
    @Override
    public void scrollBy(int x, int y)
    {
        x += getScrollX();
        if(x < 0 || x>scrollX)
        {
            x = getScrollX();
        }
        y += getScrollY();
        if(y < 0 || y>scrollY)
        {
            y = getScrollY();
        }
        scrollTo(x, y);
    }
    
    
    /**
     * 刷新光标位置，并保证其在可视范围之内，可能会主动更新视图。
     * @param dLine 行差
     * @param dPosition 位置差
     * @param drawX X位置差
     * @param isDx 为true 则是x的位置差，否则是x的位置
     * @param smallChange 是否小幅度调整位置。
     */
    public void refreshCursor(int dLine, int dPosition, int drawX, boolean isDx, boolean smallChange)
    {
        mCursor[0] += dLine;
        mCursor[1] += dPosition;
        if(isDx)
            drawCursorX += drawX;
        else
            drawCursorX = drawX;
        drawCursorY += dLine * drawRowHeight;
        if(smallChange)gotoCursor(drawCursorX, drawCursorY);
    }
    
    
    /**
     * 插入一个字符。
     * @param position 保持从开始到其不变位置的字符数。
     * @param c 要插入的字符
     * @param aboutCursor 是否将在光标（前面光标）后加入。
     * @param CLine 光标行位置，仅 aboutCursor = false 有效。
     * @param addUndoStack 操作是否放入栈堆
     */
    public void insertChar(int position, char c, boolean aboutCursor, int CLine, boolean addUndoStack)
    {
        if(setOnlyRead)return;
    
        if(SELECT_NONE != mSelectMode)
        {
            mSelectMode = SELECT_NONE;
            hideClipboardPanel();
        }
        
        removeOutRowsLength();
        mScannerLock = false;
        if(length >= mChar.length)
            extendArray(1);
        else
            length++;
        
        System.arraycopy(mChar, position -1, mChar, position, length - position);
        mChar[position - 1] = c;
        
        if(drawCharWidth[c] == 0) {
            drawCharWidth[c] = (int)(mOtherPaint.measureText(String.valueOf(c))) ;
        }
        int save;
        int line = aboutCursor? mCursor[0] : CLine;
        
        if(addUndoStack)
        {
            if(mOnEditListener != null)
            {
                mOnEditListener.addText(position, String.valueOf(c));
            }
            mUndoStack.addCommand(position, String.valueOf(c), line, UNKNOWN, System.currentTimeMillis(), true);
        }
        
        if(c == TAG.EOL)
        {
            /* 如果是最长行断开，则肯定有控件过宽现象，mMaxRowLength也可能会有精度缺失错误，
             * 但为了保证编辑时性能，这里不做考虑。
             */
            
            // 扩充行和Debug行, 这里按常数扩容。
            if(mRowStartCounts.length - 1 <= mRowCounts)
            {
                int[] l = new int[mRowCounts + 10];
                System.arraycopy(mRowStartCounts, 0, l, 0, mRowCounts + 1);
                mRowStartCounts = l;
                
                boolean[] d = new boolean[l.length];
                System.arraycopy(mDebugLines, 0, d, 0, mRowCounts + 1);
                mDebugLines = d;
            }
            
            line++;
            save = line;
            int moveLength = mRowCounts - line + 1;
            
            if(moveLength > 0)
            {
                // todo 处理断点,
                System.arraycopy(mDebugLines, line, mDebugLines, line + 1, moveLength);
                System.arraycopy(mRowStartCounts, line, mRowStartCounts, line + 1, moveLength);
            }
            
            mRowCounts++;
            mRowStartCounts[line] = mCursor[1];
            
            while (line < mRowCounts)
            {
                mRowStartCounts[++line] += 1;
            }
            
            Xoffset = (String.valueOf(mRowCounts).length() +1) * mCharLitterWidth;
            
            scrollY = mHeight;
            mHeight += drawRowHeight;
            
            if(aboutCursor)
            {
                refreshCursor(1, 1, Xoffset, false, false);
            } else
            {
                mCursor[0] = save;
                mCursor[1] = position + 1;
                drawCursorX = getLineWidth(save, mCursor[1]) + Xoffset;
                drawCursorY = save * drawRowHeight  + (int)drawFontMetrics.bottom;
            }
            
            
            // fix
            if(drawCursorY >= mDrawClip.top && drawCursorY + drawRowHeight <= mDrawClip.bottom)
                //
                if(getScrollX() > 0)
                    scrollTo(0, getScrollY());
                else
                    invalidate(mDrawClip);
            else
                scrollBy(0, drawRowHeight);
        } else
        {
            // 如果是最长行，则进行扩宽。
            int width = drawCharWidth[c];
            if (width + drawCursorX + mCharChineseWidth> getWidth() + getScrollX())
            {
                 mWidth += width;
                scrollX += width;
            }

            while (line < mRowCounts)
            {
                mRowStartCounts[++line] += 1;
            }
    
            if(aboutCursor)
            {
                refreshCursor(0, 1, width, true, false);
            } else
            {
                mCursor[0] = CLine;
                mCursor[1] = position + 1;
                drawCursorX = getLineWidth(CLine, mCursor[1]) + Xoffset;
                drawCursorY = CLine * drawRowHeight + (int)drawFontMetrics.bottom;
            }
            
            if(!mDrawClip.contains(drawCursorX, drawCursorY))
                scrollTo(Math.max(drawCursorX - getWidth() + mCharChineseWidth, 0), getScrollY());
            else
                invalidate(mDrawClip);
        }
        
    }
    
    /**
     * 插入一段字符，注意，这里加入的字符串如果过长，可能会造成卡顿。
     * @param position 保持从开始到其不变位置的字符数。
     * @param text 要插入的字符串
     * @param aboutCursor 是否将在光标后加入。
     * @param CLine1 光标行位置，（如果是光标操作，可填 {@link #UNKNOWN})。
     * @param addUndoStack 操作是否放入栈堆
     */
    public void insert(int position, String text, boolean aboutCursor, int CLine1, int CLine2, boolean addUndoStack)
    {
        if(text.length() < 1 || setOnlyRead)return;
    
        if(SELECT_NONE != mSelectMode)
        {
            mSelectMode = SELECT_NONE;
            hideClipboardPanel();
        }
        
        mScannerLock = false;
        removeOutRowsLength();
        
        int line = aboutCursor? mCursor[0] : CLine1;
        
        int i = 0;
        int addLine = 0; // 插入要增加的行
        int mt = drawCursorX; // 插入的行宽度，默认获取光标所在宽度, 二次更新后则返回目前光标位置。
        
        int[] addLineStart = new int[text.length()];// 元素序列等同于末位置
        char[] code = text.toCharArray();
        
        for(char c : code)
        {
            if(drawCharWidth[c] == 0)
            {
                drawCharWidth[c] = (int)(mOtherPaint.measureText(String.valueOf(c)));
            }
            
            mt += drawCharWidth[c];
            i++;
            
            if(c == TAG.EOL)
            {
                if(mt + getWidth() > mWidth)
                {
                    mWidth = mt + getWidth();
                }
                
                mHeight += drawRowHeight;
                addLineStart[++addLine] = i;
                mt = Xoffset;
            }
            
        }
    
        if(addUndoStack)
        {
            if(mOnEditListener != null)
            {
                mOnEditListener.addText(position, text);
            }
            mUndoStack.addCommand(position, text, line, addLine + line, System.currentTimeMillis(), true);
        }
        
        initScroll();
    
        if(mChar.length - length > code.length)
            length += code.length;
        else
            extendArray(code.length);
        
        System.arraycopy(mChar, position - 1, mChar, position + code.length - 1, length - code.length - position + 1);
        System.arraycopy(code, 0, mChar, position - 1, code.length);
        
        
        if(addLine > 0)
        {
            /* Todo 如果是最长行断开，则肯定有控件过宽现象，mMaxRowLength也可能会有精度缺失错误，
                但为了保证编辑时性能，这里不做考虑。
             */
            
            // 扩充行和Debug行, 这里按常数扩容。
            if(mRowStartCounts.length - 1 <= mRowCounts + addLine)
            {
                int[] l = new int[mRowCounts + 10 + addLine];
                System.arraycopy(mRowStartCounts, 0, l, 0, mRowCounts + 1);
                mRowStartCounts = l;
        
                boolean[] d = new boolean[l.length];
                System.arraycopy(mDebugLines, 0, d, 0, mRowCounts + 1);
                mDebugLines = d;
            }
            
            // 处理断点,
            System.arraycopy(mDebugLines, line, mDebugLines, line + addLine, mRowCounts - line + 1);
            // 行开试处处理
            System.arraycopy(mRowStartCounts, line, mRowStartCounts, line + addLine, mRowCounts - line + 1);
            
            mRowCounts += addLine;
            
            // todo 效率不是最好
            mt -= Xoffset;
            Xoffset = (String.valueOf(mRowCounts).length() +1) * mCharLitterWidth;
            mt += Xoffset;
            
            // 处理将要改变的光标下一行的开始。
            int cache = line + addLine;
            
            while (cache < mRowCounts)
            {
                mRowStartCounts[++cache] += code.length;
            }
            
            int oldPosition = mCursor[1];
                cache = line + addLine;
            
            int p = 1;
            while (line < cache)
            {
                mRowStartCounts[++line] = oldPosition + addLineStart[p++] - 1;
            }
            
            
    
            if(aboutCursor)
            {
                refreshCursor(addLine, code.length, mt, false, false);
            } else
            {
                mCursor[0] = CLine2;
                mCursor[1] = position + code.length;
                drawCursorX = getLineWidth(CLine2, mCursor[1]) + Xoffset;
                drawCursorY = CLine2 * drawRowHeight + (int)drawFontMetrics.bottom;
            }
            
            if((p = getScrollY() + addLine *drawRowHeight) < mDrawClip.bottom - drawRowHeight)
            {
                scrollTo(Math.max(drawCursorX - getWidth() + mCharChineseWidth, 0), getScrollY());
            } else
            {
                scrollTo(Math.max(drawCursorX - getWidth() + mCharChineseWidth, 0), p);
            }
            
            
            System.gc();
            invalidate(mDrawClip);
        } else
        {
            while (line < mRowCounts)
            {
                mRowStartCounts[++line] += code.length;
            }
            int width = (int) mTextPaint.measureText(code, 0, code.length);
            
            if (width + drawCursorX + mCharChineseWidth > getWidth() + getScrollX())
            {
                 mWidth += width;
                scrollX += width;
            }
            
            // TODO 光标位置，非选择状态
            
            if(aboutCursor)
            {
                refreshCursor(0, code.length, mt, false, false);
            } else
            {
                mCursor[0] = CLine1;
                mCursor[1] = position + code.length;
                drawCursorX = getLineWidth(CLine1, mCursor[1]) + Xoffset;
                drawCursorY = CLine1 * drawRowHeight + (int)drawFontMetrics.bottom;
            }
            if(!mDrawClip.contains(drawCursorX, drawCursorY))
                scrollTo(Math.max(drawCursorX - getWidth() + mCharChineseWidth, 0), getScrollY());
            else
                invalidate(mDrawClip);
        }
    }
    
    /**
     * 删除一个字符。
     * @param position 将删除的字符后一位的位置。
     * @param aboutCursor 是否将在光标前删除。
     * @param CLine 光标行位置。（如果是光标操作，可填 {@link #UNKNOWN})。
     * @param addUndoStack 操作是否放入栈堆
     */
    public void deleteChar(int position, boolean aboutCursor, int CLine, boolean addUndoStack)
    {
        if (position > 1 && !setOnlyRead)
        {
            if(SELECT_NONE != mSelectMode)
            {
                mSelectMode = SELECT_NONE;
                hideClipboardPanel();
            }
            
            mScannerLock = false;
            int line = aboutCursor? mCursor[0] : CLine;
            char dc = mChar[position - 2];
            
            int lw = (dc == TAG.EOL)? getLineWidth(line - 1) : -1;

            if(addUndoStack)
            {
                if(mOnEditListener != null)
                {
                    mOnEditListener.deleteText(position, UNKNOWN, String.valueOf(dc));
                }
                mUndoStack.addCommand(position - 1, String.valueOf(dc), line - (dc == TAG.EOL?1:0), UNKNOWN, System.currentTimeMillis(), false);
            }
            
            System.arraycopy(mChar, position - 1, mChar, position - 2, length - position + 1);
            length--;
            
            if(lw >= 0)
            {
                
                removeOutRowsLength();
    
                mHeight = scrollY;
                scrollY-= drawRowHeight;
                
                // 断点处理
                System.arraycopy(mDebugLines, line + 1, mDebugLines, line, mRowCounts - line);
                System.arraycopy(mRowStartCounts, line + 1, mRowStartCounts, line, mRowCounts - line);
                
                
                
                while (line < mRowCounts)
                {
                    mRowStartCounts[line++] -= 1;
                }
                mRowCounts--;
                
                // todo 效率不是最好
                Xoffset = (String.valueOf(mRowCounts).length() +1) * mCharLitterWidth;
    
                if(aboutCursor)
                {
                    refreshCursor(-1, -1, lw + Xoffset, false, false);
                } else
                {
                    mCursor[0] = --CLine;
                    mCursor[1] = position - 1;
                    drawCursorX = getLineWidth(CLine) + Xoffset;
                    drawCursorY = CLine * drawRowHeight + (int)drawFontMetrics.bottom;
                }
                
                if(drawCursorY - drawRowHeight >= mDrawClip.top && drawCursorY + drawRowHeight <= mDrawClip.bottom)
                    if(drawCursorX + mCharLitterWidth < mDrawClip.right)
                        invalidate(mDrawClip);
                    else
                        scrollTo(Math.max(0, drawCursorX - getWidth()%drawCursorX), Math.max( 0, getScrollY() - drawRowHeight));
                else
                    if(drawCursorX + mCharLitterWidth < mDrawClip.right)
                        scrollBy(0, -2 * drawRowHeight);
                    else
                        scrollTo(Math.max(0, drawCursorX - getWidth()%drawCursorX), Math.max( 0, getScrollY() - drawRowHeight));
            } else
            {
                // todo 这里我们不处理最长行问题，以节约性能。
                while (line < mRowCounts)
                {
                    mRowStartCounts[++line] -= 1;
                }
                // TODO 光标位置，非选择状态
                if(aboutCursor)
                {
                    refreshCursor(0, -1, -drawCharWidth[dc], true, true);
                }
                else
                {
                    mCursor[0] = CLine;
                    mCursor[1] = position -1;
                    drawCursorY = CLine * drawRowHeight + (int)drawFontMetrics.bottom;
                    drawCursorX = getLineWidth(CLine, mCursor[1]) + Xoffset;
                }
                
                if(!mDrawClip.contains(drawCursorX, drawCursorY))
                    scrollTo(Math.max(drawCursorX - getWidth() + mCharChineseWidth, 0), getScrollY());
                else
                    invalidate(mDrawClip);
            }
        }
    }
    
    /**
     * 删除一段字符串。
     * @param start 开始的位置（参考 光标 位置）
     * @param end 结束的位置（参考 光标 位置）
     * @param aboutCursor 是否是光标操作
     * @param CLine 第一个光标所在行 （如果是光标操作，可填 {@link #UNKNOWN})
     * @param CLine2 第二个光标所在行 （如果是光标操作，可填 {@link #UNKNOWN})
     * @param addUndoStack 操作是否放入栈堆
     */
    public void delete(int start, int end, boolean aboutCursor, int CLine, int CLine2, boolean addUndoStack)
    {
        
        if(end - start < 1 || setOnlyRead)
        {
            invalidate(mDrawClip);
            return;
        }
        
        if(SELECT_NONE != mSelectMode)
        {
            mSelectMode = SELECT_NONE;
            hideClipboardPanel();
        }
        
        mScannerLock = false;
        int startLine = aboutCursor? mCursor[0] : CLine;
        int endLine = aboutCursor? mCursor[2] : CLine2;
        int dLength = end - start;
        int dx = 0, dy = 0;
    
        if(addUndoStack)
        {
            char[] chars = new char[dLength];
            System.arraycopy(mChar, start - 1, chars, 0, dLength);
            String str = String.valueOf(chars, 0, chars.length);
            if(mOnEditListener != null)
            {
                mOnEditListener.deleteText(start, end, str);
            }
            
            mUndoStack.addCommand(start, str, startLine, endLine, System.currentTimeMillis(), false);
        }
        
        System.arraycopy(mChar, end -1, mChar, start - 1, length - end + 1);
        length -= dLength;
        
        if(startLine == endLine)
        {
            while (startLine < mRowCounts)
            {
                mRowStartCounts[++startLine] -= dLength;
            }
            
            if(!aboutCursor)
            {
                mCursor[0] = CLine;
                mCursor[1] = start;
                drawCursorY = CLine * drawRowHeight + (int)drawFontMetrics.bottom;
                drawCursorX = getLineWidth(CLine, mCursor[1]) + Xoffset;
            }
            
            if(drawCursorX < mDrawClip.left - mCharLitterWidth)
            {
                dx = mDrawClip.left - drawCursorX + mCharChineseWidth;
            }
            else if(drawCursorX > mDrawClip.right - mCharLitterWidth)
            {
                dx = -drawShowX + mDrawClip.right - mCharChineseWidth;
            }
            
            if(drawCursorY < mDrawClip.top)
            {
                dy = -Math.max(mDrawClip.top - drawCursorY - drawRowHeight, 0);
            }
            else if(drawCursorY > mDrawClip.bottom)
            {
                dy = mDrawClip.bottom - drawCursorY;
            }
            
            if(dx == 0 && dy == 0)
            {
                invalidate(mDrawClip);
            } else
            {
                scrollBy(dx, dy);
            }
        }
        else
        {
            removeOutRowsLength();
            int dLine = endLine - startLine;
            
            // 断点处理
            System.arraycopy(mDebugLines, endLine + 1, mDebugLines, startLine + 1, mRowCounts - endLine);
            System.arraycopy(mRowStartCounts, endLine + 1, mRowStartCounts, startLine + 1, mRowCounts - endLine);
            mRowCounts -= dLine;
    
            while (startLine < mRowCounts)
            {
                mRowStartCounts[++startLine] -= dLength;
            }
            
            mHeight -= dLine * drawRowHeight;
            scrollY = mHeight - drawRowHeight;
    
            // todo 效率不是最好
            Xoffset = (String.valueOf(mRowCounts).length() +1) * mCharLitterWidth;
        
            if(aboutCursor)
            {
                // 多行删除的时候很容易出现 Xoffest  前后不匹配导致的光标位置失精。
                drawCursorX = Xoffset + getLineWidth(mCursor[0], mCursor[1]);
            } else
            {
                mCursor[0] = CLine;
                mCursor[1] = start;
                drawCursorY = CLine * drawRowHeight + (int)drawFontMetrics.bottom;
                drawCursorX = getLineWidth(CLine, mCursor[1]) + Xoffset;
            }
            
            if(drawCursorY - drawRowHeight >= mDrawClip.top && drawCursorY + drawRowHeight <= mDrawClip.bottom)
                if(drawCursorX + mCharLitterWidth < mDrawClip.right)
                    invalidate(mDrawClip);
                else
                    scrollTo(Math.max(0, drawCursorX - getWidth()%drawCursorX), Math.max( 0, getScrollY() - drawRowHeight));
            else
            if(drawCursorX + mCharLitterWidth < mDrawClip.right)
                scrollBy(0, -2 * drawRowHeight);
            else
                scrollTo(Math.max(0, drawCursorX - getWidth()%drawCursorX), Math.max( 0, getScrollY() - drawRowHeight));
        }
    }
    
    /**
     * 字符扩容, 并同步长度(length)。
     * @param n 扩容大小
     * @return 字符集
     */
    private void extendArray(int n)
    {
        char[] nc = new char[length + n];
        System.arraycopy(mChar, 0, nc, 0, length);
        mChar = nc;
        length += n;
    }
    
    /**
     * 用二分法查找position所在行。
     * 
     * @param position 位置
     * @return 符合条件的行数
     */
    private int findLine(int position)
    {
        int startLine = 1;
        int rows = mRowCounts;
        int middle;
        while (startLine < rows)
        {
            middle = (rows + startLine - 1) >> 2;
            if(mRowStartCounts[middle] > position)
            {
                rows = middle - 1;
            } else
            {
                startLine = middle + 1;
            }
        }
        return rows;
    }
    
    /**
     *  移除错误且不用的行数据。
     */
    private void removeOutRowsLength()
    {
        int mRowEnd = mRowCounts + 1;
        int mRealLength = mRowStartCounts.length;
        if(mRowEnd < mRealLength)
        {
            int l = mRealLength - mRowEnd;
            int[] cl = new int[l];
            System.arraycopy(cl, 0, mRowStartCounts, mRowEnd, l);
        }
    }
    
    /**
     * 获取一行实际宽度。
     * @param line 要获取的行
     * @return
     */
    private int getLineWidth(int line)
    {
        int start = mRowStartCounts[line];
        char[] c;
        if(line == mRowCounts)
            c = new char[length - start - 1];
        else
            c = new char[mRowStartCounts[line + 1] - start - 1];
        
        System.arraycopy(mChar, start, c, 0, c.length);
        return (int) mOtherPaint.measureText(c, 0, c.length);
    }
    
    /**
     * 获取一行从开头到某处实际宽度。
     * @param line 获取的行。
     * @param position 行内计算结束字符
     * @return
     */
    private int getLineWidth(int line, int position)
    {
        if(position == 0)return 0;
        int start = mRowStartCounts[line];
        char[] c= new char[position - start - 1];
        
        System.arraycopy(mChar, start, c, 0, c.length);
        return (int) mOtherPaint.measureText(c, 0, c.length);
    }
    
    /**
     * 输入处理
     * @return true
     */
    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }
    
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.inputType = InputType.TYPE_CLASS_TEXT
                             | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
        outAttrs.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
                              | EditorInfo.IME_ACTION_DONE
                              | EditorInfo.IME_FLAG_NO_EXTRACT_UI;
        return mInputConnection;
    }
    
    /**
     * 显示键盘
     */
    public void showKeyboard() {
        if(!setOnlyRead)
        {
            mInputMethodManager.showSoftInput(CodeView.this, 0);
        }
        
    }
    
    /**
     * 隐藏键盘
     */
    public void hideKeyboard() {
        mInputMethodManager.hideSoftInputFromWindow(CodeView.this.getWindowToken(), 0);
    }
    
    /**
     * 截取控图片
     * @param x
     * @param y
     * @Name 截图
     */
    public Bitmap getScreenshot(int x, int y)
    {
        if(mRefreshScreen)
        {
            Matrix matrix = new Matrix();
            matrix.setScale(1, 1);
            mActivity.getWindow().getDecorView().setDrawingCacheEnabled(true);
            mBitmapScreen = mActivity.getWindow().getDecorView().getDrawingCache();
            
            int w = (int) (mDisplayMetrics.widthPixels / 2.5);
            int h = drawRowHeight * 4;
            
            y += drawRowHeight * 1.5;
            x -= w/2;
            
            if(x < 0)x = 0;
            if(y < 0)y = 0;
            
            if(x + w > mBitmapScreen.getWidth())
                x = mBitmapScreen.getWidth() - w;
            if(y + h > mBitmapScreen.getHeight())
                y = mBitmapScreen.getHeight() - h;
            
             mBitmapScreen = Bitmap.createBitmap(mBitmapScreen, x, y, w, h, matrix, true);
        }
        
        return mBitmapScreen;
    }
    
    /**
     * 如果可以左移光标，则左移，会主动更新视图。
     */
    public void gotoCursorLeft()
    {
        // 只处理非选择状态
        if(mSelectMode == CodeView.SELECT_NONE && mCursor[1] > 1)
        {
            char beforeChar = mChar[mCursor[1] - 2];
            if(beforeChar == TAG.EOL)
            {
                int beforeLineWidth = Xoffset + getLineWidth(mCursor[0] - 1);
                    refreshCursor(-1, -1, beforeLineWidth, false, false);
                    scrollTo(Math.max(drawCursorX - getWidth(), 0), Math.max(0, getScrollY()-drawRowHeight ));
                    invalidate(mDrawClip);
            } else
            {
                refreshCursor(0, -1, -drawCharWidth[beforeChar], true, true);
                postInvalidate(mDrawClip.left, drawCursorY - drawRowHeight, mDrawClip.right, drawCursorY);
            }
        }
    }
    
    /**
     * 如果可以右移光标，则右移，会主动更新视图。
     */
    public void gotoCursorRight()
    {
        // 只处理非选择状态
        if(mSelectMode == CodeView.SELECT_NONE && mCursor[1] < length)
        {
            char afterChar = mChar[mCursor[1] - 1];
            if(afterChar == TAG.EOL)
            {
                int s = getScrollY() + drawRowHeight;
                refreshCursor(1, 1, Xoffset, false, false);
                scrollTo(0, mDrawClip.bottom > s? getScrollY() : s);
                postInvalidate(mDrawClip.left, drawCursorY - 2 *drawRowHeight, mDrawClip.right, drawCursorY);
            } else
            {
                refreshCursor(0, 1, drawCharWidth[afterChar], true, true);
                postInvalidate(mDrawClip.left, drawCursorY - drawRowHeight, mDrawClip.right, drawCursorY);
            }
        }
    }
    
    /**
     * 如果可以上移光标，则上移，会主动更新视图。
     */
    public void gotoCursorUp()
    {
        int bl;
        if(mSelectMode == CodeView.SELECT_NONE && (bl = mCursor[0]) > 1)
        {
            int l = mRowStartCounts[bl] - mRowStartCounts[bl - 1];
            
            mCursor[0] -= 1;
            
            if (l > mCursor[1] - mRowStartCounts[bl] - 1)
            {
                mCursor[1] -= l;
                drawCursorX = getLineWidth(mCursor[0], mCursor[1]) + Xoffset;
                drawCursorY -= drawRowHeight;
            } else
            {
                mCursor[1] = mRowStartCounts[bl];
                drawCursorY -= drawRowHeight;
                drawCursorX = getLineWidth(mCursor[0]) + Xoffset;
            }
    
            if(drawCursorY <= mDrawClip.top)
            {
                scrollBy(0, -drawRowHeight);
            }else
            {
                postInvalidate(mDrawClip.left, drawCursorY - drawRowHeight, mDrawClip.right, drawCursorY);
            }
        }
    }
    
    /**
     * 如果可以下移光标，则下移，会主动更新视图。
     */
    public void gotoCursorDown()
    {
        int bl;
        if(mSelectMode == CodeView.SELECT_NONE && (bl = mCursor[0]) < mRowCounts)
        {
            // 一行中，光标前的字符数
            int cl = mCursor[1] - mRowStartCounts[bl] - 1;
            // 光标下一行字符总数
            int nl;
            // 光标所在行字符数
            int cnl = mRowStartCounts[bl + 1] - mRowStartCounts[bl];
            
            if(bl + 1 == mRowCounts)
            {
                nl = length - 1 - mRowStartCounts[bl + 1];
            } else
            {
                nl = mRowStartCounts[bl + 2] - mRowStartCounts[bl + 1]-1;
            }
            
            mCursor[0] += 1;
    
            if (cl < nl)
            {
                mCursor[1] += cnl;
                drawCursorX = getLineWidth(mCursor[0], mCursor[1]) + Xoffset;
                drawCursorY += drawRowHeight;
            } else
            {
                mCursor[1] = mRowStartCounts[bl + 1] + nl + 1;
                drawCursorY += drawRowHeight;
                drawCursorX = getLineWidth(mCursor[0]) + Xoffset;
            }
    
            if(drawCursorY >= mDrawClip.bottom)
            {
                scrollBy(0, drawRowHeight);
            }else
            {
                postInvalidate(mDrawClip.left, drawCursorY - drawRowHeight, mDrawClip.right, drawCursorY);
            }
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(useAllDimensions(widthMeasureSpec),
                             useAllDimensions(heightMeasureSpec));
    }
    
    private int useAllDimensions(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int result = MeasureSpec.getSize(measureSpec);
        
        if (specMode != MeasureSpec.EXACTLY && specMode != MeasureSpec.AT_MOST)
        {
            result = Integer.MAX_VALUE;
            Log.e(Tag, "MeasureSpec 无法被描述，已设置为最大值");
        }
        
        return result;
    }
    
    /**
     * 粘贴-从剪切板上。
     */
    public void setPasteInClipboardManager()
    {
        if(mClipboardManager == null || mClipboardManager.getPrimaryClip() == null)
            return;
        String str =  mClipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
        if(str.length() < 1)return;
    
        if(mSelectMode == SELECT_ING && mCursor[1] < mCursor[3])
        {
            delete(mCursor[1], mCursor[3], true, UNKNOWN, UNKNOWN, true);
            if(str.length() == 1)
            {
                insertChar(mCursor[1], str.charAt(0), true, UNKNOWN, true);
            } else
            {
                insert(mCursor[1], str, true, UNKNOWN, UNKNOWN, true);
            }
        } else
        {
            mSelectMode = SELECT_NONE;
            if(str.length() == 1)
            {
                insertChar(mCursor[1], str.charAt(0), true, UNKNOWN, true);
            } else
            {
                insert(mCursor[1], str, true, UNKNOWN, UNKNOWN, true);
            }
        }
    }
    /**
     * 返回输入操作的实例
     */
    @SuppressLint("all")
    public CodeViewInputConnection getInputClass() {return mInputConnection;}
    
    private static class CodeViewInputConnection implements InputConnection
    {
        private CodeView mCodeView;
        
        CodeViewInputConnection(CodeView codeView)
        {
            mCodeView = codeView;
        }
    
        /**
         * 取得当前光标位置前的 n 个字符文本，仅未选择时有效。
         * 本此方法在输入连接无效（如线程冲突）或客户端等待时间过长（等待几秒返回）时可能会失败。上述情况时返回null值。
         * @param n 期望的文本长度
         * @param flags 提供附加选项控制，控制文本如何返回。
         * @return 返回当前光标前的文本，返回的文本长度可能小于 n
         */
        @Override
        public CharSequence getTextBeforeCursor(int n, int flags)
        {
//            if(mCodeView.mSelectMode == CodeView.SELECT_NONE)
//            {
//                int position = Math.max(0, mCodeView.mCursor[1] - n - 1);
//
//                char[] c = new char[position == 0? mCodeView.mCursor[1] - 1 : n];
//                System.arraycopy(mCodeView.mChar, position, c, 0, c.length);
//
//                Kit.printout("getTextBeforeCursor", n, flags);
//                return String.valueOf(c);
//            }
            return null;
        }
    
        /**
         * @deprecated
         * 取得当前光标位置后的 n 个字符文本，仅未选择时有效。
         * 此方法在输入连接无效时（如线程冲突）或客户端等待时间过长（等待几秒返回）时可能会失败。上述情况时返回null值。
         * @param n 期望的文本长度
         * @param flags 提供附加选项控制，控制文本如何返回。
         * @return 返回当前光标后的文本，返回的文本长度可能小于 n
         */
        @Override
        public CharSequence getTextAfterCursor(int n, int flags)
        {
//            if(mCodeView.mSelectMode == CodeView.SELECT_NONE && n > 0)
//            {
//                int position = Math.min(mCodeView.length, mCodeView.mCursor[1] + n);
//
//                char[] c = new char[position - mCodeView.mCursor[1]];
//
//                System.arraycopy(mCodeView.mChar, mCodeView.mCursor[1] - 1, c, 0, c.length);
//                Kit.printout("getTextAfterCursor", n, flags);
//                return String.valueOf(c);
//            }
            return null;
        }
    
        /**
         * @deprecated
         * 如果有的话取得所选的文本。
         * 此方法在输入连接无效时（如线程冲突）或客户端等待时间过长（等待几秒返回）时可能会失败。上述情况时返回null值。
         * @param flags
         * @return  如果有的话返回当前选取文本，如果没有文本被选中返回null。
         */
        @Override
        public CharSequence getSelectedText(int flags)
        {
            if(mCodeView.mSelectMode == CodeView.SELECT_ING)
            {
                int s = mCodeView.mCursor[1];
                int e = mCodeView.mCursor[3];
                char[] c = new char[e - s];
                System.arraycopy(mCodeView.mChar, s - 1, c, 0, c.length);
                
                Kit.printout("getSelectedText", flags);
                return String.valueOf(c);
            }
            return null;
        }
    
        /**
         * @deprecated
         * 取得当前光标位置的文本的大小写状态。
         * @param reqModes  无效参数
         * @return 0
         */
        @Override
        public int getCursorCapsMode(int reqModes)
        {
            Kit.printout("getCursorMode", reqModes);
            
            int position;
            if((position = mCodeView.mCursor[1]) > 1)
            {
                char c = mCodeView.mChar[position - 2];
                if(Character.isJavaIdentifierPart(c))
                {
                    return InputType.TYPE_TEXT_FLAG_CAP_WORDS;
                } else if(Character.isDigit(c))
                {
                    return InputType.TYPE_TEXT_FLAG_MULTI_LINE;
                }else
                {
                    return InputType.TYPE_TEXT_FLAG_AUTO_CORRECT;
                }
            }
            return InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        }
    
        /**
         * {@deprecated}
         * 已过时。
         * 获取当前输入连接的编辑器中的当前文本，并监视是否有变化。函数返回当前文本，当文本变化时输入连接可选择性向输入法发送更新。
         * 此方法在输入联接无效时（如线程冲突）或客户端等待时间过长（等待几秒返回）时可能会失败。上述情况时返回null值。
         * @param request 描述文本如何返回
         * @param flags 控制客户端的附加选项，0或GET_EXTRACTED_TEXT_MONITOR
         * @return 返回一个ExtractedText对象描述文本视窗的状态，及所包含的提取文本。
         */
        @Override
        public ExtractedText getExtractedText(ExtractedTextRequest request, int flags)
        {
            return null;
        }
    
        /**
         * {@deprecated}
         * 已过时
         * 删除当前光标前的beforeLength个字符，并删除当前光标后的afterLength个字符，不包联想输入(composing)的文字。
         * @param beforeLength 删除的当前光标之前字符个数。
         * @param afterLength 删除的当前光标之后字符个数。
         * @return 成功返回true，当连接无效时返回flase。
         */
        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength)
        {
            return false;
        }
    
        /**
         * {@deprecated}
         * 已过时
         * @param beforeLength
         * @param afterLength
         * @return false
         */
        @Override
        public boolean deleteSurroundingTextInCodePoints(int beforeLength, int afterLength)
        {
            return false;
        }
    
        /**
         * {@deprecated}
         * 已过时
         * 将当前的光标旁正在联想文本(composing text)替代为给定文本，并设置新光标位置。以前设置的正在编辑文本自动移除。
         *如：
         * <code>
         * InputConnection ic = getCurrentInputConnection();
         *
         * ic.setComposingText("Composi", 1);
         *<code/>
         * @param text 如必要正在编辑文本有样式。如文本没有附带样式对象，正在编辑文本将应用默认样式。
         *             见如何在文本上附加样式{#link android.text.Spanned}。
         *                                {#link android.text.SpannableString}和
         *                                {#link   android.text.SpannableStringBuilder}是两种界面实现方式。
         * @param newCursorPosition 文本范围内新的光标位置。
         *                          如果大于0，从提交文本末尾－1处计起；
         *                          <= 0，提交文本开始处计起。
         *                          所以值为1时，光标将定位于你刚刚插入文本之后。
         *                          注意你不能光标定位于提交文本中，
         *                          因为编辑器可以修改你提供的文本，所以不必将光标定位在哪。
         * @return 成功返回true，当输入联接无效时返回false。
         */
        @Override
        public boolean setComposingText(CharSequence text, int newCursorPosition)
        {
            return false;
        }
    
        /**
         * {@deprecated}
         * 已过时
         *  将特定区域设为正在编辑文本。以前设置的正在编辑文本自动移除。
         *  文本使用默认正在编辑文本样式。(注：”composing text”的需要后期用例子来推测其含义。)
         * @param start 正在编辑文本开始的位置
         * @param end 正在编辑文本结束的位置。
         * @return
         */
        @Override
        public boolean setComposingRegion(int start, int end)
        {
            return false;
        }
    
        /**
         * 强制结束文本编辑，无论联想输入(composing text)是否激活。
         * 文本保持不变，移除任何与此文本的编辑样式或其他状态。光标保持不变。
        */
        
        @Override
        public boolean finishComposingText()
        {
            mCodeView.hideKeyboard();
            return true;
        }
    
        /**
         * 向文本框提交文本并设置新的光标位置。之前设置的正编辑文字将自动删除。
         * @param text 提交的文本。
         * @param newCursorPosition 文本内新的光标位置。
         * @return
         */
        @Override
        public boolean commitText(CharSequence text, int newCursorPosition)
        {
            mCodeView.isShowCursor = false;
            
            if(mCodeView.mSelectMode == CodeView.SELECT_ING)
            {
                mCodeView.mSelectMode = CodeView.SELECT_NONE;
                mCodeView.hideClipboardPanel();
                mCodeView.delete(mCodeView.mCursor[1], mCodeView.mCursor[3], true, UNKNOWN, UNKNOWN, true);
            }
            
            int l = text.length();
            if(l == 1)
            {
                mCodeView.insertChar(mCodeView.mCursor[1], text.charAt(0), true, UNKNOWN,true);
                mCodeView.mPluginUI.showAuto(
                    mCodeView.drawCursorY,
                    mCodeView.mDrawClip.bottom,
                    text.charAt(0),
                    mCodeView.mChar,
                    mCodeView.mCursor[1],
                    mCodeView.mCursor[0]);
            } else
            {
                mCodeView.insert(mCodeView.mCursor[1], text.toString(), true, UNKNOWN, UNKNOWN,true);
            }
            return true;
        }
    
        /**
         * 提交用户的选择提交的结果，选择先前向
         * {@link android.view.inputmethod.InputMethodSession#displayCompletions(CompletionInfo[])}
         * 提交的选项中的一个。
         * 其结果就像用户从实际UI中做出一样。
         */
        @Override
        public boolean commitCompletion(CompletionInfo text)
        {
            return false;
        }
    
        @Override
        public boolean commitCorrection(CorrectionInfo correctionInfo)
        {
            return false;
        }
    
        /**
         * 全选
         * @param start
         * @param end
         * @return
         */
        @Override
        public boolean setSelection(int start, int end)
        {
            return false;
        }
    
        /**
         * {@deprecated}
         * 已过时
         * 让编辑器执行一个它可以完成的操作。
         * @param editorAction 必须是动作常量EditorInfo.editorType中的一个，如EditorInfo.EDITOR_ACTION_GO。
         * @return 成功返回true,如输入连接无效返回false。
         */
        
        @Override
        public boolean performEditorAction(int editorAction)
        {
            Kit.printout("performEditorAction", editorAction);
            return false;
        }
    
        /**
         * {@deprecated}
         * 已过时
         * 在区域中执行调用上下文菜单动作，其id可能下列之一：
         * selectAll, startSelectingText, stopSelectingText, cut, copy, paste, copyUrl, 或 switchInputMethod。
         */
        @Override
        public boolean performContextMenuAction(int id)
        {
            // 粘贴
            if(id == 16908322)
            {
                mCodeView.setPasteInClipboardManager();
            }else
                Kit.printout("performContextMenuAction", id);
//            switch (id)
//            {
//                case InputConnection.
//            }
            
            return false;
        }
    
        /**
         * {@deprecated}
         * 已过时
         * 通知编辑器你将开始批量编辑操作。编辑器尽量避免向你发送状态更新，直到调用endBatchEdit()为止。
         */
        
        @Override
        public boolean beginBatchEdit()
        {
            return false;
        }
    
        /**
         * {@deprecated}
         * 已过时
         * 调用endBatchEdit()方法通知编辑器之前开始的批量编辑已完成。
         */
        
        @Override
        public boolean endBatchEdit()
        {
            return false;
        }
    
        /**
         * 向当前输入连接所附着的进程发送按键事件。
         * 事件像普通按键事件一样由当前焦点，通常是提供InputConnection的视图。
         * 但由于协议的异步性这一点并不总是这样，焦点可能在事件收到时发生改变。
         * 本方法可用于向应用程序发送按键事件。如屏幕键盘可以用这一方法模拟硬件键盘。
         * 标准键盘有三种：数字（12键），预测键盘（20键）和字母（QWERTY）。
         * 你可以通过事件的设备码（device id）确定键盘类型。
         * 在你向本API发送的所有按键事件中，你可能希望设置KeyEvent.FLAG_SOFT_KEYBOARD标志，但这一标志不可设置。
         * @param event 按键事件
         * @return 成功返回true，当输入连接无效返回false。
         */
        @Override
        public boolean sendKeyEvent(KeyEvent event)
        {
            if(event.getAction()  != KeyEvent.ACTION_UP) return false;
            
           switch (event.getKeyCode())
           {
               case KeyEvent.KEYCODE_DEL :
                   mCodeView.isShowCursor = false;
                   
                   if(mCodeView.mSelectMode == CodeView.SELECT_ING)
                   {
                       mCodeView.mSelectMode = CodeView.SELECT_NONE;
                       mCodeView.hideClipboardPanel();
                       mCodeView.delete(mCodeView.mCursor[1], mCodeView.mCursor[3], true, UNKNOWN, UNKNOWN,true);
                   } else
                   {
                       
                       mCodeView.deleteChar(mCodeView.mCursor[1], true, UNKNOWN,true);
                   }
                  
                   break;
               case KeyEvent.KEYCODE_ENTER :
                   if(mCodeView.mSelectMode == CodeView.SELECT_ING)
                   {
                       mCodeView.delete(mCodeView.mCursor[1], mCodeView.mCursor[3], true, UNKNOWN, UNKNOWN,true);
                       mCodeView.mSelectMode = CodeView.SELECT_NONE;
                       mCodeView.hideClipboardPanel();
                   }
                   mCodeView.isShowCursor = false;
//                   mCodeView.insert(mCodeView.mCursor[1],
//                       mCodeView.mjavaScriptCompletion.input(
//                           mCodeView.mChar,
//                           null,
//                           mCodeView.mCursor[1],
//                           "\n",
//                           mCodeView.mRowStartCounts,
//                           mCodeView.mCursor[0]),
//            true,
//                       UNKNOWN,
//                       UNKNOWN,
//                       true);
                   //mCodeView.mjavaScriptCompletion.input(mCodeView.mChar, null, mCodeView.mCursor[1], "\n", mCodeView.mRowStartCounts, mCodeView.mCursor[0]);
                   mCodeView.insertChar(mCodeView.mCursor[1], TAG.EOL, true, UNKNOWN, true);
                   break;
                   
// Todo 这个暂时由全局方法onKeyDown里实现，本段暂时作废。
//               case KeyEvent.KEYCODE_DPAD_LEFT :
//                   mCodeView.gotoCursorLeft();
//                   break;
//
//               case KeyEvent.KEYCODE_DPAD_RIGHT:
//                   mCodeView.gotoCursorRight();
//                   break;
//
//               case KeyEvent.KEYCODE_DPAD_UP:
//                   mCodeView.gotoCursorUp();
//                   break;
//                case KeyEvent.KEYCODE_DPAD_DOWN:
//                    mCodeView.gotoCursorDown();
//                   break;
               default:
               {
                  Kit.printout("Unknown : sendKeyEvent =" + event.toString());
                   return false;
               }
           }
           return true;
        }
    
        /**
         * {@deprecated}
         * 已过时
         * 在指定的输入连接中清除指定的元键(meta key)按下状态。
         * @param states 清除的状态，可以是KeyEvent.getMetaState()中的一位或多位结果。
         * @return  成功返回true，当连接无效时返回false。
         * 参见
         *
         *              KeyEvent
         *
         *              NUMERIC
         *
         *              PREDICTIVE
         *
         *              ALPHA
         */
        @Override
        public boolean clearMetaKeyStates(int states)
        {
            return false;
        }
    
        /**
         * @deprecated
         * 已过时
         * 由IME调用，通知客户端将在全屏与普通模式间切换。它在 InputMethodService的标准实现中被调用。
         * @param enabled
         * @return
         */
        @Override
        public boolean reportFullscreenMode(boolean enabled)
        {
            return false;
        }
    
        /**
         * {@deprecated}
         * 已过时
         * API从输入法向所连接的编辑器发送私有命令。
         * 这可用于提供仅用于特定输入法及其客户端功能的特定域（domain-specific）。
         * 注意，因为InputConnection协议是异步的，你无法取回结果或知道客户端是否懂得命令；
         * 你可能使用 EditorInfo来确定客户端是否支持某一命令。
         * @param action 要执行的命令名称。必须是作用域名，前缀你自己的包名，这样不同的开发者就不会建立让人冲突的命令
         * @param data 命令中的数据
         * @return  当命令发送后返回true（无论相关的编辑是否理解它），如输入连接无效返回false。
         */
        @Override
        public boolean performPrivateCommand(String action, Bundle data)
        {
            return false;
        }
    
        @Override
        public boolean requestCursorUpdates(int cursorUpdateMode)
        {
            return false;
        }
    
        @Override
        public Handler getHandler()
        {
            return null;
        }
    
        @Override
        public void closeConnection()
        {
        
        }
    
        @SuppressLint("all")
        @Override
        public boolean commitContent(InputContentInfo inputContentInfo, int flags, Bundle opts)
        {
            return false;
        }
    
    }
    
    
    private ArrayList<JavaScript.Module> modulesJs;
    private ArrayList<Java.Module> modulesJava;
    
    @SuppressLint("all")
    private int drawEndLine = 0;
    
    @SuppressLint("all")
    private int Xoffset = 0, // 左偏移量，即行号宽度
    
                drawY = 0,
                drawX = 0,
    
                drawShowX = 0, // 会被看到的文本绘制x
                drawTextIndex = 0,
    
                drawCursorY = 0, // 绘制默认光标的位置
                drawCursorX = 0,
    
                drawCursorX2 = 0,// 绘制第二光标的位置
                drawCursorY2 = 0,
    
                drawLine = 1,// 要被绘制的行号
                index = 0 ;// 引索
    
    private int[] drawCharWidth;// 字符宽度
    
    @SuppressLint("all")
    private char drawChar;
    private char[] drawText;// x
    
    @SuppressLint("all")
    private boolean isDrawLine = false;// 所在区域是否需要绘制行号
    
    @SuppressLint("all")
    private int cacheLeft = 0;// 关于左侧绘制的缓存单元
    
    @SuppressLint("all")
    private int cacheWidth = 0;// 关于行缓存单元
    
    @SuppressLint("all")
    private JavaScript.Module moduleJs;
    
    @SuppressLint("all")
    private Java.Module moduleJava;
    
    @SuppressLint("all")
    private int indexOfModules = 0;
    
    @SuppressLint("all")
    private int hIndex = 0;// 当前词组长度
    
    @SuppressLint("all")
    private int hLength = 0;
    
    private int dIndex = 0;
    
    @SuppressLint("all")
    private int dLength = 0; // 要绘制文本的长度
    
    @SuppressLint("all")
    private int dWidth = -1;
    /**
     *  绘制：
     *   不可视：
     *      左侧 = 只记录宽度，若 达到EOL直接跳下一行。
     *      右侧 = 绘制 + 跳过本行（需要得知下一行位置）。
     *   中间 = 记录需要绘制的文本。
     * @param canvas
     */
    @Override
    @SuppressLint("all")
    protected void onDraw(Canvas canvas)
    {
        mDrawClip.offsetTo(getScrollX(), getScrollY());
    
        drawLine   = Math.min(mRowCounts, mDrawClip.top / drawRowHeight + 1);// 将要被绘制行。
        drawY      = drawLine * drawRowHeight; // base line
        index      = mRowStartCounts[drawLine];// 绘制引索
        drawX      = Xoffset;
        isDrawLine = mDrawClip.left < Xoffset;
        cacheLeft  = mDrawClip.left - mCharChineseWidth;
        
        // 绘制滑动条
        if(mVerticalScrollBar && isShowBar)
        {
            
            canvas.drawBitmap(
                mBitmapScrollBar,
                getWidth() - mBitmapScrollBar.getWidth() + getScrollX(),
                //Math.min(
                    mDrawClip.top + (mDrawClip.top + 1) * (getHeight() - mBitmapScrollBar.getHeight()) / getViewHeigth(),
                null);
        }
        
        // 绘制行线
        if(isDrawLine) {
            canvas.drawLine(Xoffset - mCharLitterWidth, mDrawClip.top, Xoffset - mCharLitterWidth, mDrawClip.bottom, mLinePaint);
        }

        if(mSelectMode == SELECT_ING)
        {
            // 绘制选择行
           if(mDrawClip.top  <= drawCursorY2 && mDrawClip.bottom >= drawCursorY)
           {
               if(mCursor[1] == mCursor[3])
               {
                   canvas.drawBitmap(mBitmapLeftCursor, drawCursorX - mBitmapSelectSize, drawCursorY, null);
                   canvas.drawBitmap(mBitmapRightCursor, drawCursorX2, drawCursorY2,null);
               }else
               {
                   if(mCursor[0] == mCursor[2])
                   {
                       canvas.drawRect(drawCursorX , drawCursorY - drawRowHeight , drawCursorX2, drawCursorY2, mOtherPaint);
                       canvas.drawBitmap(mBitmapLeftCursor, drawCursorX - mBitmapSelectSize, drawCursorY, null);
                       canvas.drawBitmap(mBitmapRightCursor,drawCursorX2, drawCursorY2,null);
                   } else
                   {
                       canvas.drawRect(drawCursorX , drawCursorY - drawRowHeight , mDrawClip.right, drawCursorY, mOtherPaint);
                       canvas.drawRect(Math.max(Xoffset, mDrawClip.left), drawCursorY, mDrawClip.right, drawCursorY2-drawRowHeight, mOtherPaint);
                       canvas.drawRect(Math.max(Xoffset, mDrawClip.left), drawCursorY2 - drawRowHeight, drawCursorX2, drawCursorY2, mOtherPaint);
        
                       canvas.drawBitmap(mBitmapLeftCursor, drawCursorX - mBitmapSelectSize, drawCursorY, null);
                       canvas.drawBitmap(mBitmapRightCursor,drawCursorX2, drawCursorY2,null);
                   }
               }
           }
        } else
        {
            if(mDrawClip.top <= drawCursorY && mDrawClip.bottom >= drawCursorY)
            {
                // 绘制光标行
                mLinePaint.setAlpha(30);
                canvas.drawRect(mDrawClip.left, drawCursorY - drawRowHeight , mDrawClip.right, drawCursorY, mLinePaint);
        
                // 绘制光标柱
                if(mDrawClip.left <= drawCursorX && mDrawClip.right >= drawCursorX)
                {
                    mLinePaint.setAlpha(255);
                    canvas.drawRect(drawCursorX - 2, drawCursorY - drawRowHeight, drawCursorX, drawCursorY, mLinePaint);
                    if(isShowCursor)
                    {
                        canvas.drawBitmap(mBitmapCursor, drawCursorX - mBitmapSize, drawCursorY, null);
                    }
                }
            }
        }
        
        mLinePaint.setAlpha(55);
        
        // 要显示的每个字符
       if(isUseLanguage)
       {
    
           indexOfModules = 0;
           hIndex = 0;
           dWidth = -1;
           
           if(selectLanguage == LANGUAGE_NATIVE_JAVA)
           {
               // 这里处理当前Java对应高亮语法
               if(!mScannerLock)
               {
                   drawEndLine =  Math.min(mRowCounts, mDrawClip.bottom / drawRowHeight + drawLine - 1);
                   mScannerLock = true;
                   
                   mTokenJava.set(mChar, mRowStartCounts, length);
                   mTokenJava.token(index, drawLine, (drawLine == 1 || drawEndLine == mRowCounts)? length : Math.min(mRowStartCounts[drawEndLine], length) , drawEndLine);
                   modulesJava = mTokenJava.get();
               }
               else
               {
                   indexOfModules = 0;
               }
               if(modulesJava.size() > 0)
               {
                   moduleJava = modulesJava.get(indexOfModules++);
                   hIndex = 0;
                   dLength = 0;
                   hLength = moduleJava.getSecond();
               }
               else
               {
                   if(isDrawLine)
                   {
            
                       if(mDebugLines[drawLine])
                       {
                           mLinePaint.setColor(Color.RED);
                           canvas.drawText(Integer.toString(drawLine), Xoffset - mCharLitterWidth, drawY, mLinePaint);
                           mLinePaint.setColor(mTheme.getLineCountColor());
                           mLinePaint.setAlpha(55);
                       }
                       else {
                           canvas.drawText(Integer.toString(drawLine), Xoffset - mCharLitterWidth, drawY, mLinePaint);
                       }
                   }
        
                   return;
               }
    
    
    
               main :
               while(index < length && drawLine <= mRowCounts && drawY < mDrawClip.bottom)
               {
                   drawChar = mChar[index++];
        
                   if (mDrawClip.left > drawX)
                   {
                       cacheWidth = drawX;
            
                       while (cacheWidth < cacheLeft && index < length)
                       {
                           cacheWidth += drawCharWidth[drawChar];
                
                           hIndex++;
                           dLength++;
                
                           if(moduleJava != null && hIndex == moduleJava.getSecond())
                           {
                               hIndex = 0;
                               dLength = 0;
                    
                               if(modulesJava.size() > indexOfModules)
                               {
                                   moduleJava = modulesJava.get(indexOfModules++);
                                   hLength = moduleJava.getSecond();
                               }
                           }
                
                           if(drawChar == TAG.EOL)
                           {
                               cacheWidth = Xoffset;
                               drawLine++;
                               drawY += drawRowHeight;
                    
                               if(drawY > mDrawClip.bottom)break main;
                           }
                
                           drawChar = mChar[index++];
                       }
            
                       drawX = cacheWidth;
                   }
        
                   if(drawShowX <= getWidth() && drawChar != TAG.EOL)
                   {
                       if(dWidth < 0)
                       {
                           dWidth = drawX;
                       }
            
                       // 中间
                       drawText[drawTextIndex++] = drawChar;
                       drawShowX += drawCharWidth[drawChar];
            
                       hIndex++;
            
                       if(index == length)
                       {
                           dLength = 0;
                
                           if(isDrawLine)
                           {
                    
                               if(mDebugLines[drawLine])
                               {
                                   mLinePaint.setColor(Color.RED);
                                   canvas.drawText(Integer.toString(drawLine), Xoffset - mCharLitterWidth, drawY, mLinePaint);
                                   mLinePaint.setColor(mTheme.getLineCountColor());
                                   mLinePaint.setAlpha(55);
                               }
                               else {
                                   canvas.drawText(Integer.toString(drawLine), Xoffset - mCharLitterWidth, drawY, mLinePaint);
                               }
                           }
                
                           dWidth = -1;
                           drawShowX = 0;
                           drawTextIndex = 0;
                           hIndex = 0;
                           dIndex = 0;
                
                           drawX = Xoffset;
                           break main;
                       }
            
                       if(moduleJava != null && hIndex >= moduleJava.getSecond())
                       {
                
                           mTextPaint.setColor(mTheme.getColor(moduleJava.getFirst()));
                
                           canvas.drawText(drawText, dIndex, hIndex - dLength, dWidth , drawY, mTextPaint);
                           dIndex += (hIndex - dLength);
                           dLength = 0;
                
                           hIndex = 0;
                
                           dWidth = drawShowX + drawX;
                
                           if(modulesJava.size() > indexOfModules)
                           {
                               moduleJava = modulesJava.get(indexOfModules++);
                               hLength = moduleJava.getSecond();
                           }
                       }
                   }
                   else
                   {
                       drawShowX = 0;
                       dLength = 0;
                       drawShowX = 0;
                       drawTextIndex = 0;
                       // 绘制行号
                       if(isDrawLine)
                       {
                
                           if(mDebugLines[drawLine])
                           {
                               mLinePaint.setColor(Color.RED);
                               canvas.drawText(Integer.toString(drawLine), Xoffset - mCharLitterWidth, drawY, mLinePaint);
                               mLinePaint.setColor(mTheme.getLineCountColor());
                               mLinePaint.setAlpha(55);
                           }
                           else {
                               canvas.drawText(Integer.toString(drawLine), Xoffset - mCharLitterWidth, drawY, mLinePaint);
                           }
                       }
            
                       if(drawChar == TAG.EOL)
                       {
                           hLength = 0;
                           if(modulesJava.size() > indexOfModules)
                           {
                               moduleJava = modulesJava.get(indexOfModules++);
                               hLength = moduleJava.getSecond();
                           }
                       }
                       else
                       //if(drawShowX > getWidth())
                       {
                
                           mTextPaint.setColor(mTheme.getColor(moduleJava.getFirst()));
                
                           canvas.drawText(drawText, dIndex, hIndex, dWidth, drawY, mTextPaint);
                
                           moduleJava = mTokenJava.findInCache(drawLine + 1, moduleJava);
                
                           if(moduleJava == null)
                           {
                               indexOfModules = 0;
                               hLength = 0;
                               dWidth = -1;
                               hIndex = 0;
                               dIndex = 0;
    
                               drawX = Xoffset;
                               break main;
                           } else
                           {
                               indexOfModules = moduleJava.modulePosition + 1;
                               hLength = moduleJava.getSecond();
                           }
                
                
                       }
    
                       dWidth = -1;
                       hIndex = 0;
                       dIndex = 0;
                       drawX = Xoffset;
                       
                       if(drawY >= mDrawClip.bottom || drawLine >= mRowCounts)break main;
            
                       drawY += drawRowHeight;
                       index = mRowStartCounts[++drawLine];
            
                   }
               }
    
           } else
           {
               // 这里处理当前JavaScript对应高亮语法
               // 尽可能的压缩解析次数，在位置没有移动时候不会解析，比如光标移动时。
               if(!mScannerLock)
               {
                   drawEndLine =  Math.min(mRowCounts, mDrawClip.bottom / drawRowHeight + drawLine - 1);
                   mScannerLock = true;
                   
                   mTokenJavaScript.set(mChar, mRowStartCounts, length);
                   mTokenJavaScript.token(index, drawLine, (drawLine == 1 || drawEndLine == mRowCounts)? length : Math.min(mRowStartCounts[drawEndLine], length) , drawEndLine);
                   modulesJs = mTokenJavaScript.get();
               }
               else
               {
                   indexOfModules = 0;
               }
               
               if(modulesJs.size() > 0)
               {
                   moduleJs = modulesJs.get(indexOfModules++);
                   hIndex = 0;
                   dLength = 0;
                   hLength = moduleJs.getSecond();
               }
               else
               {
                   // 独立表示空字符状态。
                   if(isDrawLine)
                   {
            
                       if(mDebugLines[drawLine])
                       {
                           mLinePaint.setColor(Color.RED);
                           canvas.drawText(Integer.toString(drawLine), Xoffset - mCharLitterWidth, drawY, mLinePaint);
                           mLinePaint.setColor(mTheme.getLineCountColor());
                           mLinePaint.setAlpha(55);
                       }
                       else {
                           canvas.drawText(Integer.toString(drawLine), Xoffset - mCharLitterWidth, drawY, mLinePaint);
                       }
                   }
        
                   return;
               }
    
               main :
               while(index < length && drawLine <= mRowCounts)
               {
                   drawChar = mChar[index++];
        
                   // 左侧不可视范围只记录数值。
                   if (mDrawClip.left > drawX)
                   {
                       cacheWidth = drawX;

                       while (cacheWidth < cacheLeft && index < length)
                       {
                           cacheWidth += drawCharWidth[drawChar];

                           hIndex++;
                           dLength++;

                           if(moduleJs != null && hIndex == moduleJs.getSecond())
                           {
                               hIndex = 0;
                               dLength = 0;

                               if(modulesJs.size() > indexOfModules)
                               {
                                   moduleJs = modulesJs.get(indexOfModules++);
                                   hLength = moduleJs.getSecond();
                               }
                           }

                           if(drawChar == TAG.EOL)
                           {
                               cacheWidth = Xoffset;
                               drawLine++;
                               drawY += drawRowHeight;

                               if(drawY > mDrawClip.bottom)break main;
                           }

                           drawChar = mChar[index++];
                       }

                       drawX = cacheWidth;
                       
                   }
                   
                   //可视的绘制部分
                   if(drawShowX <= getWidth() + mCharChineseWidth && drawChar != TAG.EOL)
                   {
                       if(dWidth < 0)
                       {
                           dWidth = drawX;
                       }
                       // 中间
                       drawText[drawTextIndex++] = drawChar;
                       drawShowX += drawCharWidth[drawChar];
            
                       hIndex++;
                       
                       if(index == length)
                       {
                           dLength = 0;
                
                           if(isDrawLine)
                           {
                    
                               if(mDebugLines[drawLine])
                               {
                                   mLinePaint.setColor(Color.RED);
                                   canvas.drawText(Integer.toString(drawLine), Xoffset - mCharLitterWidth, drawY, mLinePaint);
                                   mLinePaint.setColor(mTheme.getLineCountColor());
                                   mLinePaint.setAlpha(55);
                               }
                               else {
                                   canvas.drawText(Integer.toString(drawLine), Xoffset - mCharLitterWidth, drawY, mLinePaint);
                               }
                           }
                
                           dWidth = -1;
                           drawShowX = 0;
                           drawTextIndex = 0;
                           hIndex = 0;
                           dIndex = 0;
                
                           drawX = Xoffset;
                           break main;
                       }
            
                       if(moduleJs != null && hIndex >= moduleJs.getSecond())
                       {
                
                           mTextPaint.setColor(mTheme.getColor(moduleJs.getFirst()));
                
                           canvas.drawText(drawText, dIndex, hIndex - dLength, dWidth , drawY, mTextPaint);
                           dIndex += (hIndex - dLength);
                           dLength = 0;
                
                           hIndex = 0;
                
                           dWidth = drawShowX + drawX;
                
                           if(modulesJs.size() > indexOfModules)
                           {
                               moduleJs = modulesJs.get(indexOfModules++);
                               hLength = moduleJs.getSecond();
                           }
                       }
                   }
                   else
                   {
                       drawShowX = 0;
                       dLength = 0;
                       drawShowX = 0;
                       drawTextIndex = 0;
                       
                       // 绘制行号
                       if(isDrawLine)
                       {
                
                           if(mDebugLines[drawLine])
                           {
                               mLinePaint.setColor(Color.RED);
                               canvas.drawText(Integer.toString(drawLine), Xoffset - mCharLitterWidth, drawY, mLinePaint);
                               mLinePaint.setColor(mTheme.getLineCountColor());
                               mLinePaint.setAlpha(55);
                           }
                           else {
                               canvas.drawText(Integer.toString(drawLine), Xoffset - mCharLitterWidth, drawY, mLinePaint);
                           }
                       }
            
                       if(drawChar == TAG.EOL)
                       {
                           hLength = 0;
                           if(modulesJs.size() > indexOfModules)
                           {
                               moduleJs = modulesJs.get(indexOfModules++);
                               hLength = moduleJs.getSecond();
                           }
                       }
                       else
                       //if(drawShowX >= getWidth())
                       {
                
                           mTextPaint.setColor(mTheme.getColor(moduleJs.getFirst()));
                
                           canvas.drawText(drawText, dIndex, hIndex, dWidth, drawY, mTextPaint);
                           
                           moduleJs = mTokenJavaScript.findInCache(drawLine + 1, moduleJs);
                
                           if(moduleJs == null)
                           {
                               indexOfModules = 0;
                               hLength = 0;
                               dWidth = -1;
                               hIndex = 0;
                               dIndex = 0;
    
                               drawX = Xoffset;
                               break main;
                           } else
                           {
                               indexOfModules = moduleJs.modulePosition + 1;
                               hLength = moduleJs.getSecond();
                           }
                
                
                       }
            
                       dWidth = -1;
                       hIndex = 0;
                       dIndex = 0;
                       drawX = Xoffset;
                       
                       if(drawY >= mDrawClip.bottom || drawLine >= mRowCounts)break main;
            
                       drawY += drawRowHeight;
                       index = mRowStartCounts[++drawLine];
            
                   }
               }
    
           }
           
           
       } else
       {
           // 取消高亮的绘制
           main :
           while(index < length && drawLine <= mRowCounts)
           {
               drawChar = mChar[index++];
        
        
               if (mDrawClip.left > drawX)
               {
                   cacheWidth = drawX;
            
                   while (cacheWidth < cacheLeft && index < length)
                   {
                       cacheWidth += drawCharWidth[drawChar];
                       if(drawChar == TAG.EOL)
                       {
                           cacheWidth = Xoffset;
                           drawLine++;
                           drawY += drawRowHeight;
                           if( drawY > mDrawClip.bottom)break main;
                       }
                
                       drawChar = mChar[index++];
                   }
            
                   drawX = cacheWidth;
               }
        
               if(drawShowX <= getWidth() && drawChar != TAG.EOL  && index < length)
               {
                   // 中间
                   drawText[drawTextIndex++] = drawChar;
                   drawShowX += drawCharWidth[drawChar];
               }
               else
               {
                   // 绘制行号
                   if(isDrawLine)
                   {
                
                       if(mDebugLines[drawLine])
                       {
                           mLinePaint.setColor(Color.RED);
                           canvas.drawText(Integer.toString(drawLine), Xoffset - mCharLitterWidth, drawY, mLinePaint);
                           mLinePaint.setColor(mTheme.getLineCountColor());
                           mLinePaint.setAlpha(55);
                       }
                       else {
                           canvas.drawText(Integer.toString(drawLine), Xoffset - mCharLitterWidth, drawY, mLinePaint);
                       }
                   }
            
                   // 绘制文本
                   if(drawShowX > 0)
                   {
                       canvas.drawText(drawText, 0, drawTextIndex, drawX, drawY, mTextPaint);
                       drawShowX = 0;
                       drawTextIndex = 0;
                   }
//
                   drawX = Xoffset;
                   if(drawY >= mDrawClip.bottom || drawLine >= mRowCounts)break main;
                   drawY += drawRowHeight;
                   index = mRowStartCounts[++drawLine];
            
               }
           }
       }
    }
    
    
    
    /**
     * 长时间加载启用方法
     */
    
    class AsyncLoader extends AsyncTask<String, String, Integer>
    {
        boolean loadDialog = false;
        static final int DONE = 0;
        static final int FAILED = -1;
        
        @SuppressLint("WrongThread")
        
        protected Integer doInBackground(String... params)
        {
            switch(params[0])
            {
                case "setText":
                    setText(params[1]);
                    return DONE;
                default :
                    return FAILED;
            }
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            if(loadDialog)loadWindow.show();
        }
        
        @Override
        protected void onPostExecute(Integer result)
        {
            if(loadWindow != null) {loadWindow.cancel();}
            
            if(result != 0) {Log.e(Tag, "onPostExecute : Code = " + result);}
            invalidate(mDrawClip);
        }
        
        public void setLoadDialog(boolean flag) {
            loadDialog = flag;
        }
    
    }
    
    /**
     *  隐藏剪切板。
     */
    public void hideClipboardPanel()
    {
        if(isInActionMode)
        {
            mActionMode.finish();
            isInActionMode = false;
        }
    }
    /**
     * 显示剪切板
     */
    public void showClipboardPanel()
    {
        if (mCallback == null) {initCP();}
        
        mActionMode = mActivity.startSupportActionMode(mCallback);
        isInActionMode = true;
    }
    
    /**
     * 初始化剪切板
     */
    private void initCP()
    {
        mCallback = new ActionMode.Callback() {
            @SuppressLint("ResourceType")
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // 引入新的menu
                mode.setTitle("");
                menu.add(0, 0, 0, "Frms : 全选")
                    .setIcon(R.raw.abc_ic_menu_selectall_mtrl_alpha)
                    .setShowAsActionFlags(2);
                menu.add(0, 1, 0, "Frms : 剪切")
                    .setIcon(R.raw.abc_ic_menu_cut_mtrl_alpha)
                    .setShowAsActionFlags(2);
                menu.add(0, 2, 0, "Frms : 复制")
                    .setIcon(R.raw.abc_ic_menu_copy_mtrl_am_alpha)
                    .setShowAsActionFlags(2);
                menu.add(0, 3, 0, "Frms : 粘贴")
                    .setIcon(R.raw.abc_ic_menu_paste_mtrl_am_alpha)
                    .setShowAsActionFlags(2);
                
                
//                menu.add(0, 4, 1, "只读/只写")
//                    .setShowAsActionFlags(1);
//                menu.add(0, 4, 2, "统计")
//                    .setShowAsActionFlags(1);
//                menu.add(0, 4, 1, "查找&替换")
//                    .setShowAsActionFlags(1);
                return true;
            }
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
            @Override
            public boolean onActionItemClicked(ActionMode mode,
                                               MenuItem item) {
                switch(item.getItemId())
                {
                    case 0 :
                        if(length > 1)
                        {
                            mSelectMode = SELECT_ING;
                            mCursor = new int[] {
                                1, 1, mRowCounts, length
                            };
                            drawCursorX = Xoffset;
                            drawCursorY = (int) (drawRowHeight + drawFontMetrics.bottom);
                            drawCursorX2 = getLineWidth(mRowCounts) + Xoffset;
                            drawCursorY2 = (int) (mRowCounts * drawRowHeight + drawFontMetrics.bottom);
                            scrollTo(0, 0);
                            invalidate(mDrawClip);
                        }
                        break;
                    case 1 :
                        if(mSelectMode == SELECT_ING)
                        {
                            mSelectMode = SELECT_NONE;
                            char[] c = new char[mCursor[3] - mCursor[1]];
                            System.arraycopy(mChar, mCursor[1] - 1, c, 0, c.length);
                            ClipData clipData = ClipData.newPlainText("Label", String.valueOf(c));
                            mClipboardManager.setPrimaryClip(clipData);
                            delete(mCursor[1], mCursor[3], true, UNKNOWN, UNKNOWN, true);
                            hideClipboardPanel();
                        }
                        break;
                    case 2:
                        if(mSelectMode == SELECT_ING && mCursor[1] < mCursor[3])
                        {
                            char[] c = new char[mCursor[3] - mCursor[1]];
                            System.arraycopy(mChar, mCursor[1] - 1, c, 0, c.length);
                            ClipData clipData = ClipData.newPlainText("Label", String.valueOf(c));
                            mClipboardManager.setPrimaryClip(clipData);
                            mSelectMode = SELECT_NONE;
                            hideKeyboard();
                            invalidate(mDrawClip);
                        }
                        break;
                    case 3:
                        
                        String str =  mClipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                        if(str.length() < 1)break;
                        
                        if(mSelectMode == SELECT_ING && mCursor[1] < mCursor[3])
                        {
                            delete(mCursor[1], mCursor[3], true, UNKNOWN, UNKNOWN, true);
                            if(str.length() == 1)
                            {
                                insertChar(mCursor[1], str.charAt(0), true, UNKNOWN, true);
                            } else
                            {
                                insert(mCursor[1], str, true, UNKNOWN, UNKNOWN, true);
                            }
                        } else
                        {
                            mSelectMode = SELECT_NONE;
                            if(str.length() == 1)
                            {
                                insertChar(mCursor[1], str.charAt(0), true, UNKNOWN, true);
                            } else
                            {
                                insert(mCursor[1], str, true, UNKNOWN, UNKNOWN, true);
                            }
                        }
                        break;
                    case 4:
                        switch (item.getOrder())
                        {
//                            case 3:
//                                // search and(or) replace
//                                break;
                            case 1:
                                // change mode read-only or not.
                                
                                setChangeEditMode();
                                break;
                            case 2:
                                // record
                                
                                getCharsRecord();
                                
                                break;
                            default:
                                hideClipboardPanel();
                        }
                        break;
                    default :
                    {
                        hideClipboardPanel();
                    }
                }
                return true;
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                isInActionMode = false;
            }
        };
    }
    
    /**
     * 设置文本
     * @param text 文本
     * @param isLoad 是否启用加载框阻止将可能的卡顿时的交互场景（需要自主依照设置文本的长度而定）
     */
    public void setText(String text, boolean isLoad)
    {
        
        text += TAG.EOF;
        length = text.length();
        text += com.frms.lexer.TAG.EOL;
        
        if(isLoad) {
            mAsyncLoader.setLoadDialog(true);
            mAsyncLoader.execute("setText", text);
        } else {
            setText(text);
        }
        
    }
    
    /**
     * 设置文本
     * @param t 文本
     */
    private void setText(String t)
    {
        mUndoStack.empty();
        mScannerLock = false;
        mRowCounts = 1;
        
        mChar = t.toCharArray();
        int mt = 0;
        int i = 0;
        // 理论上, 他是足够的, 并且通常远大于真实行数
        mRowStartCounts = new int[ mChar.length + 1 ];
        mDebugLines = new boolean[ mChar.length + 1 ];
        mRowStartCounts[0] = -1;
        
        for(char ca : mChar)
        {
            
            if(drawCharWidth[ca] == 0)
            {
                drawCharWidth[ca] = (int)(mTextPaint.measureText(String.valueOf(ca))) ;
            }
            
            mt += drawCharWidth[ca];
            i++;
            if(ca == TAG.EOL)
            {
                mWidth = Math.max(mWidth, mt);
                mt = 0;
                
                mRowStartCounts[++mRowCounts] = i;
                mHeight += drawRowHeight;
            }
        }
        
        mRowCounts--;
        
        Xoffset = (String.valueOf(mRowCounts).length() +1) * mCharLitterWidth;
        drawCursorX = Xoffset;
        
        initScroll();
        scrollTo(0, 0);
    }
    
    /**
     *  设置文本字体大小。
     *  会主动更新视图，可能对控件面积产生影响。
     * @param pix
     */
    public void setTextSize(int pix)
    {
        // todo lost select mode.
        // pix 会被取整，因为实验表明当Paint.class和子类对文本大小的不同量存在的比例和不同文本宽度有所不同。
        if(pix > 15 && pix < 80 && pix != mOtherPaint.getTextSize())
        {
            float oldH = drawRowHeight;
            float oldW = mCharLitterWidth;

            mTextPaint.setTextSize(pix);
            mLinePaint.setTextSize(pix);
            mOtherPaint.setTextSize(pix);
            
            initChangeTextSize();
            mPluginUI.setRowheigth(drawRowHeight);
            int i = 0;
            for(int c : drawCharWidth)
            {
                if(c != 0)
                {
                    drawCharWidth[i] = (int) mTextPaint.measureText(String.valueOf((char)i));
                }
                i++;
            }
            
            Xoffset = (String.valueOf(mRowCounts).length() +1) * mCharLitterWidth;

            char[] cw = new char[mCursor[1] - mRowStartCounts[mCursor[0]]  - 1];
            System.arraycopy(mChar, mRowStartCounts[mCursor[0]], cw, 0, cw.length);

            drawCursorX = (int) (Xoffset + mTextPaint.measureText(cw, 0, cw.length));
            drawCursorY = (int) (drawRowHeight * mCursor[0] + drawFontMetrics.bottom);

            if(mSelectMode == SELECT_ING)
            {
                char[] cw2 = new char[mCursor[3] - mRowStartCounts[mCursor[2]]  - 1];
                System.arraycopy(mChar, mRowStartCounts[mCursor[2]], cw2, 0, cw2.length);
    
                drawCursorX2 = (int) (Xoffset + mTextPaint.measureText(cw2, 0, cw2.length));
                drawCursorY2 = (int) (drawRowHeight * mCursor[2] + drawFontMetrics.bottom);
            }
            
            // 数据可能溢出造成负数。
            //if(BASE_TEXT_SIZE < pix && oldSize < pix)
            {
                mHeight = drawRowHeight * mRowCounts;
                mWidth = (int) Math.abs (mCharChineseWidth / oldW * mWidth); // 不一定准确。
    
                scrollY = mHeight - drawRowHeight;
                scrollX = mWidth + Xoffset;
            }

            float x = getScrollX() * (mCharLitterWidth / oldW);
            float y = getScrollY() * (drawRowHeight / oldH);
            
            scrollTo(Math.round(x), Math.round(y));
            
        }
    }
    
    /**
     * 获取字体大小
     * @return
     */
    public float getTextSize()
    {
        return mTextPaint.getTextSize();
    }
    
    /**
     * 获取控件宽度
     * @return
     */
    public long getViewWidth() {
        return mWidth;
    }
    
    /**
     * 获取控件高度
     * @return
     */
    public long getViewHeigth() {
        return mHeight;
    }
    
    /**
     * 获取断点
     * @return
     */
    public boolean[] getDebugs() {
        return mDebugLines;
    }
    
    /**
     * 显示放大镜
     */
    public void showMagnifier() {
        mPluginUI.canShowMagnifier();
    }
    
    /**
     * 设置编辑器主题，无论是否和原主题一样，都会重置并主动更新视图。
     * @param isDark
     */
    public void setTheme(boolean isDark)
    {
        if(isDark)
            mTheme = new ThemeDark();
        else
            mTheme = new ThemeLight();
        
        setBackgroundColor(mTheme.getBackgroundColor());
        mTextPaint.setColor(mTheme.getNormalColor());
        mLinePaint.setColor(mTheme.getLineCountColor());
        mOtherPaint.setColor(mTheme.getSelectColor());
        
        invalidate(mDrawClip);
    }
    
    /**
     * 撤销编辑指令
     */
    public void undo()
    {
        mUndoStack.undo();
    }
    
    /**
     * 重做编辑指令
     */
    public void redo()
    {
        mUndoStack.redo();
    }
    
    /**
     * 返回实例化的编辑指令
     * @return
     */
    public UndoStack getUndoStack() {
        return mUndoStack;
    }
    
    /**
     * 设置字体
     * @param t
     */
    public void setTypeface(Typeface t)
    {
        mTextPaint.setTypeface(t);
        mLinePaint.setTypeface(t);
        mOtherPaint.setTypeface(t);
    }
    
    /**
     * 设置字体
     * @param mode
     */
    public void setTypeface(int mode)
    {
        switch (mode)
        {
            case MONOSPACE :
                setTypeface(Typeface.MONOSPACE);
                break;
            case DEJAVUSANSMONO :
                setTypeface(typeface);
                break;
            case SERIF:
                setTypeface(Typeface.SERIF);
                break;
            case SANS_SERIF :
                setTypeface(Typeface.SANS_SERIF);
                break;
            default:
                setTypeface(Typeface.DEFAULT);
            
        }
    }
    
    /**
     * 启用自动补全和高亮，注意，这里可以进行扩展、修改。
     * @param language{#}
     */
    public void setShowAuto(int language)
    {
        
        
        selectLanguage = language;
        isUseLanguage = true;
        
        switch (language)
        {
            case LANGUAGE_NATIVE_JAVASCRIPT:
                mTokenJavaScript = new JavaScript();
                break;
            case LANGUAGE_NATIVE_JAVA:
                mTokenJava = new Java();
                break;
            default:
                isUseLanguage = false;
        }
        
        
        mPluginUI.canAutomaticCompletion(language);
    }
    
    /**
     * 获取语言规则
     * @return
     */
    public int getSelectLanguage()
    {
        return selectLanguage;
    }
    
    /**
     * 获取文本
     * @return
     */
    public char[] getTextChars()
    {
        if(length < 2)
        {
            return new char[0];
        } else
        {
            char[] cache = new char[length - 1];
            System.arraycopy(mChar, 0, cache, 0, cache.length);
            return cache;
        }
    }
    
    /**
     * 获取文本
     * @return
     */
    public StringBuffer getText()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(getTextChars());
        return sb;
    }
    
    /**
     * 获取光标所在位置，格式为<br>
     *  {行，位置，行，位置}, 这个位置指的是光标后的字位置，比如空文本，其默认位置是 1，
     *  因为其后是EOF, 所以位置是 1.
     * @return
     */
    public int[] getCursorPosition()
    {
        return mCursor;
    }
    
    /**
     * 获取行数
     * @return
     */
    public int getRowCounts()
    {
        return mRowCounts;
    }
    
    /**
     * 获取当前编辑器版本
     * @return
     */
    public int getVersion()
    {
       return version;
    }
    
    /**
     * 获取文本长度
     * @return
     */
    public int getTextLength()
    {
        return length - 1;
    }
    
    /**
     * 获取编辑器读写模式，为 true 表示只读
     * @return
     */
    public boolean getEditMode() {
        return setOnlyRead;
    }
    
    /**
     * 编辑器读写模式，为 true 表示只读
     */
    public void setEditMode(boolean isOnlyRead)
    {
        setOnlyRead = isOnlyRead;
        if(setOnlyRead)
        {
            hideKeyboard();
        }
    }
    
    /**
     * 改变编辑器读写模式
     */
    public void setChangeEditMode()
    {
        setOnlyRead = !setOnlyRead;
        if(setOnlyRead)
        {
            hideKeyboard();
        }
    }
    
    /**
     * 获取编辑器字体
     * @return
     */
    public Typeface getTypeface()
    {
        return mTextPaint.getTypeface();
    }
    
    /**
     * 调用统计ui
     */
    public void getCharsRecord()
    {
        mPluginUI.showRecord(length - 1, mRowCounts, drawCharWidth);
    }
    
    /**
     * 编辑接口
     */
    public interface onEditListener
    {
        /**
         * 添加文本
         * @param cursorPosition 光标位置
         * @param text 将要被添加的文本
         */
        void addText(int cursorPosition, String text);
    
        /**
         * 删除文本
         * @param cursorPosition 光标位置，如果是多选的话，表示第一个光标位置。
         * @param endCursorPosition 第二个光标位置，如果不是选择状态，则返回-1。
         * @param text 如果有文本删除，就会返回选择区内文本，否则返回null。
         */
        void deleteText(int cursorPosition, int endCursorPosition, String text);
    }
    
    public void setOnEditListener(onEditListener mOnEditListener)
    {
        this.mOnEditListener = mOnEditListener;
    }
    
    
    /**
     * 断点接口
     */
    public interface onDebugListener
    {
        /**
         * @param line 改变的行
         * @param nowMode 改变后的模式
         */
        void run(int line, boolean nowMode);
    }
    
    /**
     * 断点接口
     * @param onDl
     */
    public void setOnDebugListener(onDebugListener onDl)
    {
        onDebugListener = onDl;
    }
    
//    /**
//     * 只处理用户编辑操作
//     */
//    public interface onEditTextListener
//    {
//        void onDelete(int position);
//
//        void onAdd(int position)
//    }
    
    /**
     * 获取断点行。
     * @return
     */
    public ArrayList<String> getDebugsList()
    {
        ArrayList<String> arrayList = new ArrayList<>();
        
        for(int i =1; i <= mRowCounts; i++)
        {
            if(mDebugLines[i])
            {
                arrayList.add("行 " + i);
            }
        }
        return arrayList;
    }
    
    /**
     * 跳转到某行。
     * @param line
     */
    public void scrollToLine(int line)
    {
        if(line > 0 && line <= mRowCounts)
        {
            scrollTo(getScrollX(), drawRowHeight * (line -1));
        }
    }
    
    /**
     * 获取单位字符长度。
     * @return
     */
    public int getCharLitterUtilWidth()
    {
        return mCharLitterWidth;
    }
    
    /**
     * 获取单位字符长度。
     * @return
     */
    public int getCharChineseUtilWidth()
    {
        return mCharChineseWidth;
    }
    
    /**
     * 获取插件类。
     * @return
     */
    public PluginUI getPluginUI()
    {
        return mPluginUI;
    }
    
    /**
     * 设置是否使用垂直滚动条。
     */
    public void setVerticalScrollBar(boolean flag)
    {
        mVerticalScrollBar = flag;
        if(flag && mBitmapScrollBar == null)
        {
            mBitmapScrollBar = Kit.getBitmap(getContext(), R.drawable.a_ve, 0.07f);
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (event.getKeyCode())
        {
            case KeyEvent.KEYCODE_DPAD_LEFT :
                gotoCursorLeft();
                break;
    
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                gotoCursorRight();
                break;
    
            case KeyEvent.KEYCODE_DPAD_UP:
                gotoCursorUp();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                gotoCursorDown();
                break;
            default:
            {
                Kit.printout("Unknown KeyDown = ", event.getKeyCode());
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /**
     * 仅用作测试。
     */
    public void nativeListenEvent()
    {
        Kit.printout(Arrays.toString(mChar));
        Kit.printout("-------------Text-------------");
    }
    
}
