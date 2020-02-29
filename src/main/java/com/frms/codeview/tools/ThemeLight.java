package com.frms.codeview.tools;

import android.graphics.Color;

/**
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/2/22 11:50 = ydt)
 */
public class ThemeLight extends Theme
{
    public ThemeLight()
    {
        COMMON = 0xff444444;
        ZHUSHI = 0xff808080;
        STRING = 0xff008000;
        SYMBOL = 0xff3d3d3d;
        INTENGER = 0xff0000ff;
        KEYWORD = 0xff000080;
        CONSTANT = 0xff660e7a;
        UNKOWN = Color.BLACK;
    }
    @Override
    public int getBackgroundColor ()
    {
        return Color.WHITE;
    }
    
    @Override
    public int getLineCountColor()
    {
        return Color.BLACK;
    }
    
    @Override
    public int getNormalColor()
    {
        return Color.BLACK;
    }
    
    @Override
    public int getSelectColor()
    {
        return 0x550099CC;
    }
    
}
