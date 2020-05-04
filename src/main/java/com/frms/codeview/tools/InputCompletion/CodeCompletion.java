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

import com.frms.codeview.CodeView;
import com.frms.lexer.Token;

/**
 * 作为代码匹配。
 * 项目名称 ： app
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/4/30 16:49(ydt)
 */
public abstract class CodeCompletion
{
    public abstract String input(char[] chars, Token mToken, int position, String inputString, int[] lines, int nowLine);
    
    public abstract void delete();
}
