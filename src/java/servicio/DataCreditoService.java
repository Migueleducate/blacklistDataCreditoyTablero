package servicio;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("datacredito")
public class DataCreditoService {

    private List<Persona> personas;

    public DataCreditoService() {
        personas = new ArrayList<>();
        personas.add(new Persona(1, "Juan Garcia", 750));
        personas.add(new Persona(2, "Maria Lopez", 820));
        personas.add(new Persona(3, "Carlos Martinez", 670));
        personas.add(new Persona(4, "Laura Gomez", 910));
        personas.add(new Persona(5, "Jose Rodriguez", 580));
    }

    class Persona {
        private int id;
        private String nombre;
        private int puntaje;

        public Persona(int id, String nombre, int puntaje) {
            this.id = id;
            this.nombre = nombre;
            this.puntaje = puntaje;
        }

        public int getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public int getPuntaje() {
            return puntaje;
        }
    }

    public List<Persona> getPersonas() {
        return personas;
    }

    @GET
    @Path("/consulta/{idCliente}")
    public String consultaCredito(@PathParam("idCliente") String idCliente) {
        TableroControl.incrementTotalRequests();
        int id = Integer.parseInt(idCliente);
        for (Persona persona : personas) {
            if (persona.getId() == id) {
                String result = persona.getPuntaje() >= 700 ? "aprobado" : "rechazado";
                if (result.equals("aprobado")) {
                    TableroControl.incrementAcceptedRequests("ID=" + persona.getId() + ", Nombre=" + persona.getNombre());
                } else {
                    TableroControl.incrementRejectedRequests("ID=" + persona.getId() + ", Nombre=" + persona.getNombre());
                }
                TableroControl.addRequestDetail("Consulta credito: " + idCliente + " - " + result);
                return result;
            }
        }
        TableroControl.incrementRejectedRequests("ID=" + idCliente);
        TableroControl.addRequestDetail("Consulta credito: " + idCliente + " - rechazado");
        return "rechazado";
    }
}
