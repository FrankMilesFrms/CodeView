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

package com.frms.codeview.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.frms.codeview.CodeView;
import com.frms.codeview.MainActivity;
import com.frms.codeview.R;
import com.frms.codeview.activity.utils.FileUtils;
import com.frms.codeview.activity.utils.ViewPagerAdapter;
import com.frms.codeview.activity.view.FileBrowser;
import com.frms.codeview.tools.Kit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static android.os.ParcelFileDescriptor.MODE_WORLD_READABLE;

/**
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/3/14 16:58(ydt)
 */
public class MainEditActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener
{
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE =
        {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        };
    @SuppressLint("SdCardPath")
    private static final String CACHE_FILE_PATH = FileBrowser.ROOT_PATH + "/cacheFile.txt";
    
    public static boolean mTheme;
    private static boolean canNotPermission = false;
    
    private ArrayList<String> tabFileName;
    private ArrayList<CodeView> tabEditView;
    public static ArrayList<String> recentFiles, nowFiles;
    
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private int closeIndex = 0;
    private ViewPagerAdapter viewPagerAdapter;
    
    public static DrawerLayout drawerLayout1;
    public static SharedPreferences readCollection;
    public static ArrayList<String> collectionList;
    public static FileBrowser fileBrowser;
    
    private AsyncLoader asyncLoader;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String text = "";
    private AlertDialog loadWindow;
    private boolean mMagnifier;
    private int mDefaultLanguage;
    private int mTypeface;
    private int mTextSize;
    public static boolean mAuto;
    
    @SuppressLint("all")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
        verifyStoragePermissions(this);
     
        asyncLoader = new AsyncLoader();
        asyncLoader.doInBackground("init");
        
        
        
    
        if (mTheme) {
            setTheme(R.style.frms_Dark);
        } else {
            setTheme(R.style.frms_Night);
        }
        
        
        setContentView(R.layout.main_layout);
        
        
        
        tabLayout = findViewById(R.id.tab_layout);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
    
        if(mTheme)
        {
            tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        }
        
        setSupportActionBar(toolbar);
        
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout1 = findViewById(R.id.drawer_layout1);
        navigationView = findViewById(R.id.navigation_view);
    
        navigationView.setNavigationItemSelectedListener(this);
    
        // 文件浏览器
        fileBrowser = findViewById(R.id.file_browse);
        
        fileBrowser.setModules(
                                    (TextView)  findViewById(R.id.path_file),
                                    (ImageView) findViewById(R.id.back_parent),
                                    (ImageView) findViewById(R.id.home_path),
                                    (ImageView) findViewById(R.id.like),
                                    (ImageView) findViewById(R.id.add_file),
                                    (SwipeRefreshLayout) findViewById(R.id.refresh),
                                    drawerLayout,
                                    readCollection.edit(),
                                    collectionList
                               );
        
        fileBrowser.setOnClickFile(new FileBrowser.OnClickFile()
        {
            @Override
            public void onClick(File file)
            {
                int index = nowFiles.indexOf(file.getAbsolutePath());
                
                if(index >= 0)
                {
                    viewPager.setCurrentItem(index);
                    Snackbar.make(drawerLayout, "这个文件已被打开过了", Snackbar.LENGTH_SHORT).show();
                    return;
                } else
                {
                    nowFiles.add(file.getAbsolutePath());
                }
                
                if( (index = recentFiles.indexOf(file.getAbsolutePath())) < 0)
                {
                    recentFiles.add(file.getAbsolutePath());
                }
                
                tabFileName.add(file.getName());
                
                CodeView codeView = new CodeView(MainEditActivity.this);
                codeView.setTheme(mTheme);
                codeView.setTypeface(CodeView.DEJAVUSANSMONO + mTypeface);
                codeView.setTextSize(mTextSize);
                if(mAuto)
                {
                    String type = file.getName();
                    if(type.contains("."))
                    {
                        type = type.substring(type.lastIndexOf('.') + 1);
    
                        switch (type) {
                            case "java":
                                codeView.setShowAuto(CodeView.LANGUAGE_NATIVE_JAVA);
                                break;
                            case "js":
                                codeView.setShowAuto(CodeView.LANGUAGE_NATIVE_JAVASCRIPT);
                                break;
                            case "txt":
                            case "TXT":
                                codeView.setShowAuto(CodeView.LANGUAGE_NATIVE_NONE);
                                break;
                            default:
                                codeView.setShowAuto(mDefaultLanguage);
                        }
                    }
                }
                
                
                codeView.setText("", false);
                codeView.setEditMode(false);
                codeView.setVerticalScrollBar(true);
                //codeView.getPluginUI().canVerticalScrollBar();
                
                if(mMagnifier)
                {
                    codeView.showMagnifier();
                }
                
                tabEditView.add(codeView);
                drawerLayout1.closeDrawers();
                viewPagerAdapter.notifyDataSetChanged();
                viewPager.setCurrentItem(index);
                
                closeIndex = tabEditView.size() - 1;
                asyncLoader.doInBackground("read", file.getAbsolutePath());
            }
        });
        
