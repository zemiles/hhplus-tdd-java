package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(PointController.class)
class TestPointController {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("사용자의 포인트 조회")
	void getUserPoint() throws Exception {
		UserPoint userPoint = new UserPoint(1L, 0, System.currentTimeMillis());

		assertEquals(new UserPoint(2L, 100, System.currentTimeMillis()), userPoint);
	}

}