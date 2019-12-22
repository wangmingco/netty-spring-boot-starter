package co.wangming.nsb.context;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created By WangMing On 2019-12-20
 **/
public class ContextCache {

    public static final Map<String, ContextWrapper> cache = new ConcurrentHashMap<>();

    public static void put(ChannelHandlerContext key, Object context) {
        ContextWrapper contextWrapper = new ContextWrapper();
        contextWrapper.setContext(context);
        contextWrapper.setContextType(context.getClass());

        cache.put(getChannelId(key), contextWrapper);
    }

    public static ContextWrapper get(ChannelHandlerContext ctx) {
        return cache.get(getChannelId(ctx));
    }

    public static ContextWrapper remove(ChannelHandlerContext ctx) {
        return cache.remove(getChannelId(ctx));
    }

    private static String getChannelId(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        return channel.remoteAddress() + "_" + channel.localAddress();
    }
}
