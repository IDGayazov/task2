package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.coordinator.CoordinatorDto;
import dto.coordinator.CreateCoordinatorDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.CoordinatorService;
import util.JsonHandler;

import java.io.IOException;
import java.util.Optional;
import java.util.List;

import static util.JsonHandler.getJsonByRequest;
import static util.JsonHandler.sendJson;

@WebServlet("/coordinators/*")
public class CoordinatorServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CoordinatorServlet.class);

    private CoordinatorService coordinatorService = new CoordinatorService();
    public ObjectMapper objectMapper = JsonHandler.getObjectMapper();

    /**
     * Handles GET requests to retrieve coordinators. Can retrieve all coordinators or a coordinator by ID.
     * @param req the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        logger.info("Received GET request: {}", req.getPathInfo());

        String pathInfo = req.getPathInfo();

        if(pathInfo == null || pathInfo.equals("/")){

            List<CoordinatorDto> coordinators = coordinatorService.getAllCoordinators();

            sendJson(resp, coordinators);

        }else{

            String[] paths = pathInfo.split("/");

            if(paths.length > 2){
                logger.warn("Bad request, more than one path parameter: {}", pathInfo);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }else{
                try {

                    Integer coordinatorId = Integer.parseInt(paths[1], 10);

                    Optional<CoordinatorDto> coordinator = coordinatorService.getCoordinatorById(coordinatorId);

                    if(coordinator.isPresent()){
                        sendJson(resp, coordinator.get());
                    }else{
                        logger.warn("Student not found with ID: {}", coordinatorId);
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }

                }catch(NumberFormatException e){
                    logger.error("Invalid student ID format: {}", paths[1]);
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
        }

    }

    /**
     * Handles POST requests to create a new coordinator.
     * @param req the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        logger.info("Received POST request: {}", req.getPathInfo());

        String pathInfo = req.getPathInfo();

        if(pathInfo == null || pathInfo.equals("/")){
            String jsonData = getJsonByRequest(req);

            CreateCoordinatorDto coordinatorDto = objectMapper.readValue(jsonData, CreateCoordinatorDto.class);

            coordinatorService.saveCoordinator(coordinatorDto);
        }else{
            logger.warn("Bad request to POST /students with pathInfo: {}", pathInfo);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    /**
     * Handles PUT requests to update an existing coordinator by ID.
     * @param req the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        logger.info("Received PUT request: {}", req.getPathInfo());

        String pathInfo = req.getPathInfo();

        if(pathInfo == null || pathInfo.equals("/")) {
            logger.warn("Bad request to PUT /students with no ID specified");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String[] paths = pathInfo.split("/");
        if(paths.length > 2){
            logger.warn("Bad request, more than one path parameter: {}", pathInfo);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {

            Integer coordinatorId = Integer.parseInt(paths[1]);

            String jsonData = getJsonByRequest(req);
            CreateCoordinatorDto coordinatorDto = objectMapper.readValue(jsonData, CreateCoordinatorDto.class);
            coordinatorService.updateCoordinator(coordinatorDto, coordinatorId);

        }catch(NumberFormatException e){
            logger.error("Invalid student ID format: {}", paths[1]);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Handles DELETE requests to remove a coordinator by ID.
     * @param req the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        logger.info("Received DELETE request: {}", req.getPathInfo());

        String pathInfo = req.getPathInfo();

        if(pathInfo == null || pathInfo.equals("/")) {
            logger.warn("Bad request to DELETE /students with no ID specified");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String[] paths = pathInfo.split("/");
        if(paths.length > 2){
            logger.warn("Bad request, more than one path parameter: {}", pathInfo);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Integer coordinatorId = Integer.parseInt(paths[1]);
            coordinatorService.deleteCoordinator(coordinatorId);
        }catch(NumberFormatException e){
            logger.error("Invalid student ID format: {}", paths[1]);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}

