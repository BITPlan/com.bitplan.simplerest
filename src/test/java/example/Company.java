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
package example;

import java.util.ArrayList;
import java.util.List;

public class Company {
  String companyId;
  String companyName;
  List<Employee> employees=new ArrayList<Employee>();

  /**
   * @return the companyId
   */
  public String getCompanyId() {
    return companyId;
  }
  /**
   * @param companyId the companyId to set
   */
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  /**
   * @return the companyName
   */
  public String getCompanyName() {
    return companyName;
  }
  /**
   * @param companyName the companyName to set
   */
  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }
  /**
   * @return the employees
   */
  public List<Employee> getEmployees() {
    return employees;
  }
  /**
   * @param employees the employees to set
   */
  public void setEmployees(List<Employee> employees) {
    this.employees = employees;
  }
}
