package com.dilaraalk.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

	@GetMapping("/dashboard")
	public ResponseEntity<String> getAdminDashboard(){
		return ResponseEntity.ok("Sadece ADMIN rolü bu mesajı görebilir.");
	}
	
	
}
