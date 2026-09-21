package reports;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Recorre los archivos de ventas, suma lo de cada vendedor y lo de cada producto,
 * y entrega las dos listas ordenadas.
 *
 * Las ventas se le cuentan a quien aparezca en la primera linea del archivo y no a
 * quien diga el nombre del archivo. Por eso un vendedor puede tener varios archivos:
 * si traen el mismo documento, se suman en la misma cuenta.
 *
 * @author Grupo de trabajo - Conceptos Fundamentales de Programacion
 */
public final class SalesProcessor {

    /** La primera linea trae tipo y numero de documento. */
    private static final int HEADER_FIELD_COUNT = 2;

    /** Cada linea de venta trae, minimo, codigo de producto y cantidad. */
    private static final int SALE_FIELD_COUNT = 2;

    /** Clase de utilidad: no se instancia. */
    private SalesProcessor() {
    }

    /**
     * Lee todos los archivos de ventas y arma los dos reportes. Antes de leer nada
     * se abre una cuenta en ceros para cada vendedor y cada producto, para no tener
     * que preguntar despues si la cuenta ya existia.
     *
     * @param salesFiles    archivos de ventas
     * @param products      catalogo de productos
     * @param salesmen      vendedores
     * @param validationLog donde se anota lo que venga mal
     * @return los dos reportes, ordenados
     * @throws IOException si algun archivo no se puede leer
     */
    public static ReportBundle process(List<Path> salesFiles,
                                       Map<String, Product> products,
                                       Map<String, Salesman> salesmen,
                                       ValidationLog validationLog) throws IOException {

        Map<String, SalesmanReport> salesmenReports = new LinkedHashMap<String, SalesmanReport>();
        Map<String, ProductReport> productReports = new LinkedHashMap<String, ProductReport>();

        for (Map.Entry<String, Salesman> entry : salesmen.entrySet()) {
            salesmenReports.put(entry.getKey(), new SalesmanReport(entry.getValue()));
        }
        for (Map.Entry<String, Product> entry : products.entrySet()) {
            productReports.put(entry.getKey(), new ProductReport(entry.getValue()));
        }

        for (Path salesFile : salesFiles) {
            processSingleFile(salesFile, products, salesmenReports, productReports, validationLog);
        }

        return new ReportBundle(sortSalesmenReports(salesmenReports.values()),
                sortProductReports(productReports.values()));
    }

    /**
     * Lee un archivo de ventas y suma sus lineas buenas.
     *
     * @param salesFile       archivo a leer
     * @param products        catalogo de productos
     * @param salesmenReports cuentas de los vendedores
     * @param productReports  cuentas de los productos
     * @param validationLog   donde se anota lo que venga mal
     * @throws IOException si el archivo no se puede leer
     */
    private static void processSingleFile(Path salesFile,
                                          Map<String, Product> products,
                                          Map<String, SalesmanReport> salesmenReports,
                                          Map<String, ProductReport> productReports,
                                          ValidationLog validationLog) throws IOException {

        String fileName = salesFile.getFileName().toString();
        List<String> lines = Files.readAllLines(salesFile, AppFiles.FILE_CHARSET);

        // Buscamos la primera linea con contenido por si se colo una linea en blanco arriba.
        int headerIndex = findFirstUsefulLine(lines);
        if (headerIndex < 0) {
            validationLog.addIssue(fileName, "Formato erroneo: el archivo de ventas esta vacio.");
            return;
        }

        String[] headerFields = lines.get(headerIndex).split(AppFiles.FIELD_SEPARATOR, -1);
        if (headerFields.length < HEADER_FIELD_COUNT) {
            validationLog.addIssue(fileName, headerIndex + 1,
                    "Formato erroneo: la primera linea debe contener tipo y numero de documento del vendedor.");
            return;
        }

        String documentKey = Salesman.buildDocumentKey(headerFields[0].trim().toUpperCase(),
                headerFields[1].trim());
        SalesmanReport salesmanReport = salesmenReports.get(documentKey);
        if (salesmanReport == null) {
            // Sin vendedor conocido no hay a quien sumarle estas ventas.
            validationLog.addIssue(fileName, headerIndex + 1,
                    "Informacion incoherente: el vendedor '" + documentKey
                            + "' no existe en " + AppFiles.SALESMEN_FILE + "; se descarta el archivo completo.");
            return;
        }
        salesmanReport.registerProcessedFile();

        for (int index = headerIndex + 1; index < lines.size(); index++) {
            int lineNumber = index + 1;
            String rawLine = lines.get(index);
            if (rawLine == null || rawLine.trim().isEmpty()) {
                continue;
            }

            String[] fields = rawLine.split(AppFiles.FIELD_SEPARATOR, -1);
            if (fields.length < SALE_FIELD_COUNT) {
                validationLog.addIssue(fileName, lineNumber,
                        "Formato erroneo: se esperaba 'idProducto;cantidad' y se encontraron "
                                + fields.length + " campos.");
                continue;
            }

            String productId = fields[0].trim();
            String rawQuantity = fields[1].trim();

            Product product = products.get(productId);
            if (product == null) {
                validationLog.addIssue(fileName, lineNumber,
                        "Informacion incoherente: el producto '" + productId
                                + "' no existe en " + AppFiles.PRODUCTS_FILE + ".");
                continue;
            }

            long quantity = parsePositiveQuantity(rawQuantity);
            if (quantity <= 0L) {
                validationLog.addIssue(fileName, lineNumber,
                        "Informacion incoherente: la cantidad '" + rawQuantity
                                + "' del producto '" + productId + "' no es un entero mayor que cero.");
                continue;
            }

            BigDecimal amount = product.totalFor(quantity);
            salesmanReport.addSale(quantity, amount);
            productReports.get(productId).addQuantity(quantity);
        }
    }

