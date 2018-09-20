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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * help with displaying values
 * @author wf
 *
 */
public class ValueDisplayer implements ValueDisplay {
  
  // a null value is represented with an empty String
  protected static String nullValueRepresentation="";

  /**
   * format the given Timestamp
   * 
   * @param ts
   * @return the timeStamp as an ISO date/time value
   */
  public String formatTimeStamp(Date ts) {
    String result = nullValueRepresentation;
    if (ts != null) {
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      result = df.format(ts);
    }
    return result;
  }

  /**
   * nullValue representation replaces the value with a null value representation if it is null
   * @param value
   * @return the null value Representation of the value
   */
  public String nullValue(Object value) {
    // compare to http://www.1keydata.com/sql/sql-nvl.html
    String result = "";
    if (value == null)
      result = nullValueRepresentation;
    else {
      if (value instanceof Date) {
        result+=formatTimeStamp((Date) value);
      } else {
        result += value.toString();
      }
    } 
    return result;
  }

  @Override
  public void setNullValueRepresentation(String pNullValueRepresentation) {
    nullValueRepresentation=pNullValueRepresentation;
  }

  @Override
  public String getNullValueRepresentation() {
    return nullValueRepresentation;
  }

  @Override
  public String fromNullValue(String value) {
    if (value!=null && value.equals(nullValueRepresentation)) {
      // if the nullValueRepresentation is "" we'll keep the string as is otherwise we'll null it
      if (!"".equals(value))
        value=null;
    }
    return value;
  }
}
