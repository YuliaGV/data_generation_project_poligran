package reports;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Lee los archivos planos y genera los reportes: vendedores por dinero recaudado,
 * productos por cantidad vendida, la bitacora de errores y la copia serializada.
 *
 * No pide nada por pantalla.
 *
 * @author Grupo de trabajo - Conceptos Fundamentales de Programacion
 */
public class main {

    /**
     * Carga los catalogos, busca los archivos de ventas, saca las cuentas y escribe
     * todo. Si falta un catalogo se detiene con un mensaje, porque sin esos datos no
     * hay reporte posible.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        System.out.println("=== Generacion de reportes de ventas ===");
        ValidationLog validationLog = new ValidationLog();
        try {
            Map<String, Product> products = DataLoader.loadProducts(validationLog);
            Map<String, Salesman> salesmen = DataLoader.loadSalesmen(validationLog);
            List<Path> salesFiles = AppFiles.listSalesFiles();

            // No es un error, pero casi siempre significa que falto correr GenerateInfoFiles.
            if (salesFiles.isEmpty()) {
                validationLog.addIssue(AppFiles.DATA_DIRECTORY,
                        "No se encontro ningun archivo de ventas con el prefijo '"
                                + AppFiles.SALES_FILE_PREFIX + "'.");
            }

            System.out.println("Productos cargados: " + products.size());
            System.out.println("Vendedores cargados: " + salesmen.size());
            System.out.println("Archivos de ventas encontrados: " + salesFiles.size());

            ReportBundle reportBundle =
                    SalesProcessor.process(salesFiles, products, salesmen, validationLog);

            Path salesmenReportPath = ReportWriter.writeSalesmenReport(reportBundle.getSalesmenReports());
            Path productsReportPath = ReportWriter.writeProductsReport(reportBundle.getProductReports());
            Path serializedPath = AppFiles.path(AppFiles.SERIALIZED_REPORT_FILE);
            SerializationService.save(reportBundle, serializedPath);
            validationLog.writeTo(AppFiles.path(AppFiles.VALIDATION_LOG_FILE));

            printSummary(reportBundle);
            verifySerializedCopy(reportBundle, serializedPath);

            System.out.println();
            System.out.println("Archivo generado: " + salesmenReportPath.getFileName());
            System.out.println("Archivo generado: " + productsReportPath.getFileName());
            System.out.println("Archivo generado: " + serializedPath.getFileName());
            System.out.println("Archivo generado: " + AppFiles.VALIDATION_LOG_FILE
                    + " (" + validationLog.size() + " inconsistencias).");
            System.out.println();
            System.out.println("Proceso finalizado con exito.");
        } catch (IOException fileError) {
            System.err.println("ERROR: no fue posible generar los reportes.");
            System.err.println("Detalle: " + fileError.getMessage());
            System.exit(1);
        } catch (RuntimeException unexpectedError) {
            System.err.println("ERROR inesperado durante la generacion de los reportes.");
            System.err.println("Detalle: " + unexpectedError.getMessage());
            System.exit(1);
        }
    }

    /**
     * Muestra un resumen en pantalla para ver si las cifras tienen sentido sin
     * tener que abrir los CSV.
     *
     * @param reportBundle los reportes terminados
     */
    private static void printSummary(ReportBundle reportBundle) {
        List<SalesmanReport> salesmenReports = reportBundle.getSalesmenReports();
        List<ProductReport> productReports = reportBundle.getProductReports();

        BigDecimal grandTotal = BigDecimal.ZERO;
        long totalUnits = 0L;
        for (SalesmanReport report : salesmenReports) {
            grandTotal = grandTotal.add(report.getTotalCollected());
            totalUnits += report.getTotalUnits();
        }

        System.out.println();
        System.out.println("Unidades vendidas en total: " + totalUnits);
        System.out.println("Dinero recaudado en total: " + grandTotal.toPlainString());
        System.out.println("Productos distintos vendidos: " + productReports.size());
        if (!salesmenReports.isEmpty()) {
            // La lista ya viene ordenada, asi que el primero es el mejor vendedor.
            SalesmanReport best = salesmenReports.get(0);
            System.out.println("Mejor vendedor: " + best.getSalesman().getFullName()
                    + " con " + best.getTotalCollected().toPlainString()
                    + " en " + best.getProcessedFiles() + " archivo(s) de ventas.");
        }
    }

    /**
     * Vuelve a leer el archivo serializado y comprueba que quedo completo. Si algo
     * falla solo se avisa: los CSV ya estan escritos y siguen sirviendo.
     *
     * @param reportBundle   lo que hay en memoria
     * @param serializedPath el archivo recien escrito
     */
    private static void verifySerializedCopy(ReportBundle reportBundle, Path serializedPath) {
        try {
            ReportBundle restored = SerializationService.load(serializedPath);
            boolean sameSalesmen =
                    restored.getSalesmenReports().size() == reportBundle.getSalesmenReports().size();
            boolean sameProducts =
                    restored.getProductReports().size() == reportBundle.getProductReports().size();
            if (sameSalesmen && sameProducts) {
                System.out.println("Verificacion de serializacion: correcta ("
                        + restored.getSalesmenReports().size() + " vendedores, "
                        + restored.getProductReports().size() + " productos).");
            } else {
                System.out.println("ADVERTENCIA: el archivo serializado no coincide con el consolidado.");
            }
        } catch (IOException readError) {
            System.out.println("ADVERTENCIA: no fue posible releer el archivo serializado. "
                    + readError.getMessage());
        } catch (ClassNotFoundException classError) {
            System.out.println("ADVERTENCIA: el archivo serializado no corresponde a este programa. "
                    + classError.getMessage());
        }
    }
}
