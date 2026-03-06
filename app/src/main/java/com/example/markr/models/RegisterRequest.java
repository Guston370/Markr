package com.example.markr.models;

public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String studentId;
    private String course;
    private String semester;
    private String year;
    private String degree;
    private String college;
    
    public RegisterRequest() {}
    
    public RegisterRequest(String name, String email, String password, String studentId, 
                          String course, String semester, String year, String degree, String college) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.studentId = studentId;
        this.course = course;
        this.semester = semester;
        this.year = year;
        this.degree = degree;
        this.college = college;
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
}
