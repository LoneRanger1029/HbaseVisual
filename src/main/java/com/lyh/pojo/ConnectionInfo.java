package com.lyh.pojo;

import com.lyh.service.ConnectionService;
import javafx.scene.control.ComboBox;

import java.util.*;

public class ConnectionInfo implements ConnectionService {

    public static String CURRENT_CONNECT_HOST;
    public static List<String> all_namespace = new ArrayList<>();
    public static Map<String, List<String>> namespaceMap = new HashMap<>();

    public ConnectionInfo(){}

    @Override
    public void deleteNamespace(String namespace) {
        for (int i = 0; i < all_namespace.size(); i++) {
            if (all_namespace.get(i).equals(namespace)){
                all_namespace.remove(i);
                break;
            }
        }
        namespaceMap.remove(namespace);
    }

    @Override
    public void addNamespace(String namespace) {
        all_namespace.add(namespace);
        namespaceMap.put(namespace, new ArrayList<>());
    }

    public static void destroy() {
        // 清空所有
        CURRENT_CONNECT_HOST = null;
        all_namespace.clear();
        namespaceMap.clear();
    }

    @Override
    public void addTable(String namespace, String tableName) {
        if (!namespaceMap.containsKey(namespace)){
            addNamespace(namespace);
        }
        namespaceMap.computeIfAbsent(namespace, k -> new ArrayList<>());
        namespaceMap.get(namespace).add(tableName);
    }



    @Override
    public void setTables(String namespace,List<String> tables) {
        namespaceMap.put(namespace,tables);
    }


}
