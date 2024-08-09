package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.coordinator.CoordinatorDto;
import dto.coordinator.CreateCoordinatorDto;
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
import service.CoordinatorService;
import util.StringServletInputStream;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CoordinatorServletTest {

    @Mock
    private CoordinatorService coordinatorService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CoordinatorServlet coordinatorServlet;

    private StringWriter responseWriter;

    @BeforeEach
    public void init() throws IOException {
        MockitoAnnotations.openMocks(this);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getContentType()).thenReturn("application/json; charset=UTF-8");
        coordinatorServlet.objectMapper = new ObjectMapper();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"/"})
    void doGetAllCoordinators(String pathInfo) throws ServletException, IOException {
        List<CoordinatorDto> coordinators = Arrays.asList(
                new CoordinatorDto(1, "John Doe", null),
                new CoordinatorDto(2, "Jane Smith", null)
        );

        when(request.getPathInfo()).thenReturn(pathInfo);
        when(coordinatorService.getAllCoordinators()).thenReturn(coordinators);

        coordinatorServlet.doGet(request, response);

        String expectedJson = coordinatorServlet.objectMapper.writeValueAsString(coordinators);
        assertEquals(expectedJson, responseWriter.toString().trim());
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    void doGetCoordinatorById() throws ServletException, IOException {

        CoordinatorDto coordinatorDto = new CoordinatorDto(1, "John Doe", null);

        Integer coordinatorId = 1;

        when(request.getPathInfo()).thenReturn("/" + coordinatorId);
        when(coordinatorService.getCoordinatorById(coordinatorId)).thenReturn(Optional.of(coordinatorDto));

        coordinatorServlet.doGet(request, response);

        String expectedJson = coordinatorServlet.objectMapper.writeValueAsString(coordinatorDto);
        assertEquals(expectedJson, responseWriter.toString().trim());
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @ParameterizedTest
    @ValueSource(strings = {"/12345abc", "/incorrect", "/1/2"})
    void doGetCoordinatorByIdWithBadParam(String pathInfo) throws ServletException, IOException {

        CoordinatorDto coordinatorDto = new CoordinatorDto(1, "John Doe", null);

        when(request.getPathInfo()).thenReturn(pathInfo);

        coordinatorServlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"/"})
    void doPostWithCorrectParam(String pathInfo) throws IOException, ServletException {

        CreateCoordinatorDto coordinatorDto = new CreateCoordinatorDto(1, "John Doe");

        String coordinatorDtoJson = coordinatorServlet.objectMapper
                .writeValueAsString(coordinatorDto);

        when(request.getPathInfo()).thenReturn(pathInfo);

        when(request.getInputStream()).thenReturn(new StringServletInputStream(coordinatorDtoJson));

        coordinatorServlet.doPost(request, response);

        ArgumentCaptor<CreateCoordinatorDto> captor = ArgumentCaptor.forClass(CreateCoordinatorDto.class);

        verify(coordinatorService).saveCoordinator(captor.capture());

        CreateCoordinatorDto capturedArgument = captor.getValue();

        assertEquals(capturedArgument.getId(), coordinatorDto.getId());
        assertEquals(capturedArgument.getName(), coordinatorDto.getName());

    }

    @Test
    void doPostWithBadParam() throws IOException, ServletException {

        String incorrectPathInfo = "incorrectPathInfo";

        when(request.getPathInfo()).thenReturn(incorrectPathInfo);

        coordinatorServlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);

    }

    @ParameterizedTest
    @ValueSource(strings = {"/1"})
    void doPutWithCorrectParam(String pathInfo) throws IOException, ServletException {

        Integer coordinatorId = 1;
        CreateCoordinatorDto coordinatorDto = new CreateCoordinatorDto(1, "John Doe");

        when(request.getPathInfo()).thenReturn(pathInfo);

        String coordinatorDtoJson = coordinatorServlet.objectMapper
                .writeValueAsString(coordinatorDto);

        when(request.getInputStream()).thenReturn(new StringServletInputStream(coordinatorDtoJson));

        coordinatorServlet.doPut(request, response);

        ArgumentCaptor<CreateCoordinatorDto> captor = ArgumentCaptor.forClass(CreateCoordinatorDto.class);

        verify(coordinatorService).updateCoordinator(captor.capture(), anyInt());

        CreateCoordinatorDto capturedArgument = captor.getValue();

        assertEquals(capturedArgument.getId(), coordinatorDto.getId());
        assertEquals(capturedArgument.getName(), coordinatorDto.getName());

    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"/incorrect", "/1/2"})
    void doPutWithBadParam(String pathInfo) throws IOException, ServletException {

        when(request.getPathInfo()).thenReturn(pathInfo);

        coordinatorServlet.doPut(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);

    }

    @ParameterizedTest
    @ValueSource(strings = {"/1"})
    void doDeleteWithCorrectPath(String pathInfo) throws ServletException, IOException {

        Integer coordinatorId = 1;

        when(request.getPathInfo()).thenReturn(pathInfo);

        coordinatorServlet.doDelete(request, response);

        verify(coordinatorService).deleteCoordinator(coordinatorId);

    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"/incorrect", "/1/3"})
    void doDeleteWithIncorrectPath(String pathInfo) throws ServletException, IOException {

        when(request.getPathInfo()).thenReturn(pathInfo);

        coordinatorServlet.doDelete(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);

    }
}