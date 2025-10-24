package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
class TestPointController {

	private static final Logger log = LoggerFactory.getLogger(TestPointController.class);
	@Autowired
	private MockMvc mockMvc;

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

	@Test
	@DisplayName("사용자의 포인트 충전")
	void charge() throws Exception {
		// given
		long id = 1L;
		long amount = 1000;
		given(pointService.chargeAndUse(id, amount, TransactionType.CHARGE)).willReturn(new UserPoint(id, amount, System.currentTimeMillis()));

		//when
		UserPoint charge = pointService.chargeAndUse(id, amount, TransactionType.CHARGE);

//		mockMvc.perform(patch("/point/{id}/charge", id)
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(String.valueOf(amount)))
//				.andExpect(status().isOk())
//				.andExpect(jsonPath("$.id").value(id))
//				.andExpect(jsonPath("$.point").value(amount));


		//then
		assertEquals(1L, charge.id());

		verify(pointService, times(1)).chargeAndUse(id, amount, TransactionType.CHARGE);
	}

	@Test
	@DisplayName("특정 유저의 포인트 충전/이용 내역을 조회")
	void history() throws Exception {

	}

	@Test
	@DisplayName("포인트를 사용")
	void use() throws Exception {
		long id = 1L;
		long amount = 1000;

		UserPoint user = new UserPoint(id, amount, System.currentTimeMillis());

//		given(pointService.chargeAndUse(id, amount, TransactionType.USE)).willReturn(new UserPoint(id, amount, System.currentTimeMillis()));

		assertEquals(900, user.point());

	}


}