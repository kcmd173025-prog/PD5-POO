# Requisitos Funcionales — Sistema de Gestión de Stands (Feria del Libro)

## 1. Propósito del Documento
Definir las funcionalidades del sistema desarrollado bajo arquitectura MVC con acceso a datos genérico en MySQL para la gestión de stands, editoriales, reservaciones y notificaciones.

## 2. Catálogo de Requisitos Funcionales

### RF-01: Inicialización de Base de Datos y Catálogo Inicial
* **Descripción:** Al iniciar la aplicación, el sistema debe verificar o crear las tablas (`stands`, `editoriales`, `reservaciones`, `notificaciones`) y poblar el catálogo inicial si las tablas se encuentran vacías.
* **Prioridad:** Alta.

### RF-02: Visualización de Stands
* **Descripción:** El sistema debe mostrar en la página principal todos los stands con su ID, ubicación, metros cuadrados, costo y estado (`Disponible` o `Reservado`), y mostrar en la sesión de editorial los stands disponibles para renta.
* **Prioridad:** Alta.

### RF-03: Administración de Stands (CRUD y Tarifa por m²)
* **Descripción:** El administrador debe poder crear, consultar, actualizar y eliminar stands. Al crear o actualizar un stand, su costo debe calcularse automáticamente multiplicando sus metros cuadrados por la tarifa global por metro cuadrado vigente. Asimismo, la selección de estado al actualizar debe realizarse mediante opciones numéricas (`1. Disponible` / `2. Reservado`).
* **Prioridad:** Alta.

### RF-04: Configuración Global de Costo por Metro Cuadrado
* **Descripción:** El administrador debe poder consultar y modificar el costo global por metro cuadrado desde el menú de configuración, indicando si desea recalcular (`1. Si` / `2. No`) el costo de todos los stands existentes en la base de datos.
* **Prioridad:** Alta.

### RF-05: Registro, Inicio de Sesión y Actualización de Editoriales
* **Descripción:** El sistema debe permitir crear una nueva editorial (generando una notificación de bienvenida), iniciar sesión con una editorial existente y actualizar sus datos (nombre, país y contacto). El administrador también puede gestionar editoriales desde configuración.
* **Prioridad:** Alta.

### RF-06: Renta / Reservación de Stands
* **Descripción:** Una editorial con sesión activa (o el administrador desde configuración) debe poder rentar un stand disponible registrando folio, stand, fecha y estado de pago (`Pagado` o `Pendiente`), cambiando el estado del stand a `Reservado` y generando una notificación de confirmación.
* **Prioridad:** Alta.

### RF-07: Gestión de "Mis Reservaciones"
* **Descripción:** Desde el apartado **Mis reservaciones**, cada editorial debe consultar sus reservaciones activas y ejecutar:
  1. **Realizar pago** (cambiar estado de pago a `Pagado`).
  2. **Cambiar de stand** (reasignar a otro stand disponible, liberando el anterior y ocupando el nuevo).
  3. **Modificar fecha o datos de reservación**.
  4. **Cancelar reservación** (eliminar la reserva, liberar el stand como `Disponible` y notificar a la editorial).
* **Prioridad:** Alta.

### RF-08: Buzón de Notificaciones de Editorial y Alertas de Administrador
* **Descripción:** Cada editorial debe ver su contador de notificaciones sin leer, leer su buzón, marcar una o todas como leídas o eliminarlas. El administrador debe poder enviar notificaciones individuales o generales, administrarlas y consultar alertas de reservaciones con pago `Pendiente`.
* **Prioridad:** Media.

### RF-09: Suite de Pruebas de Integridad
* **Descripción:** El administrador debe poder ejecutar una suite de pruebas automatizadas que valide restricciones de clave primaria, clave foránea, disponibilidad de stands y manejo controlado de excepciones sin interrumpir la ejecución.
* **Prioridad:** Media.
