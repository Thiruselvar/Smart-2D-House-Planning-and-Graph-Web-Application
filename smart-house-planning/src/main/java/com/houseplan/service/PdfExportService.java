package com.houseplan.service;

import com.houseplan.model.HousePlan;
import com.houseplan.model.Room;
import com.houseplan.model.room.Exportable;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

@Service
public class PdfExportService implements Exportable {

    public byte[] exportPlan(HousePlan plan) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            Paragraph title = new Paragraph(plan.getPlanName() != null ? plan.getPlanName() : "House Plan", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(12);
            document.add(title);

            Paragraph meta = new Paragraph(String.format(
                    "Plot: %.1f m x %.1f m | Bedrooms: %d | Bathrooms: %d | Built-up: %.1f sq.m | Free: %.1f sq.m",
                    plan.getPlotWidth(), plan.getPlotHeight(), plan.getBedrooms(), plan.getBathrooms(),
                    plan.getTotalBuiltUpArea() != null ? plan.getTotalBuiltUpArea() : 0,
                    plan.getFreeArea() != null ? plan.getFreeArea() : 0), normalFont);
            meta.setSpacingAfter(16);
            document.add(meta);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            addHeader(table, "Room", headerFont);
            addHeader(table, "X (m)", headerFont);
            addHeader(table, "Y (m)", headerFont);
            addHeader(table, "W x H (m)", headerFont);
            addHeader(table, "Area (sq.m)", headerFont);

            for (Room room : plan.getRooms()) {
                table.addCell(cell(room.getRoomName(), normalFont));
                table.addCell(cell(String.format("%.1f", room.getXPosition()), normalFont));
                table.addCell(cell(String.format("%.1f", room.getYPosition()), normalFont));
                table.addCell(cell(String.format("%.1f x %.1f", room.getWidth(), room.getHeight()), normalFont));
                table.addCell(cell(String.format("%.1f", room.getArea()), normalFont));
            }
            document.add(table);

            Paragraph note = new Paragraph(
                    "Top-view 2D layout — open the plan editor for graphical export as PNG.",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.GRAY));
            note.setSpacingBefore(20);
            document.add(note);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF export failed: " + e.getMessage(), e);
        }
    }

    private void addHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new BaseColor(230, 240, 250));
        cell.setPadding(6);
        table.addCell(cell);
    }

    private PdfPCell cell(String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setPadding(5);
        return c;
    }

    @Override
    public String getExportDescription() {
        return "House plan PDF report";
    }

    @Override
    public byte[] toPdfBytes() {
        return new byte[0];
    }
}
