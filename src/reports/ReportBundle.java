package reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Empaqueta los dos reportes terminados para poder pasarlos de un lado a otro
 * y guardarlos de un solo golpe en el archivo serializado.
 *
 * @author Grupo de trabajo - Conceptos Fundamentales de Programacion
 */
public class ReportBundle implements Serializable {

    private static final long serialVersionUID = 1L;

    // Van como ArrayList y no como List porque lo que se guarda en el archivo
    // binario tiene que ser algo que Java sepa serializar.
    private final ArrayList<SalesmanReport> salesmenReports;

    private final ArrayList<ProductReport> productReports;

    private final Date generatedAt;

    /**
     * Copia las dos listas y anota la fecha. Se copian para que no las cambien
     * por fuera despues de haberlas ordenado.
     *
     * @param salesmenReports vendedores ya ordenados
     * @param productReports  productos ya ordenados
     */
    public ReportBundle(List<SalesmanReport> salesmenReports, List<ProductReport> productReports) {
        this.salesmenReports = new ArrayList<SalesmanReport>(salesmenReports);
        this.productReports = new ArrayList<ProductReport>(productReports);
        this.generatedAt = new Date();
    }

    /** @return los vendedores, en una lista que no se puede modificar. */
    public List<SalesmanReport> getSalesmenReports() {
        return Collections.unmodifiableList(salesmenReports);
    }

    /** @return los productos, en una lista que no se puede modificar. */
    public List<ProductReport> getProductReports() {
        return Collections.unmodifiableList(productReports);
    }

    /** @return una copia de la fecha en que se armo el paquete. */
    public Date getGeneratedAt() {
        return new Date(generatedAt.getTime());
    }
}
