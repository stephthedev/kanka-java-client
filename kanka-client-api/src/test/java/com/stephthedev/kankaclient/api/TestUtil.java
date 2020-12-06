package com.stephthedev.kankaclient.api;

import java.io.File;
import java.io.IOException;

public class TestUtil {

    public static File loadFile(String fileName) throws IOException {
        ClassLoader classLoader = TestUtil.class.getClassLoader();
        File responseFile = new File(classLoader.getResource(fileName).getFile());
        return responseFile;
    }
}
