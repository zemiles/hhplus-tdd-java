package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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
		List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);

		long totalAmount = pointHistories.stream()
				.filter(pointHistory -> pointHistory.amount() > 0)
				.mapToLong(PointHistory::amount)
				.sum();

		return new UserPoint(id, totalAmount, userPointTable.selectById(id).updateMillis());
	}

	public UserPoint charge(long id, long amount) {
		return this.insertPointHistoryTable(userPointTable.selectById(id), amount, TransactionType.CHARGE, System.currentTimeMillis());
	}

	public UserPoint use() {
		return null;
	}

	private UserPoint insertPointHistoryTable(UserPoint userPoint, long amount, TransactionType type,  long time) {
		PointHistory insert = pointHistoryTable.insert(userPoint.id(), amount, type, time);
		return userPointTable.insertOrUpdate(insert.id(), insert.amount());
	}

}
