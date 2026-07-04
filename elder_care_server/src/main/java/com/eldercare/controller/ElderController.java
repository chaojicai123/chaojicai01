package com.eldercare.controller;

import com.eldercare.dto.ElderDTO;
import com.eldercare.dto.PageResponse;
import com.eldercare.entity.Elder;
import com.eldercare.entity.Elder.Status;
import com.eldercare.service.ElderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/elders")
@RequiredArgsConstructor
public class ElderController {

    private final ElderService elderService;

    @GetMapping
    public ResponseEntity<PageResponse<ElderDTO>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(elderService.list(name, status, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ElderDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(elderService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ElderDTO> create(@RequestBody Elder elder) {
        return ResponseEntity.ok(elderService.create(elder));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ElderDTO> update(@PathVariable Long id, @RequestBody Elder elder) {
        return ResponseEntity.ok(elderService.update(id, elder));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        elderService.delete(id);
        return ResponseEntity.ok().build();
    }
}
