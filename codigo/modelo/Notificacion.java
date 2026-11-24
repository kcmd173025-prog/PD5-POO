package modelo;

public class Notificacion {
    private String id;
    private String idEditorial;
    private String titulo;
    private String mensaje;
    private String fecha;
    private boolean leida;

    public Notificacion(String id, String idEditorial, String titulo, String mensaje, String fecha, boolean leida) {
        this.id = id;
        this.idEditorial = idEditorial;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.leida = leida;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdEditorial() { return idEditorial; }
    public void setIdEditorial(String idEditorial) { this.idEditorial = idEditorial; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
}
