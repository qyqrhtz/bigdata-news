package com.example.bigdata_news;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class BigdataNewsApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void openApiDocsAreAvailable() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.info.title").value("Bigdata News Analytics API"))
				.andExpect(jsonPath("$.tags[?(@.name == 'Health')]").exists())
				.andExpect(jsonPath("$.tags[?(@.name == 'Statistics APIs')]").exists());
	}

	@Test
	void healthEndpointIsAvailable() throws Exception {
		mockMvc.perform(get("/api/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("UP"))
				.andExpect(jsonPath("$.service").value("bigdata-news"))
				.andExpect(jsonPath("$.dependencies.ragServiceUrl").exists())
				.andExpect(jsonPath("$.dependencies.cozeWorkflowConfigured").exists());
	}

	@Test
	void cozeDebugEndpointIsNotExposedByDefault() throws Exception {
		mockMvc.perform(get("/api/test/coze/simple")
						.param("question", "hello"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.path").value("/api/test/coze/simple"));
	}

	@Test
	void ragChatRejectsBlankQuestion() throws Exception {
		mockMvc.perform(post("/api/news/rag/chat")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"question\":\"\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.path").value("/api/news/rag/chat"));
	}

	@Test
	void searchRejectsInvalidTopK() throws Exception {
		mockMvc.perform(get("/api/news/search")
						.param("query", "climate")
						.param("topK", "99"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.path").value("/api/news/search"));
	}

	@Test
	void newsPageRejectsNegativePage() throws Exception {
		mockMvc.perform(get("/api/news/page")
						.param("page", "-1")
						.param("size", "10"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.path").value("/api/news/page"));
	}

	@Test
	void newsPageRejectsOversizedPageSize() throws Exception {
		mockMvc.perform(get("/api/news/page")
						.param("page", "0")
						.param("size", "500"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.path").value("/api/news/page"));
	}

}
