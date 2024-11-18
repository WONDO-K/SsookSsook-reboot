package com.stillalive.Ssook_BE.report.repository;

import com.stillalive.Ssook_BE.domain.Child;
import com.stillalive.Ssook_BE.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByChild(Child child);

    Report findReportById(Integer id);
}
