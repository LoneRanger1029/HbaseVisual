package com.lyh.panes.enums;

public enum POIType {
    POI_2003(2003),
    POI_2007(2007);

    private final int version;

    POIType(int version) {
        this.version = version;
    }

}
