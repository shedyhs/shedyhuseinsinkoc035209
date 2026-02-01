package com.shedyhuseinsinkoc035209.repository;

import com.shedyhuseinsinkoc035209.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    List<Region> findByActiveTrue();
}
