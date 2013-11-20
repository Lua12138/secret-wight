package com.baidu.maven.utils;

import java.util.HashMap;
import java.util.Map;

public class NestedException extends Exception {
    private Map<String, String> map;
    private ExceEnum errCode;

    public NestedException() {
        map = new HashMap<String, String>();
    }
}