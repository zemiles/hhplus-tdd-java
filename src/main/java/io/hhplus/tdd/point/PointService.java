package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PointService {

	@Autowired
	private UserPointTable userPointTable;

	@Autowired
	private PointHistoryTable pointHistoryTable;

	/*
	* 포인트 조회
	* */
	public UserPoint point(long id) {
		List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);

		long totalAmount = pointHistories.stream()
				.filter(pointHistory -> pointHistory.amount() > 0)
				.mapToLong(PointHistory::amount)
				.sum();

		return new UserPoint(id, totalAmount, userPointTable.selectById(id).updateMillis());
	}

	/*
	* 포인트 충전 및 사용
	* */
	public UserPoint chargeAndUse(long id, long amount, TransactionType type) {
		return this.insertPointHistoryTable(userPointTable.selectById(id), amount, type, System.currentTimeMillis());
	}

	public List<PointHistory> history(long id) {
		List<PointHistory> pointHistoryList = pointHistoryTable.selectAllByUserId(id);

		return pointHistoryList.stream()
				.filter(pointHistory -> pointHistory.type().isChargeOrUse())
				.toList();
	}


	private UserPoint insertPointHistoryTable(UserPoint userPoint, long amount, TransactionType type,  long time) {
		PointHistory insert = pointHistoryTable.insert(userPoint.id(), amount, type, time);
		return type.equals(TransactionType.CHARGE) ? userPointTable.insertOrUpdate(insert.id(), insert.amount())
				: userPointTable.insertOrUpdate(insert.id(), -insert.amount());
	}

}
