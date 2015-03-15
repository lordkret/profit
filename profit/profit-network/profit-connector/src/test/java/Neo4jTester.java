import javax.ws.rs.core.MediaType;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
public class Neo4jTester {

    public static final String SERVER_ROOT_URI = "http://localhost:7474/";
    
    @Test
    public void lame(){
        final String txUri = SERVER_ROOT_URI + "transaction/commit";
        WebResource resource = Client.create().resource( txUri );
        String query = "create (n:NUMBER {value:1}) ";
        String payload = "{\"statements\" : [ {\"statement\" : \"" +query + "\"} ]}";
        ClientResponse response = resource
                .accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( payload )
                .post( ClientResponse.class );

        System.out.println( String.format(
                "POST [%s] to [%s], status code [%d], returned data: "
                        + System.getProperty( "line.separator" ) + "%s",
                payload, txUri, response.getStatus(),
                response.getEntity( String.class ) ) );

        response.close();
    
    }
}
