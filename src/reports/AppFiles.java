package reports;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nombres de los archivos, separador y codificacion que usa todo el proyecto.
 * Los archivos de entrada y de salida viven en la carpeta raiz.
 *
 * @author Grupo de trabajo - Conceptos Fundamentales de Programacion
 */
public final class AppFiles {

    /** Carpeta donde se leen y escriben los archivos. */
    public static final String DATA_DIRECTORY = ".";

    /** Catalogo de productos. */
    public static final String PRODUCTS_FILE = "productos_info.txt";

    /** Datos de los vendedores. */
    public static final String SALESMEN_FILE = "vendedores_info.txt";

    /** Con que empieza el nombre de un archivo de ventas. */
    public static final String SALES_FILE_PREFIX = "ventas_";

    /** Con que termina el nombre de un archivo de ventas. */
    public static final String SALES_FILE_EXTENSION = ".txt";

    /** Reporte de vendedores ordenado por dinero recaudado. */
    public static final String SALESMEN_REPORT_FILE = "reporte_vendedores.csv";

    /** Reporte de productos ordenado por cantidad vendida. */
    public static final String PRODUCTS_REPORT_FILE = "reporte_productos.csv";

    /** Donde se anotan los errores encontrados en los datos. */
    public static final String VALIDATION_LOG_FILE = "errores_validacion.log";

    /** Copia de los reportes en formato serializado. */
    public static final String SERIALIZED_REPORT_FILE = "reportes.ser";

    /** Separador de campos. */
    public static final String FIELD_SEPARATOR = ";";

    /** UTF-8, para que no se dañen las tildes ni las eñes. */
    public static final Charset FILE_CHARSET = Charset.forName("UTF-8");

    /** Clase de constantes: no se instancia. */
    private AppFiles() {
    }

    /**
     * Arma la ruta de un archivo dentro de la carpeta de trabajo.
     *
     * @param fileName nombre del archivo
     * @return la ruta del archivo
     */
    public static Path path(String fileName) {
        return Paths.get(DATA_DIRECTORY, fileName);
    }

    /**
     * Busca en la carpeta todos los archivos de ventas que haya.
     * No importa cuantos sean ni como se llamen mientras sigan la convencion de nombres.
     *
     * @return los archivos de ventas, ordenados por nombre
     * @throws IOException si no se puede leer la carpeta
     */
    public static List<Path> listSalesFiles() throws IOException {
        List<Path> salesFiles = new ArrayList<Path>();
        Path directory = Paths.get(DATA_DIRECTORY);
        DirectoryStream<Path> stream = Files.newDirectoryStream(directory);
        try {
            for (Path candidate : stream) {
                if (isSalesFile(candidate)) {
                    salesFiles.add(candidate);
                }
            }
        } finally {
            stream.close();
        }
        // Ordenamos para que dos ejecuciones lean los archivos en el mismo orden.
        Collections.sort(salesFiles);
        return salesFiles;
    }

    /**
     * Dice si un archivo es de ventas o es otra cosa que quedo en la carpeta.
     *
     * @param candidate archivo a revisar
     * @return true si empieza por ventas_ y termina en .txt
     */
    public static boolean isSalesFile(Path candidate) {
        if (!Files.isRegularFile(candidate)) {
            return false;
        }
        String fileName = candidate.getFileName().toString();
        return fileName.startsWith(SALES_FILE_PREFIX) && fileName.endsWith(SALES_FILE_EXTENSION);
    }
}
