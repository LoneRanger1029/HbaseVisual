package com.lyh.panes.enums;
public enum FilterType {
    ROW_KEY_FILTER("rowKeyFilter"),
    PREFIX_FILTER("prefixFilter"),
    FIRST_KEY_ONLY_FILTER("firstKeyOnlyFilter"),
    VALUE_FILTER("valueFilter"),
    KEY_ONLY_FILTER("keyOnlyFilter"),
    RANDOM_ROW_FILTER("randomRowFilter"),
    INCLUSIVE_STOP_FILTER("inclusiveStopFilter"),
    COLUMN_PREFIX_FILTER("columnPrefixFilter"),
    COLUMN_COUNT_GET_FILTER("columnCountGetFilter"),
    SINGLE_COLUMN_VALUE_FILTER("singleColumnValueFilter"),
    SINGLE_COLUMN_VALUE_EXCLUDE_FILTER("singleColumnValueExcludeFilter"),
    SKIP_FILTER("skipFilter"),
    WHILE_MATCH_FILTER("whileMatchFilter"),
    FILTER_LIST("filterList");

    private final String filterName;

    FilterType(String filterName) {
        this.filterName = filterName;
    }

    public String getFilterName() {
        return filterName;
    }
}