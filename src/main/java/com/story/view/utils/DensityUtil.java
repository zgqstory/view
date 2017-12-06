package com.story.view.utils;

import android.content.Context;

/**
 * 类名称：DensityUtil
 * 类描述：长度转换工具
 * 创建人：story
 * 创建时间：2017/11/30 15:55
 */

public class DensityUtil {

    /**
     * dp长度转为px
     */
    public static int changeDpToPx(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
