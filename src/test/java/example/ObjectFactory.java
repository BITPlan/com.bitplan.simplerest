package example;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
  public Employee createEmpleado() {
    return new Employee();
  }
  
  public Company createCorporacion()  {
    return new Company();
  }
}
