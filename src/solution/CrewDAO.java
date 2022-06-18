package solution;
import java.io.BufferedReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import baseclasses.CabinCrew;
import baseclasses.Crew;
import baseclasses.DataLoadingException;
import baseclasses.ICrewDAO;
import baseclasses.Pilot;

/**
 * The CrewDAO is responsible for loading data from JSON-based crew files 
 * It contains various methods to help the scheduler find the right pilots and cabin crew
 */
public class CrewDAO implements ICrewDAO {
	
	List<CabinCrew> cabinCrew = new ArrayList<>();
	List<Pilot> pilot = new ArrayList<>();
	List<Crew> crew = new ArrayList<>();
	
	/**
	 * Loads the crew data from the specified file, adding them to the currently loaded crew
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 */
	@Override
	public void loadCrewData(Path p) throws DataLoadingException {
		// TODO Auto-generated method stub
		try {
	
			BufferedReader reader = Files.newBufferedReader(p);
			String json = "";
			String line = "";
			
			while((line = reader.readLine())!= null)
			{
				json = json + line;
			
			}
			
			try {
				JSONObject root = new JSONObject(json);
	
				JSONArray pltArr = root.getJSONArray("pilots");//array of JSON pilots
				JSONArray cbnArr = root.getJSONArray("cabincrew");//array of JSON cabinCrew
			
				//loops through array and gets each object and then places them into the array
				for (int i=0;i < cbnArr.length();i++)
				{
					
					CabinCrew cbn = new CabinCrew();
					JSONObject cbnObj = cbnArr.getJSONObject(i);
					
					JSONArray cbnTypeArr = cbnObj.getJSONArray("type_ratings");
					
					
					for(int y = 0;y< cbnTypeArr.length();y++)
					{
						cbn.setQualifiedFor(cbnTypeArr.get(y).toString());
					}
					
					cbn.setForename(cbnObj.getString("forename"));
					cbn.setHomeBase(cbnObj.getString("home_airport"));
					cbn.setSurname(cbnObj.getString("surname"));
					
					cabinCrew.add(cbn);
				}
				
				
				
				for(int i=0;i<pltArr.length();i++)
				{
					Pilot plt = new Pilot();//import the pilot class for setters
		
					JSONObject pltObj = pltArr.getJSONObject(i);//assign object to the JSONArray
					
					JSONArray pltTypeArr = pltObj.getJSONArray("type_ratings");
					
					
					for(int y = 0;y< pltTypeArr.length();y++)
					{
						plt.setQualifiedFor(pltTypeArr.get(y).toString());
					}
					plt.setForename(pltObj.getString("forename"));//get each object and palce into the setters
					plt.setHomeBase(pltObj.getString("home_airport"));
					plt.setRank(Pilot.Rank.valueOf(pltObj.getString("rank").toUpperCase()));
					plt.setSurname(pltObj.getString("surname"));
					
					pilot.add(plt);//once all have been set push into array then loop to next one
					
				}
					
			}
		
			catch(Exception e)
			{
				throw new DataLoadingException(e);
			}
			
		} catch (IOException | JSONException|  NullPointerException e) {
			// TODO Auto-generated catch block
			System.out.println("Problem Parsing JSON file: ");
			throw new DataLoadingException(e);
			
		}
	}
	
