//package com.okeyifee.hrservice.controllers;
//
//import com.okeyifee.hrservice.dto.EmployeeDto;
//import com.okeyifee.hrservice.payload.ApiResponse;
//import com.okeyifee.hrservice.services.HrService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//
//@RestController
//@RequestMapping("/hr")
//public class HrController {
//
//    private final HrService hrService;
//
//    @Autowired
//    public HrController(HrService hrService) {
//        this.hrService = hrService;
//    }
//
//    @PostMapping("/register_Employee")
//    public ResponseEntity<ApiResponse> saveEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
//        return hrService.saveEmployee(employeeDto);
//    }
//
//    @GetMapping("/retrieve_All_Employees")
//    public ResponseEntity<ApiResponse> retrieveAllEmployees() {
//        return hrService.findAllEmployees();
//    }
//
//    @GetMapping("/retrieve_Employee/{id}")
//    public ResponseEntity<ApiResponse> retrieveEmployeeById(@PathVariable Long id) {
//        return hrService.findEmployeeById(id);
//    }
//
//    @GetMapping("/retrieve_Available_Employee/{id}")
//    public ResponseEntity<ApiResponse> retrieveAvailableEmployeeById(@PathVariable Long id) {
//        return hrService.findEmployeeById(id);
//    }
//
//    @PostMapping("/edit_Employee/{id}")
//    public ResponseEntity<ApiResponse> editEmployeeById(@PathVariable Long id, @Valid @RequestBody EmployeeDto employeeDto) {
//        return hrService.editEmployeeById(id, employeeDto);
//    }
//
//    @GetMapping("/delete_Employee/{id}")
//    public ResponseEntity<ApiResponse> deleteEmployeeById(@PathVariable Long id) {
//        return hrService.deleteEmployeeById(id);
//    }
//}
//
