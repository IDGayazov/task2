package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Utility class for handling JSON operations in a web application.
 */
public class JsonHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sends a JSON response to the client.
     *
     * @param response the HttpServletResponse to send the JSON response
     * @param object the object to be converted to JSON and sent
     * @throws IOException if an input or output exception occurs
     */
    public static void sendJson(HttpServletResponse response, Object object) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        String objectJson = objectMapper.writeValueAsString(object);

        PrintWriter writer = response.getWriter();

        writer.print(objectJson);
        writer.close();
    }

    /**
     * Retrieves JSON data from an HttpServletRequest.
     *
     * @param req the HttpServletRequest containing the JSON data
     * @return a String containing the JSON data
     * @throws IOException if an input or output exception occurs
     */
    public static String getJsonByRequest(HttpServletRequest req) throws IOException{
        Scanner scanner = new Scanner(req.getInputStream(), StandardCharsets.UTF_8);
        String jsonData = scanner.useDelimiter("\\A").next();
        scanner.close();
        return jsonData;
    }

    public static ObjectMapper getObjectMapper(){
        return objectMapper;
    }
}
