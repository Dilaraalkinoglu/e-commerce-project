package com.dilaraalk.common.test;

import com.dilaraalk.user.entity.User;

public class TestDataBuilder {

    public static UserBuilder aUser() {
        return new UserBuilder();
    }

    public static class UserBuilder {

        private String userName = "testuser";

        private String email = "test@example.com";

        private String password = "password123";

        public UserBuilder withUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public UserBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public User build() {
            User user = new User();
            user.setUserName(this.userName);
            user.setEmail(this.email);
            user.setPassword(this.password);
            return user;
        }

    }

}
