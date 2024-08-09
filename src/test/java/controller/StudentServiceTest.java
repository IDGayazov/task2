package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.student.CreateStudentDto;
import dto.student.ReadStudentDto;
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
import service.StudentService;
import util.StringServletInputStream;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StudentServletTest {

    @Mock
    private StudentService studentService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private StudentServlet studentServlet;

    private StringWriter responseWriter;

    @BeforeEach
    public void init() throws IOException {
        MockitoAnnotations.openMocks(this);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getContentType()).thenReturn("application/json; charset=UTF-8");
        studentServlet.objectMapper = new ObjectMapper();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"/"})
    void doGetAllCoordinators(String pathInfo) throws ServletException, IOException {
        List<ReadStudentDto> students = Arrays.asList(
                new ReadStudentDto(1, "John Doe", null, null),
                new ReadStudentDto(2, "Jane Smith", null, null)
        );

        when(request.getPathInfo()).thenReturn(pathInfo);
        when(studentService.getAllStudents()).thenReturn(students);

        studentServlet.doGet(request, response);

        String expectedJson = studentServlet.objectMapper.writeValueAsString(students);
        assertEquals(expectedJson, responseWriter.toString().trim());
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    void doGetCoordinatorById() throws ServletException, IOException {

        ReadStudentDto student = new ReadStudentDto(1, "John Doe", null, null);

        Integer studentId = 1;

        when(request.getPathInfo()).thenReturn("/" + studentId);
        when(studentService.getStudentById(studentId)).thenReturn(Optional.of(student));

        studentServlet.doGet(request, response);

        String expectedJson = studentServlet.objectMapper.writeValueAsString(student);
        assertEquals(expectedJson, responseWriter.toString().trim());
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @ParameterizedTest
    @ValueSource(strings = {"/12345abc", "/incorrect", "/1/2"})
    void doGetCoordinatorByIdWithBadParam(String pathInfo) throws ServletException, IOException {

        ReadStudentDto studentDto = new ReadStudentDto(1, "John Doe", null, null);

        when(request.getPathInfo()).thenReturn(pathInfo);

        studentServlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"/"})
    void doPostWithCorrectParam(String pathInfo) throws IOException, ServletException {

        CreateStudentDto createStudentDto = new CreateStudentDto(1, "John Doe", null, null);

        String studentDtoJson = studentServlet.objectMapper
                .writeValueAsString(createStudentDto);

        when(request.getPathInfo()).thenReturn(pathInfo);

        when(request.getInputStream()).thenReturn(new StringServletInputStream(studentDtoJson));

        studentServlet.doPost(request, response);

        ArgumentCaptor<CreateStudentDto> captor = ArgumentCaptor.forClass(CreateStudentDto.class);

        verify(studentService).saveStudent(captor.capture());

        CreateStudentDto capturedArgument = captor.getValue();

        assertEquals(capturedArgument.getId(), createStudentDto.getId());
        assertEquals(capturedArgument.getName(), createStudentDto.getName());

    }

    @Test
    void doPostWithBadParam() throws IOException, ServletException {

        String incorrectPathInfo = "incorrectPathInfo";

        when(request.getPathInfo()).thenReturn(incorrectPathInfo);

        studentServlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);

    }

    @ParameterizedTest
    @ValueSource(strings = {"/1"})
    void doPutWithCorrectParam(String pathInfo) throws IOException, ServletException {

        Integer studentId = 1;
        CreateStudentDto coordinatorDto = new CreateStudentDto(1, "John Doe", null, null);

        when(request.getPathInfo()).thenReturn(pathInfo);

        String studentDtoJson = studentServlet.objectMapper
                .writeValueAsString(coordinatorDto);

        when(request.getInputStream()).thenReturn(new StringServletInputStream(studentDtoJson));

        studentServlet.doPut(request, response);

        ArgumentCaptor<CreateStudentDto> captor = ArgumentCaptor.forClass(CreateStudentDto.class);

        verify(studentService).updateStudent(captor.capture(), anyInt());

        CreateStudentDto capturedArgument = captor.getValue();

        assertEquals(capturedArgument.getId(), coordinatorDto.getId());
        assertEquals(capturedArgument.getName(), coordinatorDto.getName());

    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"/incorrect", "/1/2"})
    void doPutWithBadParam(String pathInfo) throws IOException, ServletException {

        when(request.getPathInfo()).thenReturn(pathInfo);

        studentServlet.doPut(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);

    }

    @ParameterizedTest
    @ValueSource(strings = {"/1"})
    void doDeleteWithCorrectPath(String pathInfo) throws ServletException, IOException {

        Integer studentId = 1;

        when(request.getPathInfo()).thenReturn(pathInfo);

        studentServlet.doDelete(request, response);

        verify(studentService).deleteStudent(studentId);

    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"/incorrect", "/1/3"})
    void doDeleteWithIncorrectPath(String pathInfo) throws ServletException, IOException {

        when(request.getPathInfo()).thenReturn(pathInfo);

        studentServlet.doDelete(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);

    }
}
