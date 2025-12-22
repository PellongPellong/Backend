package com.orumi.pelongpelong.application.bedrocktool.toolmodule

enum class FeatVisitor(val month: String, val avgVisitor: Long) {
    JANUARY("01", 13270540),
    FEBRUARY("02", 11906444),
    MARCH("03", 11294392),
    APRIL("04", 12860888),
    MAY("05", 13770124),
    JUNE("06", 12546617),
    JULY("07", 13604798),
    AUGUST("08", 16033118),
    SEPTEMBER("09", 12216031),
    OCTOBER("10", 12216031),   //data 없어서 9월거 씀
    NOVEMBER("11", 11990052),
    DECEMBER("12", 11248889);

  companion object {
    fun of(month: String): FeatVisitor {
      return FeatVisitor.entries.first { it.month == month }
    }
  }

}