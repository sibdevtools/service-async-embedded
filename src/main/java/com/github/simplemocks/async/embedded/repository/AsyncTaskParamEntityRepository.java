package com.github.simplemocks.async.embedded.repository;

import com.github.simplemocks.async.embedded.entity.AsyncTaskParamEntity;
import com.github.simplemocks.async.embedded.entity.AsyncTaskParamEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public interface AsyncTaskParamEntityRepository extends JpaRepository<AsyncTaskParamEntity, AsyncTaskParamEntityId> {
    /**
     * Get all task parameters
     *
     * @param uid task uid
     * @return list of task parameters
     */
    List<AsyncTaskParamEntity> findAllByEntityId_Uid(String uid);

    /**
     * Delete all params for specified async task
     *
     * @param uids set of async task uid
     */
    void deleteAllByEntityId_UidIn(List<String> uids);

}
