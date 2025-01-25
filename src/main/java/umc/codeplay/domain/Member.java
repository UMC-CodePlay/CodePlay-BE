package umc.codeplay.domain;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import umc.codeplay.domain.enums.Role;
import umc.codeplay.domain.mapping.MusicLike;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String password;

    private String email;

    private Role role;

    public void encodePassword(String password) {
        this.password = password;
    }

    @Column(columnDefinition = "TEXT")
    private String profileUrl;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MusicLike> likeList = new ArrayList<>();
}
