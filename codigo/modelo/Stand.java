package modelo;

public class Stand {
    private String id;
    private String ubicacion;
    private double metrosCuadrados;
    private double costo;
    private String estado;

    public Stand(String id, String ubicacion, double metrosCuadrados, double costo, String estado) {
        this.id = id;
        this.ubicacion = ubicacion;
        this.metrosCuadrados = metrosCuadrados;
        this.costo = costo;
        this.estado = estado;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public double getMetrosCuadrados() { return metrosCuadrados; }
    public void setMetrosCuadrados(double metrosCuadrados) { this.metrosCuadrados = metrosCuadrados; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
