package reports;

import java.io.Serializable;

/**
 * Lleva la cuenta de cuantas unidades se vendieron de un producto.
 *
 * @author Grupo de trabajo - Conceptos Fundamentales de Programacion
 */
public class ProductReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Product product;

    private long quantitySold;

    /**
     * Abre la cuenta de un producto en cero.
     *
     * @param product producto al que pertenece la cuenta
     */
    public ProductReport(Product product) {
        this.product = product;
        this.quantitySold = 0L;
    }

    /**
     * Suma unidades vendidas.
     *
     * @param quantity unidades a sumar
     */
    public void addQuantity(long quantity) {
        this.quantitySold += quantity;
    }

    /** @return el producto. */
    public Product getProduct() {
        return product;
    }

    /** @return las unidades vendidas. */
    public long getQuantitySold() {
        return quantitySold;
    }
}
