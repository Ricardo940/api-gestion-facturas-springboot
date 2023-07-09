package com.api.gestion.service.impl;

import com.api.gestion.dao.FacturaDAO;
import com.api.gestion.pojo.Factura;
import com.api.gestion.security.jwt.JwtFilter;
import com.api.gestion.service.FacturaService;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Service
public class FacturaServiceImpl implements FacturaService {

    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private FacturaDAO facturaDAO;

    @Override
    public ResponseEntity<String> generateReporte(Map<String, String> requestaMap) {
        log.info("Generando reporte");
        try{
            String fileName;
            return null;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void serRectangleInPdf(Document document) throws DocumentException {
        log.info("Set rectanguloInPdf");
        Rectangle rectangle = new Rectangle(577, 825, 18, 15);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBorderColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);
        document.add(rectangle);
    }

    private Font getFont(String type){
        log.info("Dentro de getFont");
        switch (type){
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            default:
                return new Font();
        }
    }

    private void addRows(PdfPTable pdfPTable, Map<String, String> data){
        log.info("Generando files");
        pdfPTable.addCell(data.get("nombre"));
        pdfPTable.addCell(data.get("categoria"));
        pdfPTable.addCell(data.get("cantidad"));
        pdfPTable.addCell(data.get("precio"));
        pdfPTable.addCell(data.get("total"));
    }

    private void addTableHeader(PdfPTable pdfPTable){
        log.info("Generando header");
        Stream.of("Nombre", "Categoria", "Cantidad", "Precio", "Sub total")
                .forEach(columnTitle ->{
                    PdfPCell pdfPCell = new PdfPCell();
                    pdfPCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    pdfPCell.setBorderWidth(2);
                    pdfPCell.setPhrase(new Phrase(columnTitle));
                    pdfPCell.setBackgroundColor(BaseColor.YELLOW);
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPTable.addCell(pdfPCell);
                });
    }

    private boolean validateRequestMap(Map<String, String> requestMap){
        return requestMap.containsKey("nombre") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("numeroContacto") &&
                requestMap.containsKey("metodoPago") &&
                requestMap.containsKey("productoDetalles") &&
                requestMap.containsKey("total");
    }

    private void insertarFactura(Map<String, Object> requestMap){
        try{
            Factura factura = new Factura();
            factura.setUuid((String) requestMap.get("uuid"));
            factura.setNombre((String) requestMap.get("nombre"));
            factura.setEmail((String) requestMap.get("email"));
            factura.setNumeroContacto((String) requestMap.get("numeroContacto"));
            factura.setMetodoPago((String) requestMap.get("metodoPago"));
            factura.setTotal((Double) requestMap.get("total"));
            factura.setProductoDetalles((String) requestMap.get("productoDetalle"));
            factura.setCreateBy(jwtFilter.getCurrentUser());
            facturaDAO.save(factura);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
