package phrase.towerclans.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionTransformer {
    private static final Pattern PATTERN = Pattern.compile("\\[(\\S+)] ?(.*)");

    public static Map<ActionType, List<String>> transform(List<String> settings) {
        Map<ActionType, List<String>> map = new HashMap<>();
        for (String setting : settings) {
            Matcher matcher = PATTERN.matcher(setting);
            ActionType actionType = null;
            String message = null;
            while (matcher.find()) {
                actionType = ActionType.valueOf(matcher.group(1).toUpperCase());
                message = matcher.group(2);
            }
            if (actionType != null && message != null) {
                String finalMessage = message;
                map.compute(actionType, (k, v) -> {
                    if (v == null || v.isEmpty()) {
                        List<String> messages = new ArrayList<>();
                        messages.add(finalMessage);
                        return messages;
                    }
                    v.add(finalMessage);
                    return v;
                });
            }
        }
        return map;
    }
}
