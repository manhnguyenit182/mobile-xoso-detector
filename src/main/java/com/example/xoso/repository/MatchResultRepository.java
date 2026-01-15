package com.example.xoso.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.xoso.model.MatchResult;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, UUID> {

}
