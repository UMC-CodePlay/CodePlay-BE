package umc.codeplay.domain;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import umc.codeplay.domain.common.BaseEntity;
import umc.codeplay.domain.enums.Role;
import umc.codeplay.domain.enums.SocialStatus;
import umc.codeplay.domain.mapping.MusicLike;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // TODO: 추후 BigInteger로 변환
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialStatus socialStatus;

    @Column(columnDefinition = "TEXT")
    private String profileUrl;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<MusicLike> likeList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<Music> musicList = new ArrayList<>();

    public void encodePassword(String password) {
        this.password = password;
    }
}
