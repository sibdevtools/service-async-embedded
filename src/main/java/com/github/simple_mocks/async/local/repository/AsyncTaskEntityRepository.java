package com.github.simple_mocks.async.local.repository;

import com.github.simple_mocks.async.local.entity.AsyncTaskEntity;
import com.github.simple_mocks.async.local.entity.AsyncTaskStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public interface AsyncTaskEntityRepository extends JpaRepository<AsyncTaskEntity, String> {

    /**
     * Find all tasks that can be executed right now.
     *
     * @param nextRetryAt date time as bound
     * @param pageable    paging
     * @return list of tasks for execution
     */
    List<AsyncTaskEntity> findAllByNextRetryAtBeforeOrderByNextRetryAt(ZonedDateTime nextRetryAt, Pageable pageable);

    /**
     * Find all tasks that can be removed
     *
     * @param lastRetryAt   date time as bound to remove
     * @param finalStatuses status to remove
     * @param pageable      paging
     * @return list of entities uids to remove
     */
    @Query("select o.uid from AsyncTaskEntity o where" +
            " o.lastRetryAt <= :lastRetryAt AND" +
            " o.status in :finalStatuses")
    List<String> findUidsToDelete(
            @Param("lastRetryAt") ZonedDateTime lastRetryAt,
            @Param("finalStatuses") List<AsyncTaskStatus> finalStatuses,
            Pageable pageable
    );

}
