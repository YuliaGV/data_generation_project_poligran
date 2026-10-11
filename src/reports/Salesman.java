package reports;

import java.io.Serializable;

/**
 * Un vendedor. El tipo y el numero de documento juntos son lo que lo identifica,
 * y es lo que une el archivo de vendedores con los archivos de ventas.
 *
 * @author Grupo de trabajo - Conceptos Fundamentales de Programacion
 */
public class Salesman implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String documentType;

    private final String documentNumber;

    private final String firstNames;

    private final String lastNames;

    /**
     * @param documentType   tipo de documento (CC, CE, TI, PA o NIT)
     * @param documentNumber numero del documento
     * @param firstNames     nombres
     * @param lastNames      apellidos
     */
    public Salesman(String documentType, String documentNumber, String firstNames, String lastNames) {
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.firstNames = firstNames;
        this.lastNames = lastNames;
    }

    /** @return el tipo de documento. */
    public String getDocumentType() {
        return documentType;
    }

    /** @return el numero del documento. */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /** @return los nombres. */
    public String getFirstNames() {
        return firstNames;
    }

    /** @return los apellidos. */
    public String getLastNames() {
        return lastNames;
    }

    /** @return nombres y apellidos juntos, que es como sale en el reporte. */
    public String getFullName() {
        return firstNames + " " + lastNames;
    }

    /** @return el documento en formato TIPO;NUMERO. */
    public String getDocumentKey() {
        return buildDocumentKey(documentType, documentNumber);
    }

    /**
     * Arma la misma llave cuando todavia no tenemos el objeto, que es lo que pasa
     * al leer la primera linea de un archivo de ventas.
     *
     * @param documentType   tipo de documento
     * @param documentNumber numero del documento
     * @return el documento en formato TIPO;NUMERO
     */
    public static String buildDocumentKey(String documentType, String documentNumber) {
        return documentType + AppFiles.FIELD_SEPARATOR + documentNumber;
    }

    /** @return el vendedor escrito como una linea, util para revisar que se cargo. */
    @Override
    public String toString() {
        return getDocumentKey() + AppFiles.FIELD_SEPARATOR + getFullName();
    }
}
