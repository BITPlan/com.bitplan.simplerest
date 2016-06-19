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
