package reports;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Escribe los dos reportes en disco. Las listas llegan aqui ya ordenadas,
 * asi que esta clase no decide nada sobre el contenido.
 *
 * @author Grupo de trabajo - Conceptos Fundamentales de Programacion
 */
public final class ReportWriter {

    private static final int MONEY_SCALE = 2;

    /** Clase de utilidad: no se instancia. */
    private ReportWriter() {
    }

    /**
     * Escribe el reporte de vendedores. Cada linea queda como NombreCompleto;DineroRecaudado.
     *
     * @param salesmenReports vendedores ya ordenados
     * @return donde quedo el archivo
     * @throws IOException si no se puede escribir
     */
    public static Path writeSalesmenReport(List<SalesmanReport> salesmenReports) throws IOException {
        Path reportPath = AppFiles.path(AppFiles.SALESMEN_REPORT_FILE);
        BufferedWriter writer = Files.newBufferedWriter(reportPath, AppFiles.FILE_CHARSET);
        try {
            for (SalesmanReport report : salesmenReports) {
                writer.write(report.getSalesman().getFullName());
                writer.write(AppFiles.FIELD_SEPARATOR);
                writer.write(formatMoney(report.getTotalCollected()));
                writer.newLine();
            }
        } finally {
            writer.close();
        }
        return reportPath;
    }

    /**
     * Escribe el reporte de productos. Cada linea queda como NombreProducto;PrecioPorUnidad,
     * y la cantidad vendida es lo que define el orden de las lineas.
     *
     * @param productReports productos ya ordenados
     * @return donde quedo el archivo
     * @throws IOException si no se puede escribir
     */
    public static Path writeProductsReport(List<ProductReport> productReports) throws IOException {
        Path reportPath = AppFiles.path(AppFiles.PRODUCTS_REPORT_FILE);
        BufferedWriter writer = Files.newBufferedWriter(reportPath, AppFiles.FILE_CHARSET);
        try {
            for (ProductReport report : productReports) {
                writer.write(report.getProduct().getName());
                writer.write(AppFiles.FIELD_SEPARATOR);
                writer.write(formatMoney(report.getProduct().getUnitPrice()));
                writer.newLine();
            }
        } finally {
            writer.close();
        }
        return reportPath;
    }

    /**
     * Deja el valor con dos decimales y punto decimal. No usamos formato de moneda
     * porque mete puntos de miles y un total de 154.024.500 se leeria como tres columnas.
     *
     * @param amount valor a escribir
     * @return el valor listo para el archivo
     */
    private static String formatMoney(BigDecimal amount) {
        return amount.setScale(MONEY_SCALE, RoundingMode.HALF_UP).toPlainString();
    }
}
