package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDao {

	public List<Movie> listMovies() {
		List<Movie> listOfMovies = new ArrayList<>();
		
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
	        try (Statement statement = connection.createStatement()) {
	            try (ResultSet results = statement.executeQuery("SELECT * FROM movie m JOIN genre g ON m.genre_id = g.idgenre")) {
	                while (results.next()) {
	                	Movie movie = new Movie();
			                    movie.setId(results.getInt("idmovie"));
			                    movie.setTitle(results.getString("title"));
			                    movie.setReleaseDate(results.getDate("release_date").toLocalDate());
			                    movie.setGenre(new Genre(results.getInt("genre_id"), null)); // Assuming Genre has a constructor with id and name
			                    movie.setDuration(results.getInt("duration"));
			                    movie.setDirector(results.getString("director"));
			                    movie.setSummary(results.getString("summary"));
	                    listOfMovies.add(movie);
	                    
	                }
	                return listOfMovies;
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	    
	}

	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> listOfMovies = new ArrayList<>();
		
		try(Connection connection = DataSourceFactory.getDataSource().getConnection()){	        
			String sqlQuery = "SELECT * FROM movie m JOIN genre g ON m.genre_id = g.idgenre WHERE g.name = ?";
			
			try(PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
				statement.setString(1, genreName);
				
				try(ResultSet results = statement.executeQuery()) {
					
					while(results.next()) {
						Genre genre = new Genre(results.getInt("idgenre"), results.getString("name"));
						Movie movie = new Movie(
								results.getInt("idmovie"), 
								results.getString("title"),
								results.getDate("release_date").toLocalDate(),
								genre,
								results.getInt("duration"),
								results.getString("director"),
								results.getString("summary"));
						listOfMovies.add(movie);
					}
					return listOfMovies;
				}
			}
		}catch(SQLException e) {
		        e.printStackTrace();
		        return null;
		}
			
	}
		        
        
	

	public Movie addMovie(Movie movie) {
		try(Connection connection = DataSourceFactory.getDataSource().getConnection();) {
			String sqlQuery = "INSERT INTO movie (title, release_date, genre_id, duration, director, summary) VALUES (?, ?, ?, ?, ?, ?)";
			try(PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, movie.getTitle());	
				statement.setString(1, movie.getTitle());
	            statement.setDate(2, java.sql.Date.valueOf(movie.getReleaseDate()));
	            statement.setInt(3, movie.getGenre().getId());
	            statement.setInt(4, movie.getDuration());
	            statement.setString(5, movie.getDirector());
	            statement.setString(6, movie.getSummary());
	            statement.executeUpdate();
	            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
	            	if (generatedKeys.next()) {
	            		movie.setId(generatedKeys.getInt(1));
	            	}
	            	return movie;
	            }
			}
		} catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
		
}