package com.example.CuoiKi.controller;

import com.example.CuoiKi.model.Contact;
import com.example.CuoiKi.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {
	@Autowired
	private ContactRepository contactRepository;

	// Tạo mới liên hệ (dùng cho form người dùng gửi)
	@PostMapping
	public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
		contact.setCreatedAt(java.time.LocalDateTime.now());
		Contact saved = contactRepository.save(contact);
		return ResponseEntity.ok(saved);
	}

	// Lấy danh sách tất cả liên hệ (dùng cho giao diện quản trị)
	@GetMapping
	public List<Contact> getAllContacts() {
		return contactRepository.findByDeleteFlg(false);
	}

	// Lấy 1 liên hệ theo ID
	@GetMapping("/{id}")
	public ResponseEntity<Contact> getContactById(@PathVariable Integer id) {
		Optional<Contact> contact = contactRepository.findById(id);
		return contact.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	// Cập nhật thông tin liên hệ
	@PutMapping("/{id}")
	public ResponseEntity<Contact> updateContact(@PathVariable Integer id, @RequestBody Contact updatedContact) {
		Optional<Contact> optionalContact = contactRepository.findById(id);
		if (optionalContact.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Contact contact = optionalContact.get();
		contact.setName(updatedContact.getName());
		contact.setEmail(updatedContact.getEmail());
		contact.setPhone(updatedContact.getPhone());
		contact.setContent(updatedContact.getContent());
		contact.setUpdateAt(LocalDateTime.now());
		return ResponseEntity.ok(contactRepository.save(contact));
	}

	// Xóa liên hệ
	/*
	 * @DeleteMapping("/{id}") public ResponseEntity<Void>
	 * deleteContact(@PathVariable Integer id) { if
	 * (!contactRepository.existsById(id)) { return
	 * ResponseEntity.notFound().build(); } contactRepository.deleteById(id); return
	 * ResponseEntity.noContent().build(); }
	 */
	@PutMapping("/delete/{id}")
	public ResponseEntity<Contact> deleteContact(@PathVariable Integer id) {
		Optional<Contact> optionalContact = contactRepository.findById(id);
		if (optionalContact.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Contact contact = optionalContact.get();
		contact.setDeleteAt(LocalDateTime.now());
		contact.setDeleteFlg(true);
		return ResponseEntity.ok(contactRepository.save(contact));
	}
}
