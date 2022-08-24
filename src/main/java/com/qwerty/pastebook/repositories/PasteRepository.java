package com.qwerty.pastebook.repositories;

import com.qwerty.pastebook.entities.PasteEntity;
import com.qwerty.pastebook.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PasteRepository extends PagingAndSortingRepository<PasteEntity, String> {

    @Query(value = "SELECT * FROM pastes WHERE access_modifier = 'PUBLIC' order by date_of_creation desc LIMIT 10",
    nativeQuery = true)
    List<PasteEntity> getTenLatest();

    Page<PasteEntity> findAllByOwnerAndExpirationAfterOrExpirationNull(UserEntity owner, Date expiration, Pageable pageable);
}
