package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {

	@Autowired
	private UserPointTable userPointTable;

	@Autowired
	private PointHistoryTable pointHistoryTable;

	public UserPoint point(long id) {
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

	public UserPoint charge(long id, long amount) {
		UserPoint userPoint = userPointTable.selectById(id);

		if(userPoint == null) {
			UserPoint tempUser = userPointTable.insertOrUpdate(id, amount);

			return this.insertPointHistoryTable(tempUser, amount, TransactionType.CHARGE, System.currentTimeMillis());
		}

		return this.insertPointHistoryTable(userPoint, amount, TransactionType.CHARGE, System.currentTimeMillis());
	}

	private UserPoint insertPointHistoryTable(UserPoint userPoint, long amount, TransactionType type,  long time) {
		PointHistory insert = pointHistoryTable.insert(userPoint.id(), amount, type, time);
		return userPointTable.insertOrUpdate(insert.id(), insert.amount());
	}

}
