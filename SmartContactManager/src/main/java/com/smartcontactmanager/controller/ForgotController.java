package com.smartcontactmanager.controller;

import javax.servlet.http.HttpSession;

import org.hibernate.boot.model.naming.ImplicitNameSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.entities.User;
import com.smartcontactmanager.helper.Message;
import com.smartcontactmanager.service.EmailService;

@Controller
public class ForgotController {

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	//email id form handler
	
	@GetMapping("/forgot")
	public String openEmailForm() {
		
		return "forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email,HttpSession session) {
		
		System.out.println("email "+email);
		//generating otp of 4 digits
		int min=1000;
		int max=10000;
		int otp = (int) (Math.random()*(max-min+1)+min);
		System.out.println("otp "+otp);
		
		//write code to send otp on registered email
		
		String subject="OTP from SCM";
		String message=""
				+ "<div style='border:1px solid #e2e2e2; padding:20px;'>"
				+ "<h1>"
				+ "Your OTP To Set Password is "
				+ "<b>"+otp
				+ "</b>"
				+ "</h1>"
				+ "</div>";
		String to=email;
		String from="pradhanmanish9502@gmail.com";
		
		boolean flag = emailService.sendEmailForText(message, subject, to, from);
		
		if(flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email",email);
			return "verify_otp";
		}else {
			session.setAttribute("message", new Message("Check your email id !!", "danger"));
			return "forgot_email_form";
		}
		
		
	}
	
	//verify otp
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") Integer otp,HttpSession session) {
		int myOtp=(int) session.getAttribute("myotp");
		String email=(String) session.getAttribute("email");
		
		if (myOtp==otp) {
			//password change form
			User user= userRepo.getUserByUserName(email);
			
			if (user==null) {
				//send error message
				session.setAttribute("message",new Message("User does not exists with this email.", "danger"));
				return "forgot_email_form";
			}else {
				//send change password form
			}
			
		//	session.setAttribute("message", new Message("Password changed successfully !!", "danger"));
			return "password_change_form";
		}else {
			session.setAttribute("message", new Message("OTP is invalid !!", "danger"));
			return "verify_otp";
		}
		
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPassword") String newPassword,HttpSession session) {
		String email=(String) session.getAttribute("email");
		User user = userRepo.getUserByUserName(email);
		user.setPassword(bCryptPasswordEncoder.encode(newPassword));
		userRepo.save(user);
		
		return "redirect:/login?change=Your password has changed successfully";
	}
	
	
}
