package com.appclimb.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 지점 직원 역할 관리 (세팅직원, 티칭직원 등)
 * 별도 로그인 없이 DB 기록용으로만 사용
 */
@Entity
@Table(name = "gym_staff")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class GymStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "staff_role", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StaffRole staffRole;

    @Column(length = 255)
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void update(String name, StaffRole staffRole, String note) {
        this.name = name;
        this.staffRole = staffRole;
        this.note = note;
    }

    public enum StaffRole {
        SETTER,   // 세팅직원
        TEACHER,  // 티칭직원
        FRONT,    // 프론트직원
        MANAGER_STAFF // 기타 매니지먼트
    }
}
