/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.samples.messaging.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.samples.messaging.data.User;
import org.springframework.security.samples.messaging.data.UserRepository;
import org.springframework.security.samples.messaging.security.CurrentUser;
import org.springframework.security.samples.messaging.security.CustomUserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rob Winch
 * @author Joe Grandja
 */
@RestController
public class SecurityController {
	private final UserRepository userRepository;

	@Autowired
	public SecurityController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@RequestMapping(value = "/principal")
	public ResponseEntity<User> currentPrincipal(@CurrentUser CustomUserDetails currentUser) {
		User user = userRepository.findOne(currentUser.getId());
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

}