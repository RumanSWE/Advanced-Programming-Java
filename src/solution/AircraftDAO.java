package solution;
import java.io.BufferedReader;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import baseclasses.Aircraft;
import baseclasses.DataLoadingException;
import baseclasses.IAircraftDAO;


/**
 * The AircraftDAO class is responsible for loading aircraft data from CSV files
 * and contains methods to help the system find aircraft when scheduling
 */
public class AircraftDAO implements IAircraftDAO {
	
	//The data structure we'll use to store the aircraft we've loaded
	List<Aircraft> aircraft = new ArrayList<>();
	
	/**
	 * Loads the aircraft data from the specified file, adding them to the currently loaded aircraft
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
     *
	 * Initially, this contains some starter code to help you get started in reading the CSV file...
	 */
	@Override
	public void loadAircraftData(Path p) throws DataLoadingException {	
		try {
			//open the file
			BufferedReader reader = Files.newBufferedReader(p);
			
			//read the file line by line
			String line = "";
			
			try {
			//skip the first line of the file - headers
			reader.readLine();
			}
			catch(IOException ioe) 
			{
				throw new DataLoadingException(ioe);
			}
			
			while( (line = reader.readLine()) != null) {
				//each line has fields separated by commas, split into an array of fields
				String[] fields = line.split(",");
			
			
			
				//put some of the fields into variables: check which fields are where atop the CSV file itself
				
				try {
					
					String tailcode = fields[0];
					String type = fields[2];
					String pos = fields[4];
					String manu = fields[3];
					String model = fields[1];
					int crew = Integer.parseInt(fields[6]);
					int seats = Integer.parseInt(fields[5]);

					//create an Aircraft object, and set (some of) its properties
					Aircraft a = new Aircraft();
				
					a.setModel(model);
					a.setTailCode(tailcode);
					a.setTypeCode(type);
					a.setSeats(seats);
					a.setStartingPosition(pos);
					a.setCabinCrewRequired(crew);
					a.setManufacturer(Aircraft.Manufacturer.valueOf(manu.toUpperCase()));
				
					//add the aircraft to our list
					aircraft.add(a);
				
				}
				catch(Exception e)
				{
					throw new DataLoadingException(e);
				}
				//System.out.println("Aircraft: " + tailcode + " is a " + type + " with " + seats + " seats.");
			}
			
		}
		
		catch (IOException |  NullPointerException ioe) {
			//There was a problem reading the file
			
			throw new DataLoadingException(ioe);
		}

	}
	
	/**
	 * Returns a list of all the loaded Aircraft with at least the specified number of seats
	 * @param seats the number of seats required
	 * @return a List of all the loaded aircraft with at least this many seats
	 */
	@Override
	public List<Aircraft> findAircraftBySeats(int seats) {

		List<Aircraft> aircraftSeats = new ArrayList<>();
		
		
			for (Aircraft a : aircraft)
			{
				if (a.getSeats() >= seats)
				{
					aircraftSeats.add(a);
				}
			}
	
		return aircraftSeats;
	}

	/**
	 * Returns a list of all the loaded Aircraft that start at the specified airport code
	 * @param startingPosition the three letter airport code of the airport at which the desired aircraft start
	 * @return a List of all the loaded aircraft that start at the specified airport
	 */
	@Override
	public List<Aircraft> findAircraftByStartingPosition(String startingPosition) {
		
		List<Aircraft> aircraftPos = new ArrayList<>();
		
		
			for (Aircraft b : aircraft)
			{
				//System.out.println(b.getStartingPosition());
				
				if(b.getStartingPosition().equals(startingPosition))
				{
					aircraftPos.add(b);
				}
			}
		
		return aircraftPos;
	}

	/**
	 * Returns the individual Aircraft with the specified tail code.
	 * @param tailCode the tail code for which to search
	 * @return the aircraft with that tail code, or null if not found
	 */
	@Override
	public Aircraft findAircraftByTailCode(String tailCode) {

		try 
		{
		
			for (Aircraft a : aircraft) 
			{
				//if object tail code is the same as the input it will return the indexed position of the aircraft array list
				if(a.getTailCode().equals(tailCode))
				{
					return aircraft.get(aircraft.indexOf(a));
				}
			}
			
		}
		catch(Exception e)
		{
			System.err.println("Error has occured:"+ e);
		}
		
		return null;
	}

	/**
	 * Returns a List of all the loaded Aircraft with the specified type code
	 * @param typeCode the type code of the aircraft you wish to find
	 * @return a List of all the loaded Aircraft with the specified type code
	 */
	@Override
	public List<Aircraft> findAircraftByType(String typeCode) {
		
		List<Aircraft> aircraftType = new ArrayList<>();
		
		
			for (Aircraft a : aircraft) 
			{
				if(a.getTypeCode().equals(typeCode))
				{
					aircraftType.add(a);
				}

			}
			
		return aircraftType;
	}

	/**
	 * Returns a List of all the currently loaded aircraft
	 * @return a List of all the currently loaded aircraft
	 */
	@Override
	public List<Aircraft> getAllAircraft() {
		
		List<Aircraft> allAir = new ArrayList<>();
		
		allAir.addAll(aircraft);
		
		return allAir;
	}

	/**
	 * Returns the number of aircraft currently loaded 
	 * @return the number of aircraft currently loaded
	 */
	@Override
	public int getNumberOfAircraft() 
	{
		return aircraft.size() ;
	}

	/**
	 * Unloads all of the aircraft currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() 
	{
		aircraft.clear();
	}

}
