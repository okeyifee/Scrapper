//package com.okeyifee.hrservice.services.HrServiceImpl;
//
//import com.okeyifee.hrservice.dto.EmployeeDto;
//import com.okeyifee.hrservice.entities.Employee;
//import com.okeyifee.hrservice.payload.ApiResponse;
//import com.okeyifee.hrservice.repositories.HrRepository;
//import com.okeyifee.hrservice.services.HrService;
//import com.okeyifee.hrservice.utils.Roles;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.okeyifee.hrservice.utils.BuildResponse.buildResponse;
//
//@Service
//public class HrServiceImpl implements HrService {
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        HrRepository hrRepository;
//        ModelMapper mapper;
//
//        @Autowired
//        public HrServiceImpl(HrRepository hrRepository, ModelMapper mapper) {
//            this.hrRepository = hrRepository;
//            this.mapper = mapper;
//        }
//
//
//        @Override
//    public ResponseEntity<ApiResponse> findAllEmployees() {
//            List<EmployeeDto> retrieved_Employees = new ArrayList<>();
//            ApiResponse<List<EmployeeDto>> response = new ApiResponse<>();
//            List<Employee> employee_list = hrRepository.findAllEmployees();
//            if (employee_list.size() > 0){
//                employee_list.forEach((Employee a) -> {
//                    EmployeeDto newEmployee = mapper.map(a, EmployeeDto.class);
//                    retrieved_Employees.add(newEmployee);
//                });
//                response.setData(retrieved_Employees);
//                response.setMessage("Employee list successfully retrieved");
//                response.setStatus(HttpStatus.OK);
//            } else {
//                response.setMessage("No Employee Found");
//                response.setStatus(HttpStatus.NOT_FOUND);
//            }
//            return buildResponse(response);
//    }
//
//    @Override
//    public ResponseEntity<ApiResponse> saveEmployee(EmployeeDto employeeDto) {
//        Employee new_Employee = mapper.map(employeeDto, Employee.class);
//        new_Employee.setIsActive(true);
//        new_Employee.setOnLeave(false);
//        new_Employee.setPassword(employeeDto.getHashedPassword());
//
//        System.out.println(employeeDto.getRole());
//
//        switch (employeeDto.getRole()){
//            case Nurse:
//                new_Employee.setRole(Roles.Nurse);
//                break;
//            case Doctor:
//                new_Employee.setRole(Roles.Doctor);
//                break;
//            case Interns:
//                new_Employee.setRole(Roles.Interns);
//                break;
//            case Pharmacist:
//                new_Employee.setRole(Roles.Pharmacist);
//                break;
//            case Lab_Technicians:
//                new_Employee.setRole(Roles.Lab_Technicians);
//                break;
//            default:
//                new_Employee.setRole(Roles.Others);
//                break;
//        }
//
//        hrRepository.save(new_Employee);
//        ApiResponse<String> response = new ApiResponse<>(HttpStatus.CREATED);
//        response.setMessage("Employee Registration Successful");
//        return buildResponse(response);
//    }
//
//    @Override
//    public ResponseEntity<ApiResponse> deleteEmployeeById(Long id) {
//        return null;
//    }
//
//    @Override
//    public ResponseEntity<ApiResponse> editEmployeeById(Long id, EmployeeDto employeeDto) {
//        return null;
//    }
//
//    @Override
//    public ResponseEntity<ApiResponse> findEmployeeById(Long id) {
//        return getApiResponseResponseEntity(id);
//    }
//
//    @Override
//    public ResponseEntity<ApiResponse> findAvailableEmployeeById(Long id) {
//        return getApiResponseResponseEntity(id);
//    }
//
//
//
//
//
//    private ResponseEntity<ApiResponse> getApiResponseResponseEntity(Long id) {
//        ApiResponse<EmployeeDto> response = new ApiResponse<>();
//        Employee answer = hrRepository.findById(id).orElse(null);
//
//        if (answer != null){
//            EmployeeDto newEmployee = mapper.map(answer, EmployeeDto.class);
//            response.setData(newEmployee);
//            response.setMessage("Employee successfully retrieved");
//            response.setStatus(HttpStatus.OK);
//        } else {
//            response.setMessage("Not Found");
//            response.setStatus(HttpStatus.NOT_FOUND);
//        }
//        return buildResponse(response);
//    }
//}
