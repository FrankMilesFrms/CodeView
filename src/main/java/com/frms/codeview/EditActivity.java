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
        
        
//        codeView = new CodeView(this);
//        codeView.setTheme(MainActivity.isDarkTheme);
//        codeView.setTypeface(CodeView.DEJAVUSANSMONO + MainActivity.typeface);
//        if(MainActivity.isAuto)
//            codeView.setShowAuto(language);
//        codeView.setText("", false);
//        codeView.setEditMode(false);
//        codeView.setOnDebugListener(new CodeView.onDebugListener()
//        {
//            @Override
//            public void run(int line, boolean nowMode)
//            {
//                Kit.printout(line, nowMode);
//            }
//        });
//        // 防止遮挡文本
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        setContentView(codeView);
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
        menu.add(0, 3, 1, "只读/写")
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
