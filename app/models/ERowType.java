package models;

import org.apache.commons.lang3.StringUtils;

public enum ERowType {
    EXPENSE, RECIPE;

    public static ERowType value(String rowType){
        try {
            return ERowType.valueOf(StringUtils.upperCase(rowType));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public String lower(){
        return this.name().toLowerCase();
    }

}
