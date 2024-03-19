package com.lyh.pojo;

import com.lyh.pojo.ConnectionInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理连接信息
 * k: host
 * v: ConnectionInfo
 */
public class ConnectInfoManager {

    public static Map<String, ConnectionInfo> connectionInfoMap = new HashMap<>();

    public static void addConnection(String hosts){
        connectionInfoMap.put(hosts,new ConnectionInfo());
    }

    public static ConnectionInfo getConnectionInfo(String hosts){
        if (!connectionInfoMap.containsKey(hosts)){
            return null;
        }
        return connectionInfoMap.get(hosts);
    }

}
