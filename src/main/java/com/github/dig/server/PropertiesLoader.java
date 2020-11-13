package com.github.dig.server;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@UtilityClass
public class PropertiesLoader {

    public static Properties load(@NonNull File file) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));
        return properties;
    }
}
