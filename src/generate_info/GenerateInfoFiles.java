package generate_info;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class GenerateInfoFiles {

    // Arreglo estático para generar información de manera pseudoaleatoria
    private static final String[] NOMBRES = {"Enrique", "Duvan", "Leandro", "Lamine", "Luis", "Laura", "Cristiano", "Isabel", "Diego", "Kylian"};
    private static final String[] APELLIDOS = {"Gomez", "Mosquera", "Perez", "Smith", "Mbappe", "Paniagua", "Messi", "Ruiz", "Torres", "Delgado"};
    private static final String[] PRODUCTOS = {"Camiseta", "Pantalon", "Chaqueta", "Zapatos", "Gorra", "Bufanda", "Cojines", "Cobijas", "Cinturon", "Gafas"};
    private static final String DOCUMENT_TYPE = "CC";

    // Nombres de los archivos que genera el programa
    private static final String PRODUCTS_FILE = "productos_info.txt";
    private static final String SALESMEN_FILE = "vendedores_info.txt";

    // Cada tanto un vendedor recibe un segundo archivo de ventas, para que el
    // caso de varios archivos por vendedor quede siempre cubierto en la prueba
    private static final double PROBABILIDAD_SEGUNDO_ARCHIVO = 0.4;
    
    private static final Random random = new Random();

    /**
     * Método principal para ejecutar la generación de archivos
     */
    public static void main(String[] args) {
    	
        System.out.println("Iniciando la generacion de archivos de prueba");

        try {
            // Se borran los archivos de ventas de la corrida anterior. Si se quedan,
            // se leen junto con los nuevos y los totales de los reportes no cuadran.
            borrarVentasAnteriores();
            
            // Generamos archivo de información de productos (por ejemplo 10 productos)
            createProductsFile(10);
            
            // Generamos archivo de información de vendedores (por ejemplo 5 vendedores)
            createSalesManInfoFile(5);
            
            // Leemos el archivo generado de salesmen y creamos un archivo sales para cada uno
            
            try (BufferedReader reader =
                    new BufferedReader(new FileReader(SALESMEN_FILE))) {

                String line;

                while ((line = reader.readLine()) != null) {

                    String[] data = line.split(";");

                    String name = data[2] + " " + data[3];

                    long id = Long.parseLong(data[1]);

                    createSalesMenFile(10, name, id);

                    // A algunos vendedores se les genera un segundo archivo
                    if (random.nextDouble() < PROBABILIDAD_SEGUNDO_ARCHIVO) {
                        createSalesMenFile(10, name, id, 2);
                    }
                }
            }
         

            System.out.println("¡Generacion de archivos finalizada de manera exitosa!");
            System.out.println("Debes refrescar la carpeta de tu proyecto en Eclipse para ver los archivos generados");

        } catch (IOException e) {
            System.err.println("Ocurrio un problema al generar los archivos: " + e.getMessage());
        }
    }

    /**
     * Borra los archivos de ventas que hayan quedado de una ejecución anterior.
     * Solo se borra lo que empieza por "ventas_" y termina en ".txt", que es lo
     * que genera este mismo programa.
     */
    private static void borrarVentasAnteriores() {
        java.io.File carpeta = new java.io.File(".");
        java.io.File[] archivos = carpeta.listFiles();
        
        if (archivos == null) {
            return;
        }
        
        int borrados = 0;
        for (java.io.File archivo : archivos) {
            String nombre = archivo.getName();
            if (archivo.isFile() && nombre.startsWith("ventas_") && nombre.endsWith(".txt")) {
                if (archivo.delete()) {
                    borrados++;
                }
            }
        }
        
        if (borrados > 0) {
            System.out.println("Se borraron " + borrados + " archivos de ventas de la ejecucion anterior");
        }
    }

    /**
     * Se recibe la cantidad de productos a generar en el archivo
     * Se crea un archivo con información pseudoaleatoria de productos.
     * Formato: Id Producto; Nombre Producto; Precio por Unidad.
     */
    public static void createProductsFile(int productsCount) throws IOException {
        String fileName = PRODUCTS_FILE;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 1; i <= productsCount; i++) {
                // Genera datos pseudoaleatorios
                String idProducto = "PROD-" + String.format("%03d", i);
                String nombreProducto = PRODUCTOS[random.nextInt(PRODUCTOS.length)] + " " + i;
                // Precio entre 10,000 y 500,000
                double precioUnidad = 10000.0 + (490000.0 * random.nextDouble()); 
                
                // Formatea y escribir la línea
                // Locale.ROOT fuerza el punto como separador decimal. Sin esto, en un
                // equipo configurado en español el precio saldría con coma y no se
                // podría volver a leer como número.
                String linea = String.format(Locale.ROOT, "%s;%s;%.2f", idProducto, nombreProducto, precioUnidad);
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
        String fileName = SALESMEN_FILE;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < salesmanCount; i++) {
                // Genera datos pseudoaleatorios
                String tipoDoc = DOCUMENT_TYPE;
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
        createSalesMenFile(randomSalesCount, name, id, 1);
    }

    /**
     * Igual que el método anterior, pero permite crear un segundo o tercer archivo
     * de ventas para el mismo vendedor. Los archivos quedan con nombres distintos y
     * todos llevan el mismo documento en la primera línea, que es lo que permite
     * sumarlos después en una sola cuenta.
     * Se recibe además el consecutivo del archivo, empezando en 1.
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id, int numeroArchivo)
            throws IOException {
        // Se crea un nombre de archivo
        String safeName = name.replace(" ", "_");
        String sufijo = numeroArchivo > 1 ? "_" + numeroArchivo : "";
        String fileName = "ventas_" + safeName + "_" + id + sufijo + ".txt";
        
        // Se leen los productos que existen de verdad en el catálogo, en vez de
        // suponer que siempre son diez. Así no se generan ventas de productos
        // inexistentes si se cambia la cantidad de productos.
        List<String> idsProductos = leerIdsDeProductos();
        if (idsProductos.isEmpty()) {
            throw new IOException("El archivo " + PRODUCTS_FILE
                    + " no tiene productos. Debe generarse antes que las ventas.");
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // CC se asume como tipo de documento por defecto
            String tipoDocumento = DOCUMENT_TYPE; 
            
            // Escribe el encabezado del vendedor (primera línea del archivo)
            writer.write(tipoDocumento + ";" + id);
            writer.newLine();
            
            // Genera las ventas
            for (int i = 0; i < randomSalesCount; i++) {
                // Toma un producto cualquiera de los que existen en el catálogo
                String idProducto = idsProductos.get(random.nextInt(idsProductos.size()));
                
                // Cantidad vendida entre 1 y 50
                int cantidadVendida = random.nextInt(50) + 1; 
                
                // Formatea y escribe la línea
                String linea = String.format("%s;%d;", idProducto, cantidadVendida);
                writer.write(linea);
                writer.newLine();
            }
        }
    }

    /**
     * Lee del archivo de productos los ids que se pueden vender.
     * Se devuelve una lista vacía si el archivo todavía no existe.
     */
    private static List<String> leerIdsDeProductos() throws IOException {
        List<String> ids = new ArrayList<String>();
        
        java.io.File archivo = new java.io.File(PRODUCTS_FILE);
        if (!archivo.isFile()) {
            return ids;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] data = line.split(";");
                if (data.length >= 1 && !data[0].trim().isEmpty()) {
                    ids.add(data[0].trim());
                }
            }
        }
        return ids;
    }
}
