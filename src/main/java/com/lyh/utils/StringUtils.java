package com.lyh.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class StringUtils {
    public static void printArray(String[] arr) {
        for (String s : arr) {
            System.out.println(s);
        }
    }


    public static String[] listToArray(List<String> list) {
        String[] arr = new String[list.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    public static String[] setToArray(Set<String> set) {
        String[] arr = new String[set.size()];
        int index = 0;
        for (String s : set) {
            arr[index++] = s;
        }
        return arr;
    }

}
