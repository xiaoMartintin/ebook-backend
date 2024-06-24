package com.klx.ebookbackend.service;

import java.time.Instant;
import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getUserStatistics(Integer userId, Instant startDate, Instant endDate);
}
