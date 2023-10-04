package com.smartcontactmanager.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smartcontactmanager.dao.ContactRepository;
import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.entities.Contact;
import com.smartcontactmanager.entities.User;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ContactRepository contactRepo;
	
	//search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> searchContacts(@PathVariable("query") String query,Principal principal){
		
		System.out.println(query);
		User user = userRepo.getUserByUserName(principal.getName());
		
		List<Contact> contacts = contactRepo.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contacts);
	}
	

}
