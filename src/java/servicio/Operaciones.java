package servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("generic")
public class Operaciones {

    @Context
    private UriInfo context;

    private static final Logger LOGGER = Logger.getLogger(Operaciones.class.getName());

    public Operaciones() {
    }

    class Persona {
        private int id;
        private String nombre;
        private String apellido;
        private Date fechaNacimiento;
        private int documento;
        private int tipoPersonaId;

        public Persona(int id, String nombre, String apellido, Date fechaNacimiento, int documento, int tipoPersonaId) {
            this.id = id;
            this.nombre = nombre;
            this.apellido = apellido;
            this.fechaNacimiento = fechaNacimiento;
            this.documento = documento;
            this.tipoPersonaId = tipoPersonaId;
        }

        ////////////////////////////
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public void setApellido(String apellido) {
            this.apellido = apellido;
        }

        public Date getFechaNacimiento() {
            return fechaNacimiento;
        }

        public void setFechaNacimiento(Date fechaNacimiento) {
            this.fechaNacimiento = fechaNacimiento;
        }

        public int getDocumento() {
            return documento;
        }

        public void setDocumento(int documento) {
            this.documento = documento;
        }

        public int getTipoPersonaId() {
            return tipoPersonaId;
        }

        public void setTipoPersonaId(int tipoPersonaId) {
            this.tipoPersonaId = tipoPersonaId;
        }
    }

    @GET
    @Path("lista")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Persona> Listar(){
        List<Persona> personas = new ArrayList<>();
        try {
            //personas.add(new Persona(1, "Juan", "Garcia", new Date(90, 0, 10), 123456789, 3));
            //personas.add(new Persona(2, "Maria", "Lopez", new Date(85, 1, 20), 987654321, 2));
            //personas.add(new Persona(3, "Carlos", "Martinez", new Date(92, 2, 15), 456123789, 1));
            //personas.add(new Persona(4, "Laura", "Gomez", new Date(88, 3, 25), 789456123, 4));
            personas.add(new Persona(5, "Jose", "Rodriguez", new Date(95, 4, 30), 321654987, 5));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error listing personas", e);
            throw new RuntimeException("Error listing personas", e);
        }
        return personas;
    }
    
    @GET
    @Path("buscar/{id}")
    public String buscar(@PathParam("id") String id){
        TableroControl.incrementTotalRequests();
        int idInt = Integer.parseInt(id);
        for (Persona persona : Listar()){
            if (persona.getId() == idInt){
                TableroControl.incrementAcceptedRequests("ID=" + persona.getId() + ", Nombre=" + persona.getNombre());
                TableroControl.addRequestDetail("Buscar: " + id + " - encontrado");
                return "Usuario encontrado: ID=" + persona.getId() + 
                       ", Nombre=" + persona.getNombre() + 
                       ", Apellido=" + persona.getApellido() + 
                       ", Fecha de Nacimiento=" + persona.getFechaNacimiento() + 
                       ", Documento=" + persona.getDocumento() + 
                       ", Tipo de Persona ID=" + persona.getTipoPersonaId();
            }
        }
        TableroControl.incrementRejectedRequests("ID=" + id);
        TableroControl.addRequestDetail("Buscar: " + id + " - no encontrado");
        return "Usuario no encontrado: " + id;
    }
}