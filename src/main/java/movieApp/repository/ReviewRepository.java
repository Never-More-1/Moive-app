package movieApp.repository;

import movieApp.model.Review;
import movieApp.model.dto.reviewDto.ReviewCreateDto;
import movieApp.model.dto.reviewDto.ReviewUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ReviewRepository {
    private static final String INSERT_REVIEW = "INSERT INTO review(user_id, film_id, review_text, rating, created_at) VALUES (?,?,?,?,?)";
    private static final String SELECT_ALL_REVIEWS = "SELECT * FROM review";
    private static final String GET_REVIEW_BY_ID = "SELECT * FROM review WHERE id = ?";
    private static final String GET_REVIEW_BY_USER_AND_FILM = "SELECT * FROM review WHERE user_id = ? AND film_id = ?";
    private static final String GET_REVIEWS_BY_USER_ID = "SELECT * FROM review WHERE user_id = ?";
    private static final String GET_REVIEWS_BY_FILM_ID = "SELECT * FROM review WHERE film_id = ?";
    private static final String REMOVE_REVIEW_BY_ID = "DELETE FROM review WHERE id = ?";
    private static final String REMOVE_REVIEW_BY_USER_AND_FILM = "DELETE FROM review WHERE user_id = ? AND film_id = ?";
    private static final String UPDATE_REVIEW_BY_ID = "UPDATE review SET review_text = ?, rating = ?, created_at = ? WHERE id = ?";
    private static final String UPDATE_REVIEW_BY_USER_AND_FILM = "UPDATE review SET review_text = ?, rating = ?, created_at = ? WHERE user_id = ? AND film_id = ?";
    private static final String CHECK_REVIEW_EXISTS = "SELECT COUNT(*) FROM review WHERE user_id = ? AND film_id = ?";
    private static final String GET_AVERAGE_RATING = "SELECT AVG(rating) FROM review WHERE film_id = ?";
    private static final String GET_USER_REVIEW_COUNT = "SELECT COUNT(*) FROM review WHERE user_id = ?";
    private static final String GET_FILM_REVIEW_COUNT = "SELECT COUNT(*) FROM review WHERE film_id = ?";

    private Connection connection;
    private final int ONE_LINE_FROM_DB = 1;

    @Autowired
    public ReviewRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Review> getAllReviews() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_REVIEWS);
            ResultSet resultSet = preparedStatement.executeQuery();
            return parseResultSetToReviewList(resultSet);
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean addReview(ReviewCreateDto review) throws SQLException {
        try {
            if (checkReviewExists(review.getUserId(), review.getFilmId())) {
                return false;
            }

            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_REVIEW);
            preparedStatement.setInt(1, review.getUserId());
            preparedStatement.setInt(2, review.getFilmId());
            preparedStatement.setString(3, review.getReviewText());
            preparedStatement.setInt(4, review.getRating());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            return preparedStatement.executeUpdate() == ONE_LINE_FROM_DB;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public Optional<Review> getReviewById(int id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_REVIEW_BY_ID);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return parseResultSetToReview(resultSet);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Review> getReviewByUserAndFilm(int userId, int filmId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_REVIEW_BY_USER_AND_FILM);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, filmId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return parseResultSetToReview(resultSet);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    public List<Review> getReviewsByUserId(int userId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_REVIEWS_BY_USER_ID);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return parseResultSetToReviewList(resultSet);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<Review> getReviewsByFilmId(int filmId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_REVIEWS_BY_FILM_ID);
            preparedStatement.setInt(1, filmId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return parseResultSetToReviewList(resultSet);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return new ArrayList<>();
    }

    public boolean updateReviewById(ReviewUpdateDto reviewUpdate, int id) throws SQLException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_REVIEW_BY_ID);
            preparedStatement.setString(1, reviewUpdate.getReviewText());
            preparedStatement.setInt(2, reviewUpdate.getRating());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setInt(4, id);
            return preparedStatement.executeUpdate() == ONE_LINE_FROM_DB;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public boolean updateReviewByUserAndFilm(ReviewUpdateDto reviewUpdate, int userId, int filmId) throws SQLException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_REVIEW_BY_USER_AND_FILM);
            preparedStatement.setString(1, reviewUpdate.getReviewText());
            preparedStatement.setInt(2, reviewUpdate.getRating());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setInt(4, userId);
            preparedStatement.setInt(5, filmId);
            return preparedStatement.executeUpdate() == ONE_LINE_FROM_DB;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public boolean removeReviewById(int id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_REVIEW_BY_ID);
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() == ONE_LINE_FROM_DB;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean removeReviewByUserAndFilm(int userId, int filmId) throws SQLException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_REVIEW_BY_USER_AND_FILM);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, filmId);
            return preparedStatement.executeUpdate() == ONE_LINE_FROM_DB;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public boolean checkReviewExists(int userId, int filmId) throws SQLException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(CHECK_REVIEW_EXISTS);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, filmId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        return false;
    }

    public Double getAverageRatingByFilmId(int filmId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_AVERAGE_RATING);
            preparedStatement.setInt(1, filmId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0.0;
    }

    public int getUserReviewCount(int userId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_REVIEW_COUNT);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public int getFilmReviewCount(int filmId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_FILM_REVIEW_COUNT);
            preparedStatement.setInt(1, filmId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    private List<Review> parseResultSetToReviewList(ResultSet resultSet) throws SQLException {
        List<Review> reviewList = new ArrayList<>();
        while (resultSet.next()) {
            reviewList.add(fillReview(resultSet));
        }
        return reviewList;
    }

    private Optional<Review> parseResultSetToReview(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return Optional.of(fillReview(resultSet));
        }
        return Optional.empty();
    }

    private Review fillReview(ResultSet resultSet) throws SQLException {
        Review review = new Review();
        review.setId(resultSet.getInt("id"));
        review.setUserId(resultSet.getInt("user_id"));
        review.setFilmId(resultSet.getInt("film_id"));
        review.setReviewText(resultSet.getString("review_text"));
        review.setRating(resultSet.getInt("rating"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            review.setCreatedAt(createdAt.toLocalDateTime());
        }
        return review;
    }
}
