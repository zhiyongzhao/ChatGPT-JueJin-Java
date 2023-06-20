package com.aijuejin.webchat.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description: 工具类
 * @Title: ObjUtil
 * @Package com.aijuejin.webchat.common.utils
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/19 15:12
 */

@Slf4j
@Component
public class ObjUtil {

    public static String getNotBlankValSequential(String defaultValue, String ...strings) {
        for (String str : strings) {
            if (str != null && str.length() > 0) {
                return str;
            }
        }
        return defaultValue;
    }

    public static int getNotNullValSequential(int defaultValue, Integer ...objects) {
        for (Integer obj : objects) {
            if (obj != null) {
                return obj;
            }
        }
        return defaultValue;
    }

    public static String getNotNullValSequential(String defaultValue, Object ...objects) {
        for (Object obj : objects) {
            if (obj != null) {
                return String.valueOf(obj);
            }
        }
        return defaultValue;
    }
}
