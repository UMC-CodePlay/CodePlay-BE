package umc.codeplay.domain;

import jakarta.persistence.*;

import lombok.*;

import org.hibernate.annotations.Comment;
import umc.codeplay.domain.common.BaseEntity;
import umc.codeplay.domain.enums.JobType;
import umc.codeplay.domain.enums.ProcessStatus;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // TODO: 추후 BigInteger로 변환
    private Long id;

    @Enumerated(EnumType.STRING)
    @Comment("task 진행 상태")
    @Builder.Default
    private ProcessStatus status = ProcessStatus.REQUESTED;

    @Enumerated(EnumType.STRING)
    @Comment("요청 기능 타입 (화성 분석, 세션 분리, 리믹스)")
    @Column(nullable = false)
    private JobType jobType;

    @Column(nullable = false)
    @Comment("요청 기능의 데이터 id")
    private Long jobId;
}
