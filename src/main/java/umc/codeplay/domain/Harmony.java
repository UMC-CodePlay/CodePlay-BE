package umc.codeplay.domain;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.Comment;
import umc.codeplay.domain.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Harmony extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // TODO: 추후 BigInteger로 변환
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Comment("스케일 = key")
    private String scale;

    @Comment("장르")
    private String genre;

    @Comment("bpm 값")
    private Integer bpm;

    @Comment("음색")
    private String voiceColor;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("입력 음악 ID")
    @JoinColumn(name = "music_id", nullable = false)
    private Music music;

    @Builder
    private Harmony(String scale, String genre, Integer bpm, String voiceColor, Music music) {
        this.title = music.getTitle().split("-", 2)[1];
        this.scale = scale;
        this.genre = genre;
        this.bpm = bpm;
        this.voiceColor = voiceColor;
        this.music = music;
    }

    public void updateHarmonyResult(String scale, Integer bpm, String genre, String voiceColor) {
        this.scale = scale;
        this.bpm = bpm;
        this.genre = genre;
        this.voiceColor = voiceColor;
    }
}
