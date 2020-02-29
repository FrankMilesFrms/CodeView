package com.frms.codeview;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.frms.UI.Gradual;
import com.frms.UI.LoadView;
import com.frms.codeview.tools.Kit;
import com.frms.lexer.TAG;

import java.util.List;

/**
 * Demo
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/2/22 11:20(ydt)
 */
public class MainActivity extends Activity
{
    private int language = 0;
    public static boolean isDarkTheme = true;
    public static boolean isMagnifier = true;
    public static int typeface = 0;
    public static boolean isAuto = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        setTitle("Settings");
        Spinner spinner = findViewById(R.id.spinner);
        
        String[] mItems = {"Text", "JavaScript", "Java"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                language = position;
            }
    
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                language = 0;
            }
        });
    
        Spinner spinner1 = findViewById(R.id.spinner2);
        String[] mItems1 = {"DejaVuSansMono[推荐]", "Monospace", "Sans Serif", "Serif" };
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
    
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                typeface = position;
            }
        
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                language = 0;
            }
        });
    
        ((Switch)findViewById(R.id.theme)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                isDarkTheme = !isChecked;
            }
        });
    
        ((Switch)findViewById(R.id.magnifier)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                isMagnifier = isChecked;
            }
        });
        
        ((Switch)findViewById(R.id.auto)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                isAuto = isChecked;
            }
        });
    }
    
    public void About(View view)
    {
        startActivity(new Intent(this, OtherActivity.class));
    }
    
    public void Help(View v)
    {
        new AlertDialog.Builder(this)
            .setTitle("帮助")
            .setMessage("手势说明：\n" + "  单指：\n" + "   非选择状态：\n" +
                        "     点击行号触发断点标记的启用或取消。\n" +
                        "     点击文本会光标定位，并显示辅助光标，点击处若无文本，则自动选取最近文本，后再启用输入法。\n" +
                        "     滑动时，若点击位置在辅助光标或光标附近，则视为滑动光标，此时若允许显示放大镜，则启用之。\n" +
                        " \n" + "  选择:\n" +
                        "       长按文本处，先计算抓取一个词组（能被Java允许的字符,不包括符号），\n" +
                        "       如果词组超过屏幕（不论两边或一边），屏幕位置移动到第一个光标处。\n" +
                        "       此时若处于滑动时，且点击位置在辅助光标或光标附近，则视为滑动光标，此时若允许显示放大镜，则启用之。\n" +
                        "  双指：\n" + "      放缩字体大小。\n" +
                        "编辑略同于输入框，注意：只有在光标同步显示在屏幕上时，修改文本时光标才会保持同步。")
            .setPositiveButton("关闭", null)
            .show();
    }
    
    public void Get(View view)
    {
        if(isQQClientAvailable(MainActivity.this)){
            final String qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=3505826836&version=1";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)));
        }else{
            Toast.makeText(MainActivity.this,"请安装QQ客户端",Toast.LENGTH_SHORT).show();
        }
    }
    
    public void Start(View view)
    {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("lang", language);
        startActivity(intent);
    }
    
    
    //    版权声明：此方法为CSDN博主「Errol_King」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
    //    原文链接：https://blog.csdn.net/u010356768/article/details/78831150
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

}
