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
public class Track extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // TODO: 추후 BigInteger로 변환
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    @Comment("보컬 url")
    private String vocalUrl;

    @Column(columnDefinition = "TEXT")
    @Comment("반주 url")
    private String instrumentalUrl;

    @Column(columnDefinition = "TEXT")
    @Comment("베이스 url")
    private String bassUrl;

    @Column(columnDefinition = "TEXT")
    @Comment("드럼 url")
    private String drumsUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("입력 음악 ID")
    @JoinColumn(name = "music_id", nullable = false)
    private Music music;

    @Builder
    public Track(
            String vocalUrl, String instrumentalUrl, String bassUrl, String drumsUrl, Music music) {
        this.title = music.getTitle().split("-", 2)[1];
        this.vocalUrl = vocalUrl;
        this.instrumentalUrl = instrumentalUrl;
        this.bassUrl = bassUrl;
        this.drumsUrl = drumsUrl;
        this.music = music;
    }

    public void updateTrackResult(
            String vocalUrl, String instrumentalUrl, String bassUrl, String drumsUrl) {
        this.vocalUrl = vocalUrl;
        this.instrumentalUrl = instrumentalUrl;
        this.bassUrl = bassUrl;
        this.drumsUrl = drumsUrl;
    }
}
