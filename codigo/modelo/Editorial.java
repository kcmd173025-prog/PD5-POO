package modelo;

public class Editorial {
    private String id;
    private String nombre;
    private String pais;
    private String contacto;

    public Editorial(String id, String nombre, String pais, String contacto) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
        this.contacto = contacto;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
}
