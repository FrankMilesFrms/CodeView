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

package com.frms.codeview.tools.InputCompletion;

import com.frms.lexer.Token;

/**
 * 项目名称 ： app
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/4/30 17:01(ydt)
 */
public class JavaScriptCompletion extends CodeCompletion
{
    private boolean mSpaceBeforeParentheses;
    private boolean mSpaceCodeAligned;
    private boolean mSpaceCodeAnnotation;
    
    private boolean mLineAutoAnnotation;
    
    @Override
    public void input(char[] chars, Token mToken, int position, String inputString)
    {
    
    }
    
    @Override
    public void delete()
    {
    
    }
    
//代码风格, 当输入时自动改变。

// Space:
    
    /**
     * Priority Level = 0x010
     *
     * If enable:
     *word (
     * else
     *world(
     */
     public void setSpaceBeforeParentheses(boolean enable)
     {
         mSpaceBeforeParentheses = enable;
     }
    
    /**
     * Priority Level = 0x100
     *
     * 代码自动对齐
     * @param enable
     */
     public void setSpaceCodeAligned(boolean enable)
     {
         mSpaceCodeAligned = enable;
     }
     
     public void setSpaceCodeAnnotation(boolean enable)
     {
         mSpaceCodeAnnotation = enable;
     }
     
// Line
    
    /**
     * Priority Level = 0x010;
     *
     * 若处于注释行：
     *  处于行注释且最近侧非注释规则：加入下一行后自动注释。否则：不自动注释。
     *  处于块注释：自动注释。注意这个只会自动对齐 ‘*’ 之后的Space
     * @param enable
     */
    public void setLineAutoAnnotation(boolean enable)
    {
        mLineAutoAnnotation = enable;
    }
}
