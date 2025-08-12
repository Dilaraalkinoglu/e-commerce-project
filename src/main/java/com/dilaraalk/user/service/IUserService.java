package com.dilaraalk.user.service;

import java.util.Optional;

import com.dilaraalk.user.entity.User;

public interface IUserService {

	Optional<User> findByUserName(String userName);
}
