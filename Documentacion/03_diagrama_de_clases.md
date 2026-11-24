# Diagrama de Clases — Arquitectura MVC y Capa de Datos

## 1. Descripción Estructural
El sistema separa las entidades de dominio (`modelo`), la persistencia y transaccionalidad (`bd`), la lógica de coordinación y tarifa por $m^2$ (`controlador`), y la interacción por consola (`vista`).

## 2. Diagrama de Clases (Mermaid)

```mermaid
classDiagram
    class Main {
        +main(String[] args) void
    }

    class VistaConsola {
        -FeriaControlador controlador
        -Scanner scanner
        +iniciar() void
        -menuAdministrador() void
        -menuConfiguracionCRUD() void
        -configurarCostoPorMetro() void
        -elegirEstadoStand(String estadoActual) String
        -menuSesionEditorial(Editorial editorial) void
        -gestionarMisReservaciones(Editorial editorial) void
        -gestionarBuzonEditorial(Editorial editorial) void
        +ejecutarPruebasDeConflictos() void
    }

    class FeriaControlador {
        -Query query
        -double costoPorMetroCuadrado
        +getCostoPorMetroCuadrado() double
        +calcularCostoStand(double metrosCuadrados) double
        +actualizarCostoPorMetroCuadrado(double nuevoCostoPorMetro, boolean recalcularStandsActuales) boolean
        +iniciarBaseDeDatos() boolean
        +registrarStand(Stand stand) boolean
        +listarStands() List~Stand~
        +listarStandsDisponibles() List~Stand~
        +actualizarStand(Stand stand) boolean
        +eliminarStand(String id) boolean
        +registrarEditorial(Editorial editorial) boolean
        +buscarEditorial(String id) Editorial
        +actualizarEditorial(Editorial editorial) boolean
        +registrarReservacion(Reservacion reservacion) boolean
        +listarReservacionesDeEditorial(String idEditorial) List~Reservacion~
        +actualizarReservacion(Reservacion reservacion) boolean
        +eliminarReservacion(String id) boolean
        +listarAlertasAdminPagosPendientes() List~String~
        +enviarNotificacion(Notificacion notificacion) boolean
        +marcarComoLeida(String idNotificacion) boolean
        +marcarTodasComoLeidas(String idEditorial) void
    }

    class CrudGenerico {
        <<abstract>>
        #ConexionBD conexionBD
        #conectar() Connection
        #ejecutarEscritura(String sql, Parametrizador params) int
        #ejecutarEscritura(Connection conn, String sql, Parametrizador params) int
        #consultarLista(String sql, Parametrizador params, Mapeador~T~ mapper) List~T~
        #consultarUno(String sql, Parametrizador params, Mapeador~T~ mapper) T
        #eliminarPorId(String tabla, String id, String nombreEntidad) void
        #contarRegistros(Connection conn, String tabla) int
        #existeRegistro(Connection conn, String tabla, String id) boolean
    }

    class Query {
        +inicializarTablas() void
        +cargarCatalogoInicialSiVacio() void
        +recalcularCostoTodosLosStands(double costoPorMetro) void
        +insertarReservacion(Reservacion r) void
        +actualizarReservacion(Reservacion r) void
        +eliminarReservacion(String id) void
        +obtenerAlertasAdminPagosPendientes() List~String~
    }

    class ConexionBD {
        +getConnection() Connection
    }

    class Stand {
        -String id
        -String ubicacion
        -double metrosCuadrados
        -double costo
        -String estado
    }

    class Editorial {
        -String id
        -String nombre
        -String pais
        -String contacto
    }

    class Reservacion {
        -String id
        -String idStand
        -String idEditorial
        -String fecha
        -String estadoPago
    }

    class Notificacion {
        -String id
        -String idEditorial
        -String titulo
        -String mensaje
        -String fecha
        -boolean leida
    }

    Main --> FeriaControlador
    Main --> VistaConsola
    VistaConsola --> FeriaControlador
    FeriaControlador --> Query
    Query --|> CrudGenerico
    CrudGenerico --> ConexionBD
    Query ..> Stand : mapea/persiste
    Query ..> Editorial : mapea/persiste
    Query ..> Reservacion : mapea/persiste
    Query ..> Notificacion : mapea/persiste
```
