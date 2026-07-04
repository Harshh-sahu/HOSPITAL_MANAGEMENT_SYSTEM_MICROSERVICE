package com.EmailService.EmailServiceMS.service;

import com.EmailService.EmailServiceMS.event.SaleCreatedEvent;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PdfInvoiceGenerator {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private static final Color INDIGO      = new Color(79, 70, 229);
    private static final Color INDIGO_LIGHT= new Color(238, 242, 255);
    private static final Color INDIGO_BDR  = new Color(199, 210, 254);
    private static final Color SLATE_800   = new Color(30, 41, 59);
    private static final Color SLATE_500   = new Color(100, 116, 139);
    private static final Color SLATE_100   = new Color(241, 245, 249);
    private static final Color WHITE       = Color.WHITE;
    private static final Color GREEN       = new Color(5, 150, 105);
    private static final Color AMBER       = new Color(217, 119, 6);

    public byte[] generate(SaleCreatedEvent event) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter.getInstance(doc, out);
            doc.open();

            addHeader(doc, event);
            addDivider(doc, INDIGO);
            addBuyerInfo(doc, event);
            addDivider(doc, new Color(226, 232, 240));
            addItemsTable(doc, event.getItems());
            addTotals(doc, event);
            addFooter(doc);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }

    private void addHeader(Document doc, SaleCreatedEvent event) throws DocumentException {
        Paragraph hospital = new Paragraph("HMS Hospital Pharmacy",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, INDIGO));
        hospital.setAlignment(Element.ALIGN_CENTER);
        doc.add(hospital);

        Paragraph sub = new Paragraph("Tax Invoice",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, SLATE_500));
        sub.setAlignment(Element.ALIGN_CENTER);
        sub.setSpacingAfter(6);
        doc.add(sub);

        if (event.getSaleId() != null) {
            PdfPTable badge = new PdfPTable(1);
            badge.setWidthPercentage(40);
            badge.setHorizontalAlignment(Element.ALIGN_CENTER);
            badge.setSpacingBefore(4);
            badge.setSpacingAfter(10);
            PdfPCell cell = new PdfPCell(new Phrase("Invoice  #" + event.getSaleId(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, WHITE)));
            cell.setBackgroundColor(INDIGO);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(9);
            cell.setBorder(Rectangle.NO_BORDER);
            badge.addCell(cell);
            doc.add(badge);
        }
    }

    private void addBuyerInfo(Document doc, SaleCreatedEvent event) throws DocumentException {
        String date = event.getSaleDate() != null ? event.getSaleDate().format(DATE_TIME) : "—";
        addInfoRow(doc, "Billed To", safe(event.getBuyerName()), "Date", date);
        addInfoRow(doc, "Contact", safe(event.getBuyerContact()), "Prescription #",
                event.getPrescriptionId() != null ? String.valueOf(event.getPrescriptionId()) : "—");
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
        wrapper.setBackgroundColor(INDIGO_LIGHT);
        wrapper.setPadding(12);
        wrapper.setBorderColor(INDIGO_BDR);
        wrapper.setBorderWidth(1);
        return wrapper;
    }

    private void addItemsTable(Document doc, List<SaleCreatedEvent.SaleItemInfo> items) throws DocumentException {
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        header.setSpacingBefore(10);
        String count = items != null ? " (" + items.size() + " item" + (items.size() != 1 ? "s" : "") + ")" : "";
        PdfPCell hCell = new PdfPCell(new Phrase("Items Purchased" + count,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, WHITE)));
        hCell.setBackgroundColor(SLATE_800);
        hCell.setPadding(8);
        hCell.setBorder(Rectangle.NO_BORDER);
        header.addCell(hCell);
        doc.add(header);

        if (items == null || items.isEmpty()) {
            PdfPTable empty = new PdfPTable(1);
            empty.setWidthPercentage(100);
            PdfPCell ec = new PdfPCell(new Phrase("No items.",
                    FontFactory.getFont(FontFactory.HELVETICA, 10, SLATE_500)));
            ec.setBackgroundColor(SLATE_100);
            ec.setPadding(10);
            ec.setBorder(Rectangle.NO_BORDER);
            empty.addCell(ec);
            doc.add(empty);
            return;
        }

        PdfPTable cols = new PdfPTable(new float[]{4, 2, 1, 2, 2});
        cols.setWidthPercentage(100);
        String[] colNames = {"Medicine", "Batch No", "Qty", "Unit Price", "Amount"};
        for (String col : colNames) {
            PdfPCell c = new PdfPCell(new Phrase(col, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, WHITE)));
            c.setBackgroundColor(INDIGO);
            c.setPadding(7);
            c.setBorder(Rectangle.NO_BORDER);
            cols.addCell(c);
        }
        doc.add(cols);

        PdfPTable rows = new PdfPTable(new float[]{4, 2, 1, 2, 2});
        rows.setWidthPercentage(100);
        rows.setSpacingAfter(4);
        int i = 0;
        for (SaleCreatedEvent.SaleItemInfo item : items) {
            Color bg = (i % 2 == 0) ? SLATE_100 : WHITE;
            addItemCell(rows, safe(item.getMedicineName()), bg, true);
            addItemCell(rows, safe(item.getBatchNo()), bg, false);
            addItemCell(rows, item.getQuantity() != null ? String.valueOf(item.getQuantity()) : "—", bg, false);
            addItemCell(rows, item.getUnitPrice() != null ? "₹" + String.format("%.2f", item.getUnitPrice()) : "—", bg, false);
            addItemCell(rows, item.getLineTotal() != null ? "₹" + String.format("%.2f", item.getLineTotal()) : "—", bg, false);
            i++;
        }
        doc.add(rows);
    }

    private void addItemCell(PdfPTable table, String text, Color bg, boolean bold) {
        Font f = bold ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, SLATE_800)
                      : FontFactory.getFont(FontFactory.HELVETICA, 9, SLATE_800);
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setBackgroundColor(bg);
        c.setPadding(7);
        c.setBorderColor(new Color(226, 232, 240));
        c.setBorderWidth(0.5f);
        table.addCell(c);
    }

    private void addTotals(Document doc, SaleCreatedEvent event) throws DocumentException {
        doc.add(Chunk.NEWLINE);

        PdfPTable totals = new PdfPTable(new float[]{6, 2});
        totals.setWidthPercentage(60);
        totals.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totals.setSpacingAfter(10);

        // Subtotal = total (no tax breakdown in the simple model)
        addTotalRow(totals, "Subtotal", event.getTotalAmount(), false, SLATE_100);
        addTotalRow(totals, "Total Amount", event.getTotalAmount(), true, INDIGO_LIGHT);

        doc.add(totals);

        Paragraph thankyou = new Paragraph("Thank you for choosing HMS Hospital Pharmacy.",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, GREEN));
        thankyou.setAlignment(Element.ALIGN_CENTER);
        doc.add(thankyou);
    }

    private void addTotalRow(PdfPTable table, String label, Double amount, boolean bold, Color bg) {
        Font f = bold ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, SLATE_800)
                      : FontFactory.getFont(FontFactory.HELVETICA, 10, SLATE_500);
        PdfPCell lc = new PdfPCell(new Phrase(label, f));
        lc.setBackgroundColor(bg);
        lc.setPadding(8);
        lc.setBorderColor(new Color(226, 232, 240));
        lc.setBorderWidth(0.5f);
        table.addCell(lc);

        String val = amount != null ? "₹" + String.format("%.2f", amount) : "—";
        Font vf = bold ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, bold ? INDIGO : SLATE_800)
                       : FontFactory.getFont(FontFactory.HELVETICA, 10, SLATE_800);
        PdfPCell vc = new PdfPCell(new Phrase(val, vf));
        vc.setBackgroundColor(bg);
        vc.setPadding(8);
        vc.setHorizontalAlignment(Element.ALIGN_RIGHT);
        vc.setBorderColor(new Color(226, 232, 240));
        vc.setBorderWidth(0.5f);
        table.addCell(vc);
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
        PdfPTable div = new PdfPTable(1);
        div.setWidthPercentage(100);
        PdfPCell line = new PdfPCell();
        line.setFixedHeight(1f);
        line.setBackgroundColor(new Color(226, 232, 240));
        line.setBorder(Rectangle.NO_BORDER);
        div.addCell(line);
        doc.add(div);

        Paragraph footer = new Paragraph("HMS Hospital · This is a computer-generated invoice. No signature required.",
                FontFactory.getFont(FontFactory.HELVETICA, 9, SLATE_500));
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }
}
