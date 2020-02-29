package com.frms.codeview;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.frms.UI.Gradual;
import com.frms.UI.LoadView;

/**
 * demo
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/2/21 10:44(ydt)
 */
public class OtherActivity extends Activity
{
    private Gradual gradual;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //requestWindowFeature(Window.FEATURE_CONTEXT_MENU);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        
       gradual = LoadView.load(this, "CodeView", "Fast and Efficient", 1000, "Hello Developer", 2000, "Welcome to use.", "A Powerful View", 2000, "Suitable for mobile phone editor", 3000, "By Frms", 3000, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        gradual.removeAllViews();
                        TextView t = new TextView(OtherActivity.this);
                        t.setText("编辑器开发人员\n\nFrms（3505-8268-36@qq.com）\n\n" +
                                  "特别感谢\n\n虚青海儿(1575-1571-46@qq.com)\n" +
                                  "《致》开源组(QQ 781-097-903)\n\n部分资源名称/来源\n\n字体\nDejaVuSansMono.ttf\n图标\nES 文件浏览器\nwww.yurencloud.com");
                        t.setTextColor(Color.WHITE);
                        t.setGravity(Gravity.CENTER);
                        t.setTypeface(Typeface.MONOSPACE);
                        t.setTextSize(20);
                        ScrollView scrollView = new ScrollView(OtherActivity.this);
                        scrollView.addView(t);
                        gradual.addView(scrollView);
                    }
                }, 2000);
            }
        });
        setContentView(gradual);
    }
    
}
