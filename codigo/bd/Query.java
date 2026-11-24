package bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import modelo.Editorial;
import modelo.Notificacion;
import modelo.Reservacion;
import modelo.Stand;

public class Query extends CrudGenerico {

    private static final Mapeador<Stand> MAP_STAND = rs -> new Stand(
            rs.getString("id"),
            rs.getString("ubicacion"),
            rs.getDouble("metros_cuadrados"),
            rs.getDouble("costo"),
            rs.getString("estado")
    );

    private static final Mapeador<Editorial> MAP_EDITORIAL = rs -> new Editorial(
            rs.getString("id"),
            rs.getString("nombre"),
            rs.getString("pais"),
            rs.getString("contacto")
    );

    private static final Mapeador<Reservacion> MAP_RESERVACION = rs -> new Reservacion(
            rs.getString("id"),
            rs.getString("id_stand"),
            rs.getString("id_editorial"),
            rs.getString("fecha"),
            rs.getString("estado_pago")
    );

    private static final Mapeador<Notificacion> MAP_NOTIFICACION = rs -> new Notificacion(
            rs.getString("id"),
            rs.getString("id_editorial"),
            rs.getString("titulo"),
            rs.getString("mensaje"),
            rs.getString("fecha"),
            rs.getBoolean("leida")
    );

