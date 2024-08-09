package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.course.CourseDto;
import dto.course.ReadCourseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.CourseService;
import util.JsonHandler;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static util.JsonHandler.getJsonByRequest;
import static util.JsonHandler.sendJson;

@WebServlet("/courses/*")
public class CourseServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CourseServlet.class);

    private CourseService courseService = new CourseService();
    public ObjectMapper objectMapper = JsonHandler.getObjectMapper();

    /**
     * Handles GET requests to retrieve courses. Can retrieve all courses or a course by ID.
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

            List<ReadCourseDto> courses = courseService.getAllCourses();

            sendJson(resp, courses);

        }else{

            String[] paths = pathInfo.split("/");

            if(paths.length > 2){
                logger.warn("Bad request, more than one path parameter: {}", pathInfo);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }else{
                try {

                    Integer courseId = Integer.parseInt(paths[1]);

                    Optional<ReadCourseDto> course = courseService.getCourseById(courseId);

                    if(course.isPresent()){
                        sendJson(resp, course.get());
                    }else{
                        logger.warn("Student not found with ID: {}", courseId);
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
     * Handles POST requests to create a new course.
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

            CourseDto courseDto = objectMapper.readValue(jsonData, CourseDto.class);

            courseService.saveCourse(courseDto);

        }else{
            logger.warn("Bad request to POST /students with pathInfo: {}", pathInfo);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Handles PUT requests to update an existing course by ID.
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

            Integer studentId = Integer.parseInt(paths[1]);

            String jsonData = getJsonByRequest(req);

            CourseDto courseDto = objectMapper.readValue(jsonData, CourseDto.class);

            courseService.updateCourse(courseDto, studentId);

        }catch(NumberFormatException e){
            logger.error("Invalid student ID format: {}", paths[1]);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Handles DELETE requests to remove a course by ID.
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
            Integer courseId = Integer.parseInt(paths[1]);
            courseService.deleteCourse(courseId);
        }catch(NumberFormatException e){
            logger.error("Invalid student ID format: {}", paths[1]);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
