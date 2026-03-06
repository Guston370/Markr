package com.example.markr.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("_id")
    private String id;
    private String name;
    private String email;
    private String password;
    @SerializedName("studentId")
    private String studentId;
    private String course;
    private String semester;
    private String year;
    private String degree;
    private String college;
    private String role;
    @SerializedName("isActive")
    private boolean active;
    @SerializedName("isEmailVerified")
    private boolean emailVerified;
    @SerializedName("lastLogin")
    private String lastLogin;
    @SerializedName("loginCount")
    private int loginCount;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("updatedAt")
    private String updatedAt;
    
    public User() {}
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getCourse() {
        return course;
    }
    
    public void setCourse(String course) {
        this.course = course;
    }
    
    public String getSemester() {
        return semester;
    }
    
    public void setSemester(String semester) {
        this.semester = semester;
    }
    
    public String getYear() {
        return year;
    }
    
    public void setYear(String year) {
        this.year = year;
    }
    
    public String getDegree() {
        return degree;
    }
    
    public void setDegree(String degree) {
        this.degree = degree;
    }
    
    public String getCollege() {
        return college;
    }
    
    public void setCollege(String college) {
        this.college = college;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isEmailVerified() {
        return emailVerified;
    }
    
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    public String getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public int getLoginCount() {
        return loginCount;
    }
    
    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
