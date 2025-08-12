package com.dilaraalk.common.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.dilaraalk.product.repository.ProductRepository;
import com.dilaraalk.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetricService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // HTTP status kodu metrikleri
    private final Map<String, Map<Integer, Integer>> requestStatusMetric = new ConcurrentHashMap<>();

    // Kullanıcı sayısı
    public long getTotalUserCount() {
        return userRepository.count();
    }
    
    //ürün sayısı 
    public long getTotalProductCount() {
        return productRepository.count();
    }

    // HTTP isteği & status kodu sayacı
    public void increaseCount(String request, int status) {
        requestStatusMetric
            .computeIfAbsent(request, r -> new ConcurrentHashMap<>())
            .merge(status, 1, Integer::sum);
    }

    public Map<String, Map<Integer, Integer>> getRequestStatusMetric() {
        return requestStatusMetric;
    }
}