    public void inicializarTablas() throws SQLException {
        String sqlStands = "CREATE TABLE IF NOT EXISTS stands ("
                + "id VARCHAR(20) PRIMARY KEY, "
                + "ubicacion VARCHAR(100) NOT NULL, "
                + "metros_cuadrados DOUBLE NOT NULL, "
                + "costo DOUBLE NOT NULL, "
                + "estado VARCHAR(30) NOT NULL"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        String sqlEditoriales = "CREATE TABLE IF NOT EXISTS editoriales ("
                + "id VARCHAR(20) PRIMARY KEY, "
                + "nombre VARCHAR(100) NOT NULL, "
                + "pais VARCHAR(60) NOT NULL, "
                + "contacto VARCHAR(100) NOT NULL"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        String sqlReservaciones = "CREATE TABLE IF NOT EXISTS reservaciones ("
                + "id VARCHAR(20) PRIMARY KEY, "
                + "id_stand VARCHAR(20) NOT NULL UNIQUE, "
                + "id_editorial VARCHAR(20) NOT NULL, "
                + "fecha VARCHAR(20) NOT NULL, "
                + "estado_pago VARCHAR(30) NOT NULL, "
                + "CONSTRAINT fk_res_stand FOREIGN KEY (id_stand) REFERENCES stands(id) "
                + "ON UPDATE RESTRICT ON DELETE RESTRICT, "
                + "CONSTRAINT fk_res_editorial FOREIGN KEY (id_editorial) REFERENCES editoriales(id) "
                + "ON UPDATE RESTRICT ON DELETE RESTRICT"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        String sqlNotificaciones = "CREATE TABLE IF NOT EXISTS notificaciones ("
                + "id VARCHAR(30) PRIMARY KEY, "
                + "id_editorial VARCHAR(20) NOT NULL, "
                + "titulo VARCHAR(100) NOT NULL, "
                + "mensaje VARCHAR(255) NOT NULL, "
                + "fecha VARCHAR(20) NOT NULL, "
                + "leida BOOLEAN NOT NULL DEFAULT FALSE, "
                + "CONSTRAINT fk_noti_editorial FOREIGN KEY (id_editorial) REFERENCES editoriales(id) "
                + "ON UPDATE RESTRICT ON DELETE CASCADE"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlStands);
            stmt.execute(sqlEditoriales);
            stmt.execute(sqlReservaciones);
            stmt.execute(sqlNotificaciones);
        }
        cargarCatalogoInicialSiVacio();
    }

    public void cargarCatalogoInicialSiVacio() throws SQLException {
        try (Connection conn = conectar()) {
            if (contarRegistros(conn, "reservaciones") == 0 && existenStandsConPrefijoST(conn)) {
                try (Statement st = conn.createStatement()) {
                    st.execute("DELETE FROM stands WHERE id LIKE 'ST-%'");
                }
            }

            if (contarRegistros(conn, "stands") == 0) {
                String sqlStand = "INSERT INTO stands (id, ubicacion, metros_cuadrados, costo, estado) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlStand)) {
                    Object[][] iniciales = {
                        {"01", "Pabellon A - Pasillo Principal", 12.0, 4500.0, "Disponible"},
                        {"02", "Pabellon A - Zona Central", 16.0, 6200.0, "Disponible"},
                        {"03", "Pabellon B - Esquina Norte", 20.0, 8000.0, "Disponible"},
                        {"04", "Pabellon B - Area Infantil", 14.0, 5000.0, "Disponible"},
                        {"05", "Pabellon C - Foro Cultural", 24.0, 9500.0, "Reservado"}
                    };
                    for (Object[] fila : iniciales) {
                        ps.setString(1, (String) fila[0]);
                        ps.setString(2, (String) fila[1]);
                        ps.setDouble(3, (Double) fila[2]);
                        ps.setDouble(4, (Double) fila[3]);
                        ps.setString(5, (String) fila[4]);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            if (contarRegistros(conn, "editoriales") == 0) {
                String sqlEd = "INSERT INTO editoriales (id, nombre, pais, contacto) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlEd)) {
                    String[][] eds = {
                        {"01", "Fondo de Cultura Economica", "Mexico", "ferias@fce.mx"},
                        {"02", "Editorial Anagrama", "Espana", "contacto@anagrama.es"}
                    };
                    for (String[] fila : eds) {
                        ps.setString(1, fila[0]);
                        ps.setString(2, fila[1]);
                        ps.setString(3, fila[2]);
                        ps.setString(4, fila[3]);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            if (contarRegistros(conn, "reservaciones") == 0
                    && existeRegistro(conn, "stands", "05")
                    && existeRegistro(conn, "editoriales", "02")) {
                String hoy = LocalDate.now().toString();
                ejecutarEscritura(conn,
                        "INSERT INTO reservaciones (id, id_stand, id_editorial, fecha, estado_pago) VALUES (?, ?, ?, ?, ?)",
                        ps -> {
                            ps.setString(1, "R-01");
                            ps.setString(2, "05");
                            ps.setString(3, "02");
                            ps.setString(4, hoy);
                            ps.setString(5, "Pendiente");
                        });
                ejecutarEscritura(conn, "UPDATE stands SET estado = 'Reservado' WHERE id = '05'", null);
            }

            if (contarRegistros(conn, "notificaciones") == 0
                    && existeRegistro(conn, "editoriales", "01")
                    && existeRegistro(conn, "editoriales", "02")) {
                String hoy = LocalDate.now().toString();
                String sqlNoti = "INSERT INTO notificaciones (id, id_editorial, titulo, mensaje, fecha, leida) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlNoti)) {
                    Object[][] notis = {
                        {"NOTI-01", "01", "Bienvenida a la Feria", "Catalogo de stands 2026 ya disponible para renta.", hoy, false},
                        {"NOTI-02", "02", "Aviso de Inscripcion", "Tu apartado del Stand 05 esta registrado. Recuerda completar tu pago.", hoy, false}
                    };
                    for (Object[] fila : notis) {
                        ps.setString(1, (String) fila[0]);
                        ps.setString(2, (String) fila[1]);
                        ps.setString(3, (String) fila[2]);
                        ps.setString(4, (String) fila[3]);
                        ps.setString(5, (String) fila[4]);
                        ps.setBoolean(6, (Boolean) fila[5]);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        }
    }

    private boolean existenStandsConPrefijoST(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM stands WHERE id LIKE 'ST-%'")) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // ==================== CRUD: STANDS ====================

    public void insertarStand(Stand s) throws SQLException {
        ejecutarEscritura(
                "INSERT INTO stands (id, ubicacion, metros_cuadrados, costo, estado) VALUES (?, ?, ?, ?, ?)",
                ps -> {
                    ps.setString(1, s.getId());
                    ps.setString(2, s.getUbicacion());
                    ps.setDouble(3, s.getMetrosCuadrados());
                    ps.setDouble(4, s.getCosto());
                    ps.setString(5, s.getEstado());
                }
        );
    }

    public List<Stand> obtenerStands() throws SQLException {
        return consultarLista("SELECT id, ubicacion, metros_cuadrados, costo, estado FROM stands ORDER BY id", null, MAP_STAND);
    }

    public List<Stand> obtenerStandsDisponibles() throws SQLException {
        return consultarLista("SELECT id, ubicacion, metros_cuadrados, costo, estado FROM stands WHERE estado = 'Disponible' ORDER BY id", null, MAP_STAND);
    }

    public void actualizarStand(Stand s) throws SQLException {
        try (Connection conn = conectar()) {
            if ("Disponible".equalsIgnoreCase(s.getEstado()) && tieneReservacionActiva(conn, s.getId())) {
                throw new SQLException("Conflicto de estado: el stand '" + s.getId()
                        + "' tiene una reservacion activa y no puede marcarse como Disponible.");
            }
            int filas = ejecutarEscritura(conn,
                    "UPDATE stands SET ubicacion = ?, metros_cuadrados = ?, costo = ?, estado = ? WHERE id = ?",
                    ps -> {
                        ps.setString(1, s.getUbicacion());
                        ps.setDouble(2, s.getMetrosCuadrados());
                        ps.setDouble(3, s.getCosto());
                        ps.setString(4, s.getEstado());
                        ps.setString(5, s.getId());
                    });
            if (filas == 0) {
                throw new SQLException("No existe ningun stand con el ID '" + s.getId() + "' para actualizar.");
            }
        }
    }

    public void recalcularCostoTodosLosStands(double costoPorMetro) throws SQLException {
        ejecutarEscritura("UPDATE stands SET costo = ROUND(metros_cuadrados * ?, 2)",
                ps -> ps.setDouble(1, costoPorMetro));
    }

    public void eliminarStand(String id) throws SQLException {
        eliminarPorId("stands", id, "stand");
    }

    // ==================== CRUD: EDITORIALES ====================

    public void insertarEditorial(Editorial e) throws SQLException {
        try (Connection conn = conectar()) {
            conn.setAutoCommit(false);
            try {
                ejecutarEscritura(conn,
                        "INSERT INTO editoriales (id, nombre, pais, contacto) VALUES (?, ?, ?, ?)",
                        ps -> {
                            ps.setString(1, e.getId());
                            ps.setString(2, e.getNombre());
                            ps.setString(3, e.getPais());
                            ps.setString(4, e.getContacto());
                        });

                registrarNotificacionInterna(conn,
                        "NOTI-B-" + e.getId(),
                        e.getId(),
                        "Bienvenida a la Feria",
                        "Editorial '" + e.getNombre() + "' registrada exitosamente. Ya puedes rentar stands disponibles.");

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Editorial> obtenerEditoriales() throws SQLException {
        return consultarLista("SELECT id, nombre, pais, contacto FROM editoriales ORDER BY id", null, MAP_EDITORIAL);
    }

    public Editorial buscarEditorialPorId(String id) throws SQLException {
        return consultarUno("SELECT id, nombre, pais, contacto FROM editoriales WHERE id = ?",
                ps -> ps.setString(1, id), MAP_EDITORIAL);
    }

    public void actualizarEditorial(Editorial e) throws SQLException {
        int filas = ejecutarEscritura(
                "UPDATE editoriales SET nombre = ?, pais = ?, contacto = ? WHERE id = ?",
                ps -> {
                    ps.setString(1, e.getNombre());
                    ps.setString(2, e.getPais());
                    ps.setString(3, e.getContacto());
                    ps.setString(4, e.getId());
                });
        if (filas == 0) {
            throw new SQLException("No existe ninguna editorial con el ID '" + e.getId() + "' para actualizar.");
        }
    }

    public void eliminarEditorial(String id) throws SQLException {
        eliminarPorId("editoriales", id, "editorial");
    }

    // ==================== CRUD: RESERVACIONES (TRANSACCIONAL) ====================

    public void insertarReservacion(Reservacion r) throws SQLException {
        try (Connection conn = conectar()) {
            conn.setAutoCommit(false);
            try {
                String estadoActual = obtenerEstadoStand(conn, r.getIdStand());
                if (estadoActual == null) {
                    throw new SQLException("Conflicto de reservacion: el stand '" + r.getIdStand() + "' no existe.");
                }
                if (!"Disponible".equalsIgnoreCase(estadoActual)) {
                    throw new SQLException("Conflicto de reservacion: el stand '" + r.getIdStand() + "' ya se encuentra ocupado.");
                }

                ejecutarEscritura(conn,
                        "INSERT INTO reservaciones (id, id_stand, id_editorial, fecha, estado_pago) VALUES (?, ?, ?, ?, ?)",
                        ps -> {
                            ps.setString(1, r.getId());
                            ps.setString(2, r.getIdStand());
                            ps.setString(3, r.getIdEditorial());
                            ps.setString(4, r.getFecha());
                            ps.setString(5, r.getEstadoPago());
                        });

                ejecutarEscritura(conn, "UPDATE stands SET estado = 'Reservado' WHERE id = ?",
                        ps -> ps.setString(1, r.getIdStand()));

                registrarNotificacionInterna(conn,
                        "NOTI-C-" + r.getId(),
                        r.getIdEditorial(),
                        "Reservacion Confirmada",
                        "Reservacion " + r.getId() + ": Stand " + r.getIdStand() + " asignado (Pago: " + r.getEstadoPago() + ").");

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Reservacion> obtenerReservaciones() throws SQLException {
        return consultarLista("SELECT id, id_stand, id_editorial, fecha, estado_pago FROM reservaciones ORDER BY id",
                null, MAP_RESERVACION);
    }

    public List<Reservacion> obtenerReservacionesPorEditorial(String idEditorial) throws SQLException {
        return consultarLista("SELECT id, id_stand, id_editorial, fecha, estado_pago FROM reservaciones WHERE id_editorial = ? ORDER BY id",
                ps -> ps.setString(1, idEditorial), MAP_RESERVACION);
    }

    public void actualizarReservacion(Reservacion r) throws SQLException {
        try (Connection conn = conectar()) {
            conn.setAutoCommit(false);
            try {
                String standAnterior = null;
                try (PreparedStatement psSel = conn.prepareStatement("SELECT id_stand FROM reservaciones WHERE id = ?")) {
                    psSel.setString(1, r.getId());
                    try (ResultSet rs = psSel.executeQuery()) {
                        if (rs.next()) {
                            standAnterior = rs.getString("id_stand");
                        }
                    }
                }

                if (standAnterior == null) {
                    throw new SQLException("No existe ninguna reservacion con el ID '" + r.getId() + "' para actualizar.");
                }

                boolean cambiaStand = !standAnterior.equals(r.getIdStand());
                if (cambiaStand) {
                    String estadoNuevoStand = obtenerEstadoStand(conn, r.getIdStand());
                    if (estadoNuevoStand == null) {
                        throw new SQLException("Conflicto al actualizar: el stand destino '" + r.getIdStand() + "' no existe.");
                    }
                    if (!"Disponible".equalsIgnoreCase(estadoNuevoStand)) {
                        throw new SQLException("Conflicto al actualizar: el stand destino '" + r.getIdStand() + "' ya esta ocupado.");
                    }
                }

                ejecutarEscritura(conn,
                        "UPDATE reservaciones SET id_stand = ?, id_editorial = ?, fecha = ?, estado_pago = ? WHERE id = ?",
                        ps -> {
                            ps.setString(1, r.getIdStand());
                            ps.setString(2, r.getIdEditorial());
                            ps.setString(3, r.getFecha());
                            ps.setString(4, r.getEstadoPago());
                            ps.setString(5, r.getId());
                        });

                if (cambiaStand) {
                    final String anterior = standAnterior;
                    ejecutarEscritura(conn, "UPDATE stands SET estado = 'Disponible' WHERE id = ?",
                            ps -> ps.setString(1, anterior));
                    ejecutarEscritura(conn, "UPDATE stands SET estado = 'Reservado' WHERE id = ?",
                            ps -> ps.setString(1, r.getIdStand()));
                }

                registrarNotificacionInterna(conn,
                        "NOTI-U-" + r.getId() + "-" + (System.currentTimeMillis() % 10000),
                        r.getIdEditorial(),
                        "Reservacion Actualizada",
                        "Reservacion " + r.getId() + " modificada: Stand " + r.getIdStand() + ", Pago: " + r.getEstadoPago() + ".");

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void eliminarReservacion(String id) throws SQLException {
        try (Connection conn = conectar()) {
            conn.setAutoCommit(false);
            try {
                String idStand = null;
                String idEditorial = null;
                try (PreparedStatement psSel = conn.prepareStatement("SELECT id_stand, id_editorial FROM reservaciones WHERE id = ?")) {
                    psSel.setString(1, id);
                    try (ResultSet rs = psSel.executeQuery()) {
                        if (rs.next()) {
                            idStand = rs.getString("id_stand");
                            idEditorial = rs.getString("id_editorial");
                        }
                    }
                }

                if (idStand == null) {
                    throw new SQLException("No existe ninguna reservacion con el ID '" + id + "' para eliminar.");
                }

                final String standLiberar = idStand;
                ejecutarEscritura(conn, "DELETE FROM reservaciones WHERE id = ?", ps -> ps.setString(1, id));
                ejecutarEscritura(conn, "UPDATE stands SET estado = 'Disponible' WHERE id = ?",
                        ps -> ps.setString(1, standLiberar));

                registrarNotificacionInterna(conn,
                        "NOTI-D-" + id + "-" + (System.currentTimeMillis() % 10000),
                        idEditorial,
                        "Reservacion Cancelada",
                        "La reservacion " + id + " del stand " + idStand + " fue cancelada y el stand quedo libre.");

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // ==================== ALERTAS DE ADMINISTRADOR ====================

    public List<String> obtenerAlertasAdminPagosPendientes() throws SQLException {
        String sql = "SELECT r.id AS id_reserva, r.fecha, e.id AS id_editorial, e.nombre AS nombre_editorial, "
                + "e.contacto, s.id AS id_stand, s.ubicacion, s.costo "
                + "FROM reservaciones r "
                + "INNER JOIN editoriales e ON r.id_editorial = e.id "
                + "INNER JOIN stands s ON r.id_stand = s.id "
                + "WHERE LOWER(r.estado_pago) = 'pendiente' "
                + "ORDER BY r.fecha ASC, r.id ASC";

        return consultarLista(sql, null, rs -> String.format(
                "[ADEUDO PENDIENTE] La editorial '%s' (ID: %s | Contacto: %s) aun no paga su reserva %s del Stand %s (%s) | Fecha: %s | Monto: $%.2f",
                rs.getString("nombre_editorial"),
                rs.getString("id_editorial"),
                rs.getString("contacto"),
                rs.getString("id_reserva"),
                rs.getString("id_stand"),
                rs.getString("ubicacion"),
                rs.getString("fecha"),
                rs.getDouble("costo")
        ));
    }

    // ==================== CRUD: NOTIFICACIONES ====================

    public void insertarNotificacion(Notificacion n) throws SQLException {
        ejecutarEscritura(
                "INSERT INTO notificaciones (id, id_editorial, titulo, mensaje, fecha, leida) VALUES (?, ?, ?, ?, ?, ?)",
                ps -> {
                    ps.setString(1, n.getId());
                    ps.setString(2, n.getIdEditorial());
                    ps.setString(3, n.getTitulo());
                    ps.setString(4, n.getMensaje());
                    ps.setString(5, n.getFecha());
                    ps.setBoolean(6, n.isLeida());
                });
    }

    public List<Notificacion> obtenerNotificaciones() throws SQLException {
        return consultarLista("SELECT id, id_editorial, titulo, mensaje, fecha, leida FROM notificaciones ORDER BY id",
                null, MAP_NOTIFICACION);
    }

    public List<Notificacion> obtenerNotificacionesPorEditorial(String idEditorial) throws SQLException {
        return consultarLista("SELECT id, id_editorial, titulo, mensaje, fecha, leida FROM notificaciones WHERE id_editorial = ? ORDER BY leida ASC, id DESC",
                ps -> ps.setString(1, idEditorial), MAP_NOTIFICACION);
    }

    public int contarNoLeidasPorEditorial(String idEditorial) throws SQLException {
        Integer total = consultarUno("SELECT COUNT(*) FROM notificaciones WHERE id_editorial = ? AND leida = FALSE",
                ps -> ps.setString(1, idEditorial), rs -> rs.getInt(1));
        return total != null ? total : 0;
    }

    public void actualizarNotificacion(Notificacion n) throws SQLException {
        int filas = ejecutarEscritura(
                "UPDATE notificaciones SET titulo = ?, mensaje = ?, leida = ? WHERE id = ?",
                ps -> {
                    ps.setString(1, n.getTitulo());
                    ps.setString(2, n.getMensaje());
                    ps.setBoolean(3, n.isLeida());
                    ps.setString(4, n.getId());
                });
        if (filas == 0) {
            throw new SQLException("No existe ninguna notificacion con el ID '" + n.getId() + "' para actualizar.");
        }
    }

    public void marcarNotificacionLeida(String id) throws SQLException {
        int filas = ejecutarEscritura("UPDATE notificaciones SET leida = TRUE WHERE id = ?",
                ps -> ps.setString(1, id));
        if (filas == 0) {
            throw new SQLException("No existe ninguna notificacion con el ID '" + id + "'.");
        }
    }

    public void marcarTodasLeidasPorEditorial(String idEditorial) throws SQLException {
        ejecutarEscritura("UPDATE notificaciones SET leida = TRUE WHERE id_editorial = ?",
                ps -> ps.setString(1, idEditorial));
    }

    public void eliminarNotificacion(String id) throws SQLException {
        eliminarPorId("notificaciones", id, "notificacion");
    }

    // ==================== AUXILIARES DE INTEGRIDAD ====================

    private void registrarNotificacionInterna(Connection conn, String idNoti, String idEditorial, String titulo, String mensaje) throws SQLException {
        String sql = "INSERT INTO notificaciones (id, id_editorial, titulo, mensaje, fecha, leida) VALUES (?, ?, ?, ?, ?, FALSE) "
                + "ON DUPLICATE KEY UPDATE titulo = VALUES(titulo), mensaje = VALUES(mensaje), fecha = VALUES(fecha), leida = FALSE";
        ejecutarEscritura(conn, sql, ps -> {
            ps.setString(1, idNoti);
            ps.setString(2, idEditorial);
            ps.setString(3, titulo);
            ps.setString(4, mensaje);
            ps.setString(5, LocalDate.now().toString());
        });
    }

    private String obtenerEstadoStand(Connection conn, String idStand) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT estado FROM stands WHERE id = ?")) {
            ps.setString(1, idStand);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("estado") : null;
            }
        }
    }

    private boolean tieneReservacionActiva(Connection conn, String idStand) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM reservaciones WHERE id_stand = ?")) {
            ps.setString(1, idStand);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
