package com.smartcontactmanager.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartcontactmanager.entities.Contact;
import com.smartcontactmanager.entities.User;

public interface ContactRepository extends JpaRepository<Contact,Integer>{

	//pagination
		//current page and contact per page i.e here 5 information inside Pageablw
	@Query("from Contact as c where c.user.id=:userId ")
	public Page<Contact> getContactsByUserid(@Param("userId")int userId,Pageable pageable);
	
	//search contact
	
	public List<Contact> findByNameContainingAndUser(String keyword,User user);
}
