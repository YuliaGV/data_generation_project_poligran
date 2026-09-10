package generate_info;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


public class GenerateInfoFiles {

    // Arreglo estático para generar información de manera pseudoaleatoria
    private static final String[] NOMBRES = {"Enrique", "Duvan", "Leandro", "Lamine", "Luis", "Laura", "Cristiano", "Isabel", "Diego", "Kylian"};
    private static final String[] APELLIDOS = {"Gomez", "Mosquera", "Perez", "Smith", "Mbappe", "Paniagua", "Messi", "Ruiz", "Torres", "Delgado"};
    private static final String[] PRODUCTOS = {"Camiseta", "Pantalon", "Chaqueta", "Zapatos", "Gorra", "Bufanda", "Cojines", "Cobijas", "Cinturon", "Gafas"};
    private static final String[] TIPO_DOCUMENTO = {"CC", "CE", "TI"};
    
    private static final Random random = new Random();

    /**
     * Método principal para ejecutar la generación de archivos
     */
    public static void main(String[] args) {
        System.out.println("Iniciando la generacion de archivos de prueba");

        try {
            // Generamos archivo de información de productos (por ejemplo 10 productos)
            createProductsFile(10);
            
            // Generamos archivo de información de vendedores (por ejemplo 5 vendedores)
            createSalesManInfoFile(5);
            
            // Generamos archivos de ventas para un par de vendedores específicos como prueba
            createSalesMenFile(15, "Santiago Gomez", 1010101010L);
            createSalesMenFile(8, "Elizabeth Ospina", 2020202020L);

            System.out.println("¡Generacion de archivos finalizada de manera exitosa!");
            System.out.println("Debes refrescar la carpeta de tu proyecto en Eclipse para ver los archivos generados");

        } catch (IOException e) {
            System.err.println("Ocurrio un problema al generar los archivos: " + e.getMessage());
        }
    }

    /**
     * Se recibe la cantidad de productos a generar en el archivo
     * Se crea un archivo con información pseudoaleatoria de productos.
     * Formato: Id Producto; Nombre Producto; Precio por Unidad.
     */
    public static void createProductsFile(int productsCount) throws IOException {
        String fileName = "productos_info.txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 1; i <= productsCount; i++) {
                // Genera datos pseudoaleatorios
                String idProducto = "PROD-" + String.format("%03d", i);
                String nombreProducto = PRODUCTOS[random.nextInt(PRODUCTOS.length)] + " " + i;
                // Precio entre 10,000 y 500,000
                double precioUnidad = 10000.0 + (490000.0 * random.nextDouble()); 
                
                // Formatea y escribir la línea
                String linea = String.format("%s;%s;%.2f", idProducto, nombreProducto, precioUnidad);
                writer.write(linea);
                writer.newLine();
            }
        }
    }

    /**
     * Se recibe la cantidad de vendedores a generar en el archivo
     * Se crea un archivo con información pseudoaleatoria de vendedores.
     * Formato: Tipo Documento; Número Documento; Nombres; Apellidos
     */
    public static void createSalesManInfoFile(int salesmanCount) throws IOException {
        String fileName = "vendedores_info.txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < salesmanCount; i++) {
                // Genera datos pseudoaleatorios
                String tipoDoc = TIPO_DOCUMENTO[random.nextInt(TIPO_DOCUMENTO.length)];
                long numDoc = 1000000000L + (long)(random.nextDouble() * 9000000000L); // Número de 10 dígitos
                String nombre = NOMBRES[random.nextInt(NOMBRES.length)];
                String apellido = APELLIDOS[random.nextInt(APELLIDOS.length)];
                
                // Formatea y escribir la línea
                String linea = String.format("%s;%d;%s;%s", tipoDoc, numDoc, nombre, apellido);
                writer.write(linea);
                writer.newLine();
            }
        }
    }

    /**
     * Se crea un archivo pseudoaleatorio de ventas para un vendedor específico.
     * Primera línea: Tipo Documento; Número Documento
     * Siguientes líneas: ID Producto; Cantidad Vendida;
     * Se reciben como parámetros: la cantiad de ventas a generar, el nombre del vendedor y el número de identificación del vendedor 
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id) throws IOException {
        // Se crea un nombre de archivo
        String safeName = name.replace(" ", "_");
        String fileName = "ventas_" + safeName + "_" + id + ".txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // CC se asume como tipo de documento por defecto
            String tipoDocumento = "CC"; 
            
            // Escribe el encabezado del vendedor (primera línea del archivo)
            writer.write(tipoDocumento + ";" + id);
            writer.newLine();
            
            // Genera las ventas
            for (int i = 0; i < randomSalesCount; i++) {
                // Genera ID de producto pseudoaleatorio (asumiendo formato PROD-XXX)
                int idNum = random.nextInt(10) + 1; // Un producto del 1 al 10
                String idProducto = "PROD-" + String.format("%03d", idNum);
                
                // Cantidad vendida entre 1 y 50
                int cantidadVendida = random.nextInt(50) + 1; 
                
                // Formatea y escribe la línea
                String linea = String.format("%s;%d;", idProducto, cantidadVendida);
                writer.write(linea);
                writer.newLine();
            }
        }
    }
}
