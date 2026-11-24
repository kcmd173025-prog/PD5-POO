import controlador.FeriaControlador;
import vista.VistaConsola;

public class Main {
    public static void main(String[] args) {
        FeriaControlador controlador = new FeriaControlador();
        VistaConsola vista = new VistaConsola(controlador);
        vista.iniciar();
    }
}
