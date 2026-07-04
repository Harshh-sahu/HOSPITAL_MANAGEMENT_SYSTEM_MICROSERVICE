package com.EmailService.EmailServiceMS.service;

import com.EmailService.EmailServiceMS.event.ReportCreatedEvent;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PdfReportGenerator {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private static final Color TEAL       = new Color(13, 148, 136);
    private static final Color TEAL_LIGHT = new Color(240, 253, 250);
    private static final Color SLATE_800  = new Color(30, 41, 59);
    private static final Color SLATE_500  = new Color(100, 116, 139);
    private static final Color SLATE_100  = new Color(241, 245, 249);
    private static final Color WHITE      = Color.WHITE;

    private static final Font FONT_TITLE   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, SLATE_800);
    private static final Font FONT_SUBTITLE = FontFactory.getFont(FontFactory.HELVETICA, 11, SLATE_500);
    private static final Font FONT_SECTION = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEAL);
    private static final Font FONT_LABEL  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, SLATE_800);
    private static final Font FONT_VALUE  = FontFactory.getFont(FontFactory.HELVETICA, 10, SLATE_800);
    private static final Font FONT_SMALL  = FontFactory.getFont(FontFactory.HELVETICA, 9, SLATE_500);

    public byte[] generate(ReportCreatedEvent event) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            doc.open();

            addHeader(doc, event);
            addDivider(doc);
            addInfoRow(doc, "Patient", safe(event.getPatientName()), "Report ID", "#" + event.getRecordId());
            addInfoRow(doc, "Doctor", safe(event.getDoctorName()), "Date", formatDate(event));
            addDivider(doc);

            addSection(doc, "Diagnosis", List.of(safe(event.getDiagnosis())), false);
            addSection(doc, "Symptoms", event.getSymptoms(), true);
            addSection(doc, "Tests Ordered", event.getTests(), true);

            if (event.getFollowUpDate() != null) {
                addSection(doc, "Follow-up Date", List.of(event.getFollowUpDate().format(DATE)), false);
            }
            if (event.getReferral() != null && !event.getReferral().isBlank()) {
                addSection(doc, "Referral", List.of(event.getReferral()), false);
            }
            if (event.getNotes() != null && !event.getNotes().isBlank()) {
                addSection(doc, "Doctor's Notes", List.of(event.getNotes()), false);
            }
            if (event.getMedicines() != null && !event.getMedicines().isEmpty()) {
                addMedicinesSection(doc, event.getMedicines());
            }

            addFooter(doc);
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate medical report PDF", e);
        }
    }

    private void addHeader(Document doc, ReportCreatedEvent event) throws DocumentException {
        // Hospital name
        Paragraph hospital = new Paragraph("HMS Hospital", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, TEAL));
        hospital.setAlignment(Element.ALIGN_CENTER);
        doc.add(hospital);

        Paragraph tagline = new Paragraph("Medical Report", FONT_SUBTITLE);
        tagline.setAlignment(Element.ALIGN_CENTER);
        tagline.setSpacingAfter(4);
        doc.add(tagline);

        // Colored badge: Appointment #X
        PdfPTable badge = new PdfPTable(1);
        badge.setWidthPercentage(40);
        badge.setHorizontalAlignment(Element.ALIGN_CENTER);
        badge.setSpacingBefore(8);
        badge.setSpacingAfter(10);
        PdfPCell cell = new PdfPCell(new Phrase("Appointment  #" + event.getAppointmentId(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, WHITE)));
        cell.setBackgroundColor(TEAL);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(7);
        cell.setBorder(Rectangle.NO_BORDER);
        badge.addCell(cell);
        doc.add(badge);
    }

    private void addDivider(Document doc) throws DocumentException {
        PdfPTable divider = new PdfPTable(1);
        divider.setWidthPercentage(100);
        divider.setSpacingBefore(4);
        divider.setSpacingAfter(10);
        PdfPCell line = new PdfPCell();
        line.setFixedHeight(2f);
        line.setBackgroundColor(TEAL);
        line.setBorder(Rectangle.NO_BORDER);
        divider.addCell(line);
        doc.add(divider);
    }

    private void addInfoRow(Document doc, String label1, String val1, String label2, String val2) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(8);
        table.addCell(infoCell(label1, val1));
        table.addCell(infoCell(label2, val2));
        doc.add(table);
    }

    private PdfPCell infoCell(String label, String value) {
        PdfPTable inner = new PdfPTable(1);
        inner.setWidthPercentage(100);

        Phrase labelPhrase = new Phrase(label.toUpperCase(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, SLATE_500));
        Phrase valuePhrase = new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, SLATE_800));

        PdfPCell lCell = new PdfPCell(labelPhrase);
        lCell.setBorder(Rectangle.NO_BORDER);
        lCell.setPaddingBottom(2);
        inner.addCell(lCell);

        PdfPCell vCell = new PdfPCell(valuePhrase);
        vCell.setBorder(Rectangle.NO_BORDER);
        inner.addCell(vCell);

        PdfPCell wrapper = new PdfPCell(inner);
        wrapper.setBackgroundColor(TEAL_LIGHT);
        wrapper.setPadding(12);
        wrapper.setBorderColor(new Color(204, 251, 241));
        wrapper.setBorderWidth(1);
        return wrapper;
    }

    private void addSection(Document doc, String title, List<String> items, boolean bullets) throws DocumentException {
        if (items == null || items.isEmpty()) return;

        // Section header
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        header.setSpacingBefore(10);
        PdfPCell hCell = new PdfPCell(new Phrase(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, WHITE)));
        hCell.setBackgroundColor(SLATE_800);
        hCell.setPadding(8);
        hCell.setBorder(Rectangle.NO_BORDER);
        header.addCell(hCell);
        doc.add(header);

        // Content
        PdfPTable content = new PdfPTable(1);
        content.setWidthPercentage(100);
        content.setSpacingAfter(4);
        for (String item : items) {
            if (item == null || item.isBlank()) continue;
            String text = bullets ? "•  " + item : item;
            PdfPCell cCell = new PdfPCell(new Phrase(text, FONT_VALUE));
            cCell.setBackgroundColor(SLATE_100);
            cCell.setPadding(10);
            cCell.setBorder(Rectangle.NO_BORDER);
            content.addCell(cCell);
        }
        doc.add(content);
    }

    private void addMedicinesSection(Document doc, List<ReportCreatedEvent.MedicineInfo> medicines) throws DocumentException {
        // Section header
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        header.setSpacingBefore(10);
        PdfPCell hCell = new PdfPCell(new Phrase("Prescribed Medicines (" + medicines.size() + ")",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, WHITE)));
        hCell.setBackgroundColor(SLATE_800);
        hCell.setPadding(8);
        hCell.setBorder(Rectangle.NO_BORDER);
        header.addCell(hCell);
        doc.add(header);

        // Column headers
        PdfPTable colHeaders = new PdfPTable(new float[]{3, 2, 2, 1, 1, 3});
        colHeaders.setWidthPercentage(100);
        String[] cols = {"Medicine", "Dosage", "Frequency", "Duration", "Type", "Instructions"};
        for (String col : cols) {
            PdfPCell c = new PdfPCell(new Phrase(col, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, WHITE)));
            c.setBackgroundColor(TEAL);
            c.setPadding(7);
            c.setBorder(Rectangle.NO_BORDER);
            colHeaders.addCell(c);
        }
        doc.add(colHeaders);

        // Medicine rows
        PdfPTable rows = new PdfPTable(new float[]{3, 2, 2, 1, 1, 3});
        rows.setWidthPercentage(100);
        rows.setSpacingAfter(4);
        int i = 0;
        for (ReportCreatedEvent.MedicineInfo m : medicines) {
            Color rowBg = (i % 2 == 0) ? SLATE_100 : WHITE;
            addMedicineCell(rows, safe(m.getName()), rowBg, true);
            addMedicineCell(rows, safe(m.getDosage()), rowBg, false);
            addMedicineCell(rows, safe(m.getFrequency()), rowBg, false);
            addMedicineCell(rows, m.getDuration() != null ? m.getDuration() + " day(s)" : "—", rowBg, false);
            addMedicineCell(rows, safe(m.getType()), rowBg, false);
            addMedicineCell(rows, safe(m.getInstructions()), rowBg, false);
            i++;
        }
        doc.add(rows);
    }

    private void addMedicineCell(PdfPTable table, String text, Color bg, boolean bold) {
        Font f = bold ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, SLATE_800)
                      : FontFactory.getFont(FontFactory.HELVETICA, 9, SLATE_800);
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setBackgroundColor(bg);
        c.setPadding(7);
        c.setBorderColor(new Color(226, 232, 240));
        c.setBorderWidth(0.5f);
        table.addCell(c);
    }

    private void addFooter(Document doc) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        addDivider(doc);
        Paragraph footer = new Paragraph("This is an auto-generated medical report from HMS Hospital. For queries contact your doctor.", FONT_SMALL);
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }

    private String formatDate(ReportCreatedEvent event) {
        if (event.getCreatedAt() != null) return event.getCreatedAt().format(DATE_TIME);
        return "—";
    }
}
