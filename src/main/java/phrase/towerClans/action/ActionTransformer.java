package phrase.towerClans.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionTransformer {

    private static final Pattern PATTERN = Pattern.compile("\\[(\\S+)] ?(.*)");

    public static Map<ActionType, String> transform(List<String> settings) {

        Map<ActionType, String> map = new HashMap<>();

        for(String setting : settings) {

            Matcher matcher = PATTERN.matcher(setting);

            ActionType actionType = null;
            String message = null;

            while(matcher.find()) {

                actionType = ActionType.valueOf(matcher.group(1).toUpperCase());
                message = matcher.group(2);

            }

            if(actionType != null && message != null) map.put(actionType, message);

        }

        return map;

    }

}
