//package com.okeyifee.hrservice.entities;
//
//import com.okeyifee.hrservice.utils.Roles;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//
//@EqualsAndHashCode(callSuper = true)
//@Data
//@Entity(name = "employees")
//public class Employee extends BaseModel{
//
//    static final long serialVersionUID = 1L;
//
//    private String surName;
//
//    private String firstName;
//
//    private String middleName;
//
//    @Column(name = "password")
//    private String password;
//
//    @Column(name = "specialty")
//    private String specialty;
//
//    @Column(name = "role")
//    private Roles role;
//
//    @Column(name = "email", unique = true)
//    private String email;
//
//    @Column(name = "phone", unique = true)
//    private String phoneNumber;
//
//    @Column(name = "isActive")
//    private Boolean isActive;
//
//    @Column(name = "onLeave")
//    private Boolean onLeave;
//}
