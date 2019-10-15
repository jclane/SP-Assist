package me.justinlane.enums;

/** 
 * Enum representing all possible search types.
 */
public enum SearchTypeEnum {
  PART_CLASS(0), PART_NUMBER(1), PART_DESCRIPTION(2), ALL(3);
  
  private Integer value; 
  
  private SearchTypeEnum(final Integer value) {
    this.value = value;
  }
  
  public Integer getSearchInt() {
    return this.value;
  }
  
}