package com.qwerty.pastebook.repositories;

import com.qwerty.pastebook.entities.PasteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasteRepository extends JpaRepository<PasteEntity, String> {
}
