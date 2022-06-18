package solution;

import java.nio.file.Path;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;


import baseclasses.DataLoadingException;
import baseclasses.IPassengerNumbersDAO;

/**
 * The PassengerNumbersDAO is responsible for loading an SQLite database
 * containing forecasts of passenger numbers for flights on dates
 */
public class PassengerNumbersDAO implements IPassengerNumbersDAO {
	
	
	
	 HashMap<String, Integer> passenHash = new HashMap<String, Integer>();

	/**
	 * Loads the passenger numbers data from the specified SQLite database into a cache for future calls to getPassengerNumbersFor()
	 * Multiple calls to this method are additive, but flight numbers/dates previously cached will be overwritten
	 * The cache can be reset by calling reset() 
	 * @param p The path of the SQLite database to load data from
	 * @throws DataLoadingException If there is a problem loading from the database
	 */
	@Override
	public void loadPassengerNumbersData(Path p) throws DataLoadingException {
		// TODO Auto-generated method stub
		Connection c = null;
	
		try {
			c = DriverManager.getConnection("jdbc:sqlite:"+p);
		}
		catch (Exception e) {
			throw new DataLoadingException(e);
		}
		
		try {
				Statement s = c.createStatement();
				ResultSet rs = s.executeQuery("SELECT * FROM PassengerNumbers");
		  
				
				while(rs.next()) {
	
					
					LocalDate dates = LocalDate.parse(rs.getString("Date"));			
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					String keys = dates.format(formatter).toString()+rs.getString("FlightNumber");	
				
					passenHash.put(keys, rs.getInt("LoadEstimate"));
					 
								}
		} catch (SQLException e) {
			throw new DataLoadingException(e);
		}
	} 
	/**
	 * Returns the number of passenger number entries in the cache
	 * @return the number of passenger number entries in the cache
	 */
	@Override
	public int getNumberOfEntries() {
		// TODO Auto-generated method stub
		return passenHash.size();
	}

	/**
	 * Returns the predicted number of passengers for a given flight on a given date, or -1 if no data available
	 * @param flightNumber The flight number of the flight to check for
	 * @param date the date of the flight to check for
	 * @return the predicted number of passengers, or -1 if no data available
	 */
	@Override
	public int getPassengerNumbersFor(int flightNumber, LocalDate date) {
		

//		for(String h : passenHash.keySet())
//		{
//			if(h == date.toString()+Integer.toString(flightNumber))
//			{
//				return passenHash.get(h);
//			}
//		}
		
		if (passenHash.get(date.toString()+flightNumber) != null)
			return passenHash.get(date.toString()+flightNumber);
		
		return -1;
	}

	/**
	 * Removes all data from the DAO, ready to start again if needed
	 */
	@Override
	public void reset() {
	
		passenHash.clear();
	}

}
