package com.example.CuoiKi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.CuoiKi.model.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	List<Contact> findByDeleteFlg(Boolean deleteFlg);

}
