package movieApp.repository;

import org.springframework.beans.factory.annotation.Autowired;
import movieApp.model.User;
import org.springframework.stereotype.Repository;
import movieApp.model.dto.UserCreateDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private static final String INSERT_USER = "INSERT INTO movie_user(username, age, role, email, created) VALUES (?,?,?,?,?)";
    //    private static final String SELECT_ALL_USERS = "SELECT * FROM movie_user";
    private static final String GET_USER_BY_ID = "SELECT * FROM movie_user WHERE id = ?";
    private static final String GET_USER_BY_USERNAME = "SELECT * FROM movie_user WHERE name = ?";
    private static final String REMOVE_USER_BY_ID = "DELETE FROM movie_user WHERE id = ?";
    private static final String UPDATE_USER_BY_ID = "UPDATE FROM movie_user WHERE id = ?";

    private Connection connection;
    private final int ONE_LINE_FROM_DB = 1;

    @Autowired
    public UserRepository(Connection connection) {
        this.connection = connection;
    }

//    public List<User> getAllUsers() {
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            List<User> movie_user = parseResultSetToUserList(resultSet);
//            return movie_user;
//        } catch (SQLException e) {
//            System.out.println("SQL Error: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return new ArrayList<>();
//    }
//
    public boolean addUser(UserCreateDto user) throws SQLException{
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setInt(2, user.getAge());
            preparedStatement.setString(3, user.getRole());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(java.time.LocalDateTime.now()));
            preparedStatement.setString(5, user.getEmail());
            return preparedStatement.executeUpdate() == ONE_LINE_FROM_DB;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

//    public List<User> getAllUsers() {
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            List<User> movie_user = parseResultSetToUserList(resultSet);
//            return movie_user;
//        } catch (SQLException e) {
//            System.out.println("SQL Error: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return new ArrayList<>();
//    }

    public boolean updateUser(UserCreateDto user) throws SQLException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_BY_ID);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setInt(2, user.getAge());
            preparedStatement.setString(3, user.getRole());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(java.time.LocalDateTime.now()));
            preparedStatement.setString(5, user.getEmail());
            return preparedStatement.executeUpdate() == ONE_LINE_FROM_DB;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public Optional<User> getUserById(int id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_ID);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return parseResultSetToUser(resultSet);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<User> getUserByUsername(String username) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_USERNAME);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            return parseResultSetToUser(resultSet);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    public List<User> parseResultSetToUserList(ResultSet resultSet) throws SQLException {
        List<User> userList = new ArrayList<>();
        while (resultSet.next()) {
            userList.add(fillUser(resultSet));
        }
        return userList;
    }

    public Optional<User> parseResultSetToUser(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return Optional.of(fillUser(resultSet));
        }
        return Optional.empty();
    }

    public User fillUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setAge(resultSet.getInt("age"));
        user.setRole(resultSet.getString("role"));
        user.setEmail(resultSet.getString("email"));
        user.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
        return user;
    }

    public boolean removeUserById(int id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_USER_BY_ID);
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() == ONE_LINE_FROM_DB;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
