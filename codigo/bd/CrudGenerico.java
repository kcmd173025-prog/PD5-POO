package bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class CrudGenerico {
    protected final ConexionBD conexionBD;

    private static final Set<String> TABLAS_PERMITIDAS = new HashSet<>(
            Arrays.asList("stands", "editoriales", "reservaciones", "notificaciones")
    );

    @FunctionalInterface
    protected interface Mapeador<T> {
        T mapear(ResultSet rs) throws SQLException;
    }

    @FunctionalInterface
    protected interface Parametrizador {
        void asignar(PreparedStatement ps) throws SQLException;
    }

    protected CrudGenerico() {
        this.conexionBD = new ConexionBD();
    }

    protected Connection conectar() throws SQLException {
        return conexionBD.getConnection();
    }

    private void validarTabla(String tabla) throws SQLException {
        if (!TABLAS_PERMITIDAS.contains(tabla)) {
            throw new SQLException("Tabla no autorizada en operacion generica: " + tabla);
        }
    }

    // ==================== OPERACIONES GLOBALES REUTILIZABLES ====================

    protected int ejecutarEscritura(String sql, Parametrizador params) throws SQLException {
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (params != null) {
                params.asignar(ps);
            }
            return ps.executeUpdate();
        }
    }

    protected int ejecutarEscritura(Connection conn, String sql, Parametrizador params) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (params != null) {
                params.asignar(ps);
            }
            return ps.executeUpdate();
        }
    }

    protected <T> List<T> consultarLista(String sql, Parametrizador params, Mapeador<T> mapper) throws SQLException {
        List<T> lista = new ArrayList<>();
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (params != null) {
                params.asignar(ps);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapper.mapear(rs));
                }
            }
        }
        return lista;
    }

    protected <T> T consultarUno(String sql, Parametrizador params, Mapeador<T> mapper) throws SQLException {
        List<T> lista = consultarLista(sql, params, mapper);
        return lista.isEmpty() ? null : lista.get(0);
    }

    protected void eliminarPorId(String tabla, String id, String nombreEntidad) throws SQLException {
        validarTabla(tabla);
        String sql = "DELETE FROM " + tabla + " WHERE id = ?";
        int filas = ejecutarEscritura(sql, ps -> ps.setString(1, id));
        if (filas == 0) {
            throw new SQLException("No existe ningun registro de " + nombreEntidad + " con el ID '" + id + "' para eliminar.");
        }
    }

    protected int contarRegistros(Connection conn, String tabla) throws SQLException {
        validarTabla(tabla);
        String sql = "SELECT COUNT(*) FROM " + tabla;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    protected boolean existeRegistro(Connection conn, String tabla, String id) throws SQLException {
        validarTabla(tabla);
        String sql = "SELECT COUNT(*) FROM " + tabla + " WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
