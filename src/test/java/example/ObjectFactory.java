package example;

public class ObjectFactory {
  public Employee createEmpleado() {
    return new Employee();
  }
  
  public Company createCorporacion()  {
    return new Company();
  }
}
