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
    private final Scanner scanner;

    public VistaConsola(FeriaControlador controlador) {
        this.controlador = controlador;
        this.scanner = new Scanner(System.in);
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

            mostrarStandsEnCuadro(controlador.listarStands(), "STANDS");

            System.out.println("1. Menu administrador");
            System.out.println("2. Crear nueva Editorial");
            System.out.println("3. Iniciar sesion con una Editorial existente");
            System.out.println("0. Salir");
            System.out.print("Selecciona una opcion: ");

            opcion = leerEntero();
            switch (opcion) {
                case 1:
                    menuAdministrador();
                    break;
                case 2:
                    crearEditorialYEntrar();
                    break;
                case 3:
                    iniciarSesionEditorialExistente();
                    break;
                case 0:
                    System.out.println("Cerrando conexion y saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }
        }
    }

    // ==================== MENU ADMINISTRADOR ====================

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
            System.out.print("Opcion Administrador: ");

            op = leerEntero();
            switch (op) {
                case 1:
                    ejecutarPruebasDeConflictos();
                    break;
                case 2:
                    mostrarEditoriales(controlador.listarEditoriales());
                    break;
                case 3:
                    verReservasYNotificacionesAdmin();
                    break;
                case 4:
                    menuConfiguracionCRUD();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }
        }
    }

    private void verReservasYNotificacionesAdmin() {
        mostrarReservaciones(controlador.listarReservaciones(), "RESERVACIONES REGISTRADAS");

        System.out.println("\n=============================================================================");
        System.out.println("  NOTIFICACIONES DE ADMINISTRADOR");
        System.out.println("=============================================================================");
        List<String> alertasPago = controlador.listarAlertasAdminPagosPendientes();
        if (alertasPago.isEmpty()) {
            System.out.println("[OK] Sin adeudos: todas las editoriales han pagado su reservacion.");
        } else {
            for (int i = 0; i < alertasPago.size(); i++) {
                System.out.println((i + 1) + ". " + alertasPago.get(i));
            }
        }
    }

    private void menuConfiguracionCRUD() {
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
            System.out.print("Opcion Configuracion: ");

            op = leerEntero();
            switch (op) {
                case 1:
                    crudAdminStands();
                    break;
                case 2:
                    crudAdminEditoriales();
                    break;
                case 3:
                    crudAdminReservaciones();
                    break;
                case 4:
                    crudAdminNotificaciones();
                    break;
                case 5:
                    configurarCostoPorMetro();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }
        }
    }

    private void configurarCostoPorMetro() {
        System.out.printf("\n--- CONFIGURAR COSTO POR METRO CUADRADO (Actual: $%.2f) ---%n",
                controlador.getCostoPorMetroCuadrado());
        System.out.print("Nuevo costo por m2 ($): ");
        double nuevoCostoM2 = leerDouble();
        System.out.println("Recalcular costo de todos los stands existentes?");
        System.out.println("1. Si");
        System.out.println("2. No");
        System.out.print("Elige opcion: ");
        int rec = leerEntero();
        boolean recalcular = (rec == 1);
        controlador.actualizarCostoPorMetroCuadrado(nuevoCostoM2, recalcular);
    }

    private String elegirEstadoStand(String estadoActual) {
        while (true) {
            System.out.println("Estado del stand" + (estadoActual != null ? " (actual: " + estadoActual + ")" : "") + ":");
            System.out.println("1. Disponible");
            System.out.println("2. Reservado");
            System.out.print("Elige opcion (1 o 2): ");
            int op = leerEntero();
            if (op == 1) return "Disponible";
            if (op == 2) return "Reservado";
            System.out.println("Opcion no valida. Ingresa 1 o 2.");
        }
    }

    private void crudAdminStands() {
        mostrarStands(controlador.listarStands(), "STANDS ACTUALES");
        System.out.printf("Tarifa global por m2: $%.2f%n", controlador.getCostoPorMetroCuadrado());
        System.out.println("\n--- CRUD STANDS: 1. Crear | 2. Actualizar | 3. Eliminar | 0. Cancelar ---");
        System.out.print("Elige accion: ");
        String acc = scanner.nextLine().trim();
        switch (acc) {
            case "1": {
                System.out.print("ID Stand: ");
                String id = scanner.nextLine().trim();
                System.out.print("Ubicacion: ");
                String ubi = scanner.nextLine().trim();
                System.out.print("Metros cuadrados: ");
                double m2 = leerDouble();
                double costoCalculado = controlador.calcularCostoStand(m2);
                System.out.printf("Costo calculado (%.1f m2 x $%.2f): $%.2f%n",
                        m2, controlador.getCostoPorMetroCuadrado(), costoCalculado);
                controlador.registrarStand(new Stand(id, ubi, m2, costoCalculado, "Disponible"));
                break;
            }
            case "2": {
                System.out.print("ID del Stand a actualizar: ");
                String idU = scanner.nextLine().trim();
                System.out.print("Nueva ubicacion: ");
                String ubiU = scanner.nextLine().trim();
                System.out.print("Nuevos metros cuadrados: ");
                double m2U = leerDouble();
                double costoCalculadoU = controlador.calcularCostoStand(m2U);
                System.out.printf("Costo calculado (%.1f m2 x $%.2f): $%.2f%n",
                        m2U, controlador.getCostoPorMetroCuadrado(), costoCalculadoU);
                String estU = elegirEstadoStand(null);
                controlador.actualizarStand(new Stand(idU, ubiU, m2U, costoCalculadoU, estU));
                break;
            }
            case "3": {
                System.out.print("ID del Stand a eliminar: ");
                String idD = scanner.nextLine().trim();
                controlador.eliminarStand(idD);
                break;
            }
            default:
                System.out.println("Accion cancelada.");
        }
    }

    private void crudAdminEditoriales() {
        mostrarEditoriales(controlador.listarEditoriales());
        System.out.println("\n--- CRUD EDITORIALES: 1. Crear | 2. Actualizar | 3. Eliminar | 0. Cancelar ---");
        System.out.print("Elige accion: ");
        String acc = scanner.nextLine().trim();
        switch (acc) {
            case "1":
                System.out.print("ID Editorial: ");
                String id = scanner.nextLine().trim();
                System.out.print("Nombre: ");
                String nom = scanner.nextLine().trim();
                System.out.print("Pais: ");
                String pais = scanner.nextLine().trim();
                System.out.print("Contacto: ");
                String con = scanner.nextLine().trim();
                controlador.registrarEditorial(new Editorial(id, nom, pais, con));
                break;
            case "2":
                System.out.print("ID de la Editorial a actualizar: ");
                String idU = scanner.nextLine().trim();
                System.out.print("Nuevo nombre: ");
                String nomU = scanner.nextLine().trim();
                System.out.print("Nuevo pais: ");
                String paisU = scanner.nextLine().trim();
                System.out.print("Nuevo contacto: ");
                String conU = scanner.nextLine().trim();
                controlador.actualizarEditorial(new Editorial(idU, nomU, paisU, conU));
                break;
            case "3":
                System.out.print("ID de la Editorial a eliminar: ");
                String idD = scanner.nextLine().trim();
                controlador.eliminarEditorial(idD);
                break;
            default:
                System.out.println("Accion cancelada.");
        }
    }

    private void crudAdminReservaciones() {
        mostrarReservaciones(controlador.listarReservaciones(), "RESERVACIONES ACTUALES");
        System.out.println("\n--- CRUD RESERVACIONES: 1. Crear/Rentar | 2. Actualizar | 3. Eliminar/Cancelar | 0. Cancelar ---");
        System.out.print("Elige accion: ");
        String acc = scanner.nextLine().trim();
        switch (acc) {
            case "1":
                mostrarStands(controlador.listarStandsDisponibles(), "STANDS DISPONIBLES");
                System.out.print("ID Reservacion: ");
                String idR = scanner.nextLine().trim();
                System.out.print("ID Stand a asignar: ");
                String idS = scanner.nextLine().trim();
                System.out.print("ID Editorial: ");
                String idE = scanner.nextLine().trim();
                System.out.print("Fecha (YYYY-MM-DD): ");
                String fecha = scanner.nextLine().trim();
                System.out.print("Estado de pago (Pagado/Pendiente): ");
                String pago = scanner.nextLine().trim();
                controlador.registrarReservacion(new Reservacion(idR, idS, idE, fecha, pago));
                break;
            case "2":
                System.out.print("ID Reservacion a actualizar: ");
                String idRU = scanner.nextLine().trim();
                System.out.print("ID Stand: ");
                String idSU = scanner.nextLine().trim();
                System.out.print("ID Editorial: ");
                String idEU = scanner.nextLine().trim();
                System.out.print("Fecha (YYYY-MM-DD): ");
                String fechaU = scanner.nextLine().trim();
                System.out.print("Nuevo estado de pago (Pagado/Pendiente): ");
                String pagoU = scanner.nextLine().trim();
                controlador.actualizarReservacion(new Reservacion(idRU, idSU, idEU, fechaU, pagoU));
                break;
            case "3":
                System.out.print("ID Reservacion a eliminar: ");
                String idRD = scanner.nextLine().trim();
                controlador.eliminarReservacion(idRD);
                break;
            default:
                System.out.println("Accion cancelada.");
        }
    }

    private void crudAdminNotificaciones() {
        mostrarNotificaciones(controlador.listarNotificaciones(), "NOTIFICACIONES ENVIADAS A EDITORIALES");
        System.out.println("\n--- CRUD NOTIFICACIONES: 1. Enviar a una | 2. Enviar aviso general | 3. Editar | 4. Eliminar | 0. Cancelar ---");
        System.out.print("Elige accion: ");
        String acc = scanner.nextLine().trim();
        switch (acc) {
            case "1":
                mostrarEditoriales(controlador.listarEditoriales());
                System.out.print("ID Notificacion: ");
                String idN = scanner.nextLine().trim();
                System.out.print("ID Editorial destino: ");
                String idEd = scanner.nextLine().trim();
                System.out.print("Titulo del aviso: ");
                String tit = scanner.nextLine().trim();
                System.out.print("Mensaje: ");
                String msg = scanner.nextLine().trim();
                controlador.enviarNotificacion(
                        new Notificacion(idN, idEd, tit, msg, LocalDate.now().toString(), false)
                );
                break;
            case "2":
                List<Editorial> todas = controlador.listarEditoriales();
                if (todas.isEmpty()) {
                    System.out.println("No hay editoriales registradas.");
                    break;
                }
                System.out.print("Prefijo de ID: ");
                String pref = scanner.nextLine().trim();
                System.out.print("Titulo del aviso general: ");
                String titG = scanner.nextLine().trim();
                System.out.print("Mensaje para todas las editoriales: ");
                String msgG = scanner.nextLine().trim();
                for (Editorial ed : todas) {
                    controlador.enviarNotificacion(
                            new Notificacion(pref + "-" + ed.getId(), ed.getId(), titG, msgG, LocalDate.now().toString(), false)
                    );
                }
                break;
            case "3":
                System.out.print("ID Notificacion a editar: ");
                String idNU = scanner.nextLine().trim();
                System.out.print("Nuevo titulo: ");
                String titU = scanner.nextLine().trim();
                System.out.print("Nuevo mensaje: ");
                String msgU = scanner.nextLine().trim();
                System.out.print("Marcar como leida? (true/false): ");
                boolean leidaU = Boolean.parseBoolean(scanner.nextLine().trim());
                controlador.actualizarNotificacion(new Notificacion(idNU, "", titU, msgU, "", leidaU));
                break;
            case "4":
                System.out.print("ID Notificacion a eliminar: ");
                String idND = scanner.nextLine().trim();
                controlador.eliminarNotificacion(idND);
                break;
            default:
                System.out.println("Accion cancelada.");
        }
    }

    // ==================== CREAR EDITORIAL Y SESION EDITORIAL ====================

    private void crearEditorialYEntrar() {
        System.out.println("\n--- CREAR NUEVA EDITORIAL ---");
        System.out.print("Crea un ID para tu editorial: ");
        String id = scanner.nextLine().trim();
        System.out.print("Nombre de la editorial: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Pais de origen: ");
        String pais = scanner.nextLine().trim();
        System.out.print("Correo o telefono de contacto: ");
        String contacto = scanner.nextLine().trim();

        Editorial nueva = new Editorial(id, nombre, pais, contacto);
        if (controlador.registrarEditorial(nueva)) {
            menuSesionEditorial(nueva);
        }
    }

    private void iniciarSesionEditorialExistente() {
        List<Editorial> lista = controlador.listarEditoriales();
        mostrarEditoriales(lista);
        if (lista.isEmpty()) {
            System.out.println("No hay editoriales registradas aun. Selecciona la opcion 2 en el menu principal para crear una.");
            return;
        }
        System.out.print("Ingresa el ID de tu editorial para iniciar sesion: ");
        String idBuscar = scanner.nextLine().trim();
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

            mostrarStandsEnCuadro(controlador.listarStandsDisponibles(), "STANDS DISPONIBLES PARA RENTA");

            System.out.println("1. Rentar / Reservar un stand");
            System.out.println("2. Mis reservaciones");
            System.out.println("3. Buzon de notificaciones");
            System.out.println("4. Actualizar datos de mi editorial");
            System.out.println("0. Cerrar sesion y volver a la pagina principal");
            System.out.print("Elige una opcion: ");

            op = leerEntero();
            switch (op) {
                case 1:
                    rentarStandEditorial(editorial);
                    break;
                case 2:
                    gestionarMisReservaciones(editorial);
                    break;
                case 3:
                    gestionarBuzonEditorial(editorial);
                    break;
                case 4:
                    System.out.print("Nuevo nombre (actual: " + editorial.getNombre() + "): ");
                    String nom = scanner.nextLine().trim();
                    System.out.print("Nuevo pais (actual: " + editorial.getPais() + "): ");
                    String pais = scanner.nextLine().trim();
                    System.out.print("Nuevo contacto (actual: " + editorial.getContacto() + "): ");
                    String con = scanner.nextLine().trim();

                    Editorial actualizada = new Editorial(editorial.getId(), nom, pais, con);
                    if (controlador.actualizarEditorial(actualizada)) {
                        editorial = actualizada;
                    }
                    break;
                case 0:
                    System.out.println("Sesion de editorial cerrada.");
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }
        }
    }

    private void rentarStandEditorial(Editorial editorial) {
        List<Stand> disponibles = controlador.listarStandsDisponibles();
        if (disponibles.isEmpty()) {
            System.out.println("Por el momento no hay stands disponibles.");
            return;
        }
        System.out.println("\n--- RENTAR STAND ---");
        System.out.print("ID del Stand a rentar: ");
        String idStand = scanner.nextLine().trim();
        System.out.print("Folio/ID de la reservacion: ");
        String idRes = scanner.nextLine().trim();
        System.out.print("Fecha de reservacion (YYYY-MM-DD): ");
        String fecha = scanner.nextLine().trim();
        System.out.print("Estado de pago (Pagado / Pendiente): ");
        String pago = scanner.nextLine().trim();

        controlador.registrarReservacion(
                new Reservacion(idRes, idStand, editorial.getId(), fecha, pago)
        );
    }

    private void gestionarMisReservaciones(Editorial editorial) {
        int op = -1;
        while (op != 0) {
            List<Reservacion> misReservas = controlador.listarReservacionesDeEditorial(editorial.getId());
            System.out.println("\n=============================================================================");
            System.out.println("  MIS RESERVACIONES - " + editorial.getNombre().toUpperCase());
            System.out.println("=============================================================================");
            mostrarReservaciones(misReservas, "RESERVACIONES ACTIVAS");

            System.out.println("\n1. Realizar pago");
            System.out.println("2. Cambiar de stand");
            System.out.println("3. Modificar fecha o datos de reservacion");
            System.out.println("4. Cancelar reservacion");
            System.out.println("0. Volver al menu de editorial");
            System.out.print("Elige una opcion: ");

            op = leerEntero();
            if (op == 0) {
                break;
            }
            if (misReservas.isEmpty()) {
                System.out.println("No tienes reservaciones registradas.");
                continue;
            }

            switch (op) {
                case 1: {
                    System.out.print("ID de la reservacion a pagar: ");
                    String idPagar = scanner.nextLine().trim();
                    Reservacion target = buscarReservacionEnLista(misReservas, idPagar);
                    if (target == null) {
                        System.out.println("[AVISO] No se encontro la reservacion '" + idPagar + "' en tu cuenta.");
                        break;
                    }
                    if ("Pagado".equalsIgnoreCase(target.getEstadoPago())) {
                        System.out.println("[AVISO] La reservacion '" + idPagar + "' ya se encuentra pagada.");
                        break;
                    }
                    controlador.actualizarReservacion(
                            new Reservacion(target.getId(), target.getIdStand(), editorial.getId(), target.getFecha(), "Pagado")
                    );
                    break;
                }
                case 2: {
                    mostrarStandsEnCuadro(controlador.listarStandsDisponibles(), "STANDS DISPONIBLES");
                    System.out.print("ID de la reservacion a cambiar: ");
                    String idCambiar = scanner.nextLine().trim();
                    Reservacion target = buscarReservacionEnLista(misReservas, idCambiar);
                    if (target == null) {
                        System.out.println("[AVISO] No se encontro la reservacion '" + idCambiar + "' en tu cuenta.");
                        break;
                    }
                    System.out.print("ID del nuevo stand: ");
                    String nuevoStand = scanner.nextLine().trim();
                    controlador.actualizarReservacion(
                            new Reservacion(target.getId(), nuevoStand, editorial.getId(), target.getFecha(), target.getEstadoPago())
                    );
                    break;
                }
                case 3: {
                    System.out.print("ID de la reservacion a modificar: ");
                    String idMod = scanner.nextLine().trim();
                    Reservacion target = buscarReservacionEnLista(misReservas, idMod);
                    if (target == null) {
                        System.out.println("[AVISO] No se encontro la reservacion '" + idMod + "' en tu cuenta.");
                        break;
                    }
                    System.out.print("ID del stand (actual: " + target.getIdStand() + "): ");
                    String nuevoStand = scanner.nextLine().trim();
                    if (nuevoStand.isEmpty()) nuevoStand = target.getIdStand();
                    System.out.print("Nueva fecha YYYY-MM-DD (actual: " + target.getFecha() + "): ");
                    String nuevaFecha = scanner.nextLine().trim();
                    if (nuevaFecha.isEmpty()) nuevaFecha = target.getFecha();
                    System.out.print("Estado de pago Pagado/Pendiente (actual: " + target.getEstadoPago() + "): ");
                    String nuevoPago = scanner.nextLine().trim();
                    if (nuevoPago.isEmpty()) nuevoPago = target.getEstadoPago();

                    controlador.actualizarReservacion(
                            new Reservacion(target.getId(), nuevoStand, editorial.getId(), nuevaFecha, nuevoPago)
                    );
                    break;
                }
                case 4: {
                    System.out.print("ID de la reservacion a cancelar: ");
                    String idCancelar = scanner.nextLine().trim();
                    Reservacion target = buscarReservacionEnLista(misReservas, idCancelar);
                    if (target == null) {
                        System.out.println("[AVISO] No se encontro la reservacion '" + idCancelar + "' en tu cuenta.");
                        break;
                    }
                    controlador.eliminarReservacion(target.getId());
                    break;
                }
                default:
                    System.out.println("Opcion no valida.");
            }
        }
    }

    private Reservacion buscarReservacionEnLista(List<Reservacion> lista, String id) {
        for (Reservacion r : lista) {
            if (r.getId().equalsIgnoreCase(id)) {
                return r;
            }
        }
        return null;
    }

    private void gestionarBuzonEditorial(Editorial editorial) {
        mostrarNotificaciones(
                controlador.listarNotificacionesDeEditorial(editorial.getId()),
                "BUZON DE NOTIFICACIONES - " + editorial.getNombre().toUpperCase()
        );
        System.out.println("\n--- ACCIONES DE BUZON: 1. Marcar una como leida | 2. Marcar todas como leidas | 3. Eliminar notificacion | 0. Volver ---");
        System.out.print("Elige accion: ");
        String acc = scanner.nextLine().trim();
        switch (acc) {
            case "1":
                System.out.print("ID de la notificacion a marcar como leida: ");
                String idL = scanner.nextLine().trim();
                controlador.marcarComoLeida(idL);
                break;
            case "2":
                controlador.marcarTodasComoLeidas(editorial.getId());
                break;
            case "3":
                System.out.print("ID de la notificacion a eliminar: ");
                String idE = scanner.nextLine().trim();
                controlador.eliminarNotificacion(idE);
                break;
            default:
                break;
        }
    }

    // ==================== PRUEBAS DE CONFLICTOS (TRY-CATCH) ====================

    public void ejecutarPruebasDeConflictos() {
        System.out.println("\n======================================================");
        System.out.println("  SUITE DE PRUEBAS DE INTEGRIDAD Y TRY-CATCH");
        System.out.println("======================================================");

        System.out.println("\n1. Registrando datos de prueba temporales en Clever Cloud...");
        controlador.registrarStand(new Stand("99", "Zona Pruebas", 10.0, 3000.0, "Disponible"));
        controlador.registrarEditorial(new Editorial("99", "Editorial Prueba", "Mexico", "test@ed.mx"));
        controlador.registrarReservacion(new Reservacion("R-99", "99", "99", "2026-11-30", "Pagado"));
        controlador.enviarNotificacion(new Notificacion("NOTI-99", "99", "Prueba", "Mensaje de prueba", "2026-11-30", false));

        System.out.println("\n2. Probando IDs duplicados (stands, editoriales, reservaciones y notificaciones)...");
        controlador.registrarStand(new Stand("99", "Duplicado", 10.0, 3000.0, "Disponible"));
        controlador.registrarEditorial(new Editorial("99", "Duplicada", "Mexico", "dup@ed.mx"));
        controlador.registrarReservacion(new Reservacion("R-99", "01", "01", "2026-11-30", "Pendiente"));
        controlador.enviarNotificacion(new Notificacion("NOTI-99", "99", "Dup", "Dup", "2026-11-30", false));

        System.out.println("\n3. Probando reservacion con stand ocupado o inexistente y notificacion a editorial inexistente...");
        controlador.registrarReservacion(new Reservacion("R-ERR1", "99", "01", "2026-11-30", "Pendiente"));
        controlador.registrarReservacion(new Reservacion("R-ERR2", "999", "01", "2026-11-30", "Pendiente"));
        controlador.enviarNotificacion(new Notificacion("NOTI-ERR", "999", "Error", "Editorial no existe", "2026-11-30", false));

        System.out.println("\n4. Probando actualizaciones con conflicto o ID inexistente...");
        controlador.actualizarStand(new Stand("99", "Zona Pruebas", 10.0, 3000.0, "Disponible"));
        controlador.actualizarEditorial(new Editorial("999", "No Existe", "Chile", "no@ed.cl"));
        controlador.marcarComoLeida("NOTI-999");

        System.out.println("\n5. Probando eliminacion de registros inexistentes o con dependencias activas...");
        controlador.eliminarStand("999");
        controlador.eliminarStand("99");
        controlador.eliminarEditorial("99");
        controlador.eliminarReservacion("R-999");
        controlador.eliminarNotificacion("NOTI-999");

        System.out.println("\n6. Limpiando datos temporales de prueba...");
        controlador.eliminarReservacion("R-99");
        controlador.eliminarStand("99");
        controlador.eliminarEditorial("99");
        System.out.println("[OK] Pruebas de excepciones completadas sin detener el programa.");
    }

    // ==================== VISUALIZACION DE TABLAS ====================

    private void mostrarStandsEnCuadro(List<Stand> stands, String titulo) {
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

    private void mostrarStands(List<Stand> stands, String titulo) {
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

    private void mostrarEditoriales(List<Editorial> editoriales) {
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

    private void mostrarReservaciones(List<Reservacion> reservaciones, String titulo) {
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

    private void mostrarNotificaciones(List<Notificacion> notificaciones, String titulo) {
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

    private int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private double leerDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Ingresa un valor numerico valido: ");
            }
        }
    }
}
