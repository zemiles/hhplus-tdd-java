package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class PointService {

	private UserPointTable userPointTable;

	private PointHistoryTable pointHistoryTable;

	public UserPoint point(Long id) {
		UserPoint userPoint = userPointTable.selectById(id);

		if(userPoint != null) {
			List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);

			long totalAmount = pointHistories.stream()
					.filter(pointHistory -> pointHistory.amount() > 0)
					.mapToLong(PointHistory::amount)
					.sum();

			return new UserPoint(id, totalAmount, System.currentTimeMillis());
		} else {
			userPointTable.insertOrUpdate(id, 0L);
			return UserPoint.empty(id);
		}
	}

}
