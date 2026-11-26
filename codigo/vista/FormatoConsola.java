package vista;

import java.util.List;
import java.util.Scanner;
import modelo.Editorial;
import modelo.Notificacion;
import modelo.Reservacion;
import modelo.Stand;

public class FormatoConsola {
    private final Scanner scanner;

    public FormatoConsola(Scanner scanner) {
        this.scanner = scanner;
    }

    public String pedirTexto(String etiqueta) {
        System.out.print(etiqueta);
        return scanner.nextLine().trim();
    }

    public String pedirTextoOpcional(String etiqueta, String valorActual) {
        System.out.print(etiqueta + " (actual: " + valorActual + "): ");
        String entrada = scanner.nextLine().trim();
        return entrada.isEmpty() ? valorActual : entrada;
    }

    public int leerEntero(String etiqueta) {
        if (etiqueta != null) {
            System.out.print(etiqueta);
        }
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public double leerDouble(String etiqueta) {
        System.out.print(etiqueta);
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Ingresa un valor numerico valido: ");
            }
        }
    }

    public void mostrarStandsEnCuadro(List<Stand> stands, String titulo) {
        System.out.println("+---------------------------------------------------------------------------+");
        System.out.printf("| %-73s |%n", titulo);
        System.out.println("+--------+--------------------------------+----------+------------+-----------+");
        System.out.printf("| %-6s | %-30s | %-8s | %-10s | %-9s |%n", "ID", "UBICACION", "M2", "COSTO", "ESTADO");
        System.out.println("+--------+--------------------------------+----------+------------+-----------+");
        if (stands.isEmpty()) {
            System.out.printf("| %-73s |%n", "(Sin stands disponibles)");
        }
        for (Stand s : stands) {
            System.out.printf("| %-6s | %-30s | %-8.1f | $%-9.2f | %-9s |%n",
                    s.getId(), s.getUbicacion(), s.getMetrosCuadrados(), s.getCosto(), s.getEstado());
        }
        System.out.println("+--------+--------------------------------+----------+------------+-----------+");
    }

    public void mostrarStands(List<Stand> stands, String titulo) {
        System.out.println("\n--- " + titulo + " ---");
        System.out.printf("%-6s | %-30s | %-8s | %-10s | %-12s%n", "ID", "UBICACION", "M2", "COSTO", "ESTADO");
        System.out.println("-----------------------------------------------------------------------------");
        if (stands.isEmpty()) {
            System.out.println("(Sin registros)");
        }
        for (Stand s : stands) {
            System.out.printf("%-6s | %-30s | %-8.1f | $%-9.2f | %-12s%n",
                    s.getId(), s.getUbicacion(), s.getMetrosCuadrados(), s.getCosto(), s.getEstado());
        }
    }

    public void mostrarEditoriales(List<Editorial> editoriales) {
        System.out.println("\n--- TABLA: EDITORIALES ---");
        System.out.printf("%-6s | %-30s | %-12s | %-22s%n", "ID", "NOMBRE", "PAIS", "CONTACTO");
        System.out.println("-----------------------------------------------------------------------------");
        if (editoriales.isEmpty()) {
            System.out.println("(Sin registros)");
        }
        for (Editorial e : editoriales) {
            System.out.printf("%-6s | %-30s | %-12s | %-22s%n",
                    e.getId(), e.getNombre(), e.getPais(), e.getContacto());
        }
    }

    public void mostrarReservaciones(List<Reservacion> reservaciones, String titulo) {
        System.out.println("\n--- " + titulo + " ---");
        System.out.printf("%-8s | %-10s | %-12s | %-12s | %-12s%n", "ID", "ID_STAND", "ID_EDITORIAL", "FECHA", "PAGO");
        System.out.println("-----------------------------------------------------------------------------");
        if (reservaciones.isEmpty()) {
            System.out.println("(Sin registros)");
        }
        for (Reservacion r : reservaciones) {
            System.out.printf("%-8s | %-10s | %-12s | %-12s | %-12s%n",
                    r.getId(), r.getIdStand(), r.getIdEditorial(), r.getFecha(), r.getEstadoPago());
        }
    }

    public void mostrarNotificaciones(List<Notificacion> notificaciones, String titulo) {
        System.out.println("\n--- " + titulo + " ---");
        System.out.printf("%-14s | %-9s | %-22s | %-10s | %-10s | %s%n",
                "ID", "EDITORIAL", "TITULO", "FECHA", "ESTADO", "MENSAJE");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        if (notificaciones.isEmpty()) {
            System.out.println("(Sin notificaciones)");
        }
        for (Notificacion n : notificaciones) {
            System.out.printf("%-14s | %-9s | %-22s | %-10s | %-10s | %s%n",
                    n.getId(),
                    n.getIdEditorial(),
                    n.getTitulo(),
                    n.getFecha(),
                    n.isLeida() ? "Leida" : "NO LEIDA",
                    n.getMensaje());
        }
    }
}
