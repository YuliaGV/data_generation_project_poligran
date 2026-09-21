package reports;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Lleva la cuenta de un vendedor: cuanto dinero recogio y cuantas unidades vendio.
 *
 * @author Grupo de trabajo - Conceptos Fundamentales de Programacion
 */
public class SalesmanReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Salesman salesman;

    private BigDecimal totalCollected;

    private long totalUnits;

    private int processedFiles;

    /**
     * Abre la cuenta de un vendedor en ceros.
     *
     * @param salesman vendedor al que pertenece la cuenta
     */
    public SalesmanReport(Salesman salesman) {
        this.salesman = salesman;
        this.totalCollected = BigDecimal.ZERO;
        this.totalUnits = 0L;
        this.processedFiles = 0;
    }

    /**
     * Suma una venta.
     *
     * @param quantity unidades vendidas
     * @param amount   dinero de esa venta
     */
    public void addSale(long quantity, BigDecimal amount) {
        this.totalUnits += quantity;
        this.totalCollected = this.totalCollected.add(amount);
    }

    /** Anota que se le conto un archivo de ventas mas a este vendedor. */
    public void registerProcessedFile() {
        this.processedFiles++;
    }

    /** @return el vendedor. */
    public Salesman getSalesman() {
        return salesman;
    }

    /** @return el dinero recaudado. */
    public BigDecimal getTotalCollected() {
        return totalCollected;
    }

    /** @return las unidades vendidas. */
    public long getTotalUnits() {
        return totalUnits;
    }

    /** @return cuantos archivos de ventas se le contaron. */
    public int getProcessedFiles() {
        return processedFiles;
    }
}
