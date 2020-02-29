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
        
        //codeView.setText("var a = new \nhfbyubaufbub\nbfbauybfyuesbubbf\nfafbuyabuybb\nfhahdbvyb\n", false);
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
        
        return super.onCreateOptionsMenu(menu);
    }
    
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        if(item.getItemId() == 1) {
            codeView.undo();
        } else{
            codeView.redo();
        }
        return true;
    }
    
}
