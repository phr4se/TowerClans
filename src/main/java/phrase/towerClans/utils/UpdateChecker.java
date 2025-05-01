package phrase.towerClans.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker {

    public static String check() {

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/phr4se/TowerClans/master/VERSION").openStream()))) {
            return bufferedReader.readLine().trim();
        } catch (IOException ignored) {}

        return null;
    }

}