    /**
     * Busca la primera linea que tenga algo escrito.
     *
     * @param lines lineas del archivo
     * @return la posicion de esa linea, o -1 si el archivo estaba vacio
     */
    private static int findFirstUsefulLine(List<String> lines) {
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line != null && !line.trim().isEmpty()) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Convierte un texto en una cantidad.
     *
     * @param rawQuantity texto tal como venia en el archivo
     * @return la cantidad, o -1 si no era un entero mayor que cero
     */
    private static long parsePositiveQuantity(String rawQuantity) {
        try {
            long quantity = Long.parseLong(rawQuantity);
            return quantity > 0L ? quantity : -1L;
        } catch (NumberFormatException invalidNumber) {
            return -1L;
        }
    }

    /**
     * Ordena los vendedores del que mas recogio al que menos. El desempate por nombre
     * es para que dos ejecuciones sobre los mismos datos entreguen el mismo orden.
     *
     * @param reports cuentas sin ordenar
     * @return lista ordenada
     */
    private static List<SalesmanReport> sortSalesmenReports(java.util.Collection<SalesmanReport> reports) {
        List<SalesmanReport> sorted = new ArrayList<SalesmanReport>(reports);
        Collections.sort(sorted, new Comparator<SalesmanReport>() {
            @Override
            public int compare(SalesmanReport first, SalesmanReport second) {
                // Al reves (segundo contra primero) para que quede de mayor a menor.
                int byMoney = second.getTotalCollected().compareTo(first.getTotalCollected());
                if (byMoney != 0) {
                    return byMoney;
                }
                return first.getSalesman().getFullName()
                        .compareToIgnoreCase(second.getSalesman().getFullName());
            }
        });
        return sorted;
    }

    /**
     * Ordena los productos del mas vendido al menos vendido y deja por fuera los que
     * no se vendieron, porque el reporte es de productos vendidos.
     *
     * @param reports cuentas sin ordenar
     * @return lista ordenada
     */
    private static List<ProductReport> sortProductReports(java.util.Collection<ProductReport> reports) {
        List<ProductReport> sorted = new ArrayList<ProductReport>();
        for (ProductReport report : reports) {
            if (report.getQuantitySold() > 0L) {
                sorted.add(report);
            }
        }
        Collections.sort(sorted, new Comparator<ProductReport>() {
            @Override
            public int compare(ProductReport first, ProductReport second) {
                int byQuantity = Long.compare(second.getQuantitySold(), first.getQuantitySold());
                if (byQuantity != 0) {
                    return byQuantity;
                }
                return first.getProduct().getName()
                        .compareToIgnoreCase(second.getProduct().getName());
            }
        });
        return sorted;
    }
}
