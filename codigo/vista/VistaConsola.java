package vista;

import controlador.FeriaControlador;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import modelo.Editorial;
import modelo.Notificacion;
import modelo.Reservacion;
import modelo.Stand;

public class VistaConsola {
    private final FeriaControlador controlador;
    private final FormatoConsola ui;

    public VistaConsola(FeriaControlador controlador) {
        this.controlador = controlador;
        this.ui = new FormatoConsola(new Scanner(System.in));
    }

    public void iniciar() {
        if (!controlador.iniciarBaseDeDatos()) {
            System.out.println("Verifica tu conexion a internet o tus credenciales en ConexionBD.java.");
            return;
        }

        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n=============================================================================");
            System.out.println("   FERIA DEL LIBRO - PAGINA PRINCIPAL");
            System.out.println("=============================================================================");
            ui.mostrarStandsEnCuadro(controlador.listarStands(), "STANDS");

            System.out.println("1. Menu administrador");
            System.out.println("2. Crear nueva Editorial");
            System.out.println("3. Iniciar sesion con una Editorial existente");
            System.out.println("0. Salir");

            opcion = ui.leerEntero("Selecciona una opcion: ");
            switch (opcion) {
                case 1 -> menuAdministrador();
                case 2 -> crearEditorialYEntrar();
                case 3 -> iniciarSesionEditorialExistente();
                case 0 -> System.out.println("Cerrando conexion y saliendo del sistema...");
                default -> System.out.println("Opcion no valida.");
            }
        }
    }

    private void menuAdministrador() {
        int op = -1;
        while (op != 0) {
            System.out.println("\n-----------------------------------------------------------------------------");
            System.out.println("  MENU ADMINISTRADOR");
            System.out.println("-----------------------------------------------------------------------------");
            System.out.println("1. Ejecutar suite");
            System.out.println("2. Ver Editoriales");
            System.out.println("3. Ver reservas y notificaciones");
            System.out.println("4. Configuracion");
            System.out.println("0. Volver a la pagina principal");

            op = ui.leerEntero("Opcion Administrador: ");
            switch (op) {
                case 1 -> ejecutarPruebasDeConflictos();
                case 2 -> ui.mostrarEditoriales(controlador.listarEditoriales());
                case 3 -> verReservasYNotificacionesAdmin();
                case 4 -> menuConfiguracion();
                case 0 -> { }
                default -> System.out.println("Opcion no valida.");
            }
        }
    }

    private void verReservasYNotificacionesAdmin() {
        ui.mostrarReservaciones(controlador.listarReservaciones(), "RESERVACIONES REGISTRADAS");
        System.out.println("\n=============================================================================");
        System.out.println("  NOTIFICACIONES DE ADMINISTRADOR");
        System.out.println("=============================================================================");
        List<String> alertasPago = controlador.listarAlertasAdminPagosPendientes();
        if (alertasPago.isEmpty()) {
            System.out.println("[OK] Sin adeudos: todas las editoriales han pagado su reservacion.");
            return;
        }
        for (int i = 0; i < alertasPago.size(); i++) {
            System.out.println((i + 1) + ". " + alertasPago.get(i));
        }
    }

    private void menuConfiguracion() {
        int op = -1;
        while (op != 0) {
            System.out.println("\n-----------------------------------------------------------------------------");
            System.out.println("  CONFIGURACION");
            System.out.printf("  Tarifa global actual por m2: $%.2f%n", controlador.getCostoPorMetroCuadrado());
            System.out.println("-----------------------------------------------------------------------------");
            System.out.println("1. CRUD Stands");
            System.out.println("2. CRUD Editoriales");
            System.out.println("3. CRUD Reservaciones");
            System.out.println("4. CRUD Notificaciones a Editoriales");
            System.out.println("5. Configurar costo por metro cuadrado");
            System.out.println("0. Volver al Menu Administrador");

            op = ui.leerEntero("Opcion Configuracion: ");
            switch (op) {
                case 1 -> crudStands();
                case 2 -> crudEditoriales();
                case 3 -> crudReservaciones();
                case 4 -> crudNotificaciones();
                case 5 -> configurarCostoPorMetro();
                case 0 -> { }
                default -> System.out.println("Opcion no valida.");
            }
        }
    }

    private void configurarCostoPorMetro() {
        System.out.printf("%n--- CONFIGURAR COSTO POR METRO CUADRADO (Actual: $%.2f) ---%n",
                controlador.getCostoPorMetroCuadrado());
        double nuevoCostoM2 = ui.leerDouble("Nuevo costo por m2 ($): ");
        System.out.println("Recalcular costo de todos los stands existentes?");
        System.out.println("1. Si");
        System.out.println("2. No");
        int rec = ui.leerEntero("Elige opcion: ");
        controlador.actualizarCostoPorMetroCuadrado(nuevoCostoM2, rec == 1);
    }

    private String elegirEstadoStand(String estadoActual) {
        while (true) {
            System.out.println("Estado del stand" + (estadoActual != null ? " (actual: " + estadoActual + ")" : "") + ":");
            System.out.println("1. Disponible");
            System.out.println("2. Reservado");
            int op = ui.leerEntero("Elige opcion (1 o 2): ");
            if (op == 1) return "Disponible";
            if (op == 2) return "Reservado";
            System.out.println("Opcion no valida. Ingresa 1 o 2.");
        }
    }

    private void crudStands() {
        ui.mostrarStands(controlador.listarStands(), "STANDS ACTUALES");
        System.out.printf("Tarifa global por m2: $%.2f%n", controlador.getCostoPorMetroCuadrado());
        System.out.println("\n--- CRUD STANDS: 1. Crear | 2. Actualizar | 3. Eliminar | 0. Cancelar ---");
        String acc = ui.pedirTexto("Elige accion: ");
        switch (acc) {
            case "1" -> {
                String id = ui.pedirTexto("ID Stand: ");
                String ubi = ui.pedirTexto("Ubicacion: ");
                double m2 = ui.leerDouble("Metros cuadrados: ");
                double costo = controlador.calcularCostoStand(m2);
                System.out.printf("Costo calculado (%.1f m2 x $%.2f): $%.2f%n",
                        m2, controlador.getCostoPorMetroCuadrado(), costo);
                controlador.registrarStand(new Stand(id, ubi, m2, costo, "Disponible"));
            }
            case "2" -> {
                String idU = ui.pedirTexto("ID del Stand a actualizar: ");
                String ubiU = ui.pedirTexto("Nueva ubicacion: ");
                double m2U = ui.leerDouble("Nuevos metros cuadrados: ");
                double costoU = controlador.calcularCostoStand(m2U);
                System.out.printf("Costo calculado (%.1f m2 x $%.2f): $%.2f%n",
                        m2U, controlador.getCostoPorMetroCuadrado(), costoU);
                controlador.actualizarStand(new Stand(idU, ubiU, m2U, costoU, elegirEstadoStand(null)));
            }
            case "3" -> controlador.eliminarStand(ui.pedirTexto("ID del Stand a eliminar: "));
            default -> System.out.println("Accion cancelada.");
        }
    }

    private void crudEditoriales() {
        ui.mostrarEditoriales(controlador.listarEditoriales());
        System.out.println("\n--- CRUD EDITORIALES: 1. Crear | 2. Actualizar | 3. Eliminar | 0. Cancelar ---");
        String acc = ui.pedirTexto("Elige accion: ");
        switch (acc) {
            case "1" -> controlador.registrarEditorial(new Editorial(
                    ui.pedirTexto("ID Editorial: "),
                    ui.pedirTexto("Nombre: "),
                    ui.pedirTexto("Pais: "),
                    ui.pedirTexto("Contacto: ")
            ));
            case "2" -> controlador.actualizarEditorial(new Editorial(
                    ui.pedirTexto("ID de la Editorial a actualizar: "),
                    ui.pedirTexto("Nuevo nombre: "),
                    ui.pedirTexto("Nuevo pais: "),
                    ui.pedirTexto("Nuevo contacto: ")
            ));
            case "3" -> controlador.eliminarEditorial(ui.pedirTexto("ID de la Editorial a eliminar: "));
            default -> System.out.println("Accion cancelada.");
        }
    }

    private void crudReservaciones() {
        ui.mostrarReservaciones(controlador.listarReservaciones(), "RESERVACIONES ACTUALES");
        System.out.println("\n--- CRUD RESERVACIONES: 1. Crear/Rentar | 2. Actualizar | 3. Eliminar/Cancelar | 0. Cancelar ---");
        String acc = ui.pedirTexto("Elige accion: ");
        switch (acc) {
            case "1" -> {
                ui.mostrarStands(controlador.listarStandsDisponibles(), "STANDS DISPONIBLES");
                controlador.registrarReservacion(new Reservacion(
                        ui.pedirTexto("ID Reservacion: "),
                        ui.pedirTexto("ID Stand a asignar: "),
                        ui.pedirTexto("ID Editorial: "),
                        ui.pedirTexto("Fecha (YYYY-MM-DD): "),
                        ui.pedirTexto("Estado de pago (Pagado/Pendiente): ")
                ));
            }
            case "2" -> controlador.actualizarReservacion(new Reservacion(
                    ui.pedirTexto("ID Reservacion a actualizar: "),
                    ui.pedirTexto("ID Stand: "),
                    ui.pedirTexto("ID Editorial: "),
                    ui.pedirTexto("Fecha (YYYY-MM-DD): "),
                    ui.pedirTexto("Nuevo estado de pago (Pagado/Pendiente): ")
            ));
            case "3" -> controlador.eliminarReservacion(ui.pedirTexto("ID Reservacion a eliminar: "));
            default -> System.out.println("Accion cancelada.");
        }
    }

    private void crudNotificaciones() {
        ui.mostrarNotificaciones(controlador.listarNotificaciones(), "NOTIFICACIONES ENVIADAS A EDITORIALES");
        System.out.println("\n--- CRUD NOTIFICACIONES: 1. Enviar a una | 2. Aviso general | 3. Editar | 4. Eliminar | 0. Cancelar ---");
        String acc = ui.pedirTexto("Elige accion: ");
        switch (acc) {
            case "1" -> {
                ui.mostrarEditoriales(controlador.listarEditoriales());
                controlador.enviarNotificacion(new Notificacion(
                        ui.pedirTexto("ID Notificacion: "),
                        ui.pedirTexto("ID Editorial destino: "),
                        ui.pedirTexto("Titulo del aviso: "),
                        ui.pedirTexto("Mensaje: "),
                        LocalDate.now().toString(),
                        false
                ));
            }
            case "2" -> {
                List<Editorial> todas = controlador.listarEditoriales();
                if (todas.isEmpty()) {
                    System.out.println("No hay editoriales registradas.");
                    return;
                }
                String pref = ui.pedirTexto("Prefijo de ID: ");
                String titG = ui.pedirTexto("Titulo del aviso general: ");
                String msgG = ui.pedirTexto("Mensaje para todas las editoriales: ");
                String hoy = LocalDate.now().toString();
                for (Editorial ed : todas) {
                    controlador.enviarNotificacion(new Notificacion(pref + "-" + ed.getId(), ed.getId(), titG, msgG, hoy, false));
                }
            }
            case "3" -> controlador.actualizarNotificacion(new Notificacion(
                    ui.pedirTexto("ID Notificacion a editar: "),
                    "",
                    ui.pedirTexto("Nuevo titulo: "),
                    ui.pedirTexto("Nuevo mensaje: "),
                    "",
                    Boolean.parseBoolean(ui.pedirTexto("Marcar como leida? (true/false): "))
            ));
            case "4" -> controlador.eliminarNotificacion(ui.pedirTexto("ID Notificacion a eliminar: "));
            default -> System.out.println("Accion cancelada.");
        }
    }

    private void crearEditorialYEntrar() {
        System.out.println("\n--- CREAR NUEVA EDITORIAL ---");
        Editorial nueva = new Editorial(
                ui.pedirTexto("Crea un ID para tu editorial: "),
                ui.pedirTexto("Nombre de la editorial: "),
                ui.pedirTexto("Pais de origen: "),
                ui.pedirTexto("Correo o telefono de contacto: ")
        );
        if (controlador.registrarEditorial(nueva)) {
            menuSesionEditorial(nueva);
        }
    }

    private void iniciarSesionEditorialExistente() {
        List<Editorial> lista = controlador.listarEditoriales();
        ui.mostrarEditoriales(lista);
        if (lista.isEmpty()) {
            System.out.println("No hay editoriales registradas aun. Selecciona la opcion 2 en el menu principal para crear una.");
            return;
        }
        String idBuscar = ui.pedirTexto("Ingresa el ID de tu editorial para iniciar sesion: ");
        Editorial editorialActiva = controlador.buscarEditorial(idBuscar);
        if (editorialActiva == null) {
            System.out.println("[AVISO] No se encontro ninguna editorial con el ID '" + idBuscar + "'.");
            return;
        }
        menuSesionEditorial(editorialActiva);
    }

    private void menuSesionEditorial(Editorial editorial) {
        int op = -1;
        while (op != 0) {
            int sinLeer = controlador.contarNoLeidas(editorial.getId());
            System.out.println("\n=============================================================================");
            System.out.println("  SESION EDITORIAL: " + editorial.getNombre().toUpperCase() + " [ID: " + editorial.getId() + "]");
            System.out.println("  Notificaciones sin leer: " + sinLeer);
            System.out.println("=============================================================================");
            ui.mostrarStandsEnCuadro(controlador.listarStandsDisponibles(), "STANDS DISPONIBLES PARA RENTA");

            System.out.println("1. Rentar / Reservar un stand");
            System.out.println("2. Mis reservaciones");
            System.out.println("3. Buzon de notificaciones");
            System.out.println("4. Actualizar datos de mi editorial");
            System.out.println("0. Cerrar sesion y volver a la pagina principal");

            op = ui.leerEntero("Elige una opcion: ");
            switch (op) {
                case 1 -> rentarStandEditorial(editorial);
                case 2 -> gestionarMisReservaciones(editorial);
                case 3 -> gestionarBuzonEditorial(editorial);
                case 4 -> {
                    Editorial actualizada = new Editorial(
                            editorial.getId(),
                            ui.pedirTextoOpcional("Nuevo nombre", editorial.getNombre()),
                            ui.pedirTextoOpcional("Nuevo pais", editorial.getPais()),
                            ui.pedirTextoOpcional("Nuevo contacto", editorial.getContacto())
                    );
                    if (controlador.actualizarEditorial(actualizada)) {
                        editorial = actualizada;
                    }
                }
                case 0 -> System.out.println("Sesion de editorial cerrada.");
                default -> System.out.println("Opcion no valida.");
            }
        }
    }

    private void rentarStandEditorial(Editorial editorial) {
        if (controlador.listarStandsDisponibles().isEmpty()) {
            System.out.println("Por el momento no hay stands disponibles.");
            return;
        }
        System.out.println("\n--- RENTAR STAND ---");
        controlador.registrarReservacion(new Reservacion(
                ui.pedirTexto("Folio/ID de la reservacion: "),
                ui.pedirTexto("ID del Stand a rentar: "),
                editorial.getId(),
                ui.pedirTexto("Fecha de reservacion (YYYY-MM-DD): "),
                ui.pedirTexto("Estado de pago (Pagado / Pendiente): ")
        ));
    }

    private void gestionarMisReservaciones(Editorial editorial) {
        int op = -1;
        while (op != 0) {
            List<Reservacion> misReservas = controlador.listarReservacionesDeEditorial(editorial.getId());
            System.out.println("\n=============================================================================");
            System.out.println("  MIS RESERVACIONES - " + editorial.getNombre().toUpperCase());
            System.out.println("=============================================================================");
            ui.mostrarReservaciones(misReservas, "RESERVACIONES ACTIVAS");

            System.out.println("\n1. Realizar pago");
            System.out.println("2. Cambiar de stand");
            System.out.println("3. Modificar fecha o datos de reservacion");
            System.out.println("4. Cancelar reservacion");
            System.out.println("0. Volver al menu de editorial");

            op = ui.leerEntero("Elige una opcion: ");
            if (op == 0) break;
            if (misReservas.isEmpty()) {
                System.out.println("No tienes reservaciones registradas.");
                continue;
            }

            switch (op) {
                case 1 -> {
                    Reservacion target = pedirReservacionPropia(misReservas, "ID de la reservacion a pagar: ");
                    if (target == null) break;
                    if ("Pagado".equalsIgnoreCase(target.getEstadoPago())) {
                        System.out.println("[AVISO] La reservacion '" + target.getId() + "' ya se encuentra pagada.");
                        break;
                    }
                    controlador.actualizarReservacion(new Reservacion(
                            target.getId(), target.getIdStand(), editorial.getId(), target.getFecha(), "Pagado"));
                }
                case 2 -> {
                    ui.mostrarStandsEnCuadro(controlador.listarStandsDisponibles(), "STANDS DISPONIBLES");
                    Reservacion target = pedirReservacionPropia(misReservas, "ID de la reservacion a cambiar: ");
                    if (target == null) break;
                    controlador.actualizarReservacion(new Reservacion(
                            target.getId(), ui.pedirTexto("ID del nuevo stand: "), editorial.getId(),
                            target.getFecha(), target.getEstadoPago()));
                }
                case 3 -> {
                    Reservacion target = pedirReservacionPropia(misReservas, "ID de la reservacion a modificar: ");
                    if (target == null) break;
                    controlador.actualizarReservacion(new Reservacion(
                            target.getId(),
                            ui.pedirTextoOpcional("ID del stand", target.getIdStand()),
                            editorial.getId(),
                            ui.pedirTextoOpcional("Nueva fecha YYYY-MM-DD", target.getFecha()),
                            ui.pedirTextoOpcional("Estado de pago Pagado/Pendiente", target.getEstadoPago())));
                }
                case 4 -> {
                    Reservacion target = pedirReservacionPropia(misReservas, "ID de la reservacion a cancelar: ");
                    if (target != null) controlador.eliminarReservacion(target.getId());
                }
                default -> System.out.println("Opcion no valida.");
            }
        }
    }

    private Reservacion pedirReservacionPropia(List<Reservacion> lista, String etiqueta) {
        String idBuscado = ui.pedirTexto(etiqueta);
        for (Reservacion r : lista) {
            if (r.getId().equalsIgnoreCase(idBuscado)) {
                return r;
            }
        }
        System.out.println("[AVISO] No se encontro la reservacion '" + idBuscado + "' en tu cuenta.");
        return null;
    }

    private void gestionarBuzonEditorial(Editorial editorial) {
        ui.mostrarNotificaciones(
                controlador.listarNotificacionesDeEditorial(editorial.getId()),
                "BUZON DE NOTIFICACIONES - " + editorial.getNombre().toUpperCase()
        );
        System.out.println("\n--- ACCIONES DE BUZON: 1. Marcar una leida | 2. Marcar todas leidas | 3. Eliminar | 0. Volver ---");
        switch (ui.pedirTexto("Elige accion: ")) {
            case "1" -> controlador.marcarComoLeida(ui.pedirTexto("ID de la notificacion a marcar como leida: "));
            case "2" -> controlador.marcarTodasComoLeidas(editorial.getId());
            case "3" -> controlador.eliminarNotificacion(ui.pedirTexto("ID de la notificacion a eliminar: "));
            default -> { }
        }
    }

    public void ejecutarPruebasDeConflictos() {
        System.out.println("\n======================================================");
        System.out.println("  SUITE DE PRUEBAS DE INTEGRIDAD Y TRY-CATCH");
        System.out.println("======================================================");

        controlador.registrarStand(new Stand("99", "Zona Pruebas", 10.0, 3000.0, "Disponible"));
        controlador.registrarEditorial(new Editorial("99", "Editorial Prueba", "Mexico", "test@ed.mx"));
        controlador.registrarReservacion(new Reservacion("R-99", "99", "99", "2026-11-30", "Pagado"));
        controlador.enviarNotificacion(new Notificacion("NOTI-99", "99", "Prueba", "Mensaje de prueba", "2026-11-30", false));

        controlador.registrarStand(new Stand("99", "Duplicado", 10.0, 3000.0, "Disponible"));
        controlador.registrarEditorial(new Editorial("99", "Duplicada", "Mexico", "dup@ed.mx"));
        controlador.registrarReservacion(new Reservacion("R-99", "01", "01", "2026-11-30", "Pendiente"));
        controlador.enviarNotificacion(new Notificacion("NOTI-99", "99", "Dup", "Dup", "2026-11-30", false));

        controlador.registrarReservacion(new Reservacion("R-ERR1", "99", "01", "2026-11-30", "Pendiente"));
        controlador.registrarReservacion(new Reservacion("R-ERR2", "999", "01", "2026-11-30", "Pendiente"));
        controlador.enviarNotificacion(new Notificacion("NOTI-ERR", "999", "Error", "Editorial no existe", "2026-11-30", false));

        controlador.actualizarStand(new Stand("99", "Zona Pruebas", 10.0, 3000.0, "Disponible"));
        controlador.actualizarEditorial(new Editorial("999", "No Existe", "Chile", "no@ed.cl"));
        controlador.marcarComoLeida("NOTI-999");

        controlador.eliminarStand("999");
        controlador.eliminarStand("99");
        controlador.eliminarEditorial("99");
        controlador.eliminarReservacion("R-999");
        controlador.eliminarNotificacion("NOTI-999");

        controlador.eliminarReservacion("R-99");
        controlador.eliminarStand("99");
        controlador.eliminarEditorial("99");
        System.out.println("[OK] Pruebas de excepciones completadas sin detener el programa.");
    }
}