        //if(tabFileName == null)
        {
            tabFileName = new ArrayList<>();
            tabFileName.add("Cache File.txt");
            nowFiles.add(CACHE_FILE_PATH);
        }
        
        if(tabEditView == null)
        {
            tabEditView = new ArrayList<>();
            CodeView codeView = new CodeView(this);
            codeView.setTheme(mTheme);
            codeView.setTextSize(mTextSize);
            codeView.setTypeface(CodeView.DEJAVUSANSMONO +mTypeface);
            if(mAuto)
                codeView.setShowAuto(mDefaultLanguage);
            codeView.setText("", text.length() > 0xffff);
            codeView.setEditMode(false);
            codeView.setVerticalScrollBar(true);
            
            tabEditView.add(codeView);
            drawerLayout.closeDrawers();
        }
        
        
        
        
        tabLayout.setTabTextColors(ColorStateList.valueOf(mTheme ? Color.WHITE : Color.BLACK));
        
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                添加选中Tab的逻辑
                closeIndex = tab.getPosition();
            }
    
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//                添加未选中Tab的逻辑
            }
    
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                再次选中tab的逻辑
                closeIndex = tab.getPosition();
    
                if(tabLayout.getTabCount() > 1)
                {
                    new AlertDialog.Builder(MainEditActivity.this)
                        .setMessage("删除本条目？\n\n文件地址："+nowFiles.get(closeIndex))
                        .setNeutralButton("确定", (dialog, which) ->
                        {
                            viewPagerAdapter.removeIndex(closeIndex);
                            nowFiles.remove(closeIndex);
                            viewPager.setAdapter(viewPagerAdapter);
                            viewPager.setCurrentItem(viewPager.getCurrentItem());
                        })
                        .setPositiveButton("取消", null)
                        .setNegativeButton("保存并关闭", (dialog, which) ->
                        {
                            try {
                                FileUtils.newFile(nowFiles.get(closeIndex), tabEditView.get(closeIndex).getText().toString());
                            } catch (IOException e) {
                                Snackbar.make(drawerLayout, "保存失败，哈希值="+e.hashCode(), Snackbar.LENGTH_SHORT).show();
                            }
                            viewPagerAdapter.removeIndex(closeIndex);
                            nowFiles.remove(closeIndex);
                            viewPager.setAdapter(viewPagerAdapter);
                            viewPager.setCurrentItem(viewPager.getCurrentItem());
                        })
                    .show();
                } else
                {
                    Snackbar.make(drawerLayout, "不能移除最后一个！", Snackbar.LENGTH_SHORT).show();
                }
               
            }
        });
        
        
        viewPager = findViewById(R.id.viewpager);
    
        tabLayout.setupWithViewPager(viewPager);
        
        viewPager.setCurrentItem(0);
        viewPagerAdapter = new ViewPagerAdapter(tabFileName, tabEditView);
        viewPager.setAdapter(viewPagerAdapter);
        
        
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
        {
            private CodeView codeView;
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                codeView = tabEditView.get(closeIndex);
                switch (menuItem.getItemId())
                {
                    case 1:
                        codeView.undo();
                        break;
        
                    case 2:
                        codeView.redo();
                        break;
                    case 3:
                        try {
                            FileUtils.newFile(nowFiles.get(closeIndex), codeView.getText().toString());
                            Snackbar.make(drawerLayout, "已保存当前条目文件", Snackbar.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Snackbar.make(drawerLayout, "保存失败，哈希值="+e.hashCode(), Snackbar.LENGTH_SHORT).show();
    
                        }
    
                        break;
                    case 4:
                        
                        ArrayList<String> arrayList = codeView.getDebugsList();
                        final String[] arr = new String[arrayList.size()];
                        
                        if(arr.length > 0)
                        {
                            arrayList.toArray(arr);
                            new AlertDialog.Builder(MainEditActivity.this)
                                .setTitle("标签列表")
                                .setItems(arr, (dialog, which) ->
                                                   codeView.scrollToLine(Integer.parseInt(arr[which].substring(2))))
                                .setNegativeButton("关闭", null)
                                .show();
                        }else
                        {
                            Snackbar.make(drawerLayout, "没有行标签", Snackbar.LENGTH_SHORT).show();
                        }
                    case 5:
                        switch (menuItem.getOrder())
                        {
                            case 1:
                                codeView.setChangeEditMode();
                                break;
                            case 2:
                                codeView.getCharsRecord();
                                break;
                            case 3:
                                if(codeView.getRowCounts() > 1)
                                {
                                    final EditText editText = new EditText(MainEditActivity.this);
                                    editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                                    editText.setHint("(1..."+codeView.getRowCounts()+")");
                                    
                                    new AlertDialog.Builder(MainEditActivity.this)
                                        .setTitle("定位行")
                                        .setView(editText)
                                        .setNegativeButton("定位", (dialog, which) ->
                                        {
                                            if(editText.getText().length() > 0)
                                            {
                                                int line = Math.min(Integer.parseInt(editText.getText().toString()), codeView.getRowCounts());
                                                codeView.scrollToLine(line);
                                            }else
                                            {
                                                codeView.hideKeyboard();
                                                Snackbar.make(drawerLayout, "不能留空", Snackbar.LENGTH_SHORT).show();
                                            }
                                            
                                        })
                                        .setPositiveButton("取消", null)
                                        .show();
                                }
                                
                        }
                        break;
                    default:
                    {
                        Kit.printout("Unknown", "MainEditActivity");
                    }
                }
                return false;
            }
        });
    }
    
    /**
     * 加载数据
     */
    private void initData()
    {
        nowFiles = new ArrayList<>();
        readCollection = getSharedPreferences("collection", MODE_PRIVATE);
        Gson gson = new Gson();
    
        String filePath = readCollection.getString("data", "");
    
        if(filePath.equals(""))
        {
            collectionList = new ArrayList<>();
        } else
        {
            collectionList = gson.fromJson(filePath, new TypeToken<ArrayList<String>>() {}.getType());
        }
    
        String recentFileData = readCollection.getString("recent", "");
        
        if(recentFileData.equals(""))
        {
            recentFiles = new ArrayList<>();
        } else
        {
            recentFiles = gson.fromJson(recentFileData, new TypeToken<ArrayList<String>>() {}.getType());
        }
    
    
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mTheme = sharedPreferences.getBoolean("preference_theme", false);
        mMagnifier = sharedPreferences.getBoolean("preference_auxiliary_keyboard", false);
        mDefaultLanguage = Integer.parseInt(sharedPreferences.getString("preference_language", "0")) ;
        mTypeface = Integer.parseInt(sharedPreferences.getString("preference_typeface", "0"));
        mTextSize = Integer.parseInt(sharedPreferences.getString("preference_text_size", "14"));
        mAuto = sharedPreferences.getBoolean("preference_switch_auto_text", false);
    }
    
    
    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(1, 1, 1, "Undo")
            .setIcon(R.raw.undo)
            .setShowAsActionFlags(2);
        menu.add(1, 2, 1, "Redo")
            .setIcon(R.raw.redo)
            .setShowAsActionFlags(2);
        menu.add(1, 3, 1, "save")
            .setIcon(R.drawable.save)
            .setShowAsActionFlags(2);
        
        menu.add(1, 4, 1, "debugs list")
            .setIcon(R.raw.favoriteslist)
            .setShowAsActionFlags(2);
        
        menu.add(0, 5, 1, "只读、写")
            .setShowAsActionFlags(1);
        menu.add(0, 5, 2, "统计")
            .setShowAsActionFlags(1);
        menu.add(0, 5, 3, "定位行")
            .setShowAsActionFlags(1);
        
        return super.onCreateOptionsMenu(menu);
    }
    /**
     * android 动态权限申请
     */
    public static void verifyStoragePermissions(Activity activity)
    {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                "android.permission.WRITE_EXTERNAL_STORAGE");
            canNotPermission = (permission != PackageManager.PERMISSION_GRANTED);
            // 没有写的权限，去申请写的权限，会弹出对话框
            if (canNotPermission)
            {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        drawerLayout.closeDrawers();
        switch (menuItem.getItemId())
        {
            case R.id.about:
                startActivity(new Intent(MainEditActivity.this, AboutActivity.class));
                break;
            case R.id.help:
                startActivity(new Intent(MainEditActivity.this, HelperActivity.class));
                break;
            case R.id.set:
                startActivity(new Intent(MainEditActivity.this,
                    PreferenceActivity.class));
                break;
            case R.id.collection:
                startActivity(new Intent(MainEditActivity.this, CollectionActivity.class));
                break;
            case R.id.lru_file:
                startActivity(new Intent(MainEditActivity.this, RecentActivity.class));
                break;
        }
        return false;
    }
    
    
    @Override
    protected void onDestroy()
    {
        SharedPreferences.Editor editor =  readCollection.edit();
    
        editor.clear();
        editor.putString("recent", new Gson().toJson(recentFiles));
        editor.apply();
    
        Toast.makeText(this, "书签数据已保存", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
    
    /*
        读取数据
    */
    class AsyncLoader extends AsyncTask<String, String, Integer>
    {
        @SuppressLint("WrongThread")
        @Override
        protected Integer doInBackground(String... strings)
        {
            switch (strings[0])
            {
                case "init":
                    initData();
                break;
                case "read":
                   if(canNotPermission)
                   {
                       verifyStoragePermissions(MainEditActivity.this);
                       
                       if(canNotPermission)
                       {
                           canNotPermission = false;
                           Snackbar.make(drawerLayout1, "你还没有给予权限，请给予后重启本应用", Snackbar.LENGTH_LONG).show();
                           break;
                       }
                       
                   }
                    try {
                        text = FileUtils.readBigFile(strings[1]);
                    } catch (Throwable e) {
                        text = "文件读取错误:\n"+Arrays.toString(e.getStackTrace());
                    }
                    
                    tabEditView.get(closeIndex).setText(text, false);
                break;
            }
            return 1;
        }
        
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            
            if(loadWindow == null)
            {
                loadWindow = new AlertDialog.Builder(MainEditActivity.this).create();
                loadWindow.setTitle("读取中...");
                loadWindow.setView(new ProgressBar(MainEditActivity.this));
                loadWindow.setCancelable(false);// 设置是否可以通过点击Back键取消
            }
            
            loadWindow.show();
            
        }
        
        @Override
        protected void onPostExecute(Integer integer)
        {
            super.onPostExecute(integer);
            loadWindow.cancel();
        }
        
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        
        if(hasFocus && mMagnifier)
        {
            tabEditView.get(0).showMagnifier();
           // tabEditView.get(0).getPluginUI().canVerticalScrollBar();
        }
    }
    
    
    @Override
    protected void onStart()
    {
        super.onStart();
        File file = new File(CACHE_FILE_PATH);
        try {
            if(file.exists())
            {
                closeIndex = 0;
                asyncLoader.doInBackground("read", CACHE_FILE_PATH);
            } else {
                FileUtils.newFile(CACHE_FILE_PATH, "");
            }
        
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
}
