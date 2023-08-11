package com.api.gestion.service.impl;

import com.api.gestion.constantes.FacturaConstantes;
import com.api.gestion.dao.FacturaDAO;
import com.api.gestion.pojo.Factura;
import com.api.gestion.security.jwt.JwtFilter;
import com.api.gestion.service.FacturaService;
import com.api.gestion.util.FacturaUtils;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class FacturaServiceImpl implements FacturaService {

    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private FacturaDAO facturaDAO;

    @Override
    public ResponseEntity<String> generateReporte(Map<String, Object> requestaMap) {
        log.info("Generando reporte");
        try{
            String fileName;
            if(validateRequestMap(requestaMap)) {
                if (requestaMap.containsKey("isGenerate") && !(Boolean) requestaMap.get("isGenerate")) {
                    fileName = (String) requestaMap.get("uuid");
                } else {
                    fileName = FacturaUtils.getUuid();
                    requestaMap.put("uuid", fileName);
                    insertarFactura(requestaMap);
                }
                String data = "Nombre : " + requestaMap.get("nombre") + "\nNumero de contacto : " + requestaMap.get("numeroContacto") +
                        "\n" + "Email : " + requestaMap.get("email") + "\n" + "Metodo de pago : " + requestaMap.get("metodoPago");

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(FacturaConstantes.STORAGE_LOCATION + "\\" + fileName + ".pdf"));

                document.open();
                serRectangleInPdf(document);

                Paragraph paragraphHeader = new Paragraph("Gestión de categorias y productos\n", getFont("Header"));
                paragraphHeader.setAlignment(Element.ALIGN_CENTER);
                document.add(paragraphHeader);

                PdfPTable pdfPTable = new PdfPTable(5);
                pdfPTable.setWidthPercentage(100);
                addTableHeader(pdfPTable);
                //JSONArray jsonArray = new JSONArray(requestaMap.get("productoDetalles").toString());
                JSONArray jsonArray = FacturaUtils.getJsonArrayFromString((String) requestaMap.get("productoDetalles"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    addRows(pdfPTable, FacturaUtils.getMapFromJson(jsonArray.get(i).toString()));
                }
                document.add(pdfPTable);

                Paragraph footer = new Paragraph("Total : " + requestaMap.get("total") + "\n" +
                        "Gracias por visitarnos, vuelva pronto!!", getFont("Data"));
                document.add(footer);

                document.close();
                return new ResponseEntity<>("{\"uuid\":\""+ fileName +"\"}", HttpStatus.OK);
            }
            return FacturaUtils.getResponseEntity("Datos requeridos no encontrados", HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Factura>> getFacturas() {
        List<Factura> facturas = new ArrayList<>();
        if(jwtFilter.isAdmin()){
            facturas = facturaDAO.getFacturas();
        }else {
            facturas = facturaDAO.getFacturasByUsername(jwtFilter.getCurrentUser());
        }
        return new ResponseEntity<>(facturas, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        log.info("Dentro de getPDF: requestMap{}",requestMap);
        try{
            byte[] byteArray = new byte[0];
            if(!requestMap.containsKey("uuid") && validateRequestMap(requestMap)){
                return new ResponseEntity<>(byteArray, HttpStatus.BAD_REQUEST);
            }

            String filePath = FacturaConstantes.STORAGE_LOCATION + "\\" + (String) requestMap.get("uuid")+".pdf";

            if(FacturaUtils.isFileExist(filePath)){
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            }else {
                requestMap.put("isGenerate",false);
                generateReporte(requestMap);
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            }

        }catch (Exception e){
            e.printStackTrace();

        }
        return null;
    }

    @Override
    public ResponseEntity<String> deleteFactura(Integer id) {
        try{
            Optional optional = facturaDAO.findById(id);
            if(!optional.isEmpty()){
                facturaDAO.deleteById(id);
                return FacturaUtils.getResponseEntity("Factura eliminada", HttpStatus.OK);
            }
            return FacturaUtils.getResponseEntity("No existe la factura con ese ID", HttpStatus.NOT_FOUND);
        }catch (Exception e){
            e.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private byte[] getByteArray(String filePath) throws IOException{
        File initialFile = new File(filePath);
        InputStream inputStream = new FileInputStream(initialFile);
        byte[] byteArray = IOUtils.toByteArray(inputStream);
        inputStream.close();
        return byteArray;
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

    private void addRows(PdfPTable pdfPTable, Map<String, Object> data){
        log.info("Generando files");
        pdfPTable.addCell((String) data.get("nombre"));
        pdfPTable.addCell((String) data.get("categoria"));
        pdfPTable.addCell((String) data.get("cantidad"));
        pdfPTable.addCell(String.valueOf( data.get("precio")));
        pdfPTable.addCell(String.valueOf( data.get("total")));
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

    private boolean validateRequestMap(Map<String, Object> requestMap){
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
            factura.setTotal(Double.parseDouble((String) requestMap.get("total")));
            factura.setProductoDetalles((String) requestMap.get("productoDetalles"));
            factura.setCreateBy(jwtFilter.getCurrentUser());
            facturaDAO.save(factura);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
