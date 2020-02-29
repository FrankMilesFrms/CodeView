package com.frms.codeview.tools;

import android.graphics.Color;

/**
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/2/22 11:51(ydt)
 */
public class ThemeDark extends Theme
{
    
    public ThemeDark()
    {
        COMMON = 0xff323232;
        ZHUSHI = 0xff808080;
        STRING = 0xff6A8759;
        SYMBOL = 0xffffffff;
        INTENGER = 0xff6897BB;
        KEYWORD = 0xffcc7832;
        CONSTANT = 0xff9876aa;
        UNKOWN = 0xffFFEC8B;
    }  
    
    @Override
    public int getBackgroundColor()
    {
        return 0xff323232;
    }
    
    @Override
    public int getLineCountColor()
    {
        return 0x55ffffff;
    }
    
    @Override
    public int getNormalColor()
    {
        return 0xffFFEC8B;
    }
    
    @Override
    public int getSelectColor()
    {
        return 0x550099CC;
    }
    
}
