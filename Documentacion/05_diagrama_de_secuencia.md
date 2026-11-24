# Diagrama de Secuencia — Renta y Gestión de Reservaciones

## 1. Flujo Principal
Representa la interacción entre `VistaConsola`, `FeriaControlador`, `Query` (heredando de `CrudGenerico`) y la base de datos al rentar o actualizar una reservación de stand.

## 2. Diagrama de Secuencia (Mermaid)

```mermaid
sequenceDiagram
    actor Editorial
    participant Vista as VistaConsola
    participant Ctrl as FeriaControlador
    participant Qry as Query / CrudGenerico
    participant BD as MySQL

    Editorial->>Vista: Selecciona "Rentar / Reservar un stand"
    Vista->>Ctrl: listarStandsDisponibles()
    Ctrl->>Qry: obtenerStandsDisponibles()
    Qry->>BD: SELECT ... WHERE estado = 'Disponible'
    BD-->>Qry: Lista de stands disponibles
    Qry-->>Ctrl: List<Stand>
    Ctrl-->>Vista: List<Stand>
    Vista-->>Editorial: Muestra stands disponibles

    Editorial->>Vista: Ingresa folio, stand, fecha y estado de pago
    Vista->>Ctrl: registrarReservacion(reservacion)
    Ctrl->>Qry: insertarReservacion(reservacion)
    Qry->>BD: BEGIN TRANSACTION
    Qry->>BD: Verifica stand disponible
    Qry->>BD: INSERT INTO reservaciones
    Qry->>BD: UPDATE stands SET estado = 'Reservado'
    Qry->>BD: INSERT INTO notificaciones (confirmación)
    Qry->>BD: COMMIT
    BD-->>Qry: OK
    Qry-->>Ctrl: OK
    Ctrl-->>Vista: true
    Vista-->>Editorial: Reservación confirmada
```
