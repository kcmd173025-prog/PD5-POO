# Requisitos No Funcionales — Sistema de Gestión de Stands (Feria del Libro)

## 1. Usabilidad
* **RNF-01:** La interfaz de consola debe presentar menús claros, tablas alineadas y selecciones numéricas (`1` / `2`) para estados y confirmaciones, reduciendo errores de captura manual.
* **RNF-02:** Los mensajes de confirmación, advertencia y conflicto deben ser comprensibles y orientar al usuario ante entradas inválidas o folios inexistentes.

## 2. Fiabilidad e Integridad de Datos
* **RNF-03:** Las operaciones que afectan múltiples tablas (crear reservación, cambiar stand, cancelar reservación o registrar editorial con notificación) deben ejecutarse dentro de transacciones SQL con `commit` ante éxito y `rollback` ante error.
* **RNF-04:** La base de datos debe resguardar la integridad referencial mediante llaves primarias, llaves foráneas y restricción `UNIQUE` en `reservaciones.id_stand` para evitar doble asignación de un mismo stand.

## 3. Mantenibilidad y Arquitectura
* **RNF-05:** El código fuente debe mantenerse separado por paquetes bajo el patrón **Modelo-Vista-Controlador (MVC)** (`modelo`, `bd`, `controlador`, `vista` y punto de entrada `Main`).
* **RNF-06:** Las operaciones SQL genéricas y reutilizables deben centralizarse en la clase base abstracta `CrudGenerico`, mientras `Query` especializa los mapeos y transacciones del dominio.

## 4. Seguridad
* **RNF-07:** Todas las sentencias SQL con parámetros de usuario deben usar `PreparedStatement` para prevenir inyección SQL, y las operaciones genéricas por tabla deben validar contra una lista blanca de tablas permitidas.
* **RNF-08:** Las credenciales de conexión a base de datos no deben difundirse en la documentación pública ni quedar expuestas en repositorios compartidos sin control de acceso.

## 5. Rendimiento y Portabilidad
* **RNF-09:** El proyecto debe compilarse de forma portable mediante `javac -encoding UTF-8` enlazando el driver JDBC desde `lib/` y ejecutarse en entornos Java compatibles con JDBC sin requerir instalación local del servidor de base de datos.
