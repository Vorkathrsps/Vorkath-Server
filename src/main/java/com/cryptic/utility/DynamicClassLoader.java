package com.cryptic.utility;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @Author: Origin
 * @Date: 2/19/24
 */
public class DynamicClassLoader {
    public static String resolveClasspath(String className) {
        if (className != null && className.length() > 0) {
            try (Stream<Path> classFiles = Files.find(getClasspathRoot(), Integer.MAX_VALUE, (filePath, fileAttr) -> filePath.endsWith(className.replace('.', '/') + ".class"))) {
                Optional<Path> foundClassFile = classFiles.findFirst();
                if (foundClassFile.isPresent()) {
                    Path classFile = foundClassFile.get();
                    return getClasspathFromPath(classFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String getClasspathFromPath(Path classFile) {
        String filePath = classFile.toString();
        return filePath.substring(Objects.requireNonNull(getClasspathRoot()).toString().length() + 1, filePath.length() - 6).replace(FileSystems.getDefault().getSeparator(), ".");
    }

    private static Path getClasspathRoot() {
        try {
            return Paths.get(DynamicClassLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
