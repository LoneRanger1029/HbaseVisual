package com.lyh.manager;

import javafx.scene.Node;

import java.util.HashMap;

public class ViewManager {

    private static ViewManager instance;
    private static final HashMap<String, Node> CONTROLS = new HashMap<>();
    private static String controlName;

    public ViewManager(){}

    public static ViewManager getInstance() {
        if (instance == null){
            instance = new ViewManager();
        }
        return instance;
    }

    public void put(String name,Node node){
        controlName = name;
        CONTROLS.put(name,node);
    }

    public Node get(String name){
        return CONTROLS.get(name);
    }

    public int getSize(){
        return CONTROLS.size();
    }

}
