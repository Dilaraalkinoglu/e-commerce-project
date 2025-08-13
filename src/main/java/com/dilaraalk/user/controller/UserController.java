package com.dilaraalk.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dilaraalk.common.base.BaseController;
import com.dilaraalk.user.entity.User;



@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController{      

	@GetMapping("/profile")
	public ResponseEntity<String> getProfile(Authentication authentication){
		User user = (User) authentication.getPrincipal();
        return ok("Merhaba " + user.getUserName() + ", profil bilgilerin burada!");
	}
	
	
}
