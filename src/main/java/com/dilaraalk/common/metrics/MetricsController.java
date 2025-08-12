package com.dilaraalk.common.metrics;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/metrics")
public class MetricsController {

    private final MetricService metricService;

    public MetricsController(MetricService metricService) {
        this.metricService = metricService;
    }

    // Kullanıcı sayısı
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUserMetrics() {
        long userCount = metricService.getTotalUserCount();
        return ResponseEntity.ok(Map.of("totalUserCount", userCount));
    }
    
    //ürün sayısı 
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProductMetrics() {
        long productCount = metricService.getTotalProductCount();
        return ResponseEntity.ok(Map.of("totalProductCount", productCount));
    }

    

    // İsteklerin HTTP status kod istatistikleri
    @GetMapping("/requests")
    public ResponseEntity<Map<String, Map<Integer, Integer>>> getRequestMetrics() {
        return ResponseEntity.ok(metricService.getRequestStatusMetric());
    }
}


