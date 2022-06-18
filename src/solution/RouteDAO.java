package solution;
import java.io.IOException;


import java.nio.file.Path;

import java.text.DateFormatSymbols;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.DayOfWeek;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import baseclasses.DataLoadingException;
import baseclasses.IRouteDAO;
import baseclasses.Route;

/**
 * The RouteDAO parses XML files of route information, each route specifying
 * where the airline flies from, to, and on which day of the week
 */
public class RouteDAO implements IRouteDAO {
	
	List<Route> route = new ArrayList<>();

	/**
	 * Loads the route data from the specified file, adding them to the currently loaded routes
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 */
	@Override
	public void loadRouteData(Path p) throws DataLoadingException {
		
		try 
		 { 

			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();			
			Document doc = db.parse(p.toString());
			
			try {      
				
			Element root = doc.getDocumentElement(); 
			NodeList routes = root.getElementsByTagName("Route");
		
			for(int i=0;i<routes.getLength();i++) 
			{
				Route rts = new Route();
				
				
				Node node = routes.item(i);
				
				Element eElement = (Element) node;
				
				if(eElement.getElementsByTagName("*").getLength() == 9)
				{
					String shortDate = eElement.getElementsByTagName("DayOfWeek").item(0).getTextContent();
					String daysList[]  = new DateFormatSymbols().getShortWeekdays(); //gets a array of shortend days
					
					if(Arrays.asList(daysList).contains(shortDate)){//check to ensure it matches
						 rts.setDayOfWeek(shortDate);
					}
					else
					{
						throw new DataLoadingException();
					}

					rts.setFlightNumber(Integer.parseInt(eElement.getElementsByTagName("FlightNumber").item(0).getTextContent()));
					rts.setDepartureTime(LocalTime.parse(eElement.getElementsByTagName("DepartureTime").item(0).getTextContent()));
					rts.setDepartureAirport(eElement.getElementsByTagName("DepartureAirport").item(0).getTextContent());
					rts.setDepartureAirportCode(eElement.getElementsByTagName("DepartureAirportIATACode").item(0).getTextContent());
					rts.setArrivalAirport(eElement.getElementsByTagName("ArrivalAirport").item(0).getTextContent());
					rts.setArrivalAirportCode(eElement.getElementsByTagName("ArrivalAirportIATACode").item(0).getTextContent());
					rts.setArrivalTime(LocalTime.parse(eElement.getElementsByTagName("ArrivalTime").item(0).getTextContent()));
					rts.setDuration(Duration.parse(eElement.getElementsByTagName("Duration").item(0).getTextContent()));
					route.add(rts);
				}
				else
				{
					throw new DataLoadingException();
				}
				
			} 
			
			} 
			catch(Exception e) 
			{throw new DataLoadingException(e);}
		 }
		
	catch (ParserConfigurationException | SAXException | IOException |  NullPointerException e) 
	 {
		System.err.println("Error opening XML file:" + e); 
		throw new DataLoadingException(e);
	 }
}

	/**
	 * Finds all flights that depart on the specified day of the week
	 * @param dayOfWeek A three letter day of the week, e.g. "Tue"
	 * @return A list of all routes that depart on this day
	 */
	@Override
	public List<Route> findRoutesByDayOfWeek(String dayOfWeek) {
		// TODO Auto-generated method stub
		List<Route> dayRoute = new ArrayList<>();
		
		for(Route r : route)
		{
			if(r.getDayOfWeek().equals(dayOfWeek))
			{
				dayRoute.add(r);
			}
		}
		return dayRoute;
	}

	/**
	 * Finds all of the flights that depart from a specific airport on a specific day of the week
	 * @param airportCode the three letter code of the airport to search for, e.g. "MAN"
	 * @param dayOfWeek the three letter day of the week code to searh for, e.g. "Tue"
	 * @return A list of all routes from that airport on that day
	 */
	@Override
	public List<Route> findRoutesByDepartureAirportAndDay(String airportCode, String dayOfWeek) {
		// TODO Auto-generated method stub
		List<Route> newRoute = new ArrayList<>();
		
		for(Route r : route)
		{
			if(r.getDepartureAirportCode().equals(airportCode) && r.getDayOfWeek().equals(dayOfWeek))
			{
					newRoute.add(r);
			}
		}
		return newRoute;
	}

	/**
	 * Finds all of the flights that depart from a specific airport
	 * @param airportCode the three letter code of the airport to search for, e.g. "MAN"
	 * @return A list of all of the routes departing the specified airport
	 */
	@Override
	public List<Route> findRoutesDepartingAirport(String airportCode) {
		// TODO Auto-generated method stub
		List<Route> airRoute = new ArrayList<>();
		
		for(Route r : route)
		{
			if(r.getDepartureAirportCode().equals(airportCode))
			{
				airRoute.add(r);
			}
		}
		return airRoute;
	}

	/**
	 * Finds all of the flights that depart on the specified date
	 * @param date the date to search for
	 * @return A list of all routes that dpeart on this date
	 */
	@Override
	public List<Route> findRoutesbyDate(LocalDate date) {
		// TODO Auto-generated method stub
		List<Route> dateRoute = new ArrayList<>();
		 
		if(date == null) 
		{
			return dateRoute;
		}
		else {
					
			try {
				DayOfWeek day = date.getDayOfWeek();//gets the day from the full localdate input
				String days = day.getDisplayName(TextStyle.SHORT,Locale.getDefault());//converts the day to a short date (Friday -> Fri)
				
				for(Route r : route)
				{
					if(r.getDayOfWeek().equals(days))
					{
						dateRoute.add(r);
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	
			return dateRoute;
		}
	}

	/**
	 * Returns The full list of all currently loaded routes
	 * @return The full list of all currently loaded routes
	 */
	@Override
	public List<Route> getAllRoutes() {
		// TODO Auto-generated method stub
		List<Route> allRoute = new ArrayList<>();
		
		allRoute.addAll(route);
		
		return allRoute;
	}

	/**
	 * Returns The number of routes currently loaded
	 * @return The number of routes currently loaded
	 */
	@Override
	public int getNumberOfRoutes() {
		// TODO Auto-generated method stub
		return route.size();
	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		
		route.clear();

	}

}
