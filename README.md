# Generación y clasificación de datos de ventas

Proyecto del módulo Conceptos Fundamentales de Programación, Politécnico Grancolombiano.

Un programa se inventa los archivos de ventas de una empresa y otro los lee, saca las
cuentas y dice quién vendió más y qué producto se movió más. Todo con archivos planos.

Elaborado por:
Santiago Gómez Peña,
Elizabeth Ospina Castaño,
Yuliana Gaviria Valencia,
Miller Hernán Niño Cárdenas

---

## Cómo se ejecuta

Hay dos clases con método `main` y ninguna pide datos por teclado. El orden importa: la
segunda no tiene nada que leer si la primera no ha corrido.

**1. `generate_info.GenerateInfoFiles`** deja los archivos de entrada:

| Archivo | Qué trae |
|---|---|
| `productos_info.txt` | 10 productos con su id, nombre y precio |
| `vendedores_info.txt` | 5 vendedores con documento, nombres y apellidos |
| `ventas_<nombre>_<documento>.txt` | las ventas, uno o dos archivos por vendedor |

**2. `reports.main`** deja los resultados:

| Archivo | Qué trae |
|---|---|
| `reporte_vendedores.csv` | los vendedores, del que más recogió al que menos |
| `reporte_productos.csv` | los productos, del más vendido al menos vendido |
| `errores_validacion.log` | lo que vino mal en los datos, con archivo y línea |
| `reportes.ser` | los mismos resultados guardados como objetos |

En Eclipse hay que refrescar la carpeta del proyecto (F5) para ver los archivos generados.

Desde terminal:

```bash
javac -encoding UTF-8 -d bin src/generate_info/*.java src/reports/*.java
java -cp bin generate_info.GenerateInfoFiles
java -cp bin reports.main
```

## Formato de los archivos

```
productos_info.txt    IDProducto;NombreProducto;PrecioPorUnidad
                      PROD-001;Pantalon 1;111300.36

vendedores_info.txt   TipoDocumento;NumeroDocumento;Nombres;Apellidos
                      CC;2541978445;Duvan;Torres

ventas_*.txt          TipoDocumento;NumeroDocumento      <- primera línea
                      IDProducto;CantidadVendida;        <- una por venta
```

Los dos reportes salen separados por punto y coma, un registro por línea y sin fila de
títulos. El de vendedores lleva nombre y dinero recaudado; el de productos, nombre y
precio, ordenado por la cantidad vendida.

## Cómo está repartido el código

```
src/generate_info/
    GenerateInfoFiles.java     genera los archivos de entrada

src/reports/
    main.java                  lee, saca las cuentas y escribe los reportes
    AppFiles.java              nombres de archivo y codificación, en un solo sitio
    Product.java               un producto del catálogo
    Salesman.java              un vendedor
    ProductReport.java         cuenta de unidades vendidas de un producto
    SalesmanReport.java        cuenta de dinero y unidades de un vendedor
    ReportBundle.java          los dos reportes empaquetados
    DataLoader.java            abre los catálogos y revisa que estén bien
    SalesProcessor.java        recorre las ventas, suma y ordena
    ReportWriter.java          escribe los dos CSV
    SerializationService.java  guarda y recupera los reportes en binario
    ValidationLog.java         donde se anota todo lo que vino mal
```

## Lo que el programa hace de más

- **Un mismo vendedor puede tener varios archivos de ventas.** Las ventas se le cuentan a
  quien aparezca en la primera línea del archivo, no a quien diga el nombre del archivo,
  así que dos archivos con el mismo documento se suman en una única cuenta.
- **Los reportes también quedan serializados** en `reportes.ser`, y el programa los vuelve
  a leer para comprobar que quedaron completos.
- **Los datos malos no tumban el proceso.** Se detectan líneas incompletas, ids de producto
  que no existen, cantidades y precios que no son números o que son cero o negativos, tipos
  de documento desconocidos, ids y documentos repetidos, vendedores que no están en
  `vendedores_info.txt` y archivos vacíos. El dato se descarta, queda anotado en
  `errores_validacion.log` con archivo y línea, y el proceso sigue.

## Un par de decisiones

- **El dinero se maneja con `BigDecimal`, no con `double`**, porque al sumar totales con
  `double` aparecen decimales que no corresponden a ninguna suma real. Se escribe con dos
  decimales y punto decimal, sin separador de miles, para no partir el CSV en columnas.
- **Los precios se generan con `Locale.ROOT`.** Sin eso, en un equipo configurado en
  español el archivo salía con coma decimal y después no se podía leer.
- **Los empates se desempatan por nombre**, para que dos corridas sobre los mismos datos
  entreguen el reporte en el mismo orden.
- **Los archivos generados no se versionan.** Cambian en cada ejecución; se regeneran
  corriendo el primer programa.

Requiere Java 8 o superior.
