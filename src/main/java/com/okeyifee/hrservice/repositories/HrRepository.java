//package com.okeyifee.hrservice.repositories;
//
//import com.okeyifee.hrservice.entities.Employee;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface HrRepository extends JpaRepository<Employee, Long> {
//
//    Employee save(Employee employee);
//
//    @Query(value="SELECT * FROM employees u WHERE  Is_Active = true AND id = ?1", nativeQuery = true)
//    Employee findEmployeeById(Long id);
//
//    @Query(value="SELECT * FROM employees u WHERE  Is_Active = true AND on_Leave = false AND id = ?1", nativeQuery = true)
//    Employee findAvailableEmployeeById(Long id);
//
//    @Query(value="SELECT * FROM employees u WHERE is_Active = true", nativeQuery = true)
//    List<Employee> findAllEmployees();
//}
