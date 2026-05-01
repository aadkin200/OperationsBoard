package apsoftware.operationsboard.service;

import apsoftware.operationsboard.dto.EscalationItemDto;
import apsoftware.operationsboard.dto.ExecutiveDashboardDto;
import apsoftware.operationsboard.dto.TeamHealthDto;
import apsoftware.operationsboard.dto.TeamMetricDto;
import apsoftware.operationsboard.dto.UserWorkloadDto;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;

@Service
public class ReportService {

    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    // ── Excel ─────────────────────────────────────────────────────────────────

    public byte[] generateExcelReport(ExecutiveDashboardDto data) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Styles s = new Styles(workbook);
            buildSummarySheet(workbook, s, data);
            buildTeamHealthSheet(workbook, s, data);
            buildEscalationSheet(workbook, s, data);
            buildWorkloadSheet(workbook, s, data);
            buildStaffingSheet(workbook, s, data);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    private void buildSummarySheet(XSSFWorkbook wb, Styles s, ExecutiveDashboardDto data) {
        XSSFSheet sheet = wb.createSheet("Executive Summary");
        sheet.setColumnWidth(0, 8000);
        sheet.setColumnWidth(1, 5000);

        int row = 0;

        XSSFRow titleRow = sheet.createRow(row++);
        titleRow.setHeightInPoints(28);
        XSSFCell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("OPERATIONS BOARD — EXECUTIVE REPORT");
        titleCell.setCellStyle(s.sheetTitle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

        XSSFRow dateRow = sheet.createRow(row++);
        XSSFCell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("Generated: " + LocalDate.now().format(DISPLAY_DATE));
        dateCell.setCellStyle(s.subTitle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

        row++;

        XSSFRow secRow = sheet.createRow(row++);
        XSSFCell secCell = secRow.createCell(0);
        secCell.setCellValue("ORGANIZATION SUMMARY");
        secCell.setCellStyle(s.sectionHeader);
        sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 6));

        row++;

        String[] labels = {"Active Tasks", "Blocked", "Overdue", "Due in 7 Days", "Critical Risk", "Unassigned", "Completed This Month"};
        long[] values = {
                data.getTotalActiveTasks(), data.getTotalBlocked(), data.getTotalOverdue(),
                data.getTotalDueSoon(), data.getTotalCriticalRisk(), data.getTotalUnassigned(),
                data.getCompletedThisMonth()
        };
        XSSFCellStyle[] metricStyles = {
                s.metricBlue, s.metricRed, s.metricRed,
                s.metricYellow, s.metricRed, s.metricYellow,
                s.metricGreen
        };

        for (int i = 0; i < labels.length; i++) {
            sheet.setColumnWidth(i, 4500);
        }

        XSSFRow labelRow = sheet.createRow(row++);
        labelRow.setHeightInPoints(16);
        XSSFRow valueRow = sheet.createRow(row++);
        valueRow.setHeightInPoints(36);

        for (int i = 0; i < labels.length; i++) {
            XSSFCell lc = labelRow.createCell(i);
            lc.setCellValue(labels[i]);
            lc.setCellStyle(s.metricLabel);

            XSSFCell vc = valueRow.createCell(i);
            vc.setCellValue(values[i]);
            vc.setCellStyle(metricStyles[i]);
        }
    }

