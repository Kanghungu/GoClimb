package com.appclimb.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "record_entries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class RecordEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private ClimbingRecord record;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", nullable = false)
    private DifficultyColor color;

    @Column(name = "planned_count", nullable = false)
    private Integer plannedCount;

    @Column(name = "completed_count", nullable = false)
    private Integer completedCount;

    public void updateCounts(int plannedCount, int completedCount) {
        this.plannedCount = plannedCount;
        this.completedCount = completedCount;
    }
}
