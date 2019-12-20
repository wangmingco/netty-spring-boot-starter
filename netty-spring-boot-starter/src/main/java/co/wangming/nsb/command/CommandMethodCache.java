package co.wangming.nsb.command;

import java.util.HashMap;
import java.util.Map;

/**
 * Created By WangMing On 2019-12-07
 **/
public class CommandMethodCache {

    private static Map<String, CommandMethod> ID2MethodInfo = new HashMap<>();


    public static CommandMethod add(String id, CommandMethod commandMethod) {
        return ID2MethodInfo.put(id, commandMethod);
    }

    public static CommandMethod getMethodInfo(String id) {
        return ID2MethodInfo.get(id);
    }

}
