package com.example.logserver.repo;

import com.example.logserver.entity.Log;
import com.example.logserver.service.dto.LogResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

}