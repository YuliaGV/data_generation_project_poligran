package reports;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Va anotando todo lo que viene mal en los archivos de entrada.
 *
 * Un dato dañado no detiene el proceso: se descarta, se anota en que archivo y en
 * que linea estaba, y se sigue con el resto.
 *
 * @author Grupo de trabajo - Conceptos Fundamentales de Programacion
 */
public class ValidationLog {

    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final List<String> issues = new ArrayList<String>();

    /**
     * Anota un problema de una linea concreta.
     *
     * @param fileName   archivo donde aparecio
     * @param lineNumber numero de linea, contando desde uno
     * @param detail     que fue lo que paso
     */
    public void addIssue(String fileName, int lineNumber, String detail) {
        issues.add(String.format("[%s:%d] %s", fileName, Integer.valueOf(lineNumber), detail));
    }

    /**
     * Anota un problema del archivo completo.
     *
     * @param fileName archivo donde aparecio
     * @param detail   que fue lo que paso
     */
    public void addIssue(String fileName, String detail) {
        issues.add(String.format("[%s] %s", fileName, detail));
    }

    /** @return true si no se anoto ningun problema. */
    public boolean isEmpty() {
        return issues.isEmpty();
    }

    /** @return cuantos problemas se anotaron. */
    public int size() {
        return issues.size();
    }

    /** @return los problemas anotados, en una lista que no se puede modificar. */
    public List<String> getIssues() {
        return Collections.unmodifiableList(issues);
    }

    /**
     * Escribe el archivo de errores. Se escribe incluso cuando no hubo ninguno,
     * porque un archivo que no existe se confunde con un proceso que nunca corrio.
     *
     * @param logFile donde se va a escribir
     * @throws IOException si no se puede escribir
     */
    public void writeTo(Path logFile) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(logFile, AppFiles.FILE_CHARSET);
        try {
            String timestamp = new SimpleDateFormat(TIMESTAMP_PATTERN).format(new Date());
            writer.write("Bitacora de validacion generada el " + timestamp);
            writer.newLine();
            writer.write("Inconsistencias detectadas: " + issues.size());
            writer.newLine();
            writer.newLine();
            if (issues.isEmpty()) {
                writer.write("No se detectaron inconsistencias en los archivos de entrada.");
                writer.newLine();
            } else {
                for (String issue : issues) {
                    writer.write(issue);
                    writer.newLine();
                }
            }
        } finally {
            writer.close();
        }
    }
}
