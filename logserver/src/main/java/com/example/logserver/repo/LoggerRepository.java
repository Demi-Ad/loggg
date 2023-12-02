package com.example.logserver.repo;

import com.example.logserver.entity.Logger;
import com.example.logserver.service.dto.ServiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LoggerRepository extends JpaRepository<Logger, Long> {

    Optional<Logger> findByService(String service);

    @Query(value = """
            select new com.example.logserver.service.dto.ServiceResponse(l.service, l.createdAt, (select count (*) from Log lo where l = lo.logger))
            from Logger l
            order by l.createdAt asc
            """,
    countQuery = "select count (*) from Logger")
    Page<ServiceResponse> findAllToResponse(Pageable pageable);

}