    private void buildTeamHealthSheet(XSSFWorkbook wb, Styles s, ExecutiveDashboardDto data) {
        XSSFSheet sheet = wb.createSheet("Team Health");
        int[] colWidths = {7000, 3500, 3000, 3000, 3000, 3500, 4500};
        for (int i = 0; i < colWidths.length; i++) sheet.setColumnWidth(i, colWidths[i]);

        int row = 0;
        XSSFRow titleRow = sheet.createRow(row++);
        titleRow.setHeightInPoints(22);
        XSSFCell tc = titleRow.createCell(0);
        tc.setCellValue("TEAM HEALTH SCORECARD");
        tc.setCellStyle(s.sheetTitle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

        XSSFRow dateRow = sheet.createRow(row++);
        dateRow.createCell(0).setCellValue("Generated: " + LocalDate.now().format(DISPLAY_DATE));
        dateRow.getCell(0).setCellStyle(s.subTitle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));
        row++;

        XSSFRow header = sheet.createRow(row++);
        header.setHeightInPoints(18);
        String[] headers = {"Team", "Status", "Active", "Blocked", "Overdue", "Unassigned", "Completed/Mo"};
        for (int i = 0; i < headers.length; i++) {
            XSSFCell c = header.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(s.tableHeader);
        }

        for (TeamHealthDto team : data.getTeamHealth()) {
            XSSFRow dr = sheet.createRow(row++);
            dr.setHeightInPoints(18);

            XSSFCellStyle rowStyle = "RED".equals(team.getHealthStatus()) ? s.healthRed :
                    "YELLOW".equals(team.getHealthStatus()) ? s.healthYellow : s.healthGreen;
            String statusLabel = "RED".equals(team.getHealthStatus()) ? "At Risk" :
                    "YELLOW".equals(team.getHealthStatus()) ? "Watch" : "Healthy";

            setCell(dr, 0, team.getTeamName(), s.dataLeft);
            setCell(dr, 1, statusLabel, rowStyle);
            setNumCell(dr, 2, team.getOpenCount(), s.dataCenter);
            setNumCell(dr, 3, team.getBlockedCount(), team.getBlockedCount() > 0 ? s.dangerCell : s.dataCenter);
            setNumCell(dr, 4, team.getOverdueCount(), team.getOverdueCount() > 0 ? s.dangerCell : s.dataCenter);
            setNumCell(dr, 5, team.getUnassignedCount(), s.dataCenter);
            setNumCell(dr, 6, team.getCompletedThisMonth(), s.positiveCell);
        }
    }

