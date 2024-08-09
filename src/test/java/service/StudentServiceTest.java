package service;

import dao.StudentDao;
import dto.course.CourseDto;
import dto.student.CreateStudentDto;
import dto.student.ReadStudentDto;
import entity.Course;
import entity.Student;
import mapper.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StudentServiceTest {

    @Mock
    private StudentDao studentDao;

    @Mock
    private Mapper<CreateStudentDto, Student> createStudentDtoToStudentMapper;

    @Mock
    private Mapper<Student, ReadStudentDto> studentToReadStudentDtoMapper;

    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveStudent() throws SQLException {

        CreateStudentDto dto = CreateStudentDto.builder()
                .id(1)
                .name("Ivan")
                .build();

        Student student = Student.builder()
                .id(1)
                .name("Ivan")
                .build();

        when(createStudentDtoToStudentMapper.map(dto)).thenReturn(student);

        studentService.saveStudent(dto);

        verify(studentDao).save(student);
    }

    @Test
    public void testGetAllStudents() throws SQLException {

        Student student = Student.builder()
                .id(1)
                .name("James")
                .build();

        ReadStudentDto readStudentDto = ReadStudentDto.builder()
                .id(1)
                .name("James")
                .build();

        List<Student> students = Arrays.asList(student);
        List<ReadStudentDto> readStudentDtos = Arrays.asList(readStudentDto);

        when(studentDao.getAll()).thenReturn(students);
        when(studentToReadStudentDtoMapper.map(student)).thenReturn(readStudentDto);

        List<ReadStudentDto> result = studentService.getAllStudents();

        assertEquals(readStudentDtos, result);
    }

    @Test
    public void testGetStudentById() throws SQLException {
        Integer id = 1;
        Student student = Student.builder()
                .id(1)
                .name("James")
                .build();

        ReadStudentDto readStudentDto = ReadStudentDto.builder()
                .id(1)
                .name("James")
                .build();

        when(studentDao.get(id)).thenReturn(Optional.of(student));
        when(studentToReadStudentDtoMapper.map(student)).thenReturn(readStudentDto);

        Optional<ReadStudentDto> result = studentService.getStudentById(id);

        assertTrue(result.isPresent());
        assertEquals(readStudentDto, result.get());
    }

    @Test
    public void testUpdateStudent() throws SQLException {

        List<CourseDto> courses = new ArrayList<>(List.of(
                new CourseDto(1, "math"),
                new CourseDto(2, "eng"),
                new CourseDto(3, "rus")
        ));

        CreateStudentDto dto = CreateStudentDto.builder()
                .id(1)
                .name("James")
                .coordinatorId(null)
                .courses(courses)
                .build();

        Student student1 = Student.builder()
                .id(1)
                .name("James")
                .coordinator(null)
                .courses(courses.stream()
                        .map(
                        courseDto -> Course.builder()
                                .id(courseDto.getId())
                                .name(courseDto.getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        Student student2 = Student.builder()
                .id(1)
                .name("James")
                .coordinator(null)
                .courses(new ArrayList<>())
                .build();

        Integer id = 1;

        when(createStudentDtoToStudentMapper.map(dto)).thenReturn(student1);
        when(studentDao.get(id)).thenReturn(Optional.of(student2));

        studentService.updateStudent(dto, id);

        verify(studentDao).update(student1, id);
    }

    @Test
    public void testDeleteStudent() throws SQLException {
        Integer id = 1;

        studentService.deleteStudent(id);

        verify(studentDao).delete(id);
    }
}