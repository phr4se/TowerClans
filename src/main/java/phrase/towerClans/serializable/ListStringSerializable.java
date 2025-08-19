package phrase.towerClans.serializable;

import java.util.Arrays;
import java.util.List;

public class ListStringSerializable {

    public static String listToString(List<String> list) {
        return String.join("|", list);
    }

    public static List<String> stringToList(String data) {
        return Arrays.asList(data.split("\\|"));
    }

}
