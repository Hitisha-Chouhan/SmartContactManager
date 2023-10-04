package com.smartcontactmanager.controller;

import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.objenesis.instantiator.basic.NewInstanceInstantiator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontactmanager.dao.ContactusRepository;
import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.entities.Contactus;
import com.smartcontactmanager.entities.User;
import com.smartcontactmanager.helper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ContactusRepository contactusRepo;
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Home - Smart Contact Manager");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title","About - Smart Contact Manager");
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title","Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		
		return "signup";
	}
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result,@RequestParam(value="agreement",defaultValue = "false") boolean agreement, 
			Model model,HttpSession session) {
		
		try {
			if (!agreement) {
				System.out.println("You have not agreed with our terms and conditions");
				throw new Exception("You have not agreed with our terms and conditons");
			}
			
			if(result.hasErrors()) {
				System.out.println("ERROR "+result.toString());
				model.addAttribute("user",user);
				return "signup";
			}
			if (user.getImageUrl()==null) {
				user.setImageUrl("default.png");
				
			}else {
				user.setImageUrl(user.getImageUrl());
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("agreement "+agreement);
			System.out.println("user "+user);
			User result1 = userRepo.save(user);
			System.out.println("result1 "+result1);
			model.addAttribute("user",new User());
			session.setAttribute("message",new Message("Successfully Registered", "alert-success"));
			return "signup";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message",new Message("Something went wrong !!"+e.getMessage(), "alert-danger"));
			return "signup";
		}
	}
	
	//login handler
	
	@GetMapping("/login")
	public String loginHandler(Model model) {
		model.addAttribute("title","Login - Smart Contact Manager");
		return "login";
	}
	
	
	
	@GetMapping("/contact")
	public String contactUs(Model model) {
		model.addAttribute("title","Contact Us - Smart Contact Manager");
		return "contactus";
		
	}
	
	@PostMapping("/do_contactus")
	public String contactusdb(@Valid @ModelAttribute("contactus") Contactus contactus,BindingResult result,Model model,HttpSession session) {
		try {
			
			
			Date date=new Date();
			contactus.setDate(date);
			contactusRepo.save(contactus);
			//model.addAttribute("contactUs",new Contactus());
			session.setAttribute("message",new Message("Your message has sent successfully", "alert-success"));
			return "contactus";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			session.setAttribute("message",new Message("Something went wrong. Please try again !!"+e.getMessage(), "alert-danger"));
			return "contactus";
		}
	}
	
	

}
