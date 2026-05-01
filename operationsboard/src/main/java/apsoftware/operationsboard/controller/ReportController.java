package apsoftware.operationsboard.controller;

import apsoftware.operationsboard.dto.ExecutiveDashboardDto;
import apsoftware.operationsboard.security.CurrentUserService;
import apsoftware.operationsboard.service.DashboardService;
import apsoftware.operationsboard.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final DashboardService dashboardService;
    private final CurrentUserService currentUserService;

    public ReportController(ReportService reportService,
                            DashboardService dashboardService,
                            CurrentUserService currentUserService) {
        this.reportService = reportService;
        this.dashboardService = dashboardService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/executive/excel")
    public ResponseEntity<byte[]> downloadExcel() {
        Long userId = currentUserService.getCurrentUserId();
        ExecutiveDashboardDto data = dashboardService.getExecutiveDashboard(userId);
        byte[] bytes = reportService.generateExcelReport(data);

        String filename = "executive-report-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    @GetMapping("/executive/pdf")
    public ResponseEntity<byte[]> downloadPdf() {
        Long userId = currentUserService.getCurrentUserId();
        ExecutiveDashboardDto data = dashboardService.getExecutiveDashboard(userId);
        byte[] bytes = reportService.generatePdfReport(data);

        String filename = "executive-report-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }
}
