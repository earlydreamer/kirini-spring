package dev.earlydreamer.kirini.repository;

import dev.earlydreamer.kirini.domain.Freeboard;
import dev.earlydreamer.kirini.domain.Freeboard.DeleteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeboardRepository extends JpaRepository<Freeboard, Integer> {

    Page<Freeboard> findByDeleteStatus(DeleteStatus deleteStatus, Pageable pageable);

    Optional<Freeboard> findByIdAndDeleteStatus(Integer id, DeleteStatus status);

    @Modifying
    @Query("update Freeboard f set f.readCount = f.readCount + 1 where f.id = :id")
    int increaseReadCount(@Param("id") Integer id);
}

