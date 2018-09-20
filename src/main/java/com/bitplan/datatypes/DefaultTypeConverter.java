/**
 * Copyright (c) 2016-2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.simplerest
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.datatypes;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the Default Type Converter.
 * 
 * @author wf
 * 
 */
public class DefaultTypeConverter extends ValueDisplayer
    implements TypeConverter {
  /**
   * the default LOGGER
   */
  protected static final Logger LOGGER = Logger.getLogger("com.bitplan.datatypes");

  /**
   * the representation of the value null
   */
  public static String NULL_VALUE = nullValueRepresentation;
  // pseudo null values as long as there is no NULL support for Types FIXME
  public static Short SHORT_NULL_VALUE = 0;
  public static Integer INTEGER_NULL_VALUE = 0;
  public static Long LONG_NULL_VALUE = 0l;
  public static Float FLOAT_NULL_VALUE = 0.0f;
  public static Double DOUBLE_NULL_VALUE = 0.0d;
  // FIXME - have real tristate?
  public static Boolean BOOLEAN_NULL_VALUE = false;
  public static String UNICODE_TRUE = "\u2705"; // e.g ✅
                                                // http://www.fileformat.info/info/unicode/char/2705/index.html
                                                // Heavy Check Mark
  public static String UNICODE_FALSE = "\u274c"; // ❌
                                                 // http://www.fileformat.info/info/unicode/char/274C/index.html
                                                 // Cross Mark 274C
  public static String UNICODE_NULL = "\u2753"; // ❓
                                                // http://www.fileformat.info/info/unicode/char/2753/index.html
                                                // Black Question Mark
  public static int UNICODE_NULL_VALUE = UNICODE_NULL.charAt(0);
  public static int UNICODE_TRUE_VALUE = UNICODE_TRUE.charAt(0);
  public static int UNICODE_FALSE_VALUE = UNICODE_FALSE.charAt(0);

  /**
   * flag for debugging
   */
  boolean debug = false;

  /**
   * handle the given exception
   * 
   * @param th
   */
  public void handleException(Throwable th) {
    if (debug) {
      LOGGER.log(Level.SEVERE, "", th);
      // System.err.println(th); // FIXME log exception
      // th.printStackTrace();
    } else {
      throw new RuntimeException(th);
    }
  }

  /**
   * check null or empty
   * 
   * @param value
   * @return reconverted empty and string NULL_VALUES to null
   */
  public String checkNullOrEmpty(String value) {
    if (value == null)
      return null;
    if (value.equals(NULL_VALUE))
      return null;
    if (value.trim().equals(""))
      return null;
    return value;
  }

  @Override
  public Long getLong(Object pLong) {
    Long result = 0L;
    if (pLong instanceof Long) {
      return ((Long) pLong).longValue();
    }

    if (pLong instanceof String) {
      String sValue = checkNullOrEmpty((String) pLong);
      if (sValue == null)
        return LONG_NULL_VALUE;
      try {
        result = Long.parseLong((String) pLong);
      } catch (Exception pe) {
        handleException(pe);
      }
    }

    return result;
  }

  @Override
  public String getString(Object pString) {
    if (pString instanceof String) {
      return super.fromNullValue((String) pString);
    }
    return null;
  }

  @Override
  public Boolean getBoolean(Object pBoolean) {
    Boolean result = null;
    if (pBoolean instanceof Boolean) {
      return (Boolean) pBoolean;
    }

    String sValue = checkNullOrEmpty((String) pBoolean);
    if (sValue == null)
      return BOOLEAN_NULL_VALUE;
    try {
      if (sValue.equals(UNICODE_TRUE))
        result = Boolean.TRUE;
      else if (sValue.equals(UNICODE_FALSE))
        result = Boolean.FALSE;
      else if (sValue.equals(UNICODE_NULL))
        result = BOOLEAN_NULL_VALUE;
      else
        result = Boolean.parseBoolean(sValue);
    } catch (Exception pe) {
      handleException(pe);
    }

    return result;
  }

  @Override
  public Integer getInteger(Object pInteger) {
    Integer result = null;
    if (pInteger instanceof Integer) {
      return (Integer) pInteger;
    }

    String sValue = checkNullOrEmpty((String) pInteger);
    if (sValue == null)
      return INTEGER_NULL_VALUE;
    try {
      result = Integer.parseInt(sValue);
    } catch (Exception pe) {
      handleException(pe);
    }

    return result;
  }

  @Override
  public Short getShort(Object pShort) {
    Short result = null;
    if (pShort instanceof Short) {
      return (Short) pShort;
    }

    String sValue = checkNullOrEmpty((String) pShort);
    if (sValue == null)
      return SHORT_NULL_VALUE;
    try {
      result = Short.parseShort(sValue);
    } catch (Exception pe) {
      handleException(pe);
    }

    return result;
  }

  @Override
  public Float getFloat(Object pFloat) {
    Float result = null;
    if (pFloat instanceof Float) {
      return (Float) pFloat;
    }

    String sValue = checkNullOrEmpty((String) pFloat);
    if (sValue == null)
      return FLOAT_NULL_VALUE;
    try {
      result = Float.parseFloat(sValue);
    } catch (Exception pe) {
      handleException(pe);
    }

    return result;
  }

  @Override
  public Double getDouble(Object pDouble) {
    Double result = null;
    if (pDouble instanceof Double) {
      return (Double) pDouble;
    }

    String sValue = checkNullOrEmpty((String) pDouble);
    if (sValue == null)
      return DOUBLE_NULL_VALUE;
    try {
      // http://stackoverflow.com/a/4323628/1497139
      NumberFormat format = NumberFormat.getInstance();
      Number number = format.parse(sValue);
      result = number.doubleValue();
    } catch (Exception pe) {
      handleException(pe);
    }

    return result;
  }

  @Override
  public Date getDate(Object pDate) {
    Exception pe = null;
    if (pDate instanceof Date) {
      return (Date) pDate;
    }

    if (pDate instanceof String) {
      String sValue = checkNullOrEmpty((String) pDate);
      if (sValue == null)
        return null;
      // TODO this is where all the magic happens...
      // support ISO yyyy-mm-dd
      String formatsToTest[] = { 
          "yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm", "yyyy-MM-dd", "dd.MM.yyyy HH:mm", "dd.MM.yy HH:mm",
          "dd.MM.yyyy", "dd.MM.yy" };
      for (String format : formatsToTest) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
          return formatter.parse((String) pDate);
        } catch (ParseException e) {
          pe = e;
        }
      }
      if (pe != null)
        handleException(pe);
      else
        handleException(
            new IllegalArgumentException("getDate cannot handle " + pDate));
    }

    return null;
  }

}
