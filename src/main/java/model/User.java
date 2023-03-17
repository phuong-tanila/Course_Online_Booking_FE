package model;

import lombok.*;

import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    public int id;
    public String password;
    public String fullname;
    public String phone;
    public String email;
    public String role;
    public String description;
    public String avatar;
    public Set<Course> coursesCreateBy;
    public Set<Course> coursesTeacher;
    public Set<Course> coursesLastUpdateUser;
    public Set<Order> orders;
    public Set<Feedback> feedbacks;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", fullname='" + fullname + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", description='" + description + '\'' +
                ", avatar='" + avatar + '\'' +
                ", coursesCreateBy=" + coursesCreateBy +
                ", coursesTeacher=" + coursesTeacher +
                ", coursesLastUpdateUser=" + coursesLastUpdateUser +
                ", orders=" + orders +
                ", feedbacks=" + feedbacks +
                '}';
    }
}
