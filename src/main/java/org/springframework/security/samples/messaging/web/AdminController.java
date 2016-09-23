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
import org.springframework.security.samples.messaging.data.Message;
import org.springframework.security.samples.messaging.data.MessageRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Joe Grandja
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
	private final MessageRepository messageRepository;

	@Autowired
	public AdminController(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String root() {
		return "redirect:/admin/index";
	}

	@RequestMapping("/index")
	public String index() {
		return "redirect:/admin/messages";
	}

	@RequestMapping(value = "/messages")
	public String messages(Model model) {
		Iterable<Message> messages = messageRepository.findAll();
		model.addAttribute("messages", messages);
		return "admin/messages";
	}

	@RequestMapping(value = "/messages/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable Long id, Model model) {
		messageRepository.delete(id);
		return "redirect:/admin/messages";
	}

	@RequestMapping("/login")
	public String login() {
		return "admin/login";
	}

	@RequestMapping("/login-error")
	public String loginError(Model model) {
		model.addAttribute("loginError", true);
		return login();
	}

}