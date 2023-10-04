package com.smartcontactmanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartcontactmanager.entities.Contactus;



public interface ContactusRepository extends JpaRepository<Contactus, Integer> {

}
