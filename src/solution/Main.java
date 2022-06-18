package solution;

import java.nio.file.Paths;

import java.time.LocalDate;

import baseclasses.DataLoadingException;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;
import baseclasses.IScheduler;

/**
 * This class allows you to run the code in your classes yourself, for testing and development
 */

public class Main {

	public static void main(String[] args) {
		
		IAircraftDAO aircraft = new AircraftDAO();
		ICrewDAO crew = new CrewDAO();
		IRouteDAO route = new RouteDAO();
		IPassengerNumbersDAO pass = new PassengerNumbersDAO();
		IScheduler sched = new Scheduler();
		
				
		try {
			
			aircraft.loadAircraftData(Paths.get("./data/aircraft.csv"));
			route.loadRouteData(Paths.get("./data/routes.xml"));
			crew.loadCrewData(Paths.get("./data/crew.json"));
			pass.loadPassengerNumbersData(Paths.get("./data/passengernumbers.db"));
			
			
			
			System.out.println(sched.generateSchedule(aircraft, crew, route, pass, LocalDate.parse("2021-07-01"), LocalDate.parse("2021-08-31")));
			
			
			
		}
		catch (DataLoadingException |  NullPointerException dle) {
			System.err.println("Error loading aircraft data");
			dle.printStackTrace();
		}
	}

}
