package umc.codeplay.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.handler.GeneralHandler;
import umc.codeplay.domain.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    default Task findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new GeneralHandler(ErrorStatus.TASK_NOT_FOUND));
    }
}
