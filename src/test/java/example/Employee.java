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

public class Employee {
  int empId;
  String empName;
  double salary;
  EmployeeType type;
  
  /**
   * @return the empId
   */
  public int getEmpId() {
    return empId;
  }
  /**
   * @param empId the empId to set
   */
  public void setEmpId(int empId) {
    this.empId = empId;
  }
  /**
   * @return the empName
   */
  public String getEmpName() {
    return empName;
  }
  /**
   * @param empName the empName to set
   */
  public void setEmpName(String empName) {
    this.empName = empName;
  }
  /**
   * @return the salary
   */
  public double getSalary() {
    return salary;
  }
  /**
   * @param salary the salary to set
   */
  public void setSalary(double salary) {
    this.salary = salary;
  }
  /**
   * @return the type
   */
  public EmployeeType getType() {
    return type;
  }
  /**
   * @param type the type to set
   */
  public void setType(EmployeeType type) {
    this.type = type;
  }
 
}
