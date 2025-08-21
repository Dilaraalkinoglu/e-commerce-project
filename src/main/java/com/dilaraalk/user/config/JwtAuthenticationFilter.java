package com.dilaraalk.user.config;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;
import com.dilaraalk.user.service.impl.CustomUserDetails;
import com.dilaraalk.user.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;



@Component
@RequiredArgsConstructor
public class  JwtAuthenticationFilter extends OncePerRequestFilter{
	
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, 
			HttpServletResponse response,
			FilterChain filterChain)
			throws ServletException, IOException {
		
		String authHeader = request.getHeader("Authorization");
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		
		String token = authHeader.substring(7);
		String userName = jwtUtil.extractUsername(token);
		
		if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			Optional<User> userOptional = userRepository.findByUserName(userName);
			if (userOptional.isPresent() && jwtUtil.validateToken(token, userName)) {
				User user = userOptional.get();
				
	              CustomUserDetails userDetails = new CustomUserDetails(user);

	                UsernamePasswordAuthenticationToken authToken =
	                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

	                SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		
		filterChain.doFilter(request, response);
		
		
	}

}