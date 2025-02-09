package umc.codeplay.domain;

import jakarta.persistence.*;

import lombok.*;

import org.hibernate.annotations.Comment;
import umc.codeplay.domain.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Remix extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // TODO: 추후 BigInteger로 변환
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Comment("음높이의 변화")
    private Integer scaleModulation;

    @Comment("원래 템포에 대한 비율")
    private Double tempoRatio;

    @Comment("리버브의 강도/정도")
    private Double reverbAmount;

    @Comment("코러스 on/off")
    private Boolean isChorusOn;

    @Column(columnDefinition = "TEXT")
    @Comment("결과 s3 URL")
    @Setter
    private String resultMusicUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("입력 음악 ID")
    @JoinColumn(name = "music_id", nullable = false)
    private Music music;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("이전 단계 리믹스 ID")
    @JoinColumn(name = "parent_remix_id")
    private Remix parentRemix;

    @Builder
    public Remix(
            Integer scaleModulation,
            Double tempoRatio,
            Double reverbAmount,
            Boolean isChorusOn,
            String resultMusicUrl,
            Music music) {
        this.title = music.getTitle().split("-", 2)[1];
        this.scaleModulation = scaleModulation;
        this.tempoRatio = tempoRatio;
        this.reverbAmount = reverbAmount;
        this.isChorusOn = isChorusOn;
        this.resultMusicUrl = resultMusicUrl;
        this.music = music;
    }
}
