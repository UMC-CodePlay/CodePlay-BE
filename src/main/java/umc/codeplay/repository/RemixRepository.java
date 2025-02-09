package umc.codeplay.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.handler.GeneralHandler;
import umc.codeplay.domain.Remix;

public interface RemixRepository extends JpaRepository<Remix, Long> {

    default Remix findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new GeneralHandler(ErrorStatus.REMIX_NOT_FOUND));
    }
}
