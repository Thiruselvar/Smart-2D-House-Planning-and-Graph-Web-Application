package com.houseplan.model.room;

/**
 * Interface for plan elements that can be exported to PDF/image.
 */
public interface Exportable {
    String getExportDescription();
    byte[] toPdfBytes();
}