    private void buildEscalationSheet(XSSFWorkbook wb, Styles s, ExecutiveDashboardDto data) {
        XSSFSheet sheet = wb.createSheet("Escalation Queue");
        int[] colWidths = {10000, 5000, 5000, 3500, 3500, 3500, 14000};
        for (int i = 0; i < colWidths.length; i++) sheet.setColumnWidth(i, colWidths[i]);

        int row = 0;
        XSSFRow titleRow = sheet.createRow(row++);
        titleRow.setHeightInPoints(22);
        XSSFCell tc = titleRow.createCell(0);
        tc.setCellValue("ESCALATION QUEUE — BLOCKED 7+ DAYS");
        tc.setCellStyle(s.sheetTitle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        row++;

        if (data.getEscalationQueue().isEmpty()) {
            XSSFRow er = sheet.createRow(row);
            er.createCell(0).setCellValue("No escalations — all blocked tasks are under 7 days.");
            return;
        }

        XSSFRow header = sheet.createRow(row++);
        header.setHeightInPoints(18);
        String[] headers = {"Task", "Team", "Assigned To", "Priority", "Days Blocked", "Due Date", "Blocker Reason"};
        for (int i = 0; i < headers.length; i++) {
            XSSFCell c = header.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(s.tableHeader);
        }

        for (EscalationItemDto item : data.getEscalationQueue()) {
            XSSFRow dr = sheet.createRow(row++);
            dr.setHeightInPoints(18);
            setCell(dr, 0, item.getTitle(), s.dataLeft);
            setCell(dr, 1, item.getTeamName(), s.dataLeft);
            setCell(dr, 2, item.getAssignedUserName(), s.dataLeft);
            setCell(dr, 3, item.getPriority(), "CRITICAL".equals(item.getPriority()) ? s.dangerCell : s.dataCenter);
            setNumCell(dr, 4, item.getDaysBlocked(), s.dangerCell);
            setCell(dr, 5, item.getDueDate() != null ? item.getDueDate() : "—", s.dataCenter);
            setCell(dr, 6, item.getBlockerReason() != null ? item.getBlockerReason() : "—", s.dataLeft);
        }
    }

    private void buildWorkloadSheet(XSSFWorkbook wb, Styles s, ExecutiveDashboardDto data) {
        XSSFSheet sheet = wb.createSheet("Workload by Team");
        sheet.setColumnWidth(0, 8000);
        sheet.setColumnWidth(1, 4000);

        int row = 0;
        XSSFRow titleRow = sheet.createRow(row++);
        titleRow.setHeightInPoints(22);
        XSSFCell tc = titleRow.createCell(0);
        tc.setCellValue("ACTIVE WORKLOAD BY TEAM");
        tc.setCellStyle(s.sheetTitle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        row++;

        XSSFRow header = sheet.createRow(row++);
        header.setHeightInPoints(18);
        setCell(header, 0, "Team", s.tableHeader);
        setCell(header, 1, "Active Tasks", s.tableHeader);
        setCell(header, 2, "Completed This Month", s.tableHeader);
        sheet.setColumnWidth(2, 5000);

        Map<Long, Long> completedMap = new HashMap<>();
        for (TeamMetricDto t : data.getCompletedByTeam()) {
            completedMap.put(t.getTeamId(), t.getCount());
        }

        for (TeamMetricDto team : data.getWorkloadByTeam()) {
            XSSFRow dr = sheet.createRow(row++);
            dr.setHeightInPoints(18);
            setCell(dr, 0, team.getTeamName(), s.dataLeft);
            setNumCell(dr, 1, team.getCount(), s.dataCenter);
            setNumCell(dr, 2, completedMap.getOrDefault(team.getTeamId(), 0L), s.positiveCell);
        }
    }

    private void buildStaffingSheet(XSSFWorkbook wb, Styles s, ExecutiveDashboardDto data) {
        XSSFSheet sheet = wb.createSheet("Staffing Pressure");
        sheet.setColumnWidth(0, 7000);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 4000);

        int row = 0;
        XSSFRow titleRow = sheet.createRow(row++);
        titleRow.setHeightInPoints(22);
        XSSFCell tc = titleRow.createCell(0);
        tc.setCellValue("STAFFING PRESSURE — WORKLOAD BY EMPLOYEE");
        tc.setCellStyle(s.sheetTitle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        row++;

        XSSFRow header = sheet.createRow(row++);
        header.setHeightInPoints(18);
        setCell(header, 0, "Employee", s.tableHeader);
        setCell(header, 1, "Team", s.tableHeader);
        setCell(header, 2, "Active Tasks", s.tableHeader);

        for (UserWorkloadDto person : data.getWorkloadByEmployee()) {
            XSSFRow dr = sheet.createRow(row++);
            dr.setHeightInPoints(18);
            setCell(dr, 0, person.getFullName(), s.dataLeft);
            setCell(dr, 1, person.getTeamName(), s.dataLeft);
            setNumCell(dr, 2, person.getOpenTaskCount(), person.getOpenTaskCount() >= 5 ? s.dangerCell : s.dataCenter);
        }
    }

    // ── PDF ───────────────────────────────────────────────────────────────────

//    public byte[] generatePdfReport(ExecutiveDashboardDto data) {
//        ClassLoader original = Thread.currentThread().getContextClassLoader();
//        try {
//            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
//
//            InputStream jrxmlStream = getClass().getResourceAsStream("/reports/executive_dashboard.jrxml");
//            if (jrxmlStream == null) {
//                throw new RuntimeException("Report template not found at classpath:/reports/executive_dashboard.jrxml");
//            }
//
//            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
//
//            Map<String, Object> params = new HashMap<>();
//            params.put("reportDate",        "Generated: " + LocalDate.now().format(DISPLAY_DATE));
//            params.put("totalActiveTasks",  data.getTotalActiveTasks());
//            params.put("totalBlocked",      data.getTotalBlocked());
//            params.put("totalOverdue",      data.getTotalOverdue());
//            params.put("totalDueSoon",      data.getTotalDueSoon());
//            params.put("totalCriticalRisk", data.getTotalCriticalRisk());
//            params.put("totalUnassigned",   data.getTotalUnassigned());
//            params.put("orgCompleted",      data.getCompletedThisMonth());
//
//            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data.getTeamHealth());
//            JasperPrint print = JasperFillManager.fillReport(jasperReport, params, dataSource);
//            return JasperExportManager.exportReportToPdf(print);
//
//        } catch (RuntimeException re) {
//            throw re;
//        } catch (Exception e) {
//            Throwable cause = e.getCause() != null ? e.getCause() : e;
//            throw new RuntimeException("PDF generation failed: " + cause.getClass().getSimpleName() + " — " + cause.getMessage(), e);
//        } finally {
//            Thread.currentThread().setContextClassLoader(original);
//        }
//    }
    
//    public byte[] generatePdfReport(ExecutiveDashboardDto data) {
//        ClassLoader original = Thread.currentThread().getContextClassLoader();
//
//        try {
//            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
//
//            InputStream jrxmlStream = getClass().getResourceAsStream("/reports/executive_dashboard.jrxml");
//
//            if (jrxmlStream == null) {
//                throw new RuntimeException("Report template not found at classpath:/reports/executive_dashboard.jrxml");
//            }
//
//            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
//
//            Map<String, Object> params = new HashMap<>();
//            params.put("reportDate", "Generated: " + LocalDate.now().format(DISPLAY_DATE));
//            params.put("totalActiveTasks", data.getTotalActiveTasks());
//            params.put("totalBlocked", data.getTotalBlocked());
//            params.put("totalOverdue", data.getTotalOverdue());
//            params.put("totalDueSoon", data.getTotalDueSoon());
//            params.put("totalCriticalRisk", data.getTotalCriticalRisk());
//            params.put("totalUnassigned", data.getTotalUnassigned());
//            params.put("orgCompleted", data.getCompletedThisMonth());
//
//            JRBeanCollectionDataSource dataSource =
//                    new JRBeanCollectionDataSource(data.getTeamHealth());
//
//            JasperPrint print =
//                    JasperFillManager.fillReport(jasperReport, params, dataSource);
//
//            return JasperExportManager.exportReportToPdf(print);
//
//        } catch (Exception e) {
//            System.err.println("========== PDF GENERATION FAILED ==========");
//            e.printStackTrace();
//
//            Throwable cause = e.getCause();
//            int depth = 1;
//
//            while (cause != null) {
//                System.err.println("========== CAUSE " + depth + " ==========");
//                cause.printStackTrace();
//                cause = cause.getCause();
//                depth++;
//            }
//
//            throw new RuntimeException("PDF generation failed. Check full stack trace above.", e);
//
//        } finally {
//            Thread.currentThread().setContextClassLoader(original);
//        }
//    }
    
//    public byte[] generatePdfReport(ExecutiveDashboardDto data) {
//        try {
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//            Document document = new Document(PageSize.A4, 30, 30, 30, 30);
//            PdfWriter.getInstance(document, out);
//            document.open();
//
//            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD, Color.WHITE);
//            Font subtitleFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.DARK_GRAY);
//            Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.BLACK);
//            Font headerFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
//            Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.BLACK);
//            Font boldCellFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.BLACK);
//
//            PdfPTable titleTable = new PdfPTable(1);
//            titleTable.setWidthPercentage(100);
//
//            PdfPCell titleCell = new PdfPCell(new Phrase("OPERATIONS BOARD - EXECUTIVE REPORT", titleFont));
//            titleCell.setBackgroundColor(new Color(30, 58, 95));
//            titleCell.setPadding(12);
//            titleCell.setBorder(Rectangle.NO_BORDER);
//            titleTable.addCell(titleCell);
//
//            document.add(titleTable);
//
//            Paragraph date = new Paragraph("Generated: " + LocalDate.now().format(DISPLAY_DATE), subtitleFont);
//            date.setSpacingBefore(8);
//            date.setSpacingAfter(16);
//            document.add(date);
//
//            Paragraph summaryTitle = new Paragraph("ORGANIZATION SUMMARY", sectionFont);
//            summaryTitle.setSpacingAfter(8);
//            document.add(summaryTitle);
//
//            PdfPTable summary = new PdfPTable(7);
//            summary.setWidthPercentage(100);
//            summary.setWidths(new float[]{1, 1, 1, 1, 1, 1, 1});
//
//            addMetric(summary, "Active Tasks", data.getTotalActiveTasks(), new Color(239, 246, 255));
//            addMetric(summary, "Blocked", data.getTotalBlocked(), new Color(254, 242, 242));
//            addMetric(summary, "Overdue", data.getTotalOverdue(), new Color(254, 242, 242));
//            addMetric(summary, "Due 7 Days", data.getTotalDueSoon(), new Color(255, 251, 235));
//            addMetric(summary, "Critical Risk", data.getTotalCriticalRisk(), new Color(254, 242, 242));
//            addMetric(summary, "Unassigned", data.getTotalUnassigned(), new Color(255, 251, 235));
//            addMetric(summary, "Completed", data.getCompletedThisMonth(), new Color(240, 253, 244));
//
//            document.add(summary);
//
//            Paragraph healthTitle = new Paragraph("TEAM HEALTH SCORECARD", sectionFont);
//            healthTitle.setSpacingBefore(20);
//            healthTitle.setSpacingAfter(8);
//            document.add(healthTitle);
//
//            PdfPTable table = new PdfPTable(7);
//            table.setWidthPercentage(100);
//            table.setWidths(new float[]{2.6f, 1.2f, 1f, 1f, 1f, 1.2f, 1.5f});
//
//            addHeader(table, "Team", headerFont);
//            addHeader(table, "Status", headerFont);
//            addHeader(table, "Active", headerFont);
//            addHeader(table, "Blocked", headerFont);
//            addHeader(table, "Overdue", headerFont);
//            addHeader(table, "Unassigned", headerFont);
//            addHeader(table, "Completed/Mo", headerFont);
//
//            for (TeamHealthDto team : data.getTeamHealth()) {
//                Color rowColor = getHealthColor(team.getHealthStatus());
//
//                addCell(table, team.getTeamName(), boldCellFont, rowColor, Element.ALIGN_LEFT);
//                addCell(table, healthLabelForPdf(team.getHealthStatus()), boldCellFont, rowColor, Element.ALIGN_CENTER);
//                addCell(table, String.valueOf(team.getOpenCount()), cellFont, rowColor, Element.ALIGN_CENTER);
//                addCell(table, String.valueOf(team.getBlockedCount()), cellFont, rowColor, Element.ALIGN_CENTER);
//                addCell(table, String.valueOf(team.getOverdueCount()), cellFont, rowColor, Element.ALIGN_CENTER);
//                addCell(table, String.valueOf(team.getUnassignedCount()), cellFont, rowColor, Element.ALIGN_CENTER);
//                addCell(table, String.valueOf(team.getCompletedThisMonth()), cellFont, rowColor, Element.ALIGN_CENTER);
//            }
//
//            document.add(table);
//
//            document.close();
//            return out.toByteArray();
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to generate PDF report", e);
//        }
//    }
    
    public byte[] generatePdfReport(ExecutiveDashboardDto data) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD, Color.WHITE);
            Font subtitleFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.DARK_GRAY);
            Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.BLACK);
            Font headerFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
            Font cellFont = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.BLACK);
            Font boldCellFont = new Font(Font.HELVETICA, 8, Font.BOLD, Color.BLACK);

            // ───────────────── HEADER ─────────────────

            PdfPTable titleTable = new PdfPTable(1);
            titleTable.setWidthPercentage(100);

            PdfPCell titleCell = new PdfPCell(
                    new Phrase("OPERATIONS BOARD - EXECUTIVE REPORT", titleFont));

            titleCell.setBackgroundColor(new Color(30, 58, 95));
            titleCell.setPadding(12);
            titleCell.setBorder(Rectangle.NO_BORDER);

            titleTable.addCell(titleCell);

            document.add(titleTable);

            Paragraph date = new Paragraph(
                    "Generated: " + LocalDate.now().format(DISPLAY_DATE),
                    subtitleFont);

            date.setSpacingBefore(8);
            date.setSpacingAfter(18);

            document.add(date);

            // ───────────────── SUMMARY METRICS ─────────────────

            Paragraph summaryTitle = new Paragraph(
                    "ORGANIZATION SUMMARY",
                    sectionFont);

            summaryTitle.setSpacingAfter(8);

            document.add(summaryTitle);

            PdfPTable summary = new PdfPTable(7);
            summary.setWidthPercentage(100);
            summary.setSpacingAfter(20);

            addMetric(summary, "Active Tasks",
                    data.getTotalActiveTasks(),
                    new Color(239, 246, 255));

            addMetric(summary, "Blocked",
                    data.getTotalBlocked(),
                    new Color(254, 242, 242));

            addMetric(summary, "Overdue",
                    data.getTotalOverdue(),
                    new Color(254, 242, 242));

            addMetric(summary, "Due in 7 Days",
                    data.getTotalDueSoon(),
                    new Color(255, 251, 235));

            addMetric(summary, "Critical Risk",
                    data.getTotalCriticalRisk(),
                    new Color(254, 242, 242));

            addMetric(summary, "Unassigned",
                    data.getTotalUnassigned(),
                    new Color(255, 251, 235));

            addMetric(summary, "Completed",
                    data.getCompletedThisMonth(),
                    new Color(240, 253, 244));

            document.add(summary);

            // ───────────────── TEAM HEALTH ─────────────────

            Paragraph healthTitle = new Paragraph(
                    "TEAM HEALTH SCORECARD",
                    sectionFont);

            healthTitle.setSpacingBefore(8);
            healthTitle.setSpacingAfter(8);

            document.add(healthTitle);

            PdfPTable healthTable = new PdfPTable(7);
            healthTable.setWidthPercentage(100);
            healthTable.setSpacingAfter(20);

            healthTable.setWidths(new float[]{2.5f, 1.2f, 1f, 1f, 1f, 1.2f, 1.4f});

            addHeader(healthTable, "Team", headerFont);
            addHeader(healthTable, "Status", headerFont);
            addHeader(healthTable, "Active", headerFont);
            addHeader(healthTable, "Blocked", headerFont);
            addHeader(healthTable, "Overdue", headerFont);
            addHeader(healthTable, "Unassigned", headerFont);
            addHeader(healthTable, "Completed", headerFont);

            for (TeamHealthDto team : data.getTeamHealth()) {

                Color rowColor = getHealthColor(team.getHealthStatus());

                addCell(healthTable,
                        team.getTeamName(),
                        boldCellFont,
                        rowColor,
                        Element.ALIGN_LEFT);

                addCell(healthTable,
                        healthLabelForPdf(team.getHealthStatus()),
                        boldCellFont,
                        rowColor,
                        Element.ALIGN_CENTER);

                addCell(healthTable,
                        String.valueOf(team.getOpenCount()),
                        cellFont,
                        rowColor,
                        Element.ALIGN_CENTER);

                addCell(healthTable,
                        String.valueOf(team.getBlockedCount()),
                        cellFont,
                        rowColor,
                        Element.ALIGN_CENTER);

                addCell(healthTable,
                        String.valueOf(team.getOverdueCount()),
                        cellFont,
                        rowColor,
                        Element.ALIGN_CENTER);

                addCell(healthTable,
                        String.valueOf(team.getUnassignedCount()),
                        cellFont,
                        rowColor,
                        Element.ALIGN_CENTER);

                addCell(healthTable,
                        String.valueOf(team.getCompletedThisMonth()),
                        cellFont,
                        rowColor,
                        Element.ALIGN_CENTER);
            }

            document.add(healthTable);

            // ───────────────── ESCALATION QUEUE ─────────────────

            Paragraph escalationTitle = new Paragraph(
                    "ESCALATION QUEUE - BLOCKED 7+ DAYS",
                    sectionFont);

            escalationTitle.setSpacingBefore(8);
            escalationTitle.setSpacingAfter(8);

            document.add(escalationTitle);

            if (data.getEscalationQueue().isEmpty()) {

                Paragraph none = new Paragraph(
                        "No escalations in queue.",
                        cellFont);

                none.setSpacingAfter(20);

                document.add(none);

            } else {

                PdfPTable escalationTable = new PdfPTable(7);

                escalationTable.setWidthPercentage(100);
                escalationTable.setSpacingAfter(20);

                escalationTable.setWidths(
                        new float[]{3.4f, 1.5f, 1.7f, 1f, 1f, 1.2f, 3.2f});

                addHeader(escalationTable, "Task", headerFont);
                addHeader(escalationTable, "Team", headerFont);
                addHeader(escalationTable, "Assigned", headerFont);
                addHeader(escalationTable, "Priority", headerFont);
                addHeader(escalationTable, "Days", headerFont);
                addHeader(escalationTable, "Due", headerFont);
                addHeader(escalationTable, "Blocker Reason", headerFont);

                for (EscalationItemDto item : data.getEscalationQueue()) {

                    Color rowColor =
                            "CRITICAL".equals(item.getPriority())
                                    ? new Color(254, 242, 242)
                                    : Color.WHITE;

                    addCell(escalationTable,
                            item.getTitle(),
                            cellFont,
                            rowColor,
                            Element.ALIGN_LEFT);

                    addCell(escalationTable,
                            item.getTeamName(),
                            cellFont,
                            rowColor,
                            Element.ALIGN_LEFT);

                    addCell(escalationTable,
                            item.getAssignedUserName(),
                            cellFont,
                            rowColor,
                            Element.ALIGN_LEFT);

                    addCell(escalationTable,
                            item.getPriority(),
                            boldCellFont,
                            rowColor,
                            Element.ALIGN_CENTER);

                    addCell(escalationTable,
                            String.valueOf(item.getDaysBlocked()),
                            cellFont,
                            rowColor,
                            Element.ALIGN_CENTER);

                    addCell(escalationTable,
                            item.getDueDate() != null
                                    ? item.getDueDate()
                                    : "-",
                            cellFont,
                            rowColor,
                            Element.ALIGN_CENTER);

                    addCell(escalationTable,
                            item.getBlockerReason() != null
                                    ? item.getBlockerReason()
                                    : "-",
                            cellFont,
                            rowColor,
                            Element.ALIGN_LEFT);
                }

                document.add(escalationTable);
            }

            // ───────────────── STAFFING PRESSURE ─────────────────

            Paragraph staffingTitle = new Paragraph(
                    "STAFFING PRESSURE",
                    sectionFont);

            staffingTitle.setSpacingBefore(8);
            staffingTitle.setSpacingAfter(8);

            document.add(staffingTitle);

            PdfPTable staffingTable = new PdfPTable(3);

            staffingTable.setWidthPercentage(100);
            staffingTable.setSpacingAfter(20);

            staffingTable.setWidths(new float[]{2.5f, 2f, 1f});

            addHeader(staffingTable, "Employee", headerFont);
            addHeader(staffingTable, "Team", headerFont);
            addHeader(staffingTable, "Active Tasks", headerFont);

            for (UserWorkloadDto person : data.getWorkloadByEmployee()) {

                Color rowColor =
                        person.getOpenTaskCount() >= 5
                                ? new Color(254, 242, 242)
                                : Color.WHITE;

                addCell(staffingTable,
                        person.getFullName(),
                        cellFont,
                        rowColor,
                        Element.ALIGN_LEFT);

                addCell(staffingTable,
                        person.getTeamName(),
                        cellFont,
                        rowColor,
                        Element.ALIGN_LEFT);

                addCell(staffingTable,
                        String.valueOf(person.getOpenTaskCount()),
                        boldCellFont,
                        rowColor,
                        Element.ALIGN_CENTER);
            }

            document.add(staffingTable);

            // ───────────────── WORKLOAD BY TEAM ─────────────────

            Paragraph workloadTitle = new Paragraph(
                    "WORKLOAD BY TEAM",
                    sectionFont);

            workloadTitle.setSpacingBefore(8);
            workloadTitle.setSpacingAfter(8);

            document.add(workloadTitle);

            PdfPTable workloadTable = new PdfPTable(3);

            workloadTable.setWidthPercentage(100);
            workloadTable.setSpacingAfter(20);

            workloadTable.setWidths(new float[]{3f, 1.3f, 1.5f});

            addHeader(workloadTable, "Team", headerFont);
            addHeader(workloadTable, "Active Tasks", headerFont);
            addHeader(workloadTable, "Completed This Month", headerFont);

            Map<Long, Long> completedMap = new HashMap<>();

            for (TeamMetricDto t : data.getCompletedByTeam()) {
                completedMap.put(t.getTeamId(), t.getCount());
            }

            for (TeamMetricDto team : data.getWorkloadByTeam()) {

                addCell(workloadTable,
                        team.getTeamName(),
                        cellFont,
                        Color.WHITE,
                        Element.ALIGN_LEFT);

                addCell(workloadTable,
                        String.valueOf(team.getCount()),
                        cellFont,
                        Color.WHITE,
                        Element.ALIGN_CENTER);

                addCell(workloadTable,
                        String.valueOf(
                                completedMap.getOrDefault(team.getTeamId(), 0L)),
                        boldCellFont,
                        new Color(240, 253, 244),
                        Element.ALIGN_CENTER);
            }

            document.add(workloadTable);

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void setCell(XSSFRow row, int col, String value, XSSFCellStyle style) {
        XSSFCell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private void setNumCell(XSSFRow row, int col, long value, XSSFCellStyle style) {
        XSSFCell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
    
    private void addMetric(PdfPTable table, String label, long value, Color backgroundColor) {
        Font valueFont = new Font(Font.HELVETICA, 16, Font.BOLD, Color.BLACK);
        Font labelFont = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);

        Paragraph p = new Paragraph();
        p.add(new Phrase(String.valueOf(value), valueFont));
        p.add(new Phrase("\n" + label, labelFont));

        PdfPCell cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(backgroundColor);
        cell.setPadding(8);
        cell.setMinimumHeight(55);
        table.addCell(cell);
    }

    private void addHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(30, 58, 95));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String text, Font font, Color backgroundColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private Color getHealthColor(String status) {
        if ("RED".equals(status)) {
            return new Color(254, 242, 242);
        }
        if ("YELLOW".equals(status)) {
            return new Color(255, 251, 235);
        }
        return new Color(240, 253, 244);
    }

    private String healthLabelForPdf(String status) {
        if ("RED".equals(status)) {
            return "At Risk";
        }
        if ("YELLOW".equals(status)) {
            return "Watch";
        }
        return "Healthy";
    }

    // ── Style registry ────────────────────────────────────────────────────────

    private static class Styles {
        final XSSFCellStyle sheetTitle, subTitle, sectionHeader, tableHeader;
        final XSSFCellStyle dataLeft, dataCenter;
        final XSSFCellStyle metricBlue, metricRed, metricYellow, metricGreen, metricLabel;
        final XSSFCellStyle healthRed, healthYellow, healthGreen;
        final XSSFCellStyle dangerCell, positiveCell;

        Styles(XSSFWorkbook wb) {
            sheetTitle   = build(wb, "1E3A5F", "FFFFFF", 14, true,  HorizontalAlignment.LEFT,   false);
            subTitle     = build(wb, "FFFFFF", "6B7280",  9, false, HorizontalAlignment.LEFT,   false);
            sectionHeader= build(wb, "FFFFFF", "111827", 11, true,  HorizontalAlignment.LEFT,   false);
            tableHeader  = build(wb, "1E3A5F", "FFFFFF", 10, true,  HorizontalAlignment.CENTER, true);
            dataLeft     = build(wb, "FFFFFF", "374151",  9, false, HorizontalAlignment.LEFT,   true);
            dataCenter   = build(wb, "FFFFFF", "374151",  9, false, HorizontalAlignment.CENTER, true);
            metricBlue   = buildMetric(wb, "EFF6FF", "1D4ED8", 18);
            metricRed    = buildMetric(wb, "FEF2F2", "B91C1C", 18);
            metricYellow = buildMetric(wb, "FFFBEB", "B45309", 18);
            metricGreen  = buildMetric(wb, "F0FDF4", "15803D", 18);
            metricLabel  = build(wb, "F9FAFB", "6B7280",  8, false, HorizontalAlignment.CENTER, false);
            healthRed    = build(wb, "FEF2F2", "B91C1C",  9, true,  HorizontalAlignment.CENTER, true);
            healthYellow = build(wb, "FFFBEB", "92400E",  9, true,  HorizontalAlignment.CENTER, true);
            healthGreen  = build(wb, "F0FDF4", "065F46",  9, true,  HorizontalAlignment.CENTER, true);
            dangerCell   = build(wb, "FEF2F2", "DC2626",  9, true,  HorizontalAlignment.CENTER, true);
            positiveCell = build(wb, "FFFFFF", "059669",  9, true,  HorizontalAlignment.CENTER, true);
        }

        private XSSFCellStyle build(XSSFWorkbook wb, String bg, String fg, int size,
                boolean bold, HorizontalAlignment align, boolean border) {
            XSSFCellStyle style = wb.createCellStyle();
            style.setFillForegroundColor(hex(bg));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(align);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            if (border) {
                style.setBorderBottom(BorderStyle.THIN);
                style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
                style.setBorderRight(BorderStyle.THIN);
                style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            }
            XSSFFont font = wb.createFont();
            font.setColor(hex(fg));
            font.setBold(bold);
            font.setFontHeightInPoints((short) size);
            style.setFont(font);
            return style;
        }

        private XSSFCellStyle buildMetric(XSSFWorkbook wb, String bg, String fg, int size) {
            XSSFCellStyle style = wb.createCellStyle();
            style.setFillForegroundColor(hex(bg));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            XSSFFont font = wb.createFont();
            font.setColor(hex(fg));
            font.setBold(true);
            font.setFontHeightInPoints((short) size);
            style.setFont(font);
            return style;
        }

        private XSSFColor hex(String hex) {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            return new XSSFColor(new byte[]{(byte) r, (byte) g, (byte) b}, null);
        }
    }
}
