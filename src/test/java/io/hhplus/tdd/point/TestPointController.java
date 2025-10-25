package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebMvcTest(PointController.class)
class TestPointController {

	private static final Logger log = LoggerFactory.getLogger(TestPointController.class);

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

		//then
		assertEquals(1L, charge.id());

		verify(pointService, times(1)).chargeAndUse(id, amount, TransactionType.CHARGE);
	}

	@Test
	@DisplayName("특정 유저의 포인트 충전/이용 내역을 조회 - 성공 케이스")
	void history() throws Exception {
		//given
		long id = 1L;
		long currentTime = System.currentTimeMillis();
		
		// 충전과 사용 내역이 섞인 히스토리 데이터
		List<PointHistory> mockHistory = Arrays.asList(
			new PointHistory(1L, id, 1000L, TransactionType.CHARGE, currentTime),
			new PointHistory(2L, id, 500L, TransactionType.USE, currentTime + 1000),
			new PointHistory(3L, id, 2000L, TransactionType.CHARGE, currentTime + 2000),
			new PointHistory(4L, id, 300L, TransactionType.USE, currentTime + 3000)
		);
		
		given(pointService.history(id)).willReturn(mockHistory);

		//when
		List<PointHistory> result = pointService.history(id);

		//then
		assertEquals(4, result.size());
		assertEquals(TransactionType.CHARGE, result.get(0).type());
		assertEquals(TransactionType.USE, result.get(1).type());
		assertEquals(TransactionType.CHARGE, result.get(2).type());
		assertEquals(TransactionType.USE, result.get(3).type());
		
		verify(pointService, times(1)).history(id);
	}

	@Test
	@DisplayName("특정 유저의 포인트 충전/이용 내역을 조회 - 빈 결과 케이스")
	void history_EmptyResult() throws Exception {
		//given
		long id = 999L; // 존재하지 않는 사용자
		given(pointService.history(id)).willReturn(Collections.emptyList());

		//when
		List<PointHistory> result = pointService.history(id);

		//then
		assertTrue(result.isEmpty());
		verify(pointService, times(1)).history(id);
	}


	@Test
	@DisplayName("포인트를 사용")
	void use() throws Exception {

		//given
		long id = 1L;
		long amount = 1000;

		given(pointService.chargeAndUse(id, amount, TransactionType.USE)).willReturn(new UserPoint(id, 0, System.currentTimeMillis()));

		//when
		UserPoint user = pointService.chargeAndUse(id, amount, TransactionType.USE);

		//then
		assertEquals(0, user.point());

		verify(pointService, times(1)).chargeAndUse(id, amount, TransactionType.USE);
	}


}