package com.frms.codeview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.frms.codeview.tools.Kit;

/**
 * Demo
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/2/22 11:17(ydt)
 */
public class EditActivity extends Activity
{
    CodeView codeView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        int language = getIntent().getExtras().getInt ("lang");
        
        
        codeView = new CodeView(this);
        codeView.setTheme(MainActivity.isDarkTheme);
        codeView.setTypeface(CodeView.DEJAVUSANSMONO + MainActivity.typeface);
        if(MainActivity.isAuto)
            codeView.setShowAuto(language);
        codeView.setText("", false);
        codeView.setEditMode(false);
        // 防止遮挡文本
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(codeView);
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus && MainActivity.isMagnifier)
        {
            codeView.showMagnifier();
        }
    }
    
    private long mExitTime;
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                
                mExitTime = System.currentTimeMillis();
            } else {
                
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    
    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(1, 1, 1, "Undo")
            .setIcon(R.raw.undo_w)
            .setShowAsActionFlags(2);
        menu.add(1, 2, 2, "Redo")
            .setIcon(R.raw.redo_w)
            .setShowAsActionFlags(2);
        menu.add(0, 3, 1, "只读/只写")
            .setShowAsActionFlags(1);
        menu.add(0, 3, 2, "统计")
            .setShowAsActionFlags(1);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        switch (item.getItemId())
        {
            case 1:
                codeView.undo();
            break;
            
            case 2:
                codeView.redo();
            break;
            
            case 3:
                if(item.getOrder() == 1)
                {
                    codeView.setChangeEditMode();
                } else
                {
                    codeView.getCharsRecord();
                }
            break;
            default:
            {
                Kit.printout("Unknown", "EditActivity");
            }
        }
        if(item.getItemId() == 1) {
        
        } else{
        
        }
        return true;
    }
    
}
