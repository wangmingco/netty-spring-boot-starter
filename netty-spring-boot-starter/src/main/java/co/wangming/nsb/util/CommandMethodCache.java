package co.wangming.nsb.util;

import co.wangming.nsb.vo.MethodInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created By WangMing On 2019-12-07
 **/
public class CommandMethodCache {

    private static Map<String, MethodInfo> ID2MethodInfo = new HashMap<>();


    public static MethodInfo add(String id, MethodInfo methodInfo) {
        return ID2MethodInfo.put(id, methodInfo);
    }

    public static MethodInfo getMethodInfo(String id) {
        return ID2MethodInfo.get(id);
    }

}
