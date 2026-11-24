# Modelo Entidad-Relación — Base de Datos Relacional

## 1. Tablas y Restricciones
* **`stands`:** `id` (PK), `ubicacion`, `metros_cuadrados`, `costo`, `estado` (`Disponible` / `Reservado`).
* **`editoriales`:** `id` (PK), `nombre`, `pais`, `contacto`.
* **`reservaciones`:** `id` (PK), `id_stand` (FK -> `stands.id`, `UNIQUE`), `id_editorial` (FK -> `editoriales.id`), `fecha`, `estado_pago` (`Pagado` / `Pendiente`).
* **`notificaciones`:** `id` (PK), `id_editorial` (FK -> `editoriales.id`, `ON DELETE CASCADE`), `titulo`, `mensaje`, `fecha`, `leida`.

## 2. Diagrama Entidad-Relación (Mermaid)

```mermaid
erDiagram
    STANDS {
        varchar id PK
        varchar ubicacion
        double metros_cuadrados
        double costo
        varchar estado
    }

    EDITORIALES {
        varchar id PK
        varchar nombre
        varchar pais
        varchar contacto
    }

    RESERVACIONES {
        varchar id PK
        varchar id_stand UK,FK
        varchar id_editorial FK
        varchar fecha
        varchar estado_pago
    }

    NOTIFICACIONES {
        varchar id PK
        varchar id_editorial FK
        varchar titulo
        varchar mensaje
        varchar fecha
        boolean leida
    }

    STANDS ||--o| RESERVACIONES : "asigna"
    EDITORIALES ||--o{ RESERVACIONES : "realiza"
    EDITORIALES ||--o{ NOTIFICACIONES : "recibe"
```
