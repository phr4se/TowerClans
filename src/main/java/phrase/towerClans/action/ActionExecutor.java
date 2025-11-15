package phrase.towerClans.action;

import org.bukkit.entity.Player;
import phrase.towerClans.action.context.Context;
import phrase.towerClans.action.context.impl.StringContext;

import java.util.Map;
import java.util.function.Function;

public class ActionExecutor {

    private static final Map<Class<? extends Context>, Function<String, Context>> VALIDATORS = Map.of(
            StringContext.class, StringContext::validate
    );

    public static void execute(Player player, Map<ActionType, String> map) {

        map.entrySet().forEach(entry -> {
            ActionType actionType = entry.getKey();
            String message = entry.getValue();
            Action<Context> action = actionType.getAction();
            Function<String, ? extends Context> validate = VALIDATORS.get(actionType.getContext());
            action.execute(player, validate.apply(message));
        });

    }

}
