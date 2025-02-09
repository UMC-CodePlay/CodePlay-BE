package umc.codeplay.domain;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import umc.codeplay.domain.common.BaseEntity;
import umc.codeplay.domain.mapping.MusicLike;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Music extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // TODO: 추후 BigInteger로 변환
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String musicUrl;

    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<MusicLike> likeList = new ArrayList<>();
}
