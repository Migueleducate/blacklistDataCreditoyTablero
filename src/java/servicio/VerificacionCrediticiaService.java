package servicio;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Path("verificacion")
public class VerificacionCrediticiaService {

    private Operaciones operaciones;
    private DataCreditoService dataCreditoService;

    public VerificacionCrediticiaService() {
        operaciones = new Operaciones();
        dataCreditoService = new DataCreditoService();
    }

    @GET
    @Path("/verificar/{idCliente}")
    public String verificarCliente(@PathParam("idCliente") String idCliente) {
        TableroControl.incrementTotalRequests();
        String nombreCliente = getNombreCliente(idCliente);
        if (nombreCliente.equals("Desconocido")) {
            TableroControl.incrementRejectedRequests("ID=" + idCliente);
            TableroControl.addRequestDetail("Verificar: " + idCliente + " - no encontrado");
            return "Usuario no encontrado en el sistema";
        }
        if (consultarBlackList(idCliente)) {
            TableroControl.incrementRejectedRequests("ID=" + idCliente + ", Nombre=" + nombreCliente);
            TableroControl.addRequestDetail("Verificar: " + idCliente + " - en BlackList");
            return "Cliente " + nombreCliente + " está en la BlackList - Solicitud rechazada";
        }

        if (!consultarDataCredito(idCliente)) {
            TableroControl.incrementRejectedRequests("ID=" + idCliente + ", Nombre=" + nombreCliente);
            TableroControl.addRequestDetail("Verificar: " + idCliente + " - no aprobado por DataCredito");
            return "Cliente " + nombreCliente + " no aprobado por DataCredito - Solicitud rechazada";
        }

        TableroControl.incrementAcceptedRequests("ID=" + idCliente + ", Nombre=" + nombreCliente);
        TableroControl.addRequestDetail("Verificar: " + idCliente + " - aprobado");
        return "Cliente " + nombreCliente + " aprobado - Verificación exitosa";
    }

    @GET
    @Path("/enviarResultado/{idCliente}/{email}")
    public String enviarResultadoPorEmailEndpoint(@PathParam("idCliente") String idCliente, @PathParam("email") String email) {
        enviarResultadoPorEmail(idCliente, email);
        return "Email enviado a " + email;
    }

    public void enviarResultadoPorEmail(String idCliente, String email) {
        String resultado = verificarCliente(idCliente);
        String nombreCliente = getNombreCliente(idCliente);
        String mensaje;
        if (resultado.contains("aprobado")) {
            mensaje = "Estimado " + nombreCliente + ", su solicitud fue aprobada.";
        } else {
            mensaje = "Estimado " + nombreCliente + ", su solicitud fue rechazada.";
        }
        enviarEmail(email, mensaje);
    }

    private boolean consultarBlackList(String idCliente) {
        int id = Integer.parseInt(idCliente);
        for (Operaciones.Persona persona : operaciones.Listar()) {
            if (persona.getId() == id) {
                return true; 
            }
        }
        return false; 
    }

    private boolean consultarDataCredito(String idCliente) {
        int id = Integer.parseInt(idCliente);
        for (DataCreditoService.Persona persona : dataCreditoService.getPersonas()) {
            if (persona.getId() == id) {
                return persona.getPuntaje() >= 700; 
            }
        }
        return false; 
    }

    private String getNombreCliente(String idCliente) {
        int id = Integer.parseInt(idCliente);
        for (Operaciones.Persona persona : operaciones.Listar()) {
            if (persona.getId() == id) {
                return persona.getNombre();
            }
        }
        for (DataCreditoService.Persona persona : dataCreditoService.getPersonas()) {
            if (persona.getId() == id) {
                return persona.getNombre();
            }
        }
        return "Desconocido";
    }

    private void enviarEmail(String destinatario, String mensaje) {
        String remitente = "mrochac@uniempresarial.edu.co"; 
        String clave = "Uempresarial@2024"; 

        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.uniempresarial.edu.co"); 
        props.put("mail.smtp.user", remitente);
        props.put("mail.smtp.clave", clave);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(remitente));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            message.setSubject("Resultado de Verificación Crediticia");
            message.setText(mensaje);
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.uniempresarial.edu.co", remitente, clave);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException me) {
            me.printStackTrace();
        }
    }
}