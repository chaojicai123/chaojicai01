package com.eldercare.controller;

import com.eldercare.dto.NursingStaffDTO;
import com.eldercare.dto.PageResponse;
import com.eldercare.entity.NursingStaff;
import com.eldercare.service.NursingStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class NursingStaffController {

    private final NursingStaffService nursingStaffService;

    @GetMapping
    public ResponseEntity<PageResponse<NursingStaffDTO>> list(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(nursingStaffService.list(name, page, size));
    }

    @GetMapping("/available")
    public ResponseEntity<List<NursingStaffDTO>> listAvailable() {
        return ResponseEntity.ok(nursingStaffService.listAvailable());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NursingStaffDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(nursingStaffService.getById(id));
    }

    @PostMapping
    public ResponseEntity<NursingStaffDTO> create(@RequestBody NursingStaff staff) {
        return ResponseEntity.ok(nursingStaffService.create(staff));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NursingStaffDTO> update(@PathVariable Long id, @RequestBody NursingStaff staff) {
        return ResponseEntity.ok(nursingStaffService.update(id, staff));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        nursingStaffService.delete(id);
        return ResponseEntity.ok().build();
    }
}
