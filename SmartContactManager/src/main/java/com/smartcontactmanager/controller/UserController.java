package com.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartcontactmanager.dao.ContactRepository;
import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.entities.Contact;
import com.smartcontactmanager.entities.User;
import com.smartcontactmanager.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ContactRepository contactRepo;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("UserName "+userName);
		//get the user using username
		User userByUserName = userRepo.getUserByUserName(userName);
		System.out.println("USERBYUSERNAME "+userByUserName);
		model.addAttribute("user",userByUserName);
	}
	
	
	//dashboard home
	@GetMapping("/index")
	public String dashboard(Model model,Principal principal) {
		model.addAttribute("title","User Dashboard");
		
		return "normal/user_dashboard";
	}
	
	//open add form handler
	@GetMapping("add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		return "normal/add_contact_form";
	}
	
	//processing add contact form
	@PostMapping("/process-contact")
	public String addContactHandler(@Valid @ModelAttribute Contact contact,BindingResult result, @RequestParam("profileImage") MultipartFile file, Principal principal,Model model,HttpSession session) 
	{
		try {
		String name = principal.getName();
		User user = userRepo.getUserByUserName(name);
		if (result.hasErrors()) {
			System.out.println("ERROR "+result.toString());
			model.addAttribute("contact",contact);
			return "normal/add_contact_form";
			
		}
		
		//processing and uploading file
		
		if (file.isEmpty()) {
			System.out.println("File is empty");
			contact.setImage("profile.png");
			
		}else {
			//uplaod the file into the folder and update the name to contact
			contact.setImage(file.getOriginalFilename());
			File saveFile = new ClassPathResource("static/images").getFile();
		
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
		
			
			Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING );
		System.out.println("Image is uploaded successfully");
		
		}
		
		
		contact.setUser(user);
		user.getContacts().add(contact);
		userRepo.save(user);
		System.out.println("user in contact handler "+user);
		System.out.println("Contact added successfully");
		session.setAttribute("message",new Message("Contact added successfylly !! Add More..","success"));
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("ERROR "+e.getMessage());
			e.printStackTrace();
			session.setAttribute("message",new Message("Something went wrong... try again to add contact","danger"));
			return "normal/add_contact_form";
		}
		return "normal/add_contact_form";
	}
	
	//show contacts handler
	//per page=5[n]
	//current page=0 [page]
	@GetMapping("/view-contacts/{page}")
	public String showContactsHandler(@PathVariable("page") Integer page,Model model,Principal principal
			) {
		model.addAttribute("title","Show Contacts");
		
		/*//way first but this one is not good approach so follow second way approach
		 * String name = principal.getName();
		 * User userByUserName = userRepo.getUserByUserName(name); List<Contact>
		 * contacts = userByUserName.getContacts();
		 * model.addAttribute("contacts",contacts);
		 */
		
		//way second approach
		
		String name = principal.getName();
		System.out.println("get name "+name);
		User user = userRepo.getUserByUserName(name);
		System.out.println("userr "+user);
		
		Pageable pageable = PageRequest.of(page, 5);
		
		Page<Contact> contacts = contactRepo.getContactsByUserid(user.getId(),pageable);
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/show_contacts";
	}
	
	//showing particular contact details
	@GetMapping("{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId,Model model,Principal principal) {
		
		Optional<Contact> contactOptional = contactRepo.findById(cId);
		Contact contact = contactOptional.get();
		
		String name = principal.getName();
		User user= userRepo.getUserByUserName(name);
		
		if (user.getId()==contact.getUser().getId()) {
			model.addAttribute("contact",contact);
			model.addAttribute("title",contact.getName());
			
		}else {
			model.addAttribute("title","Unauthorized Access");
		}
		
		
		return "normal/contact_detail";
	}
	
	//delete specific contact handler
	
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId,Model model,
			HttpSession session,Principal principal) {
		String name = principal.getName();
		User user = userRepo.getUserByUserName(name);
		
		Optional<Contact> contactOptional = contactRepo.findById(cId);
		Contact contact = contactOptional.get();
		
		if(user.getId()==contact.getUser().getId()) {
			//remove image as well this is assignment
			/*
			 * File saveFile = new ClassPathResource("static/images").getFile();
			 * 
			 * Path path =
			 * Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename(
			 * ));
			 */
			
			/* using this we can't able to delete the contact from the database
			 * contact.setUser(null); contactRepo.delete(contact);
			 */
			
			user.getContacts().remove(contact);
			userRepo.save(user);
			System.out.println("contact deleted successfully");
		session.setAttribute("message",new Message("Contact Deleted Successfully","success"));
		return "redirect:/user/view-contacts/0";
		}
		
		return "redirect:/user/view-contacts/0";
	}
	
	//open Update form handler
	
	@PostMapping("update-contact/{cId}")
	public String updateForm(@PathVariable("cId") Integer cId,Model model
			,HttpSession session) {
		model.addAttribute("title","Update Contact");
		
		Contact contact = contactRepo.findById(cId).get();
		model.addAttribute("contact", contact);
	//	session.setAttribute("message", new Message("Contact Updated Successfully","success"));
		
		return "normal/update_form";
	}
	
	
	//update contact handler
	
	@PostMapping("/process-update")
	public String updateHandler(@Valid @ModelAttribute Contact contact,BindingResult result,
			@RequestParam("profileImage") MultipartFile file,Model model,
			HttpSession session,Principal principal) {
		try {
			//old contact details
			Contact oldContactDetail = contactRepo.findById(contact.getcId()).get();
			if (!file.isEmpty()) {
				//file work...
				//rewrite
				
				//delete old photo
				File deleteFile = new ClassPathResource("static/images").getFile();
				File file1=new File(deleteFile, oldContactDetail.getImage());
				file1.delete();
				
				
				// update new photo
				
				File saveFile = new ClassPathResource("static/images").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
				
				Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING );
		
				contact.setImage(file.getOriginalFilename());
				
				
			}else {
				contact.setImage(oldContactDetail.getImage());
			}
			
			User user = userRepo.getUserByUserName(principal.getName());
			contact.setUser(user);
			if (result.hasErrors()) {
				System.out.println("ERROR "+result.toString());
				model.addAttribute("contact",contact);
				return "normal/update_form";
				
			}
			contactRepo.save(contact);
			session.setAttribute("message",new Message("Your Contact Updated Successfully", "success"));
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	
	
	//your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Your Profile");
		return "normal/profile";
		
	}
	
	//open setting handler
	@GetMapping("/settings")
	public String openSettings() {
		return "normal/settings";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,Principal principal,HttpSession session) {
		System.out.println("OLD PASSWORD "+oldPassword);
		System.out.println("NEW PASSWORD "+newPassword);
		
		String userName = principal.getName();
		User currentUser = userRepo.getUserByUserName(userName);
		System.out.println(currentUser.getPassword());
		
		if (bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			//change the password
			currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
			userRepo.save(currentUser);
			session.setAttribute("message",new Message("Your Password has Changed Successfully", "success"));
		}else {
			//error
			session.setAttribute("message",new Message("Please enter correct old password", "danger"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/logout";
	}
	
	
	
	
	
	
}
