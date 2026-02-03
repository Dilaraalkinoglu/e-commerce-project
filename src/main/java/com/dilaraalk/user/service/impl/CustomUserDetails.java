package com.dilaraalk.user.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.dilaraalk.user.entity.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails, Serializable {

	private static final long serialVersionUID = 1L;

	private final Long id;

	private final String username;

	private final String password;

	private final String email;

	private final List<GrantedAuthority> authorities;

	public CustomUserDetails(User user) {
		this.id = user.getId();
		this.username = user.getUserName();
		this.password = user.getPassword();
		this.email = user.getEmail();
		this.authorities = user.getRoles() == null ? List.of()
				: user.getRoles().stream()
						.map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
						.collect(Collectors.toList());
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
