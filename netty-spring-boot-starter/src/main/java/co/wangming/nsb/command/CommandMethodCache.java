package co.wangming.nsb.command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created By WangMing On 2019-12-07
 **/
public class CommandMethodCache {

    private static Map<String, CommandMethod> ID2MethodInfo = new ConcurrentHashMap<>();


    public static synchronized CommandMethod add(String id, CommandMethod commandMethod) {
        return ID2MethodInfo.put(id, commandMethod);
    }

    public static synchronized CommandMethod getMethodInfo(String id) {
        return ID2MethodInfo.get(id);
    }

}
