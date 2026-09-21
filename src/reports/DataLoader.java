package reports;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lee el catalogo de productos y el archivo de vendedores, y los deja convertidos
 * en objetos. Cada linea se revisa por separado: la que venga mal se descarta y
 * se anota, pero no tumba la carga de las demas.
 *
 * @author Grupo de trabajo - Conceptos Fundamentales de Programacion
 */
public final class DataLoader {

    /** Una linea de producto trae codigo, nombre y precio. */
    private static final int PRODUCT_FIELD_COUNT = 3;

    /** Una linea de vendedor trae tipo, numero, nombres y apellidos. */
    private static final int SALESMAN_FIELD_COUNT = 4;

    /** Tipos de documento que aceptamos. */
    private static final List<String> VALID_DOCUMENT_TYPES =
            Arrays.asList("CC", "CE", "TI", "PA", "NIT");

    /** Clase de utilidad: no se instancia. */
    private DataLoader() {
    }

    /**
     * Lee el catalogo de productos. Se descarta la linea a la que le falten campos,
     * la que venga sin codigo o sin nombre, la que traiga un precio que no sea un
     * numero mayor que cero, y la que repita un codigo ya visto.
     *
     * @param validationLog donde se anota lo que venga mal
     * @return los productos validos, buscables por codigo
     * @throws IOException si el archivo no existe o no se puede leer
     */
    public static Map<String, Product> loadProducts(ValidationLog validationLog) throws IOException {
        Path productsPath = AppFiles.path(AppFiles.PRODUCTS_FILE);
        requireExistingFile(productsPath, "el catalogo de productos");

        // LinkedHashMap para poder buscar por codigo y conservar el orden del archivo.
        Map<String, Product> products = new LinkedHashMap<String, Product>();
        List<String> lines = Files.readAllLines(productsPath, AppFiles.FILE_CHARSET);
        String fileName = AppFiles.PRODUCTS_FILE;

        for (int index = 0; index < lines.size(); index++) {
            // El archivo empieza en la linea 1 y la lista en la posicion 0.
            int lineNumber = index + 1;
            String rawLine = lines.get(index);
            if (isBlank(rawLine)) {
                continue;
            }

            String[] fields = rawLine.split(AppFiles.FIELD_SEPARATOR, -1);
            if (fields.length < PRODUCT_FIELD_COUNT) {
                validationLog.addIssue(fileName, lineNumber,
                        "Formato erroneo: se esperaban " + PRODUCT_FIELD_COUNT
                                + " campos (id;nombre;precio) y se encontraron " + fields.length + ".");
                continue;
            }

            String productId = fields[0].trim();
            String productName = fields[1].trim();
            String rawPrice = fields[2].trim();

            if (productId.isEmpty() || productName.isEmpty()) {
                validationLog.addIssue(fileName, lineNumber,
                        "Informacion incoherente: el identificador y el nombre del producto no pueden estar vacios.");
                continue;
            }
            if (products.containsKey(productId)) {
                validationLog.addIssue(fileName, lineNumber,
                        "Informacion incoherente: el identificador de producto '" + productId
                                + "' esta repetido; se conserva la primera aparicion.");
                continue;
            }

            BigDecimal unitPrice = parsePositiveAmount(rawPrice);
            if (unitPrice == null) {
                validationLog.addIssue(fileName, lineNumber,
                        "Informacion incoherente: el precio '" + rawPrice
                                + "' del producto '" + productId + "' no es un numero mayor que cero.");
                continue;
            }

            products.put(productId, new Product(productId, productName, unitPrice));
        }

        if (products.isEmpty()) {
            validationLog.addIssue(fileName, "No se cargo ningun producto valido desde el catalogo.");
        }
        return products;
    }

