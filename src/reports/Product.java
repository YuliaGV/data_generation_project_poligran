package reports;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Un producto del catalogo: codigo, nombre y precio por unidad.
 *
 * @author Grupo de trabajo - Conceptos Fundamentales de Programacion
 */
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;

    private final String name;

    private final BigDecimal unitPrice;

    /**
     * @param id        codigo del producto
     * @param name      nombre del producto
     * @param unitPrice precio de una unidad
     */
    public Product(String id, String name, BigDecimal unitPrice) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    /** @return el codigo del producto. */
    public String getId() {
        return id;
    }

    /** @return el nombre del producto. */
    public String getName() {
        return name;
    }

    /** @return el precio de una unidad. */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * Precio por cantidad.
     *
     * @param quantity unidades vendidas
     * @return el dinero que suman esas unidades
     */
    public BigDecimal totalFor(long quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /** @return el producto escrito como una linea, util para revisar que se cargo. */
    @Override
    public String toString() {
        return id + AppFiles.FIELD_SEPARATOR + name + AppFiles.FIELD_SEPARATOR + unitPrice;
    }
}
