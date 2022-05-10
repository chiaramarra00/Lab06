package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	MeteoDAO meteoDao;

	private List<Rilevamento> sequenzaMigliore;
	private int costoMigliore;
	private List<String> leCitta;
	private List<Rilevamento> rilevamentiMese;

	public Model() {
		meteoDao = new MeteoDAO();
		leCitta = getAllCitta();
	}

	private List<String> getAllCitta() {
		return meteoDao.getAllCitta();
	}

	// of course you can change the String output with what you think works best
	public Map<String, Double> getUmiditaMedia(int mese) {
		MeteoDAO meteoDao = new MeteoDAO();
		return meteoDao.getUmiditaMedia(mese);
	}

	// of course you can change the String output with what you think works best
	public List<Rilevamento> trovaSequenza(int mese) {
		rilevamentiMese = new ArrayList<Rilevamento>();
		for (int i = 0; i < leCitta.size(); i++)
			rilevamentiMese.addAll(meteoDao.getAllRilevamentiLocalitaMese(mese, leCitta.get(i)));
		sequenzaMigliore = null;
		List<Rilevamento> parziale = new ArrayList<Rilevamento>();
		// for (Citta c : leCitta)
		// c.setRilevamenti(meteoDao.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		System.out.println("RICERCA MESE " + Integer.toString(mese));
		trovaSequenza_ricorsiva(parziale, 0);
		return sequenzaMigliore;
	}

	private void trovaSequenza_ricorsiva(List<Rilevamento> parziale, int livello) {
		if (parziale.size() == NUMERO_GIORNI_TOTALI) {
			int costo = calcolaCosto(parziale);
			if (sequenzaMigliore == null || costo < costoMigliore) {
				System.out.format("%d %s\n", costo, parziale);
				sequenzaMigliore = new ArrayList<Rilevamento>(parziale);
				costoMigliore = costo;
			}
			System.out.println(parziale);
		} else {
			for (int i = 0; i < leCitta.size(); i++) {
				if (isValid(leCitta.get(i),parziale)) {
					parziale.add(cercaRilevamento(leCitta.get(i), livello));
					trovaSequenza_ricorsiva(parziale, livello + 1);
					parziale.remove(parziale.size() - 1);
				}
			}
		}
	}

	private Rilevamento cercaRilevamento(String localita, int livello) {

		for (Rilevamento r : rilevamentiMese) {
			if (r.getLocalita().equals(localita) && r.getData().toLocalDate().getDayOfMonth() == livello + 1)
				return r;
		}

		return null;
	}

	private boolean isValid(String provaCitta, List<Rilevamento> parziale) {
//		int flag = 0;
//		if (livello == 0 || !parziale.get(livello).getLocalita().equals(parziale.get(livello - 1).getLocalita())) {
//			for (int j = 1; j < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN && livello + j < NUMERO_GIORNI_TOTALI; j++) {
//				parziale.add(cercaRilevamento(leCitta.get(i), livello + j));
//				flag = j;
//			}
//		}
		int livello = parziale.size();
		if (livello-1 == NUMERO_GIORNI_TOTALI && !contieneTutteLocalita(parziale))
			return false;
		if (numGiorniCittaMaxSuperato(parziale, provaCitta))
			return false;
		if ((livello==1 || livello==2) && !parziale.get(livello-1).getLocalita().equals(provaCitta))
			return false;
		if (livello>2 && !parziale.get(livello-1).getLocalita().equals(provaCitta) && !parziale.get(livello-1).getLocalita().equals(parziale.get(livello-2).getLocalita()))
			return false;
		if (livello>2 && !parziale.get(livello-1).getLocalita().equals(provaCitta) && parziale.get(livello-1).getLocalita().equals(parziale.get(livello-2).getLocalita()) && !parziale.get(livello-2).getLocalita().equals(parziale.get(livello-3).getLocalita()))
			return false;
		return true;
	}

	private boolean numGiorniCittaMaxSuperato(List<Rilevamento> parziale, String provaCitta) {
		int cont=0;
		for (Rilevamento r : parziale) {
			if (r.getLocalita().equals(provaCitta))
				cont++;
		}
		if (cont >= NUMERO_GIORNI_CITTA_MAX)
			return true;
		return false;
	}

	private boolean contieneTutteLocalita(List<Rilevamento> parziale) {
		int contG = 0;
		int contM = 0;
		int contT = 0;
		for (Rilevamento r : parziale) {
			if (r.getLocalita().equals("Genova"))
				contG++;
			if (r.getLocalita().equals("Milano"))
				contM++;
			else if (r.getLocalita().equals("Torino"))
				contT++;
		}
		if (contG == 0 || contM == 0 || contT == 0)
			return false;
		return true;
	}

	private int calcolaCosto(List<Rilevamento> parziale) {
		int somma = 0;
		for (Rilevamento r : parziale)
			somma += r.getUmidita();
		for (int i = 1; i < parziale.size(); i++)
			if (!parziale.get(i).getLocalita().equals(parziale.get(i - 1).getLocalita()))
				somma += COST;
		return somma;
	}

}
