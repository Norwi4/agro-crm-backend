package com.agrocrm.domain.document;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorService {
    private static final Logger log = LoggerFactory.getLogger(PdfGeneratorService.class);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    
    /**
     * Генерирует PDF документ из данных CompanyDocument
     */
    public byte[] generateCompanyDocumentPdf(CompanyDocument document) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document pdfDocument = new Document(PageSize.A4);
            PdfWriter.getInstance(pdfDocument, baos);
            
            pdfDocument.open();
            
            // Заголовок документа
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph(document.getTitle(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            pdfDocument.add(title);
            
            // Информация о документе
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
            
            // Описание
            if (document.getDescription() != null && !document.getDescription().trim().isEmpty()) {
                Paragraph descriptionHeader = new Paragraph("Описание:", headerFont);
                descriptionHeader.setSpacingBefore(15);
                pdfDocument.add(descriptionHeader);
                
                Paragraph description = new Paragraph(document.getDescription(), normalFont);
                description.setSpacingAfter(15);
                pdfDocument.add(description);
            }
            
            // Тип документа
            Paragraph typeHeader = new Paragraph("Тип документа:", headerFont);
            typeHeader.setSpacingBefore(10);
            pdfDocument.add(typeHeader);
            
            Paragraph type = new Paragraph(document.getDocumentType(), normalFont);
            type.setSpacingAfter(10);
            pdfDocument.add(type);
            
            // Статус
            Paragraph statusHeader = new Paragraph("Статус:", headerFont);
            statusHeader.setSpacingBefore(10);
            pdfDocument.add(statusHeader);
            
            Paragraph status = new Paragraph(document.getStatus(), normalFont);
            status.setSpacingAfter(10);
            pdfDocument.add(status);
            
            // Версия
            if (document.getVersion() != null) {
                Paragraph versionHeader = new Paragraph("Версия:", headerFont);
                versionHeader.setSpacingBefore(10);
                pdfDocument.add(versionHeader);
                
                Paragraph version = new Paragraph(String.valueOf(document.getVersion()), normalFont);
                version.setSpacingAfter(10);
                pdfDocument.add(version);
            }
            
            // Дата создания
            if (document.getCreatedAt() != null) {
                Paragraph createdHeader = new Paragraph("Дата создания:", headerFont);
                createdHeader.setSpacingBefore(10);
                pdfDocument.add(createdHeader);
                
                Paragraph created = new Paragraph(document.getCreatedAt().format(DATE_FORMATTER), normalFont);
                created.setSpacingAfter(10);
                pdfDocument.add(created);
            }
            
            // Дата истечения
            if (document.getExpiresAt() != null) {
                Paragraph expiresHeader = new Paragraph("Дата истечения:", headerFont);
                expiresHeader.setSpacingBefore(10);
                pdfDocument.add(expiresHeader);
                
                Paragraph expires = new Paragraph(document.getExpiresAt().format(DATE_FORMATTER), normalFont);
                expires.setSpacingAfter(10);
                pdfDocument.add(expires);
            }
            
            // ID департамента
            if (document.getDepartmentId() != null) {
                Paragraph deptHeader = new Paragraph("ID департамента:", headerFont);
                deptHeader.setSpacingBefore(10);
                pdfDocument.add(deptHeader);
                
                Paragraph dept = new Paragraph(String.valueOf(document.getDepartmentId()), normalFont);
                dept.setSpacingAfter(10);
                pdfDocument.add(dept);
            }
            
            // ID документа
            Paragraph idHeader = new Paragraph("ID документа:", headerFont);
            idHeader.setSpacingBefore(10);
            pdfDocument.add(idHeader);
            
            Paragraph id = new Paragraph(document.getId().toString(), normalFont);
            id.setSpacingAfter(20);
            pdfDocument.add(id);
            
            // Подпись
            Paragraph signature = new Paragraph("Документ сгенерирован автоматически", new Font(Font.HELVETICA, 10, Font.ITALIC));
            signature.setAlignment(Element.ALIGN_CENTER);
            signature.setSpacingBefore(30);
            pdfDocument.add(signature);
            
            pdfDocument.close();
            
            byte[] pdfBytes = baos.toByteArray();
            log.info("Generated PDF for document: id={}, title={}, size={} bytes", 
                    document.getId(), document.getTitle(), pdfBytes.length);
            
            return pdfBytes;
        } catch (DocumentException e) {
            log.error("Failed to generate PDF for document: id={}", document.getId(), e);
            throw new IOException("Ошибка при генерации PDF: " + e.getMessage());
        }
    }
}
