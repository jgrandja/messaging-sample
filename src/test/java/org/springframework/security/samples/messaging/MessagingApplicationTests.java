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
package org.springframework.security.samples.messaging;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.samples.messaging.data.Message;
import org.springframework.security.samples.messaging.data.User;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author Rob Winch
 * @author Joe Grandja
 *
 */
@DirtiesContext
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MessagingApplication.class)
@AutoConfigureMockMvc
@Transactional
@WithUserDetails("joe@example.com")
public class MessagingApplicationTests {
	@Autowired
	MockMvc mockMvc;

	@Test
	@WithAnonymousUser
	public void securityEnabled() throws Exception {
		mockMvc
				.perform(get("messages/inbox")
				.header("X-Requested-With", "XMLHttpRequest"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void deleteJoesMessage() throws Exception {
		mockMvc.perform(delete("/messages/{id}", 200L)
				.header("X-Requested-With", "XMLHttpRequest")
				.with(csrf()))
				.andExpect(status().isOk());
	}

	@Test
	public void getJoesInbox() throws Exception {
		MvcResult result = mockMvc.perform(get("/messages/inbox")
				.header("X-Requested-With", "XMLHttpRequest"))
				.andExpect(status().isOk())
				.andReturn();

		String json = result.getResponse().getContentAsString();

		List<Message> messages = JsonUtil.readValue(json, new TypeReference<List<Message>>(){});
		assertThat(messages.size()).isEqualTo(3);

		assertThat(messages).extracting(m-> m.getSummary()).containsOnly("Hello Joe","Greetings Joe", "Is this secure?");
	}

	@Test
	public void getJoesSent() throws Exception {
		MvcResult result = mockMvc.perform(get("/messages/sent")
				.header("X-Requested-With", "XMLHttpRequest"))
				.andExpect(status().isOk())
				.andReturn();

		String json = result.getResponse().getContentAsString();

		List<Message> messages = JsonUtil.readValue(json, new TypeReference<List<Message>>(){});
		assertThat(messages).isNotEmpty();

		assertThat(messages).extracting(m-> m.getSummary()).containsOnly("Hello Rob","How are you Rob?", "Is this secure?","Greetings Admin");
	}

	@Test
	public void getMessage() throws Exception {
		MvcResult result = mockMvc.perform(get("/messages/{id}", 201L)
				.header("X-Requested-With", "XMLHttpRequest"))
				.andExpect(status().isOk())
				.andReturn();

		String json = result.getResponse().getContentAsString();

		Message message = JsonUtil.readValue(json, new TypeReference<Message>(){});

		assertThat(message.getSummary()).isEqualTo("Greetings Joe");
	}

	@Test
	public void getUsers() throws Exception {
		MvcResult result = mockMvc.perform(get("/users")
				.header("X-Requested-With", "XMLHttpRequest"))
				.andExpect(status().isOk())
				.andReturn();

		String json = result.getResponse().getContentAsString();

		List<User> messages = JsonUtil.readValue(json, new TypeReference<List<User>>(){});
		assertThat(messages.size()).isEqualTo(3);

		assertThat(messages).extracting(m-> m.getEmail()).containsOnly("rob@example.com","joe@example.com", "admin@example.com");
	}

	@Test
	public void save() throws Exception {
		Message toCreate = toCreate();

		String body = JsonUtil.writeValue(toCreate);

		mockMvc.perform(post("/messages")
				.header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
				.with(csrf())
				.content(body))
			.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void saveValidationFails() throws Exception {
		Message toCreate = toCreate();
		toCreate.setSummary(null);

		String body = JsonUtil.writeValue(toCreate);

		mockMvc.perform(post("/messages")
				.header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
				.with(csrf())
				.content(body))
			.andExpect(status().isBadRequest());
	}

	private Message toCreate() {
		Message toCreate = new Message();
		toCreate.setCreated(Calendar.getInstance());
		toCreate.setSummary("This is a test..");
		toCreate.setText("of the emergency broadcast system");
		toCreate.setTo(rob());
		return toCreate;
	}

	public static User rob() {
		User user = new User();
		user.setEmail("rob@example.com");
		user.setFirstName("Rob");
		user.setLastName("Winch");
		user.setId(0L);
		return user;
	}
}