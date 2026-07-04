package com.EmailService.EmailServiceMS.service;

import com.EmailService.EmailServiceMS.event.DoctorOnboardedEvent;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Component
public class PdfOnboardingLetterGenerator {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    private static final Color BLUE       = new Color(37, 99, 235);
    private static final Color BLUE_LIGHT = new Color(239, 246, 255);
    private static final Color BLUE_BDR   = new Color(191, 219, 254);
    private static final Color SLATE_800  = new Color(30, 41, 59);
    private static final Color SLATE_600  = new Color(71, 85, 105);
    private static final Color SLATE_400  = new Color(148, 163, 184);
    private static final Color SLATE_100  = new Color(241, 245, 249);
    private static final Color WHITE      = Color.WHITE;
    private static final Color GREEN      = new Color(5, 150, 105);

    public byte[] generate(DoctorOnboardedEvent event) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 72, 72, 80, 72);
            PdfWriter.getInstance(doc, out);
            doc.open();

            addLetterhead(doc, event);
            addSalutation(doc, event);
            addWelcomeParagraph(doc, event);
            addCredentialsTable(doc, event);
            addAdvisory(doc);
            addSignature(doc);
            addFooter(doc);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate onboarding letter PDF", e);
        }
    }

    private void addLetterhead(Document doc, DoctorOnboardedEvent event) throws DocumentException {
        String dateStr = event.getOnboardedAt() != null ? event.getOnboardedAt().format(DATE) : "";

        // Hospital name top left, date top right
        PdfPTable header = new PdfPTable(new float[]{3, 1});
        header.setWidthPercentage(100);
        header.setSpacingAfter(20);

        PdfPCell left = new PdfPCell();
        left.setBorder(Rectangle.NO_BORDER);
        Paragraph name = new Paragraph("HMS Hospital", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BLUE));
        Paragraph tagline = new Paragraph("Human Resource Department",
                FontFactory.getFont(FontFactory.HELVETICA, 10, SLATE_400));
        left.addElement(name);
        left.addElement(tagline);
        header.addCell(left);

        PdfPCell right = new PdfPCell(new Phrase(dateStr,
                FontFactory.getFont(FontFactory.HELVETICA, 10, SLATE_600)));
        right.setBorder(Rectangle.NO_BORDER);
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);
        right.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(right);
        doc.add(header);

        // Divider
        PdfPTable div = new PdfPTable(1);
        div.setWidthPercentage(100);
        div.setSpacingAfter(20);
        PdfPCell line = new PdfPCell();
        line.setFixedHeight(3f);
        line.setBackgroundColor(BLUE);
        line.setBorder(Rectangle.NO_BORDER);
        div.addCell(line);
        doc.add(div);

        // Title
        Paragraph title = new Paragraph("APPOINTMENT LETTER",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, SLATE_800));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        doc.add(title);
    }

    private void addSalutation(Document doc, DoctorOnboardedEvent event) throws DocumentException {
        Paragraph salutation = new Paragraph(
                "Dear Dr. " + safe(event.getName()) + ",",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, SLATE_800));
        salutation.setSpacingAfter(14);
        doc.add(salutation);
    }

    private void addWelcomeParagraph(Document doc, DoctorOnboardedEvent event) throws DocumentException {
        String spec = event.getSpecialization() != null ? event.getSpecialization() : "your respective department";
        String dept = event.getDepartment() != null ? event.getDepartment() : "our hospital";
        String date = event.getOnboardedAt() != null ? event.getOnboardedAt().format(DATE) : "today";

        Paragraph para = new Paragraph(
                "We are delighted to welcome you to HMS Hospital. This letter formally confirms your appointment "
                + "as a Consultant in " + spec + " under the " + dept + " department, effective " + date + ".\n\n"
                + "Your expertise and experience will be a great asset to our team. We look forward to your "
                + "contributions towards excellent patient care and medical excellence.",
                FontFactory.getFont(FontFactory.HELVETICA, 11, SLATE_600));
        para.setLeading(18);
        para.setSpacingAfter(20);
        doc.add(para);
    }

    private void addCredentialsTable(Document doc, DoctorOnboardedEvent event) throws DocumentException {
        Paragraph sectionTitle = new Paragraph("Credential Details",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, SLATE_800));
        sectionTitle.setSpacingAfter(8);
        doc.add(sectionTitle);

        PdfPTable table = new PdfPTable(new float[]{2, 3});
        table.setWidthPercentage(80);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setSpacingAfter(20);

        addCredRow(table, "Doctor ID", event.getDoctorId() != null ? String.valueOf(event.getDoctorId()) : "—", true);
        addCredRow(table, "Full Name", "Dr. " + safe(event.getName()), false);
        addCredRow(table, "License No.", safe(event.getLicenseNo()), true);
        addCredRow(table, "Specialization", safe(event.getSpecialization()), false);
        addCredRow(table, "Department", safe(event.getDepartment()), true);
        addCredRow(table, "Experience", event.getTotalExp() != null ? event.getTotalExp() + " year(s)" : "—", false);
        addCredRow(table, "Contact", safe(event.getPhone()), true);
        addCredRow(table, "Email", safe(event.getEmail()), false);

        doc.add(table);
    }

    private void addCredRow(PdfPTable table, String label, String value, boolean shaded) {
        Color bg = shaded ? SLATE_100 : WHITE;
        Font lf = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, SLATE_600);
        Font vf = FontFactory.getFont(FontFactory.HELVETICA, 10, SLATE_800);

        PdfPCell lc = new PdfPCell(new Phrase(label, lf));
        lc.setBackgroundColor(bg);
        lc.setPadding(9);
        lc.setBorderColor(new Color(226, 232, 240));
        lc.setBorderWidth(0.5f);
        table.addCell(lc);

        PdfPCell vc = new PdfPCell(new Phrase(value, vf));
        vc.setBackgroundColor(bg);
        vc.setPadding(9);
        vc.setBorderColor(new Color(226, 232, 240));
        vc.setBorderWidth(0.5f);
        table.addCell(vc);
    }

    private void addAdvisory(Document doc) throws DocumentException {
        PdfPTable box = new PdfPTable(1);
        box.setWidthPercentage(100);
        box.setSpacingAfter(20);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BLUE_LIGHT);
        cell.setBorderColor(BLUE_BDR);
        cell.setBorderWidth(1f);
        cell.setBorderWidthLeft(4f);
        cell.setPadding(14);

        Paragraph title = new Paragraph("Important Notes",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BLUE));
        title.setSpacingAfter(8);
        cell.addElement(title);

        String[] notes = {
                "Please report to the HR department on your first day with original credential documents.",
                "Your ID card and access credentials will be issued upon document verification.",
                "Review and acknowledge the Hospital Code of Conduct before commencing duty.",
                "For queries, contact HR at hr@hmshospital.com or the Medical Director's office."
        };
        for (String note : notes) {
            Paragraph p = new Paragraph("• " + note,
                    FontFactory.getFont(FontFactory.HELVETICA, 10, SLATE_800));
            p.setLeading(16);
            cell.addElement(p);
        }
        box.addCell(cell);
        doc.add(box);
    }

    private void addSignature(Document doc) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        Paragraph closing = new Paragraph("Yours Sincerely,",
                FontFactory.getFont(FontFactory.HELVETICA, 11, SLATE_600));
        closing.setSpacingAfter(30);
        doc.add(closing);

        PdfPTable sig = new PdfPTable(1);
        sig.setWidthPercentage(35);
        sig.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell nameLine = new PdfPCell(new Phrase("Medical Director",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, SLATE_800)));
        nameLine.setBorder(Rectangle.TOP);
        nameLine.setBorderColor(SLATE_400);
        nameLine.setBorderWidth(1f);
        nameLine.setPaddingTop(8);
        nameLine.setPaddingBottom(2);
        sig.addCell(nameLine);

        PdfPCell hospLine = new PdfPCell(new Phrase("HMS Hospital",
                FontFactory.getFont(FontFactory.HELVETICA, 10, SLATE_600)));
        hospLine.setBorder(Rectangle.NO_BORDER);
        sig.addCell(hospLine);

        doc.add(sig);
    }

    private void addFooter(Document doc) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        PdfPTable div = new PdfPTable(1);
        div.setWidthPercentage(100);
        PdfPCell line = new PdfPCell();
        line.setFixedHeight(1f);
        line.setBackgroundColor(new Color(226, 232, 240));
        line.setBorder(Rectangle.NO_BORDER);
        div.addCell(line);
        doc.add(div);

        Paragraph footer = new Paragraph("HMS Hospital · This is a system-generated letter. No physical signature required.",
                FontFactory.getFont(FontFactory.HELVETICA, 9, SLATE_400));
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }
}
