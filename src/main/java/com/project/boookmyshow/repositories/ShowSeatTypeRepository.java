package com.project.boookmyshow.repositories;

import com.project.boookmyshow.models.Show;
import com.project.boookmyshow.models.ShowSeatType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShowSeatTypeRepository extends JpaRepository<ShowSeatType, Long> {


    List<ShowSeatType> findAllByShow(Show show);
}
