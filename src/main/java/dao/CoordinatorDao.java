package dao;

import entity.Coordinator;
import entity.Student;
import util.DBManager;

import java.sql.*;
import java.util.*;

/**
 * DAO implementation for managing Coordinator entities.
 */
public class CoordinatorDao implements Dao<Coordinator, Integer>{

    private static final String INSERT_COORDINATOR = "INSERT INTO coordinator(cord_name) VALUES (?)";

    private static final String SELECT_ALL_COORDINATORS =
            "SELECT coordinator.id AS id, cord_name, student.id AS studId, sname FROM coordinator " +
            "LEFT JOIN student ON student.coordinator_id = coordinator.id";

    private static final String SELECT_COORDINATOR_BY_ID = SELECT_ALL_COORDINATORS + " WHERE coordinator.id = ?";

    private static final String UPDATE_COORDINATOR_BY_ID = "UPDATE coordinator SET cord_name = ? WHERE id = ?";

    private static final String DELETE_COORDINATOR_BY_ID = "DELETE FROM coordinator WHERE id = ?";

    private final DBManager dbManager;

    public CoordinatorDao(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public CoordinatorDao() {
        this.dbManager = new DBManager();
    }

    /**
     * Saves a new coordinator to the database.
     *
     * @param item the coordinator to save
     */
    @Override
    public void save(Coordinator item) throws SQLException {


        try(Connection connection = this.dbManager.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(INSERT_COORDINATOR, Statement.RETURN_GENERATED_KEYS)){

            statement.setString(1, item.getName());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if(generatedKeys.next()){
                item.setId(generatedKeys.getObject("id", Integer.class));
            }

        }
    }

    /**
     * Updates an existing student in the database.
     *
     * @param item the student to update
     * @param id the ID of the student to update
     */
    @Override
    public void update(Coordinator item, Integer id) throws SQLException {

        try(Connection connection = this.dbManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_COORDINATOR_BY_ID)) {

            preparedStatement.setObject(1, item.getName());
            preparedStatement.setObject(2, id);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Retrieves a coordinator from the database by ID.
     *
     * @param id the ID of the coordinator to retrieve
     * @return an Optional containing the found coordinator, or empty if not found
     */
    @Override
    public Optional<Coordinator> get(Integer id) throws SQLException {

        Coordinator coordinator = null;

        try(Connection connection = this.dbManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_COORDINATOR_BY_ID)){

            preparedStatement.setObject(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                Integer studId = resultSet.getObject("studId", Integer.class);
                if(coordinator != null && studId != null){
                    coordinator.getStudents().add(Student.builder()
                                    .id(studId)
                                    .name(resultSet.getString("sname"))
                                    .build());
                }else{
                    coordinator = buildCoordinator(resultSet);
                }
            }

        }

        return Optional.ofNullable(coordinator);
    }

    /**
     * Retrieves all coordinators from the database.
     *
     * @return a list of all coordinators
     */
    @Override
    public List<Coordinator> getAll() throws SQLException {

        List<Coordinator> coordinators = new ArrayList<>();

        try(Connection connection = this.dbManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL_COORDINATORS)){

            ResultSet resultSet = statement.executeQuery();

            Map<Integer, Coordinator> coordinatorIdToCoordinatorMap = new HashMap<>();

            while(resultSet.next()){

                Integer coordId = resultSet.getObject("id", Integer.class);
                if(coordinatorIdToCoordinatorMap.containsKey(coordId)){

                    Integer studId = resultSet.getObject("studId", Integer.class);
                    if(studId != null){
                        coordinatorIdToCoordinatorMap.get(coordId).getStudents().add(
                                Student.builder()
                                        .id(studId)
                                        .name(resultSet.getString("sname"))
                                        .build());
                    }
                }else{
                    Coordinator newCoordinator = buildCoordinator(resultSet);
                    coordinators.add(newCoordinator);
                    coordinatorIdToCoordinatorMap.put(coordId, newCoordinator);
                }
            }

        }
        return coordinators;
    }

    /**
     * Deletes a coordinator from the database by ID.
     *
     * @param id the ID of the coordinator to delete
     */
    @Override
    public void delete(Integer id) throws SQLException {

        try(Connection connection = this.dbManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_COORDINATOR_BY_ID)){

            preparedStatement.setObject(1, id);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Builds a Coordinator object from a ResultSet.
     *
     * @param resultSet the ResultSet to build the coordinator from
     * @return the built Coordinator object
     * @throws SQLException if an SQL error occurs
     */
    private static Coordinator buildCoordinator(ResultSet resultSet) throws SQLException {

        Integer studId = resultSet.getObject("studId", Integer.class);
        Student student = null;
        if(studId != null){
            student = Student.builder()
                    .id(studId)
                    .name(resultSet.getString("sname"))
                    .build();
        }

        return Coordinator.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("cord_name"))
                .students(student != null ? new ArrayList<>(Collections.singletonList(student)) : null)
                .build();
    }

}
