package phrase.towerclans.action;

import org.bukkit.entity.Player;
import phrase.towerclans.action.context.Context;
import phrase.towerclans.action.context.impl.StringContext;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ActionExecutor {
    private static final Map<Class<? extends Context>, Function<String, Context>> VALIDATORS = Map.of(
            StringContext.class, StringContext::validate
    );

    public static void execute(Player player, Map<ActionType, List<String>> map) {
        map.entrySet().forEach(entry -> {
            ActionType actionType = entry.getKey();
            List<String> messages = entry.getValue();
            Action<Context> action = actionType.getAction();
            Function<String, ? extends Context> validate = VALIDATORS.get(actionType.getContext());
            messages.forEach(message -> action.execute(player, validate.apply(message)));
        });
    }
}
