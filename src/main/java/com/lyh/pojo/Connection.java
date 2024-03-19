package com.lyh.pojo;

public class Connection {
    private String host;
    private String name;
    private String port;

    public Connection(){}


    public Connection(String host, String name, String port) {
        this.host = host;
        this.name = name;
        this.port = port;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "host='" + host + '\'' +
                ", name='" + name + '\'' +
                ", port='" + port + '\'' +
                '}';
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
