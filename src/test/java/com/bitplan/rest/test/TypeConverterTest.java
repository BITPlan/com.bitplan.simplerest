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
package com.bitplan.rest.test;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;
import static org.junit.Assert.*;
import com.bitplan.datatypes.DefaultTypeConverter;


/**
 * test default TypeConverter
 * 
 * @author wf
 * 
 */
public class TypeConverterTest  {
  protected Logger LOGGER = Logger.getLogger("com.bitplan.rest.test");
	protected boolean debug=false;
	
	/**
	 * get the class Name of the given object
	 * @param o
	 * @return
	 */
	public String getClassName(Object o) {
		String result="?";
		if (o!=null) {
			result=o.getClass().getSimpleName();
		} 
		return result;
	}

	/**
	 * check the value against the expected
	 * @param title
	 * @param value
	 * @param expected
	 */
	public void check(String title,Object value, Object expected) {
		String expectedClassName=this.getClassName(expected);
		String valueClassName=this.getClassName(value);
		if (debug)
		  LOGGER.log(Level.INFO,"check "+title+":"+expected+"("+valueClassName+")="+value+"("+expectedClassName+")?");
		if (expected==null) { 
			assertNull(title,value); 
		} else {
		  assertEquals(title,expected,value);
		}
	}
	
	/**
	 * get an isoDate from the given String
	 * @param isoDate
	 * @return
	 * @throws ParseException
	 */
	public Date getIsoDate(String isoDate) throws ParseException {
		SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY);
		Date result=ISO8601DATEFORMAT.parse(isoDate);
		return result;
	}
	
	/**
	 * test the type converters
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTypeConverter() throws Exception {
		DefaultTypeConverter tc = new DefaultTypeConverter();
		check("String null" ,tc.getString (DefaultTypeConverter.NULL_VALUE   ),"".equals(DefaultTypeConverter.NULL_VALUE) ? "":null);	
		check("String some" ,tc.getString ("some"),"some");
	  check("Boolean true",tc.getBoolean("true"),new Boolean(true));
	  check("Boolean false",tc.getBoolean("FALSE"),new Boolean(false));
	  check("Boolean",tc.getBoolean(DefaultTypeConverter.UNICODE_TRUE),new Boolean(true));
	  check("Boolean",tc.getBoolean(DefaultTypeConverter.UNICODE_FALSE),new Boolean(false));
	  // FIXME this is not really tri-state ...
	  check("Boolean",tc.getBoolean(DefaultTypeConverter.UNICODE_NULL),new Boolean(false));
	  check("Iso date",tc.getDate("2013-03-26"),getIsoDate("2013-03-26 00:00:00"));
	  double doubleValue=2.15;
	  NumberFormat dformat=NumberFormat.getInstance();
	  String doubleStr=dformat.format(doubleValue);
	  check("comma in numbers",tc.getDouble(doubleStr),new Double(2.15));
	}

}
