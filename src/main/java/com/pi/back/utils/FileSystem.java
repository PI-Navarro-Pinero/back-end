package com.pi.back.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public enum FileSystem {

    BASEDIR(Path.BASE_DIR),
    LOGS(Path.LOGS_DIR),
    OUTPUTS(Path.OUTPUTS_DIR);

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Path {
        public static String BASE_DIR = System.getenv("BASE_DIR");
        public static String OUTPUTS_DIR = BASE_DIR + "/outputs";
        public static String LOGS_DIR = BASE_DIR + "/.logs";
    }

    private final String path;
}
