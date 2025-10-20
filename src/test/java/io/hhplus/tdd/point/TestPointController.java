package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@WebMvcTest(PointController.class)
class TestPointController {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PointController pointController;

	@MockBean
	private PointService pointService;

	@Test
	@DisplayName("사용자의 포인트 조회")
	void getUserPoint() throws Exception {
		UserPoint testUserPoint = new UserPoint(1L, 0, System.currentTimeMillis());
		given(pointService.point(1L)).willReturn(testUserPoint);

		UserPoint userPoint = pointService.point(1L);

		assertEquals(testUserPoint, userPoint);
	}

}