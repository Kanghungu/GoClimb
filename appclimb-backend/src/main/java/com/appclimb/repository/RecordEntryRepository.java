package com.appclimb.repository;

import com.appclimb.domain.RecordEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordEntryRepository extends JpaRepository<RecordEntry, Long> {
}
