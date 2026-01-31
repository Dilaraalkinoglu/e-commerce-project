package com.dilaraalk.common.rateLimiting;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitingService rateLimitingService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Sadece Auth endpointlerini koruma altına alıyoruz
        if (request.getRequestURI().startsWith("/api/auth")) {
            String ipAddress = request.getRemoteAddr();
            Bucket tokenBucket = rateLimitingService.resolveBucket(ipAddress);
            // 1 token tüketmeye çalış
            ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);

            if (probe.isConsumed()) {
                // İzin verildi, header'a kalan hakkı ekle bilgi amaçlı
                response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
                filterChain.doFilter(request, response);
            } else {
                // Limit aşıldı! 429 Too Many Requests
                long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.addHeader("X-Rate-Limiting-Retry-After-Seconds", String.valueOf(waitForRefill));
                response.getWriter()
                        .write("Çok fazla istek! Lütfen " + waitForRefill + " saniye sonra tekrar deneyin.");
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

}
