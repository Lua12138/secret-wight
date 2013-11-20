package com.baidu.maven.utils;

public enum ExceEnum {
    Zero(0), Nomal(100);

    ExceEnum(int s) {
        this.code = s;
    }

    private int code;
}