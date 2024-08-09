package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.course.CourseDto;
import dto.course.ReadCourseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import service.CourseService;
import util.StringServletInputStream;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CourseServletTest {

    @Mock
    private CourseService courseService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CourseServlet courseServlet;

    private StringWriter responseWriter;

    @BeforeEach
    public void init() throws IOException {
        MockitoAnnotations.openMocks(this);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getContentType()).thenReturn("application/json; charset=UTF-8");
        courseServlet.objectMapper = new ObjectMapper();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"/"})
    void doGetAllCoordinators(String pathInfo) throws ServletException, IOException {
        List<ReadCourseDto> courses = Arrays.asList(
                new ReadCourseDto(1, "Eng", null),
                new ReadCourseDto(2, "Math", null)
        );

        when(request.getPathInfo()).thenReturn(pathInfo);
        when(courseService.getAllCourses()).thenReturn(courses);

        courseServlet.doGet(request, response);

        String expectedJson = courseServlet.objectMapper.writeValueAsString(courses);
        assertEquals(expectedJson, responseWriter.toString().trim());
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    void doGetCoordinatorById() throws ServletException, IOException {

        ReadCourseDto course = new ReadCourseDto(1, "John Doe", null);

        Integer courseId = 1;

        when(request.getPathInfo()).thenReturn("/" + courseId);
        when(courseService.getCourseById(courseId)).thenReturn(Optional.of(course));

        courseServlet.doGet(request, response);

        String expectedJson = courseServlet.objectMapper.writeValueAsString(course);
        assertEquals(expectedJson, responseWriter.toString().trim());
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @ParameterizedTest
    @ValueSource(strings = {"/12345abc", "/incorrect", "/1/2"})
    void doGetCoordinatorByIdWithBadParam(String pathInfo) throws ServletException, IOException {

        ReadCourseDto courseDto = new ReadCourseDto(1, "John Doe", null);

        when(request.getPathInfo()).thenReturn(pathInfo);

        courseServlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"/"})
    void doPostWithCorrectParam(String pathInfo) throws IOException, ServletException {

        CourseDto courseDto = new CourseDto(1, "John Doe");

        String courseDtoJson = courseServlet.objectMapper
                .writeValueAsString(courseDto);

        when(request.getPathInfo()).thenReturn(pathInfo);

        when(request.getInputStream()).thenReturn(new StringServletInputStream(courseDtoJson));

        courseServlet.doPost(request, response);

        ArgumentCaptor<CourseDto> captor = ArgumentCaptor.forClass(CourseDto.class);

        verify(courseService).saveCourse(captor.capture());

        CourseDto capturedArgument = captor.getValue();

        assertEquals(capturedArgument.getId(), courseDto.getId());
        assertEquals(capturedArgument.getName(), courseDto.getName());

    }

    @Test
    void doPostWithBadParam() throws IOException, ServletException {

        String incorrectPathInfo = "incorrectPathInfo";

        when(request.getPathInfo()).thenReturn(incorrectPathInfo);

        courseServlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);

    }

    @ParameterizedTest
    @ValueSource(strings = {"/1"})
    void doPutWithCorrectParam(String pathInfo) throws IOException, ServletException {

        Integer courseId = 1;
        CourseDto courseDto = new CourseDto(1, "Math");

        when(request.getPathInfo()).thenReturn(pathInfo);

        String courseDtoJson = courseServlet.objectMapper
                .writeValueAsString(courseDto);

        when(request.getInputStream()).thenReturn(new StringServletInputStream(courseDtoJson));

        courseServlet.doPut(request, response);

        ArgumentCaptor<CourseDto> captor = ArgumentCaptor.forClass(CourseDto.class);

        verify(courseService).updateCourse(captor.capture(), anyInt());

        CourseDto capturedArgument = captor.getValue();

        assertEquals(capturedArgument.getId(), courseDto.getId());
        assertEquals(capturedArgument.getName(), courseDto.getName());

    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"/incorrect", "/1/2"})
    void doPutWithBadParam(String pathInfo) throws IOException, ServletException {

        when(request.getPathInfo()).thenReturn(pathInfo);

        courseServlet.doPut(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);

    }

    @ParameterizedTest
    @ValueSource(strings = {"/1"})
    void doDeleteWithCorrectPath(String pathInfo) throws ServletException, IOException {

        Integer courseId = 1;

        when(request.getPathInfo()).thenReturn(pathInfo);

        courseServlet.doDelete(request, response);

        verify(courseService).deleteCourse(courseId);

    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"/incorrect", "/1/3"})
    void doDeleteWithIncorrectPath(String pathInfo) throws ServletException, IOException {

        when(request.getPathInfo()).thenReturn(pathInfo);

        courseServlet.doDelete(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);

    }
}
