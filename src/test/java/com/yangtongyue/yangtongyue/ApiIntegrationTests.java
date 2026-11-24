package com.yangtongyue.yangtongyue;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTests {

	@Autowired
	MockMvc mvc;
	@Autowired
	ObjectMapper json;

	private MockHttpSession login(String user, String pass) throws Exception {
		MvcResult r = mvc.perform(post("/api/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"" + user + "\",\"password\":\"" + pass + "\"}"))
				.andExpect(status().isOk()).andReturn();
		return (MockHttpSession) r.getRequest().getSession(false);
	}

	private JsonNode body(MvcResult r) throws Exception {
		return json.readTree(r.getResponse().getContentAsString());
	}

	@Test
	void apiRequiresLogin() throws Exception {
		mvc.perform(get("/api/media")).andExpect(status().isUnauthorized());
		mvc.perform(post("/api/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"admin\",\"password\":\"wrong\"}")).andExpect(status().isUnauthorized());
	}

	@Test
	void writesRequireCsrfToken() throws Exception {
		MockHttpSession s = login("admin", "admin-pass");
		mvc.perform(post("/api/albums").session(s).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"x\"}"))
				.andExpect(status().isForbidden());
	}

	@Test
	void uploadAlbumAndShareFlow() throws Exception {
		MockHttpSession s = login("admin", "admin-pass");
		mvc.perform(get("/api/auth/me").session(s)).andExpect(jsonPath("$.role").value("ADMIN"));
		mvc.perform(get("/api/profile").session(s)).andExpect(jsonPath("$.birthday").value("2020-01-15"));

		int albumId = body(mvc.perform(post("/api/albums").session(s).with(csrf())
				.contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"一岁生日\"}"))
				.andExpect(status().isCreated()).andReturn()).get("id").asInt();

		MockMultipartFile file = new MockMultipartFile("file", "cake.png", "image/png", new byte[] { 1, 2, 3 });
		JsonNode media = body(mvc.perform(multipart("/api/media").file(file).session(s).with(csrf())
				.param("albumId", String.valueOf(albumId)).param("lastModified", "1611000000000"))
				.andExpect(status().isCreated()).andReturn());
		int mediaId = media.get("id").asInt();
		mvc.perform(get(media.get("url").asText())).andExpect(status().isOk());

		mvc.perform(get("/api/media").session(s).param("albumId", String.valueOf(albumId)))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].type").value("IMAGE"))
				.andExpect(jsonPath("$[0].time").value(1611000000000L))
				.andExpect(jsonPath("$[0].url", startsWith("/files/media/")));

		mvc.perform(patch("/api/media/" + mediaId).session(s).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content("{\"caption\":\"第一次吃蛋糕\"}")).andExpect(jsonPath("$.caption").value("第一次吃蛋糕"));

		mvc.perform(get("/api/albums/" + albumId).session(s))
				.andExpect(jsonPath("$.count").value(1))
				.andExpect(jsonPath("$.coverUrl").value(media.get("url").asText()));

		JsonNode share = body(mvc.perform(post("/api/shares").session(s).with(csrf())
				.contentType(MediaType.APPLICATION_JSON).content("{\"albumId\":" + albumId + ",\"expiresInDays\":7}"))
				.andExpect(status().isCreated()).andReturn());
		String token = share.get("token").asText();

		mvc.perform(get("/api/public/shares/" + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.albumName").value("一岁生日"))
				.andExpect(jsonPath("$.babyName").value("Test Baby"))
				.andExpect(jsonPath("$.media", hasSize(1)));

		mvc.perform(delete("/api/shares/" + share.get("id").asInt()).session(s).with(csrf()))
				.andExpect(status().isNoContent());
		mvc.perform(get("/api/public/shares/" + token)).andExpect(status().isNotFound());

		mvc.perform(delete("/api/albums/" + albumId).session(s).with(csrf())).andExpect(status().isNoContent());
		mvc.perform(get("/api/media").session(s))
				.andExpect(jsonPath("$[?(@.id == " + mediaId + ")].albumId").value(org.hamcrest.Matchers.contains((Object) null)));

		mvc.perform(delete("/api/media/" + mediaId).session(s).with(csrf())).andExpect(status().isNoContent());
		mvc.perform(get(media.get("url").asText())).andExpect(status().isNotFound());
	}

	@Test
	void notesAndMemberPermissions() throws Exception {
		MockHttpSession admin = login("admin", "admin-pass");
		mvc.perform(post("/api/users").session(admin).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"mama\",\"displayName\":\"妈妈\",\"password\":\"secret1\"}"))
				.andExpect(status().isCreated());

		MockHttpSession mama = login("mama", "secret1");
		mvc.perform(get("/api/users").session(mama)).andExpect(status().isForbidden());

		mvc.perform(post("/api/notes").session(mama).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content("{\"author\":\"mama\",\"content\":\"宝贝生日快乐\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.createdBy").value("mama"));
		mvc.perform(get("/api/notes").session(admin))
				.andExpect(jsonPath("$[0].content").value("宝贝生日快乐"));
	}
}
