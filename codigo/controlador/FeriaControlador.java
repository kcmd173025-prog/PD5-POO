package controlador;

import bd.Query;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collections;
import java.util.List;
import modelo.Editorial;
import modelo.Notificacion;
import modelo.Reservacion;
import modelo.Stand;

public class FeriaControlador {
    private final Query query;
    private double costoPorMetroCuadrado = 375.0;

    public FeriaControlador() {
        this.query = new Query();
    }

    public double getCostoPorMetroCuadrado() {
        return costoPorMetroCuadrado;
    }

    public double calcularCostoStand(double metrosCuadrados) {
        return Math.round(metrosCuadrados * costoPorMetroCuadrado * 100.0) / 100.0;
    }

    public boolean actualizarCostoPorMetroCuadrado(double nuevoCostoPorMetro, boolean recalcularStandsActuales) {
        if (nuevoCostoPorMetro <= 0) {
            System.out.println("[AVISO] El costo por metro cuadrado debe ser mayor a 0.");
            return false;
        }
        this.costoPorMetroCuadrado = nuevoCostoPorMetro;
        System.out.printf("[OK] Costo global por metro cuadrado actualizado a $%.2f.%n", nuevoCostoPorMetro);
        if (recalcularStandsActuales) {
            try {
                query.recalcularCostoTodosLosStands(nuevoCostoPorMetro);
                System.out.println("[OK] Costo de todos los stands recalculado en la base de datos.");
            } catch (SQLException e) {
                System.out.println("[ERROR SQL] No se pudo recalcular el costo de los stands: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public boolean iniciarBaseDeDatos() {
        try {
            query.inicializarTablas();
            System.out.println("[OK] Conexion establecida con Clever Cloud y catalogo de stands listo.");
            return true;
        } catch (SQLException e) {
            System.err.println("[ERROR DE CONEXION] No se pudo conectar a Clever Cloud: " + e.getMessage());
            return false;
        }
    }

    // ==================== STANDS ====================

    public boolean registrarStand(Stand stand) {
        try {
            query.insertarStand(stand);
            System.out.println("[OK] Stand registrado en la nube: " + stand.getId());
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("[CONFLICTO - ID DUPLICADO] Ya existe un stand con ID '" + stand.getId() + "'.");
            return false;
        } catch (SQLException e) {
            System.out.println("[ERROR SQL] No se pudo registrar el stand: " + e.getMessage());
            return false;
        }
    }

    public List<Stand> listarStands() {
        try {
            return query.obtenerStands();
        } catch (SQLException e) {
            System.out.println("[ERROR SQL] No se pudieron consultar los stands: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Stand> listarStandsDisponibles() {
        try {
            return query.obtenerStandsDisponibles();
        } catch (SQLException e) {
            System.out.println("[ERROR SQL] No se pudieron consultar los stands disponibles: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean actualizarStand(Stand stand) {
        try {
            query.actualizarStand(stand);
            System.out.println("[OK] Stand actualizado: " + stand.getId());
            return true;
        } catch (SQLException e) {
            System.out.println("[CONFLICTO AL ACTUALIZAR STAND] " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarStand(String id) {
        try {
            query.eliminarStand(id);
            System.out.println("[OK] Stand eliminado: " + id);
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("[CONFLICTO AL ELIMINAR STAND] No se puede eliminar '" + id
                    + "' porque tiene una reservacion asociada.");
            return false;
        } catch (SQLException e) {
            System.out.println("[CONFLICTO AL ELIMINAR STAND] " + e.getMessage());
            return false;
        }
    }

    // ==================== EDITORIALES ====================

    public boolean registrarEditorial(Editorial editorial) {
        try {
            query.insertarEditorial(editorial);
            System.out.println("[OK] Editorial guardada en la base de datos: " + editorial.getNombre() + " (" + editorial.getId() + ")");
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("[CONFLICTO - ID DUPLICADO] Ya existe una editorial con ID '" + editorial.getId() + "'.");
            return false;
        } catch (SQLException e) {
            System.out.println("[ERROR SQL] No se pudo registrar la editorial: " + e.getMessage());
            return false;
        }
    }

    public List<Editorial> listarEditoriales() {
        try {
            return query.obtenerEditoriales();
        } catch (SQLException e) {
            System.out.println("[ERROR SQL] No se pudieron consultar las editoriales: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Editorial buscarEditorial(String id) {
        try {
            return query.buscarEditorialPorId(id);
        } catch (SQLException e) {
            System.out.println("[ERROR SQL] Error al buscar la editorial: " + e.getMessage());
            return null;
        }
    }

    public boolean actualizarEditorial(Editorial editorial) {
        try {
            query.actualizarEditorial(editorial);
            System.out.println("[OK] Editorial actualizada: " + editorial.getId());
            return true;
        } catch (SQLException e) {
            System.out.println("[CONFLICTO AL ACTUALIZAR EDITORIAL] " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarEditorial(String id) {
        try {
            query.eliminarEditorial(id);
            System.out.println("[OK] Editorial eliminada: " + id);
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("[CONFLICTO AL ELIMINAR EDITORIAL] No se puede eliminar '" + id
                    + "' porque tiene reservaciones asociadas.");
            return false;
        } catch (SQLException e) {
            System.out.println("[CONFLICTO AL ELIMINAR EDITORIAL] " + e.getMessage());
            return false;
        }
    }

    // ==================== RESERVACIONES ====================

    public boolean registrarReservacion(Reservacion reservacion) {
        try {
            query.insertarReservacion(reservacion);
            System.out.println("[OK] Stand " + reservacion.getIdStand() + " rentado exitosamente (Reservacion: " + reservacion.getId() + ").");
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("[CONFLICTO EN RESERVACION] ID duplicado, stand ya reservado o editorial inexistente ("
                    + e.getMessage() + ").");
            return false;
        } catch (SQLException e) {
            System.out.println("[CONFLICTO EN RESERVACION] " + e.getMessage());
            return false;
        }
    }

    public List<Reservacion> listarReservaciones() {
        try {
            return query.obtenerReservaciones();
        } catch (SQLException e) {
            System.out.println("[ERROR SQL] No se pudieron consultar las reservaciones: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Reservacion> listarReservacionesDeEditorial(String idEditorial) {
        try {
            return query.obtenerReservacionesPorEditorial(idEditorial);
        } catch (SQLException e) {
            System.out.println("[ERROR SQL] No se pudieron consultar tus reservaciones: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean actualizarReservacion(Reservacion reservacion) {
        try {
            query.actualizarReservacion(reservacion);
            System.out.println("[OK] Reservacion actualizada: " + reservacion.getId());
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("[CONFLICTO AL ACTUALIZAR RESERVACION] El stand o la editorial indicada no existe o genera conflicto ("
                    + e.getMessage() + ").");
            return false;
        } catch (SQLException e) {
            System.out.println("[CONFLICTO AL ACTUALIZAR RESERVACION] " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarReservacion(String id) {
        try {
            query.eliminarReservacion(id);
            System.out.println("[OK] Reservacion cancelada y stand liberado: " + id);
            return true;
        } catch (SQLException e) {
            System.out.println("[CONFLICTO AL ELIMINAR RESERVACION] " + e.getMessage());
            return false;
        }
    }

    // ==================== ALERTAS DE ADMINISTRADOR ====================

    public List<String> listarAlertasAdminPagosPendientes() {
        try {
            return query.obtenerAlertasAdminPagosPendientes();
        } catch (SQLException e) {
            System.out.println("[ERROR SQL] No se pudieron consultar las alertas de pago pendiente: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ==================== NOTIFICACIONES (EDITORIALES) ====================

    public boolean enviarNotificacion(Notificacion notificacion) {
        try {
            query.insertarNotificacion(notificacion);
            System.out.println("[OK] Notificacion enviada a '" + notificacion.getIdEditorial() + "' (ID: " + notificacion.getId() + ").");
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("[CONFLICTO EN NOTIFICACION] ID de notificacion duplicado o editorial destino inexistente.");
            return false;
        } catch (SQLException e) {
            System.out.println("[ERROR SQL] No se pudo enviar la notificacion: " + e.getMessage());
            return false;
        }
    }

    public List<Notificacion> listarNotificaciones() {
        try {
            return query.obtenerNotificaciones();
        } catch (SQLException e) {
            System.out.println("[ERROR SQL] No se pudieron consultar las notificaciones: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Notificacion> listarNotificacionesDeEditorial(String idEditorial) {
        try {
            return query.obtenerNotificacionesPorEditorial(idEditorial);
        } catch (SQLException e) {
            System.out.println("[ERROR SQL] No se pudieron consultar tus notificaciones: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public int contarNoLeidas(String idEditorial) {
        try {
            return query.contarNoLeidasPorEditorial(idEditorial);
        } catch (SQLException e) {
            return 0;
        }
    }

    public boolean actualizarNotificacion(Notificacion notificacion) {
        try {
            query.actualizarNotificacion(notificacion);
            System.out.println("[OK] Notificacion actualizada: " + notificacion.getId());
            return true;
        } catch (SQLException e) {
            System.out.println("[CONFLICTO AL ACTUALIZAR NOTIFICACION] " + e.getMessage());
            return false;
        }
    }

    public boolean marcarComoLeida(String idNotificacion) {
        try {
            query.marcarNotificacionLeida(idNotificacion);
            System.out.println("[OK] Notificacion marcada como leida: " + idNotificacion);
            return true;
        } catch (SQLException e) {
            System.out.println("[CONFLICTO EN NOTIFICACION] " + e.getMessage());
            return false;
        }
    }

    public void marcarTodasComoLeidas(String idEditorial) {
        try {
            query.marcarTodasLeidasPorEditorial(idEditorial);
            System.out.println("[OK] Todas las notificaciones marcadas como leidas.");
        } catch (SQLException e) {
            System.out.println("[ERROR SQL] No se pudieron actualizar las notificaciones: " + e.getMessage());
        }
    }

    public boolean eliminarNotificacion(String idNotificacion) {
        try {
            query.eliminarNotificacion(idNotificacion);
            System.out.println("[OK] Notificacion eliminada: " + idNotificacion);
            return true;
        } catch (SQLException e) {
            System.out.println("[CONFLICTO AL ELIMINAR NOTIFICACION] " + e.getMessage());
            return false;
        }
    }
}
