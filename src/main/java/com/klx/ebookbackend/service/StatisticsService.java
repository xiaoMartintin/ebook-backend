package com.klx.ebookbackend.service;

import java.time.LocalDate;
import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getPurchaseStatistics(Integer userId, LocalDate startDate, LocalDate endDate, int pageIndex, int pageSize);
    Map<String, Object> getSalesStatistics(LocalDate startDate, LocalDate endDate, int pageIndex, int pageSize);
    Map<String, Object> getUserPurchaseStatistics(LocalDate startDate, LocalDate endDate, int pageIndex, int pageSize);
}
