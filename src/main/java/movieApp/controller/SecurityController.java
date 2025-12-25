package movieApp.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import movieApp.exception.UsernameExistException;
import movieApp.model.dto.UserRegistrationDto;
import movieApp.service.SecurityService;
import movieApp.service.UserService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/security")
public class SecurityController {
    public SecurityService securityService;
    public UserService userService;

    public SecurityController(SecurityService securityService, UserService userService) {
        this.securityService = securityService;
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String showRegistrationForm() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@Valid UserRegistrationDto userRegistrationDto,
                               BindingResult bindingResult,
                               Model model) throws SQLException, UsernameExistException {
        if (bindingResult.hasErrors()) {
            List<String> errMessages = new ArrayList<>();

            for (ObjectError objectError : bindingResult.getAllErrors()) {
                System.out.println(objectError);
                errMessages.add(objectError.getDefaultMessage());
            }
            throw new ValidationException(String.valueOf(errMessages));
        }
//        if (securityService.registration(userRegistrationDto)) {
//            return "redirect:/user"; //редирект вместо прямого возврата
//        }
        model.addAttribute("error_message", "Ошибка регистрации");
        return "registration";

    }

    @ExceptionHandler(UsernameExistException.class)
    public ModelAndView usernameExistException(UsernameExistException e) {
        System.out.println("User already exist: " + e.getUsername());
        ModelAndView model = new ModelAndView("error-page");
        model.setStatus(HttpStatus.BAD_REQUEST);
        model.addObject("errors", "Username '" + e.getUsername() + "' already exist");
        return model;
    }
}
