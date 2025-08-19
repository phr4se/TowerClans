package phrase.towerClans.serializable;

import phrase.towerClans.clan.permission.Permission;
import phrase.towerClans.clan.permission.PermissionType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PermissionSerializable {

    public static String permissionToString(Permission permission) {
        return String.join("|", permission.getPermissionTypes().stream().map(Enum::toString).toList());
    }

    public static List<PermissionType> stringToPermission(String data) {
        return Arrays.stream(data.split("\\|")).map(string -> {

            PermissionType permissionType = null;
            try {
                permissionType = PermissionType.valueOf(string);
            } catch (IllegalArgumentException ignored) {}

            return permissionType;

        }).filter(permissionType -> permissionType != null).collect(Collectors.toList());
    }

}
