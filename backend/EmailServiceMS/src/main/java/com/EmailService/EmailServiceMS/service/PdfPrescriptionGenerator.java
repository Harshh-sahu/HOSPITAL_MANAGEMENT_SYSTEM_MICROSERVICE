package com.EmailService.EmailServiceMS.service;

import com.EmailService.EmailServiceMS.event.PrescriptionCreatedEvent;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PdfPrescriptionGenerator {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private static final Color GREEN       = new Color(5, 150, 105);
    private static final Color GREEN_LIGHT = new Color(236, 253, 245);
    private static final Color GREEN_BORDER= new Color(167, 243, 208);
    private static final Color SLATE_800   = new Color(30, 41, 59);
    private static final Color SLATE_500   = new Color(100, 116, 139);
    private static final Color SLATE_100   = new Color(241, 245, 249);
    private static final Color WHITE       = Color.WHITE;
    private static final Color AMBER_50    = new Color(255, 251, 235);
    private static final Color AMBER       = new Color(217, 119, 6);

    public byte[] generate(PrescriptionCreatedEvent event) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter.getInstance(doc, out);
            doc.open();

            addHeader(doc, event);
            addDivider(doc);
            addPatientDoctorInfo(doc, event);
            addDivider(doc);
            addMedicinesTable(doc, event.getMedicineDetails());
            addNotesSection(doc, event.getNotes());
            addValidity(doc, event);
            addFooter(doc);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate prescription PDF", e);
        }
    }

    private void addHeader(Document doc, PrescriptionCreatedEvent event) throws DocumentException {
        Paragraph hospital = new Paragraph("HMS Hospital",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, GREEN));
        hospital.setAlignment(Element.ALIGN_CENTER);
        doc.add(hospital);

        Paragraph rx = new Paragraph("℞  Medical Prescription",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, SLATE_500));
        rx.setAlignment(Element.ALIGN_CENTER);
        rx.setSpacingAfter(6);
        doc.add(rx);

        if (event.getPrescriptionId() != null) {
            PdfPTable badge = new PdfPTable(1);
            badge.setWidthPercentage(45);
            badge.setHorizontalAlignment(Element.ALIGN_CENTER);
            badge.setSpacingBefore(4);
            badge.setSpacingAfter(10);
            PdfPCell cell = new PdfPCell(new Phrase("Prescription  #" + event.getPrescriptionId(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, WHITE)));
            cell.setBackgroundColor(GREEN);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(9);
            cell.setBorder(Rectangle.NO_BORDER);
            badge.addCell(cell);
            doc.add(badge);
        }
    }

    private void addPatientDoctorInfo(Document doc, PrescriptionCreatedEvent event) throws DocumentException {
        String date = event.getPrescriptionDate() != null ? event.getPrescriptionDate().format(DATE) : "—";
        addInfoRow(doc, "Patient", safe(event.getPatientName()), "Doctor", "Dr. " + safe(event.getDoctorName()));
        addInfoRow(doc, "Prescription Date", date, "Appointment #", event.getAppointmentId() != null ? String.valueOf(event.getAppointmentId()) : "—");
    }

    private void addInfoRow(Document doc, String l1, String v1, String l2, String v2) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(8);
        table.addCell(infoCell(l1, v1));
        table.addCell(infoCell(l2, v2));
        doc.add(table);
    }

    private PdfPCell infoCell(String label, String value) {
        PdfPTable inner = new PdfPTable(1);
        inner.setWidthPercentage(100);

        PdfPCell lc = new PdfPCell(new Phrase(label.toUpperCase(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, SLATE_500)));
        lc.setBorder(Rectangle.NO_BORDER);
        lc.setPaddingBottom(3);
        inner.addCell(lc);

        PdfPCell vc = new PdfPCell(new Phrase(value,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, SLATE_800)));
        vc.setBorder(Rectangle.NO_BORDER);
        inner.addCell(vc);

        PdfPCell wrapper = new PdfPCell(inner);
        wrapper.setBackgroundColor(GREEN_LIGHT);
        wrapper.setPadding(12);
        wrapper.setBorderColor(GREEN_BORDER);
        wrapper.setBorderWidth(1);
        return wrapper;
    }

    private void addMedicinesTable(Document doc, List<PrescriptionCreatedEvent.MedicineInfo> medicines) throws DocumentException {
        // Section header
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        header.setSpacingBefore(10);
        String count = medicines != null ? " (" + medicines.size() + ")" : "";
        PdfPCell hCell = new PdfPCell(new Phrase("Prescribed Medicines" + count,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, WHITE)));
        hCell.setBackgroundColor(SLATE_800);
        hCell.setPadding(8);
        hCell.setBorder(Rectangle.NO_BORDER);
        header.addCell(hCell);
        doc.add(header);

        if (medicines == null || medicines.isEmpty()) {
            PdfPTable empty = new PdfPTable(1);
            empty.setWidthPercentage(100);
            PdfPCell ec = new PdfPCell(new Phrase("No medicines prescribed.",
                    FontFactory.getFont(FontFactory.HELVETICA, 10, SLATE_500)));
            ec.setBackgroundColor(SLATE_100);
            ec.setPadding(10);
            ec.setBorder(Rectangle.NO_BORDER);
            empty.addCell(ec);
            doc.add(empty);
            return;
        }

        // Column headers
        PdfPTable cols = new PdfPTable(new float[]{3, 2, 2, 1, 1, 3});
        cols.setWidthPercentage(100);
        String[] colNames = {"Medicine", "Dosage", "Frequency", "Duration", "Type", "Instructions"};
        for (String col : colNames) {
            PdfPCell c = new PdfPCell(new Phrase(col, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, WHITE)));
            c.setBackgroundColor(GREEN);
            c.setPadding(7);
            c.setBorder(Rectangle.NO_BORDER);
            cols.addCell(c);
        }
        doc.add(cols);

        // Rows
        PdfPTable rows = new PdfPTable(new float[]{3, 2, 2, 1, 1, 3});
        rows.setWidthPercentage(100);
        rows.setSpacingAfter(4);
        int i = 0;
        for (PrescriptionCreatedEvent.MedicineInfo m : medicines) {
            Color bg = (i % 2 == 0) ? SLATE_100 : WHITE;
            addMedCell(rows, safe(m.getName()), bg, true);
            addMedCell(rows, safe(m.getDosage()), bg, false);
            addMedCell(rows, safe(m.getFrequency()), bg, false);
            addMedCell(rows, m.getDuration() != null ? m.getDuration() + " day(s)" : "—", bg, false);
            addMedCell(rows, safe(m.getType()), bg, false);
            addMedCell(rows, safe(m.getInstructions()), bg, false);
            i++;
        }
        doc.add(rows);
    }

    private void addMedCell(PdfPTable table, String text, Color bg, boolean bold) {
        Font f = bold ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, SLATE_800)
                      : FontFactory.getFont(FontFactory.HELVETICA, 9, SLATE_800);
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setBackgroundColor(bg);
        c.setPadding(7);
        c.setBorderColor(new Color(226, 232, 240));
        c.setBorderWidth(0.5f);
        table.addCell(c);
    }

    private void addNotesSection(Document doc, String notes) throws DocumentException {
        if (notes == null || notes.isBlank()) return;
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(12);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(AMBER_50);
        cell.setBorderColor(AMBER);
        cell.setBorderWidth(1f);
        cell.setBorderWidthLeft(4f);
        cell.setPadding(12);

        Paragraph title = new Paragraph("Doctor's Notes",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, AMBER));
        title.setSpacingAfter(6);
        cell.addElement(title);
        cell.addElement(new Paragraph(notes, FontFactory.getFont(FontFactory.HELVETICA, 10, SLATE_800)));
        table.addCell(cell);
        doc.add(table);
    }

    private void addValidity(Document doc, PrescriptionCreatedEvent event) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        Paragraph valid = new Paragraph("This prescription is valid for 30 days from the date of issue.",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, SLATE_500));
        valid.setAlignment(Element.ALIGN_CENTER);
        doc.add(valid);
    }

    private void addDivider(Document doc) throws DocumentException {
        PdfPTable div = new PdfPTable(1);
        div.setWidthPercentage(100);
        div.setSpacingBefore(4);
        div.setSpacingAfter(10);
        PdfPCell line = new PdfPCell();
        line.setFixedHeight(2f);
        line.setBackgroundColor(GREEN);
        line.setBorder(Rectangle.NO_BORDER);
        div.addCell(line);
        doc.add(div);
    }

    private void addFooter(Document doc) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        PdfPTable div = new PdfPTable(1);
        div.setWidthPercentage(100);
        div.setSpacingBefore(4);
        div.setSpacingAfter(6);
        PdfPCell line = new PdfPCell();
        line.setFixedHeight(1f);
        line.setBackgroundColor(new Color(226, 232, 240));
        line.setBorder(Rectangle.NO_BORDER);
        div.addCell(line);
        doc.add(div);

        Paragraph footer = new Paragraph("HMS Hospital · Auto-generated prescription. For queries, contact your doctor.",
                FontFactory.getFont(FontFactory.HELVETICA, 9, SLATE_500));
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }
}
