package com.EmailService.EmailServiceMS.service;

import com.EmailService.EmailServiceMS.event.AppointmentCreatedEvent;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Component
public class PdfAppointmentGenerator {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private static final Color TEAL       = new Color(13, 148, 136);
    private static final Color TEAL_LIGHT = new Color(240, 253, 250);
    private static final Color TEAL_BORDER= new Color(204, 251, 241);
    private static final Color SLATE_800  = new Color(30, 41, 59);
    private static final Color SLATE_500  = new Color(100, 116, 139);
    private static final Color SLATE_100  = new Color(241, 245, 249);
    private static final Color WHITE      = Color.WHITE;
    private static final Color AMBER_50   = new Color(255, 251, 235);
    private static final Color AMBER      = new Color(217, 119, 6);

    public byte[] generate(AppointmentCreatedEvent event) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter.getInstance(doc, out);
            doc.open();

            addHeader(doc, event);
            addDivider(doc, TEAL);
            addInfoGrid(doc, event);
            addDivider(doc, TEAL);
            addAdvisory(doc);
            addFooter(doc);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate appointment confirmation PDF", e);
        }
    }

    private void addHeader(Document doc, AppointmentCreatedEvent event) throws DocumentException {
        Paragraph hospital = new Paragraph("HMS Hospital",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, TEAL));
        hospital.setAlignment(Element.ALIGN_CENTER);
        doc.add(hospital);

        Paragraph subtitle = new Paragraph("Appointment Confirmation Slip",
                FontFactory.getFont(FontFactory.HELVETICA, 12, SLATE_500));
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(6);
        doc.add(subtitle);

        // Appointment ID badge
        if (event.getAppointmentId() != null) {
            PdfPTable badge = new PdfPTable(1);
            badge.setWidthPercentage(45);
            badge.setHorizontalAlignment(Element.ALIGN_CENTER);
            badge.setSpacingBefore(6);
            badge.setSpacingAfter(10);
            PdfPCell cell = new PdfPCell(new Phrase("Appointment  #" + event.getAppointmentId(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, WHITE)));
            cell.setBackgroundColor(TEAL);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(9);
            cell.setBorder(Rectangle.NO_BORDER);
            badge.addCell(cell);
            doc.add(badge);
        }
    }

    private void addInfoGrid(Document doc, AppointmentCreatedEvent event) throws DocumentException {
        String when = event.getAppointmentTime() != null
                ? event.getAppointmentTime().format(DATE_TIME) : "—";

        addInfoRow(doc, "Patient Name", safe(event.getPatientName()),
                        "Doctor",       "Dr. " + safe(event.getDoctorName()));
        addInfoRow(doc, "Date & Time",  when,
                        "Reason",       safe(event.getReason()));
        addInfoRow(doc, "Status",       "CONFIRMED",
                        "Location",     "HMS Hospital, OPD");
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
        wrapper.setBackgroundColor(TEAL_LIGHT);
        wrapper.setPadding(12);
        wrapper.setBorderColor(TEAL_BORDER);
        wrapper.setBorderWidth(1);
        return wrapper;
    }

    private void addAdvisory(Document doc) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(AMBER_50);
        cell.setBorderColor(AMBER);
        cell.setBorderWidth(1f);
        cell.setPadding(14);
        cell.setBorder(Rectangle.BOX);

        Paragraph title = new Paragraph("Patient Advisory",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, AMBER));
        title.setSpacingAfter(6);
        cell.addElement(title);

        String[] points = {
            "Please arrive at least 15 minutes before your scheduled time.",
            "Carry a valid photo ID and this confirmation slip.",
            "Bring any previous reports or prescriptions relevant to your visit.",
            "For cancellations, please inform us at least 2 hours in advance."
        };
        for (String point : points) {
            Paragraph p = new Paragraph("•  " + point,
                    FontFactory.getFont(FontFactory.HELVETICA, 10, SLATE_800));
            p.setSpacingAfter(3);
            cell.addElement(p);
        }
        table.addCell(cell);
        doc.add(table);
    }

    private void addDivider(Document doc, Color color) throws DocumentException {
        PdfPTable div = new PdfPTable(1);
        div.setWidthPercentage(100);
        div.setSpacingBefore(4);
        div.setSpacingAfter(10);
        PdfPCell line = new PdfPCell();
        line.setFixedHeight(2f);
        line.setBackgroundColor(color);
        line.setBorder(Rectangle.NO_BORDER);
        div.addCell(line);
        doc.add(div);
    }

    private void addFooter(Document doc) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        addDivider(doc, new Color(226, 232, 240));
        Paragraph footer = new Paragraph(
                "HMS Hospital · This is a system-generated confirmation slip.",
                FontFactory.getFont(FontFactory.HELVETICA, 9, SLATE_500));
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }
}
