package umc.codeplay.domain;

import jakarta.persistence.*;

import lombok.*;

import umc.codeplay.domain.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Harmony extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    private String harmonyKey;

    private String scale;

    private String chord;

    private Integer bpm;

    private Integer soundPressure;

    @Column(columnDefinition = "TEXT")
    private String harmonyUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id")
    private Music music;
}
