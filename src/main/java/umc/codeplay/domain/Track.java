package umc.codeplay.domain;

import jakarta.persistence.*;

import lombok.*;

import umc.codeplay.domain.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Track extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String guitarUrl;

    @Column(columnDefinition = "TEXT")
    private String drumUrl;

    @Column(columnDefinition = "TEXT")
    private String keyboardUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id")
    private Music music;
}
