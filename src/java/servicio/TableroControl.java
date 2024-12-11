package servicio;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("tablero")
public class TableroControl {

    private static int totalRequests = 0;
    private static int acceptedRequests = 0;
    private static int rejectedRequests = 0;
    private static List<String> requestDetails = new ArrayList<>();
    private static List<String> acceptedUsers = new ArrayList<>();
    private static List<String> rejectedUsers = new ArrayList<>();

    public static void incrementTotalRequests() {
        totalRequests++;
    }

    public static void incrementAcceptedRequests(String userDetail) {
        acceptedRequests++;
        acceptedUsers.add(userDetail);
    }

    public static void incrementRejectedRequests(String userDetail) {
        rejectedRequests++;
        rejectedUsers.add(userDetail);
    }

    public static void addRequestDetail(String detail) {
        requestDetails.add(detail);
    }

    @GET
    @Path("/stats")
    @Produces(MediaType.APPLICATION_JSON)
    public String getStats() {
        return "{ \"Solicitudes totales\": " + totalRequests + 
               ", \"Solicitudes aceptadas\": " + acceptedRequests + 
               ", \"Solicitudes rechazadas\": " + rejectedRequests + 
               ", \"Usuarios aceptados\": " + acceptedUsers + 
               ", \"Usuarios rechazados\": " + rejectedUsers + " }";
    }
}
