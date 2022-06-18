package solution;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import baseclasses.Aircraft;
import baseclasses.CabinCrew;
import baseclasses.DoubleBookedException;
import baseclasses.FlightInfo;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;
import baseclasses.IScheduler;
import baseclasses.InvalidAllocationException;
import baseclasses.Pilot;
import baseclasses.Schedule;

/**
 * The Scheduler class is responsible for deciding which aircraft and crew will be
 * used for each of an airline's flights in a specified period of time, referred to
 * as a "scheduling horizon". A schedule must have an aircraft, two pilots, and 
 * sufficient cabin crew for the aircraft allocated to every flight in the horizon 
 * to be valid.
 */
public class Scheduler implements IScheduler {

	
	/**
	 * Generates a schedule, providing you with ready-loaded DAO objects to get your data from
	 * @param aircraftDAO the DAO for the aircraft to be used when scheduling
	 * @param crewDAO the DAO for the crew to be used when scheduling
	 * @param routeDAO the DAO to use for routes when scheduling
	 * @param passengerNumbersDAO the DAO to use for passenger numbers when scheduling
	 * @param startDate the start of the scheduling horizon
	 * @param endDate the end of the scheduling horizon
	 * @return The generated schedule - which must happen inside 2 minutes
	 */
	@Override
	public Schedule generateSchedule(IAircraftDAO aircraftDAO, ICrewDAO crewDAO, IRouteDAO routeDAO, 
			IPassengerNumbersDAO passengerNumbersDAO, LocalDate startDate, LocalDate endDate) {
		
		//FlightInfo f = new FlightInfo(null, endDate);
		
		
		//all aircraft
		//removes the busy ones reduces it 
		//removes aircraft wrong airport
		//remove aircraft that are not big enough
		//after those if (list is empty go back to previous step)
		
		//all crew look at free to fly and check if there qualified
		Schedule s = new Schedule(routeDAO, startDate, endDate);
		
		List<FlightInfo> flightSched = s.getRemainingAllocations();

		for(FlightInfo f :flightSched)
		{
			List<Aircraft> airLST = aircraftDAO.getAllAircraft();
			List<CabinCrew> crewLST = crewDAO.getAllCabinCrew();
			List<Pilot> pilotLST = crewDAO.getAllPilots();
			
			//List<Route> routeLST = routeDAO.getAllRoutes();
			
			boolean found = false;
			
			Aircraft a = airLST.get(0);
			
			LocalDate localEndDate = f.getDepartureDateTime().toLocalDate();
			int pass = passengerNumbersDAO.getPassengerNumbersFor(f.getFlight().getFlightNumber(),localEndDate);
			
			int countNew =0;
			
			while(! found)
			{
				Collections.shuffle(airLST);
				a = airLST.get(0);
				countNew++;
				int factor = a.getSeats() - pass;
				
				if(! s.hasConflict(a, f))
				{
					if(factor >= -100 && factor <= 100) //100 seems to be the best size after experimenting
					{
					found = true;
					}
					else if(countNew >= airLST.size())
					{
					found = true;	
					}
				}
			}
			
			try {
				s.allocateAircraftTo(a, f);
			} catch (DoubleBookedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int crewPOS = -1;
			int countc = 0;
			
			//boolean breaks = false;
			
			List<String> check = new ArrayList<String>();
			check.clear();
			
			 
			while(crewPOS == -1)
			{
					
				for(int i=0;i<a.getCabinCrewRequired();i++)
				{
					//countc = 0;
					
					for(CabinCrew c: crewLST)
					{
						countc++;
						
						if(! s.hasConflict(c, f) && ! check.contains(c.getForename()+c.getSurname()))
						{
								if(c.getTypeRatings().contains(a.getTypeCode()))
								{
									if(c.getHomeBase().equals(a.getStartingPosition()))
									{
										
										//if(flightSched.indexOf(f) >= 1)
										//{
										//	System.out.println(s.getCompletedAllocationsFor(c).get(flightSched.indexOf(f)).getLandingDateTime());
										//	System.out.println(f.getDepartureDateTime());
										//}
										
											
											check.add(c.getForename()+c.getSurname());
											crewPOS = crewLST.indexOf(c);
											break;
									}
									else if(countc >= crewLST.size())
									{
										
											
												check.add(c.getForename()+c.getSurname());
												crewPOS = crewLST.indexOf(c);
												break;
										
									}
								}
								else if(countc >= crewLST.size())
								{
									
									
											check.add(c.getForename()+c.getSurname());
											crewPOS = crewLST.indexOf(c);
											break;

								}
							
						}
						
						
					}
						if(crewPOS == -1)
						{
							break;
						}
						else
						{
							try {
								s.allocateCabinCrewTo(crewLST.get(crewPOS), f);
							} catch (DoubleBookedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
				}
		
				}
					
			
			
			
			
			int capPOS = -1;
			int count = 0;
			
			while(capPOS == -1)
			{
				count++;
				for(Pilot p : pilotLST)
				{
					//System.out.println(pilotLST.size());
					if(! s.hasConflict(p, f))
					{
						if(p.getRank().equals(Pilot.Rank.valueOf("CAPTAIN")))//&& 
						{
							if(p.getTypeRatings().contains(a.getTypeCode()))
							{
								capPOS = pilotLST.indexOf(p);
								break;
							}
							else if(count >= pilotLST.size() && p.getHomeBase().equals(a.getStartingPosition()))
							{
								capPOS = pilotLST.indexOf(p);
								break;
							}
							else if(count > pilotLST.size())
							{
								capPOS = pilotLST.indexOf(p);
								break;
							}
						}
					}
				}
			}
			
			try {
				s.allocateCaptainTo(pilotLST.get(capPOS), f);
				//System.out.println(s.getRemainingAllocations().size());
				//System.out.println(capPOS);
			} catch (DoubleBookedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int offPOS = -1;
			int countO = 0;
			
			while(offPOS == -1)
			{
				countO++;
				for(Pilot p : pilotLST)
				{
					if(! s.hasConflict(p, f))
					{
						
						if(p.getRank().equals(Pilot.Rank.valueOf("FIRST_OFFICER")))
						{
							if(p.getTypeRatings().contains(a.getTypeCode()))
							{
								offPOS = pilotLST.indexOf(p);
								break;
							}
							else if(count >= pilotLST.size() && p.getHomeBase().equals(a.getStartingPosition()))
							{
								offPOS = pilotLST.indexOf(p);
								break;
							}
							else if(countO > pilotLST.size())
							{
								offPOS = pilotLST.indexOf(p);
								break;
							}	
						}
					}
				}
			}
			
			try {
				//System.out.println(offPOS);
				s.allocateFirstOfficerTo(pilotLST.get(offPOS), f);
			} catch (DoubleBookedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println("pls ive been working very hard");
			
			if (s.isValid(f) == true)
			{
				try {
					s.completeAllocationFor(f);
				} catch (InvalidAllocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
			}
			else
			{
				System.err.print("something went wrong");
			}
			
			
			
			
			
		}
	
		
		return s;
	}
}
