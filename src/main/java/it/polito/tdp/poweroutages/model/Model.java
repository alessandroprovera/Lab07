package it.polito.tdp.poweroutages.model;

import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import it.polito.tdp.poweroutages.DAO.PowerOutageDAO;

public class Model {
	
	PowerOutageDAO podao;
	
	public Model() {
		podao = new PowerOutageDAO();
	}
	
	public List<Nerc> getNercList() {
		return podao.getNercList();
	}
	
	List<PowerOutage> soluzioneBest = new ArrayList<>();
	int max = 0;
	
	public List<PowerOutage> trovaWorstCase(Nerc nerc, int xMaxYears, int yOreDisservizio) {
		List<PowerOutage> parziale = new ArrayList<>();
		List<PowerOutage> powerOutages = podao.getPowerOutagesByNerc(nerc);
		cerca(powerOutages,parziale,xMaxYears,yOreDisservizio,1);
		return soluzioneBest;
	}
	
	private void cerca(List<PowerOutage> powerOutages, List<PowerOutage> parziale, int xMaxYears, int yOreDisservizio, int livello) {
		//aggiorno la soluzione parziale
		if(calcolaMax(parziale) > max) {
			max = calcolaMax(parziale);
			soluzioneBest.clear();
			for(PowerOutage temp: parziale) {
				soluzioneBest.add(temp);
			}
		}
		
		//genero soluzione parziale
		for(PowerOutage po: powerOutages) {
			if(!parziale.contains(po)) {
				parziale.add(po);
				if(isMinoreY(parziale,yOreDisservizio) && isMinoreX(parziale,xMaxYears))
					cerca(powerOutages,parziale,xMaxYears,yOreDisservizio,livello+1);
				parziale.remove(parziale.size()-1);
			}
		}
		
	}

	private boolean isMinoreX(List<PowerOutage> parziale, int xMaxYears) {
		LocalDateTime recent = parziale.get(0).getDate_event_finished();
		LocalDateTime oldest = parziale.get(parziale.size()-1).getDate_event_began();
		Duration duration = Duration.between(recent, oldest);
		
		return (duration.toDays()/365)<xMaxYears;
	}

	private boolean isMinoreY(List<PowerOutage> parziale, int yOreDisservizio) {
		int tot = 0;
		for(PowerOutage p: parziale) {
			Duration duration = Duration.between(p.getDate_event_began(), p.getDate_event_finished());
			tot += duration.toHours();
		}
		return tot<yOreDisservizio;
	}

	private int calcolaMax(List<PowerOutage> parziale) {
		int tot = 0;
		for(PowerOutage p: parziale) {
			tot+=p.getCustomers_affected();
		}
		return tot;
	}

}
