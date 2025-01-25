package umc.codeplay.domain;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

import lombok.*;

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
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL)
    private List<MusicLike> likeList = new ArrayList<>();
}
