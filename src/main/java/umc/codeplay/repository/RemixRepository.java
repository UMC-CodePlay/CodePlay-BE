package umc.codeplay.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.handler.GeneralHandler;
import umc.codeplay.domain.Remix;

public interface RemixRepository extends JpaRepository<Remix, Long> {

    default Remix findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new GeneralHandler(ErrorStatus.REMIX_NOT_FOUND));
    }

    /*
        remix는 60일 동안 보관하지만, 한 remix의 결과가 다른 remix의 input이 되므로 60일 동안 보관하지만,
        다른 기능과 통일을 위해 14일이 지나면 마이페이지에서 보이지 않아야하므로
        TODO: 마이페이지에서 특정 사용자의 Remix 리스트 확인시, deleted_at이 있는 경우를 제외하고 검색 및 조회하기
    */
}
