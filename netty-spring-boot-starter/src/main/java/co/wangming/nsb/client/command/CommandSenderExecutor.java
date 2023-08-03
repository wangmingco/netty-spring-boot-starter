package co.wangming.nsb.client.command;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ming.wang
 * @date 2023/8/3
 */
public class CommandSenderExecutor {

    private static final List<Runnable> commands = new ArrayList<>();

    public static void addCommand(Runnable runnable) {
        commands.add(runnable);
    }

    public static void sendCommands() {
        for (Runnable command : commands) {
            command.run();
        }
    }

}
