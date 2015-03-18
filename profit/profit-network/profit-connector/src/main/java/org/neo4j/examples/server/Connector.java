/**
 * Licensed to Neo Technology under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Neo Technology licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.neo4j.examples.server;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Connector
{
    private static final String SERVER_ROOT_URI = "http://profit.willautomate.com:7474/db/data/";

    public static void main( String[] args ) throws URISyntaxException
    {
        checkDatabaseIsRunning();

        // START SNIPPET: nodesAndProps
//        URI firstNode = createNode();
//        addProperty( firstNode, "value", "1" );
//        URI secondNode = createNode();
//        addProperty( secondNode, "value", "2" );
        // END SNIPPET: nodesAndProps

        // START SNIPPET: addRel
//        URI relationshipUri = addRelationship( firstNode, secondNode, "singer",
//                "{ \"from\" : \"1976\", \"until\" : \"1986\" }" );
        // END SNIPPET: addRel

        // START SNIPPET: addMetaToRel
//        addMetadataToProperty( relationshipUri, "stars", "5" );
        // END SNIPPET: addMetaToRel

        // START SNIPPET: queryForSingers
//        findSingersInBands( firstNode );
        // END SNIPPET: queryForSingers
        
//        createNumbers();
//        [14.0, 23.0, 24.0, 30.0, 49.0]
//        createLetter(11,23,26,38,44,1,8);
//        createPrediction(14, 23, 24, 30, 49, 0, 0, 30, 2);
    }

    public static void createPrediction(int m1,int m2,int m3, int m4, int m5, int l1, int l2, int wordSize, int distance){
        StringBuilder sb = new StringBuilder("match (for:Letter) ");
        if (m1 != 0){
            sb.append(",(m1:Number),"
                    + "(m2:Number),"
                    + "(m3:Number),"
                    + "(m4:Number),"
                    + "(m5:Number)");
        }
        if (l1 != 0){
            sb.append(",(l1:Number),"
                    + "(l2:Number)");
        }
        sb.append(" where ");
        if (m1 != 0){
            sb.append(String.format("m1.value=%s and m2.value=%s and m3.value=%s and m4.value=%s and m5.value=%s and ",m1,m2,m3,m4,m5));
        }
        if (l1 != 0){
            sb.append(String.format("l1.value=%s and l2.value=%s and " , l1,l2));
        }
        sb.append(" has(for.LATEST) ");
        sb.append(String.format("create (n:Letter:Prediction {wordsize:%s, distance:%s}) "
                + "create (n)-[:FOR]->(for) ",wordSize,distance));
        if (m1 != 0){
            sb.append("create (n)-[:MAIN]->(m1) "
                + "create (n)-[:MAIN]->(m2) "
                + "create (n)-[:MAIN]->(m3) "
                + "create (n)-[:MAIN]->(m4) "
                + "create (n)-[:MAIN]->(m5) ");
        }
        if (l1 != 0){
            sb.append("create (n)-[:LUCKY]->(l1) "
                    + "create (n)-[:LUCKY]->(l2) ");
        }
        sb.append(" return n");
        sendTransactionalCypherQuery(sb.toString());
        
    }
    private static void createLetter(int m1,int m2,int m3, int m4, int m5, int l1, int l2){
        sendTransactionalCypherQuery( String.format("match "
                + "(m1:Number),"
                + "(m2:Number),"
                + "(m3:Number),"
                + "(m4:Number),"
                + "(m5:Number),"
                + "(l1:Number),"
                + "(l2:Number) "
                + "where m1.value=%s and m2.value=%s and m3.value=%s and m4.value=%s and m5.value=%s"
                + " and l1.value=%s and l2.value=%s"
                + " create (n:Letter) "
                + " create (n)-[:MAIN]->(m1) "
                + "create (n)-[:MAIN]->(m2) "
                + "create (n)-[:MAIN]->(m3) "
                + "create (n)-[:MAIN]->(m4) "
                + "create (n)-[:MAIN]->(m5) "
                + "create (n)-[:LUCKY]->(l1) "
                + "create (n)-[:LUCKY]->(l2) "
                + "return Id(n)",m1,m2,m3,m4,m5,l1,l2));
        
    }
    private static void createNumbers(){
        for (int i=1;i<=50;i++){
            sendTransactionalCypherQuery(String.format("create (n:Number {value:%s})", i));
        }
    }
    public static String sendTransactionalCypherQuery(String query) {
        // START SNIPPET: queryAllNodes
        final String txUri = SERVER_ROOT_URI + "transaction/commit";
        WebResource resource = Client.create().resource( txUri );

        String payload = "{\"statements\" : [ {\"statement\" : \"" +query + "\"} ]}";
        ClientResponse response = resource
                .accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( payload )
                .post( ClientResponse.class );
        String result = response.getEntity( String.class ) ;
        System.out.println( String.format(
                "POST [%s] to [%s], status code [%d], returned data: "
                        + System.getProperty( "line.separator" ) + "%s",
                payload, txUri, response.getStatus(),
                result ) );
         response.close();
         return result;
        // END SNIPPET: queryAllNodes
    }

    private static void findSingersInBands( URI startNode )
            throws URISyntaxException
    {
        // START SNIPPET: traversalDesc
        // TraversalDefinition turns into JSON to send to the Server
        TraversalDefinition t = new TraversalDefinition();
        t.setOrder( TraversalDefinition.DEPTH_FIRST );
        t.setUniqueness( TraversalDefinition.NODE );
        t.setMaxDepth( 10 );
        t.setReturnFilter( TraversalDefinition.ALL );
        t.setRelationships( new Relation( "singer", Relation.OUT ) );
        // END SNIPPET: traversalDesc

        // START SNIPPET: traverse
        URI traverserUri = new URI( startNode.toString() + "/traverse/node" );
        WebResource resource = Client.create()
                .resource( traverserUri );
        String jsonTraverserPayload = t.toJson();
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( jsonTraverserPayload )
                .post( ClientResponse.class );

        System.out.println( String.format(
                "POST [%s] to [%s], status code [%d], returned data: "
                        + System.getProperty( "line.separator" ) + "%s",
                jsonTraverserPayload, traverserUri, response.getStatus(),
                response.getEntity( String.class ) ) );
        response.close();
        // END SNIPPET: traverse
    }

    // START SNIPPET: insideAddMetaToProp
    private static void addMetadataToProperty( URI relationshipUri,
            String name, String value ) throws URISyntaxException
    {
        URI propertyUri = new URI( relationshipUri.toString() + "/properties" );
        String entity = toJsonNameValuePairCollection( name, value );
        WebResource resource = Client.create()
                .resource( propertyUri );
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( entity )
                .put( ClientResponse.class );

        System.out.println( String.format(
                "PUT [%s] to [%s], status code [%d]", entity, propertyUri,
                response.getStatus() ) );
        response.close();
    }

    // END SNIPPET: insideAddMetaToProp

    private static String toJsonNameValuePairCollection( String name,
            String value )
    {
        return String.format( "{ \"%s\" : \"%s\" }", name, value );
    }

    private static URI createNode()
    {
        // START SNIPPET: createNode
        final String nodeEntryPointUri = SERVER_ROOT_URI + "node";
        // http://localhost:7474/db/data/node

        WebResource resource = Client.create()
                .resource( nodeEntryPointUri );
        // POST {} to the node entry point URI
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( "{}" )
                .post( ClientResponse.class );

        final URI location = response.getLocation();
        System.out.println( String.format(
                "POST to [%s], status code [%d], location header [%s]",
                nodeEntryPointUri, response.getStatus(), location.toString() ) );
        response.close();

        return location;
        // END SNIPPET: createNode
    }

    // START SNIPPET: insideAddRel
    private static URI addRelationship( URI startNode, URI endNode,
            String relationshipType, String jsonAttributes )
            throws URISyntaxException
    {
        URI fromUri = new URI( startNode.toString() + "/relationships" );
        String relationshipJson = generateJsonRelationship( endNode,
                relationshipType, jsonAttributes );

        WebResource resource = Client.create()
                .resource( fromUri );
        // POST JSON to the relationships URI
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( relationshipJson )
                .post( ClientResponse.class );

        final URI location = response.getLocation();
        System.out.println( String.format(
                "POST to [%s], status code [%d], location header [%s]",
                fromUri, response.getStatus(), location.toString() ) );

        response.close();
        return location;
    }
    // END SNIPPET: insideAddRel

    private static String generateJsonRelationship( URI endNode,
            String relationshipType, String... jsonAttributes )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "{ \"to\" : \"" );
        sb.append( endNode.toString() );
        sb.append( "\", " );

        sb.append( "\"type\" : \"" );
        sb.append( relationshipType );
        if ( jsonAttributes == null || jsonAttributes.length < 1 )
        {
            sb.append( "\"" );
        }
        else
        {
            sb.append( "\", \"data\" : " );
            for ( int i = 0; i < jsonAttributes.length; i++ )
            {
                sb.append( jsonAttributes[i] );
                if ( i < jsonAttributes.length - 1 )
                { // Miss off the final comma
                    sb.append( ", " );
                }
            }
        }

        sb.append( " }" );
        return sb.toString();
    }

    private static void addProperty( URI nodeUri, String propertyName,
            String propertyValue )
    {
        // START SNIPPET: addProp
        String propertyUri = nodeUri.toString() + "/properties/" + propertyName;
        // http://localhost:7474/db/data/node/{node_id}/properties/{property_name}

        WebResource resource = Client.create()
                .resource( propertyUri );
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( "\"" + propertyValue + "\"" )
                .put( ClientResponse.class );

        System.out.println( String.format( "PUT to [%s], status code [%d]",
                propertyUri, response.getStatus() ) );
        response.close();
        // END SNIPPET: addProp
    }

    private static void checkDatabaseIsRunning()
    {
        // START SNIPPET: checkServer
        WebResource resource = Client.create()
                .resource( SERVER_ROOT_URI );
        ClientResponse response = resource.get( ClientResponse.class );

        System.out.println( String.format( "GET on [%s], status code [%d]",
                SERVER_ROOT_URI, response.getStatus() ) );
        response.close();
        // END SNIPPET: checkServer
    }
}