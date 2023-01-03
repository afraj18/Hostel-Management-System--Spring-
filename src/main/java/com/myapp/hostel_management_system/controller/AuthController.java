package com.myapp.hostel_management_system.controller;

import com.myapp.hostel_management_system.entity.Student;
import com.myapp.hostel_management_system.entity.User;
import com.myapp.hostel_management_system.repository.StudentRepository;
import com.myapp.hostel_management_system.repository.UserRepository;
import com.myapp.hostel_management_system.repository.WardenRepository;
import com.myapp.hostel_management_system.util.MD5;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    WardenRepository wardenRepository;

    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(User user, Model model) {
        try {
            if (user.getEmail().endsWith("@std.uwu.ac.lk")) {
                // create Student object using User object
                Student student = (Student) user;
                studentRepository.save(student);
                model.addAttribute("msg", "Account created successfully");
            } else {
                model.addAttribute("error", "Enter university email address");
            }
        } catch (Exception e) {
            model.addAttribute("error", "There was an error processing the request\n" +
                    "try again later");
        }
        return "auth/register";
    }

    @GetMapping("/login")
    public String viewLogin() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String loginUser(User user, Model model, HttpSession session) {
        User dbUser = userRepository.getReferenceByEmail(user.getEmail());
        if (dbUser != null) {
            if (dbUser.getPassword().equals(MD5.getMd5(user.getPassword()))) {
                model.addAttribute("msg", "Welcome " + dbUser.getFirstname() + " " + dbUser.getLastname());
                session.setAttribute("user", dbUser);
                return "app/home";
            } else {
                model.addAttribute("error", "Invalid password");
            }
        } else {
            model.addAttribute("error", "Invalid email");
        }
        return "auth/login";
    }

    @GetMapping("/logout")
    public String logoutUser(HttpServletRequest request) {
        request.getSession().invalidate();
        return "app/home";
    }
}