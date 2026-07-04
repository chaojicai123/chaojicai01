package com.eldercare.controller;

import com.eldercare.service.AuthService;
import com.eldercare.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StatisticsService statisticsService;
    private final AuthService authService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> dashboard() {
        return ResponseEntity.ok(statisticsService.dashboard());
    }

    /** 为指定用户设置关联老人（家属仅能查看该老人健康数据） */
    @PatchMapping("/users/{userId}/linked-elder")
    public ResponseEntity<Void> setUserLinkedElder(@PathVariable Long userId, @RequestBody Map<String, Long> body) {
        Long linkedElderId = body != null ? body.get("linkedElderId") : null;
        authService.setLinkedElder(userId, linkedElderId);
        return ResponseEntity.ok().build();
    }
}
