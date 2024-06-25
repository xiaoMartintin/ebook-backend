package com.klx.ebookbackend.service;

import java.time.LocalDate;
import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getPurchaseStatistics(Integer userId, LocalDate startDate, LocalDate endDate);
}
