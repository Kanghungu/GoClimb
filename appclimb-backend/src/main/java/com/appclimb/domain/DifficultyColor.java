package com.appclimb.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "difficulty_colors")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class DifficultyColor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;

    @Column(name = "color_name", nullable = false, length = 30)
    private String colorName;

    @Column(name = "color_hex", nullable = false, length = 7)
    private String colorHex;

    @Column(name = "level_order", nullable = false)
    private Integer levelOrder;
}
