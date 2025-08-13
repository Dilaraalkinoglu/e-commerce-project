package com.dilaraalk.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dilaraalk.common.base.BaseController;

@RestController
@RequestMapping("/api/admin")
public class AdminController extends BaseController{

	@GetMapping("/dashboard")
	public ResponseEntity<String> getAdminDashboard(){
        return ok("Sadece ADMIN rolü bu mesajı görebilir.");
	}
	
	
}
