import co.wangming.nsb.server.spring.springboot.SpringBootNettyProperties;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created By WangMing On 2019-12-21
 **/
public class GenerateConfigurationMetadata {

    public static void main(String[] args) {

        List list = new ArrayList();
        for (Field declaredField : SpringBootNettyProperties.class.getDeclaredFields()) {

            Map map = new HashMap();
            list.add(map);

            map.put("sourceType", SpringBootNettyProperties.class.getCanonicalName());
            map.put("name", "spring.boot.netty." + declaredField.getName());
            map.put("type", declaredField.getType().getCanonicalName());
            map.put("description", "");

        }

        System.out.println(list);
    }
}
