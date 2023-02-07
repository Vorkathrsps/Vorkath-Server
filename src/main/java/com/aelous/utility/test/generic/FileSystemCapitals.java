package com.aelous.utility.test.generic;

import java.io.File;
import java.nio.file.Paths;

public class FileSystemCapitals {

    public static void main(String[] args) {
        // windows is case insensitive
        File file = Paths.get("./data/saves/characters/LA HACIENDA.json").toFile();
        File file1 = Paths.get("./data/saves/characters/LA HACIENDA.json".toLowerCase()).toFile();
        System.out.printf("%s %s %n", file.exists(), file1.exists());

        File file2 = new File("./data/saves/characters/LA HACIENDA.json");
        File file3 = new File("./data/saves/characters/LA HACIENDA.json".toLowerCase());
        System.out.printf("%s %s %s %s %n", file2.exists(), file3.exists(),
            file2.getName(), file3.getName());
    }
}
