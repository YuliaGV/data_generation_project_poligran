package reports;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Guarda los reportes en un archivo binario y los vuelve a traer.
 *
 * A diferencia del CSV, aqui no se guarda texto sino los objetos como estan en
 * memoria, asi que al leerlos vuelven ordenados y validados sin repetir el proceso.
 *
 * @author Grupo de trabajo - Conceptos Fundamentales de Programacion
 */
public final class SerializationService {

    /** Clase de utilidad: no se instancia. */
    private SerializationService() {
    }

    /**
     * Guarda el paquete de reportes.
     *
     * @param reportBundle lo que se quiere guardar
     * @param targetPath   donde se guarda
     * @throws IOException si no se puede escribir
     */
    public static void save(ReportBundle reportBundle, Path targetPath) throws IOException {
        OutputStream fileStream = Files.newOutputStream(targetPath);
        ObjectOutputStream objectStream = new ObjectOutputStream(new BufferedOutputStream(fileStream));
        try {
            objectStream.writeObject(reportBundle);
            objectStream.flush();
        } finally {
            objectStream.close();
        }
    }

    /**
     * Lee el paquete de reportes que se habia guardado.
     *
     * @param sourcePath de donde se lee
     * @return el paquete recuperado
     * @throws IOException            si no se puede leer
     * @throws ClassNotFoundException si el archivo no tenia adentro lo que esperabamos
     */
    public static ReportBundle load(Path sourcePath) throws IOException, ClassNotFoundException {
        InputStream fileStream = Files.newInputStream(sourcePath);
        ObjectInputStream objectStream = new ObjectInputStream(new BufferedInputStream(fileStream));
        try {
            return (ReportBundle) objectStream.readObject();
        } finally {
            objectStream.close();
        }
    }
}