    /**
     * Lee el archivo de vendedores. Se descarta la linea a la que le falten campos,
     * la que traiga un tipo de documento desconocido, la que tenga un numero que no
     * sea entero positivo, la que venga sin nombres o apellidos, y la que repita un
     * documento ya registrado.
     *
     * @param validationLog donde se anota lo que venga mal
     * @return los vendedores validos, buscables por TIPO;NUMERO
     * @throws IOException si el archivo no existe o no se puede leer
     */
    public static Map<String, Salesman> loadSalesmen(ValidationLog validationLog) throws IOException {
        Path salesmenPath = AppFiles.path(AppFiles.SALESMEN_FILE);
        requireExistingFile(salesmenPath, "la informacion de vendedores");

        Map<String, Salesman> salesmen = new LinkedHashMap<String, Salesman>();
        List<String> lines = Files.readAllLines(salesmenPath, AppFiles.FILE_CHARSET);
        String fileName = AppFiles.SALESMEN_FILE;

        for (int index = 0; index < lines.size(); index++) {
            int lineNumber = index + 1;
            String rawLine = lines.get(index);
            if (isBlank(rawLine)) {
                continue;
            }

            String[] fields = rawLine.split(AppFiles.FIELD_SEPARATOR, -1);
            if (fields.length < SALESMAN_FIELD_COUNT) {
                validationLog.addIssue(fileName, lineNumber,
                        "Formato erroneo: se esperaban " + SALESMAN_FIELD_COUNT
                                + " campos (tipo;numero;nombres;apellidos) y se encontraron " + fields.length + ".");
                continue;
            }

            // A mayusculas para que "cc" y "CC" sean lo mismo.
            String documentType = fields[0].trim().toUpperCase();
            String documentNumber = fields[1].trim();
            String firstNames = fields[2].trim();
            String lastNames = fields[3].trim();

            if (!VALID_DOCUMENT_TYPES.contains(documentType)) {
                validationLog.addIssue(fileName, lineNumber,
                        "Informacion incoherente: el tipo de documento '" + documentType
                                + "' no pertenece a los tipos validos " + VALID_DOCUMENT_TYPES + ".");
                continue;
            }
            if (!isPositiveInteger(documentNumber)) {
                validationLog.addIssue(fileName, lineNumber,
                        "Informacion incoherente: el numero de documento '" + documentNumber
                                + "' no es un entero positivo.");
                continue;
            }
            if (firstNames.isEmpty() || lastNames.isEmpty()) {
                validationLog.addIssue(fileName, lineNumber,
                        "Informacion incoherente: los nombres y apellidos del vendedor no pueden estar vacios.");
                continue;
            }

            String documentKey = Salesman.buildDocumentKey(documentType, documentNumber);
            if (salesmen.containsKey(documentKey)) {
                validationLog.addIssue(fileName, lineNumber,
                        "Informacion incoherente: el documento '" + documentKey
                                + "' esta repetido; se conserva la primera aparicion.");
                continue;
            }

            salesmen.put(documentKey, new Salesman(documentType, documentNumber, firstNames, lastNames));
        }

        if (salesmen.isEmpty()) {
            validationLog.addIssue(fileName, "No se cargo ningun vendedor valido.");
        }
        return salesmen;
    }

    /**
     * Revisa que un archivo necesario si exista. Aqui si paramos: sin productos o
     * sin vendedores no hay reporte posible.
     *
     * @param path        archivo que se necesita
     * @param description como llamarlo en el mensaje de error
     * @throws IOException si el archivo no esta
     */
    private static void requireExistingFile(Path path, String description) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IOException("No se encontro el archivo con " + description + ": "
                    + path.toAbsolutePath() + ". Ejecute primero la clase GenerateInfoFiles.");
        }
    }

    /**
     * Convierte un texto en un valor de dinero.
     *
     * @param rawValue texto tal como venia en el archivo
     * @return el valor, o null si no era un numero mayor que cero
     */
    private static BigDecimal parsePositiveAmount(String rawValue) {
        try {
            BigDecimal amount = new BigDecimal(rawValue);
            return amount.compareTo(BigDecimal.ZERO) > 0 ? amount : null;
        } catch (NumberFormatException invalidNumber) {
            return null;
        }
    }

    /**
     * Dice si un texto son puros digitos y el numero es mayor que cero.
     *
     * @param rawValue texto a revisar
     * @return true si es un entero positivo
     */
    private static boolean isPositiveInteger(String rawValue) {
        if (rawValue == null || rawValue.isEmpty()) {
            return false;
        }
        for (int index = 0; index < rawValue.length(); index++) {
            if (!Character.isDigit(rawValue.charAt(index))) {
                return false;
            }
        }
        // BigDecimal y no Long porque un documento puede traer mas digitos de los que cabe un long.
        return new BigDecimal(rawValue).compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Dice si una linea esta vacia o solo tiene espacios.
     *
     * @param line linea a revisar
     * @return true si se puede saltar
     */
    private static boolean isBlank(String line) {
        return line == null || line.trim().isEmpty();
    }
}
