package dao;

import entity.Coordinator;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import util.DBManager;
import util.PropertiesLoader;

import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CoordinatorDaoTest {

    static PropertiesLoader propertiesLoader = new PropertiesLoader("application.properties");

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:10-alpine")
            .withDatabaseName(propertiesLoader.getProperty("database.name"))
            .withUsername(propertiesLoader.getProperty("database.username"))
            .withPassword(propertiesLoader.getProperty("database.password"));

    private DBManager connectionProvider;

    private CoordinatorDao coordinatorDao;

    @BeforeAll
    static void startContainer() {
        postgres.start();

        Flyway flyway = Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .locations("classpath:db/migration")
                .load();

        flyway.migrate();


    }

    @AfterAll
    static void stopContainer() {
        postgres.stop();
    }

    @BeforeEach
    void init() {
        connectionProvider = new DBManager(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        coordinatorDao = new CoordinatorDao(connectionProvider);

        // clean table before each test
        try(Connection connection = connectionProvider.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM coordinator"
        )){
            preparedStatement.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Test
    @Order(1)
    void save() {

        Integer coordId = 1;
        String coordName = "James";

        Coordinator coordinator = Coordinator.builder()
                .id(coordId)
                .name(coordName)
                .build();

        // save coordinator
        try {
            coordinatorDao.save(coordinator);
        }catch(SQLException e){
            fail();
        }

        Coordinator result = new Coordinator();

        // get coordinator from table
        try(Connection connection = connectionProvider.getConnection();
                PreparedStatement getCoordinatorByIdStatement = connection.prepareStatement(
               "SELECT id, cord_name FROM coordinator WHERE id = ?"
            )){

            getCoordinatorByIdStatement.setObject(1, coordinator.getId());
            ResultSet resultSet = getCoordinatorByIdStatement.executeQuery();

            if(resultSet.next()){
                result.setId(resultSet.getObject("id", Integer.class));
                result.setName(resultSet.getString("cord_name"));
            }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }

        assertEquals(coordinator, result);
    }

    @Test
    @Order(2)
    void update() {

        Integer coordId = 1;
        String coordName = "James";

        Coordinator coordinator = Coordinator.builder()
                .id(coordId)
                .name(coordName)
                .build();

        // save coordinator
        try {
            coordinatorDao.save(coordinator);
        }catch(SQLException e) {
            fail();
        }

        Coordinator newCoordinator = Coordinator.builder()
                .id(1)
                .name("Will")
                .build();

        // update coordinator
        try {
            coordinatorDao.update(newCoordinator, coordinator.getId());
        }catch(SQLException e) {
            fail();
        }

        Coordinator result = new Coordinator();

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement getCoordinatorByIdStatement = connection.prepareStatement(
                "SELECT id, cord_name FROM coordinator WHERE id = ?"
        )){

            getCoordinatorByIdStatement.setObject(1, coordinator.getId());
            ResultSet resultSet = getCoordinatorByIdStatement.executeQuery();

            if(resultSet.next()){
                result.setId(resultSet.getObject("id", Integer.class));
                result.setName(resultSet.getString("cord_name"));
            }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }

        assertEquals(newCoordinator.getName(), result.getName());

    }

    @Test
    @Order(3)
    void get() {

        Integer id = 1;
        String name = "John";

        Coordinator coordinator = new Coordinator(id, name, null);

        try {
            coordinatorDao.save(coordinator);
        }catch(SQLException e) {
            fail();
        }

        Optional<Coordinator> result = Optional.empty();
        try {
            result = coordinatorDao.get(coordinator.getId());
        }catch(SQLException e){
            fail();
        }

        if(result.isPresent()){
            assertEquals(result.get().getName(), coordinator.getName());
        }else{
            fail();
        }
    }

    @Test
    @Order(4)
    void getAll() {

        List<Coordinator> coordinators = new ArrayList<>(List.of(
                new Coordinator(1, "James", null),
                new Coordinator(2, "Will", null),
                new Coordinator(3, "Bill", null)
        ));

        for(Coordinator coordinator: coordinators){

            try{
                coordinatorDao.save(coordinator);
            }catch(SQLException e){
                fail();
            }

        }

        List<Coordinator> result = new ArrayList<>();
        try{
            result = coordinatorDao.getAll();
        }catch(SQLException e){
            fail();
        }

        coordinators.sort(Comparator.comparing(Coordinator::getId));
        result.sort(Comparator.comparing(Coordinator::getId));

        assertEquals(coordinators, result);

    }

    @Test
    @Order(5)
    void delete() {

        Integer id = 1;
        String name = "John";

        Coordinator coordinator = new Coordinator(id, name, null);

        // save coordinator
        try {
            coordinatorDao.save(coordinator);
        }catch(SQLException e){
            fail();
        }

        // delete coordinator
        try {
            coordinatorDao.delete(id);
        } catch (SQLException e) {
            fail();
        }

        // check if coordinator was deleted
        Optional<Coordinator> result = Optional.empty();
        try {
            result = coordinatorDao.get(id);
        }catch(SQLException e){
            fail();
        }

        if(result.isPresent()){
            fail();
        }else{
            assertTrue(true);
        }
    }
}