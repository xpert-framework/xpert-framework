package com.xpert.faces.primefaces;

/**
 * Define a count type for LazyDataModelImpl, used in query to count records
 * 
 * @author ayslan
 */
public enum LazyCountType {

      /**
     * Always count data (Execute count even in paging event, but only when filters change).It's the default value on LazyDataModelImpl
     */
    ALWAYS,
    /**
     * Don't count data. Useful for a data table with many records.
     */
    NONE,
    /**
     * Only count data once. Next paginations will get the first count.
     */
    ONLY_ONCE;
}
