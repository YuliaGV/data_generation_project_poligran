package generate_info;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase principal (main) para la lectura, procesamiento y exportacion de datos.
 * Esta clase representa la version preliminar de la Entrega 2.
 * 
 */
public class Main {

    public static void main(String[] args) {
    	
        System.out.println("Arrancando el sistema de lectura de archivos (Versión Preliminar - Entrega 2)...");

        // 1. ESTRUCTURAS DE DATOS (Mapeo en Memoria)
        Map<String, Double> preciosProductos = new HashMap<>();
        Map<String, String> nombresProductos = new HashMap<>();
        Map<Long, String> nombresVendedores = new HashMap<>();
        
        Map<Long, Double> totalVentasPorVendedor = new HashMap<>();
        Map<String, Integer> cantidadPorProducto = new HashMap<>();

        try {
            // 2. LECTURA DE ARCHIVOS MAESTROS
            System.out.println("Leyendo productos_info.txt...");
            BufferedReader lectorProductos = new BufferedReader(new FileReader("productos_info.txt"));
            String lineaProducto;
            
            while ((lineaProducto = lectorProductos.readLine()) != null) {
                String[] partes = lineaProducto.split(";");
                String idProd = partes[0];
                String nombreProd = partes[1];
                double precio = Double.parseDouble(partes[2].replace(",", ".")); 
                
                preciosProductos.put(idProd, precio);
                nombresProductos.put(idProd, nombreProd);
                cantidadPorProducto.put(idProd, 0); 
            }
            lectorProductos.close();

            System.out.println("Leyendo vendedores_info.txt...");
            BufferedReader lectorVendedores = new BufferedReader(new FileReader("vendedores_info.txt"));
            String lineaVendedor;
            
            while ((lineaVendedor = lectorVendedores.readLine()) != null) {
                String[] partes = lineaVendedor.split(";");
                long idVendedor = Long.parseLong(partes[1]);
                String nombreCompleto = partes[2] + " " + partes[3];
                
                nombresVendedores.put(idVendedor, nombreCompleto);
                totalVentasPorVendedor.put(idVendedor, 0.0); 
            }
            lectorVendedores.close();

            // 3. PROCESAMIENTO DE ARCHIVOS DE VENTAS
            System.out.println("Buscando archivos de ventas en la carpeta raiz...");
            File carpeta = new File(".");
            File[] listaArchivos = carpeta.listFiles();

            if (listaArchivos != null) {
                for (int i = 0; i < listaArchivos.length; i++) {
                    File archivo = listaArchivos[i];
                    
                    if (archivo.isFile() && archivo.getName().startsWith("ventas_") && archivo.getName().endsWith(".txt")) {
                        procesarArchivoVenta(archivo, preciosProductos, totalVentasPorVendedor, cantidadPorProducto);
                    }
                }
            }

            // 4. GENERACION DE REPORTES FINALES (CSV)
            System.out.println("Generando reportes CSV...");
            generarReporteVendedores(nombresVendedores, totalVentasPorVendedor);
            generarReporteProductos(nombresProductos, preciosProductos, cantidadPorProducto);

            System.out.println("¡Listo! Los reportes se crearon exitosamente.");

        } catch (Exception e) {
            System.out.println("Error fatal leyendo los archivos maestros. Verifique que los archivos existan en la raiz.");
            e.printStackTrace(); 
        }
    }

    private static void procesarArchivoVenta(File archivo, Map<String, Double> preciosProductos, 
                                         Map<Long, Double> totalVentasPorVendedor, Map<String, Integer> cantidadPorProducto) {
        try (BufferedReader lectorVenta = new BufferedReader(new FileReader(archivo))) {
            String encabezado = lectorVenta.readLine();
            if (encabezado == null) return; 
            
            String[] datosEncabezado = encabezado.split(";");
            long idVendedor = Long.parseLong(datosEncabezado[1]);
            
            String lineaVenta;
            while ((lineaVenta = lectorVenta.readLine()) != null) {
                String[] partes = lineaVenta.split(";");
                String idProducto = partes[0];
                int cantidad = Integer.parseInt(partes[1]);
                
                double precio = 0.0;
                if (preciosProductos.containsKey(idProducto)) {
                    precio = preciosProductos.get(idProducto);
                }
                
                double totalVenta = precio * cantidad;
                
                if (totalVentasPorVendedor.containsKey(idVendedor)) {
                    double acumuladoActual = totalVentasPorVendedor.get(idVendedor);
                    totalVentasPorVendedor.put(idVendedor, acumuladoActual + totalVenta);
                }
                
                if (cantidadPorProducto.containsKey(idProducto)) {
                    int cantidadActual = cantidadPorProducto.get(idProducto);
                    cantidadPorProducto.put(idProducto, cantidadActual + cantidad);
                }
            }
        } catch (Exception e) {
            System.out.println("Error procesando el archivo de venta especifico: " + archivo.getName());
            e.printStackTrace();
        }
    }

    private static void generarReporteVendedores(Map<Long, String> nombresVendedores, Map<Long, Double> totalVentasPorVendedor) throws IOException {
        List<Map.Entry<Long, Double>> listaVendedores = new ArrayList<>(totalVentasPorVendedor.entrySet());
        
        Collections.sort(listaVendedores, new Comparator<Map.Entry<Long, Double>>() {
            public int compare(Map.Entry<Long, Double> v1, Map.Entry<Long, Double> v2) {
                return v2.getValue().compareTo(v1.getValue());
            }
        });

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter("reporte_vendedores.csv"))) {
            for (int i = 0; i < listaVendedores.size(); i++) {
                Map.Entry<Long, Double> entrada = listaVendedores.get(i);
                long id = entrada.getKey();
                String nombre = nombresVendedores.get(id);
                
                if (nombre == null) nombre = "Desconocido";
                
                escritor.write(nombre + ";" + String.format("%.2f", entrada.getValue()));
                escritor.newLine(); 
            }
        }
    }

    /**
     * Ordena los productos por la cantidad total de unidades vendidas y genera el archivo CSV final.
     */
    private static void generarReporteProductos(Map<String, String> nombresProductos, Map<String, Double> preciosProductos, Map<String, Integer> cantidadPorProducto) throws IOException {
        List<Map.Entry<String, Integer>> listaProductos = new ArrayList<>(cantidadPorProducto.entrySet());
        
        Collections.sort(listaProductos, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> p1, Map.Entry<String, Integer> p2) {
                return p2.getValue().compareTo(p1.getValue());
            }
        });

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter("reporte_productos.csv"))) {
            for (int i = 0; i < listaProductos.size(); i++) {
                Map.Entry<String, Integer> entrada = listaProductos.get(i);
                String id = entrada.getKey();
                String nombre = nombresProductos.get(id);
                
                if (nombre == null) nombre = "Desconocido";

                // Obtenemos el precio unitario consultando el mapa de precios
                double precioUnitario = 0.0;
                if (preciosProductos.containsKey(id)) {
                    precioUnitario = preciosProductos.get(id);
                }
                
                // Formato exigido: Nombre;Precio (dejando el ordenamiento por la cantidad vendida)
                escritor.write(nombre + ";" + String.format("%.2f", precioUnitario));
                escritor.newLine();
            }
        }
    }
}