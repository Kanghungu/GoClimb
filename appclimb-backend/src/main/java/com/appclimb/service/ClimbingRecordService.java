package com.appclimb.service;

import com.appclimb.domain.*;
import com.appclimb.dto.request.ClimbingRecordRequest;
import com.appclimb.dto.response.ClimbingRecordResponse;
import com.appclimb.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClimbingRecordService {

    private final ClimbingRecordRepository recordRepository;
    private final GymRepository gymRepository;
    private final UserRepository userRepository;
    private final DifficultyColorRepository colorRepository;

    @Transactional(readOnly = true)
    public List<ClimbingRecordResponse> getRecords(Long userId, String month) {
        if (month != null) {
            YearMonth ym = YearMonth.parse(month);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();
            return recordRepository.findByUserIdAndRecordDateBetween(userId, start, end)
                    .stream().map(ClimbingRecordResponse::from).toList();
        }
        return recordRepository.findByUserIdAndRecordDateBetween(
                        userId, LocalDate.now().minusYears(1), LocalDate.now())
                .stream().map(ClimbingRecordResponse::from).toList();
    }

    @Transactional
    public ClimbingRecordResponse createRecord(Long userId, ClimbingRecordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Gym gym = gymRepository.findById(request.getGymId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지점입니다."));

        // 같은 날짜/지점 중복 체크
        if (recordRepository.findByUserIdAndGymIdAndRecordDate(
                userId, gym.getId(), request.getRecordDate()).isPresent()) {
            throw new IllegalArgumentException("해당 날짜에 이미 기록이 존재합니다.");
        }

        ClimbingRecord record = ClimbingRecord.builder()
                .user(user)
                .gym(gym)
                .recordDate(request.getRecordDate())
                .entries(new ArrayList<>())
                .build();

        ClimbingRecord saved = recordRepository.save(record);

        // 난이도별 기록 추가
        if (request.getEntries() != null) {
            for (ClimbingRecordRequest.EntryRequest entryReq : request.getEntries()) {
                DifficultyColor color = colorRepository.findById(entryReq.getColorId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 난이도입니다."));
                RecordEntry entry = RecordEntry.builder()
                        .record(saved)
                        .color(color)
                        .plannedCount(entryReq.getPlannedCount())
                        .completedCount(entryReq.getCompletedCount())
                        .build();
                saved.getEntries().add(entry);
            }
        }

        return ClimbingRecordResponse.from(recordRepository.save(saved));
    }

    @Transactional
    public void deleteRecord(Long userId, Long recordId) {
        ClimbingRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기록입니다."));
        if (!record.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 기록만 삭제할 수 있습니다.");
        }
        recordRepository.deleteById(recordId);
    }
}