	/**
	 * Returns a list of all the cabin crew based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at the airport with the specified airport code
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBase(String airportCode) {
		
		List<CabinCrew> cabinHome = new ArrayList<>();
		
		for(CabinCrew c : cabinCrew)
		{
			if(c.getHomeBase().equals(airportCode))
			{
				cabinHome.add(c);
			}
		}
		return cabinHome;
	}
	

	/**
	 * Returns a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find cabin crew for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		
		List<CabinCrew> newCabin = new ArrayList<>();
		
		for(CabinCrew c : cabinCrew)
		{
			if(c.getHomeBase().equals(airportCode))
			{
				for(int i=0; i < c.getTypeRatings().size();i++)//if there are multiple types of planes
				{
					if(c.getTypeRatings().get(i).equals(typeCode))//checks against input
					{
						newCabin.add(c);//adds to list
					}
				}	
			}
		}
		return newCabin;
	}

	/**
	 * Returns a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find cabin crew for
	 * @return a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 */
	@Override
	public List<CabinCrew> findCabinCrewByTypeRating(String typeCode) {
		
		// TODO Auto-generated method stub
		List<CabinCrew> cbnCode = new ArrayList<>();
		
		for(CabinCrew c : cabinCrew)
		{
			for(int i=0; i < c.getTypeRatings().size();i++)//if there are multiple types of planes
			{
				if(c.getTypeRatings().get(i).equals(typeCode))//checks against input
				{
					cbnCode.add(c);//adds to list
				}
			}
		}
		return cbnCode;
	}
	

	/**
	 * Returns a list of all the pilots based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at the airport with the specified airport code
	 */
	@Override
	public List<Pilot> findPilotsByHomeBase(String airportCode) {
		// TODO Auto-generated method stub
		List<Pilot> pilotHome = new ArrayList<>();
		
		for(Pilot p : pilot)
		{
			if(p.getHomeBase().equals(airportCode))
			{
				pilotHome.add(p);
			}
		}
		return pilotHome;
	}

	/**
	 * Returns a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find pilots for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 */
	@Override
	public List<Pilot> findPilotsByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		
		List<Pilot> newPilot = new ArrayList<>();
		
		for(Pilot p : pilot)
		{
			if(p.getHomeBase().equals(airportCode))
			{
				for(int i=0; i < p.getTypeRatings().size();i++)//if there are multiple types of planes
				{
					if(p.getTypeRatings().get(i).equals(typeCode))//checks against input
					{
						newPilot.add(p);//adds to list
					}
				}	
			}
		}
		return newPilot;
	}

	/**
	 * Returns a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find pilots for
	 * @return a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 */
	@Override
	public List<Pilot> findPilotsByTypeRating(String typeCode) {

		List<Pilot> pilotRating = new ArrayList<>();
		
		for(Pilot p : pilot)
		{
			
			for(int i=0; i < p.getTypeRatings().size();i++)//if there are multiple types of planes
			{
				
				if(p.getTypeRatings().get(i).equals(typeCode))//checks against input
				{
					pilotRating.add(p);//adds to list
				}
			}
		}
		return pilotRating;//retuns empty or list of pilots
	}

	/**
	 * Returns a list of all the cabin crew currently loaded
	 * @return a list of all the cabin crew currently loaded
	 */
	@Override
	public List<CabinCrew> getAllCabinCrew() {
		// TODO Auto-generated method stub
		List<CabinCrew> nullCabin = new ArrayList<>();//empty list of pilots if non are there
		
		return (cabinCrew != null ? cabinCrew : nullCabin );
	}

	/**
	 * Returns a list of all the crew, regardless of type
	 * @return a list of all the crew, regardless of type
	 */
	@Override
	public List<Crew> getAllCrew() {
		
		List<Crew> crews = new ArrayList<>();
		
		
		crews.addAll(pilot);
		crews.addAll(cabinCrew);

		return crews;
	}

	/**
	 * Returns a list of all the pilots currently loaded
	 * @return a list of all the pilots currently loaded
	 */
	@Override
	public List<Pilot> getAllPilots() {
		
		List<Pilot> allPilot = new ArrayList<>();//empty list of pilots if non are there
		
		allPilot.addAll(pilot);
		
		return allPilot;
	}

	@Override
	public int getNumberOfCabinCrew() {
		// TODO Auto-generated method stub
		return cabinCrew.size();
	}

	/**
	 * Returns the number of pilots currently loaded
	 * @return the number of pilots currently loaded
	 */
	@Override
	public int getNumberOfPilots() {
		// TODO Auto-generated method stub
		return pilot.size();
	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		pilot.clear();
		cabinCrew.clear();

		
	}

}
