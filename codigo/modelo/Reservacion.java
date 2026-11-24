package modelo;

public class Reservacion {
    private String id;
    private String idStand;
    private String idEditorial;
    private String fecha;
    private String estadoPago;

    public Reservacion(String id, String idStand, String idEditorial, String fecha, String estadoPago) {
        this.id = id;
        this.idStand = idStand;
        this.idEditorial = idEditorial;
        this.fecha = fecha;
        this.estadoPago = estadoPago;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdStand() { return idStand; }
    public void setIdStand(String idStand) { this.idStand = idStand; }

    public String getIdEditorial() { return idEditorial; }
    public void setIdEditorial(String idEditorial) { this.idEditorial = idEditorial; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
}
