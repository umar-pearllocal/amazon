package com.documentor.amazon.Repository;

import com.documentor.amazon.Entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History,Long> {
}
