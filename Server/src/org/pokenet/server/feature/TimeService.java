package org.pokenet.server.feature;

import java.util.Calendar;

import org.pokenet.server.GameServer;
import org.pokenet.server.battle.DataService;
import org.pokenet.server.battle.mechanics.statuses.field.FieldEffect;
import org.pokenet.server.battle.mechanics.statuses.field.HailEffect;
import org.pokenet.server.battle.mechanics.statuses.field.RainEffect;
import org.pokenet.server.battle.mechanics.statuses.field.SandstormEffect;
import org.pokenet.server.battle.mechanics.statuses.field.SunEffect;

/**
 * Handles game time and weather
 * @author shadowkanji
 *
 */
public class TimeService implements Runnable {
	private boolean m_isRunning;
	private long m_lastWeatherUpdate;
	private Thread m_thread;
	private static int m_hour;
	private static int m_minutes;
	private static Weather m_weather = Weather.NORMAL;
	
	/*
	 * NOTE: HAIL = SNOW
	 */
	public enum Weather { NORMAL, RAIN, HAIL, SANDSTORM, FOG }
	
	/**
	 * Default constructor
	 */
	public TimeService() {
		m_lastWeatherUpdate = System.currentTimeMillis();
		m_thread = new Thread(this);
	}
	
	/**
	 * Called by m_thread.start()
	 */
	public void run() {
		Calendar cal = Calendar.getInstance();
		m_hour = cal.get(Calendar.HOUR_OF_DAY);
		m_minutes = 0;
		while(m_isRunning) {
			//Update the time. Time moves 4 times faster.
			m_minutes = m_minutes == 59 ? 0 : m_minutes + 1;
			if(m_minutes == 0)
				m_hour = m_hour == 23 ? 0 : m_hour + 1;
			//Check if weather should be updated
			if(System.currentTimeMillis() - m_lastWeatherUpdate >= 10800000) {
				generateWeather();
				m_lastWeatherUpdate = System.currentTimeMillis();
			}
			try {
				Thread.sleep(15000);
			} catch (Exception e) {}
		}
		System.out.println("INFO: Time Service stopped");
	}
	
	/**
	 * Starts this Time Service
	 */
	public void start() {
		m_isRunning = true;
		m_thread.start();
		System.out.println("INFO: Time Service started");
	}

	/**
	 * Stops this Time Service
	 */
	public void stop() {
		m_isRunning = false;
	}
	
	/**
	 * Generates a new weather status
	 */
	private void generateWeather() {
		GameServer.getServiceManager().getDataService();
		switch(DataService.getBattleMechanics().getRandom().nextInt(4)) {
		case 0:
			m_weather = Weather.NORMAL;
			break;
		case 1:
			m_weather = Weather.RAIN;
			break;
		case 2:
			m_weather = Weather.HAIL;
			break;
		case 3:
			m_weather = Weather.FOG;
			break;
		default:
			m_weather = Weather.NORMAL;
		}
	}
	
	/**
	 * Returns the field effect based on current weather
	 * @return
	 */
	public static FieldEffect getWeatherEffect() {
		switch(m_weather) {
		case NORMAL:
			return null;
		case RAIN:
			return new RainEffect();
		case HAIL:
			return new HailEffect();
		case SANDSTORM:
			return new SandstormEffect();
		case FOG:
			return null;
		default:
			return null;
		}
	}
	
	/**
	 * Returns a string representation of the current time, e.g. 1201
	 * @return
	 */
	public static String getTime() {
		return "" + (m_hour < 10 ? "0" + m_hour : m_hour) + (m_minutes < 10 ? "0" + m_minutes : m_minutes);
	}
	
	/**
	 * Returns the hour of the day (game time)
	 * @return
	 */
	public static int getHourOfDay() {
		return m_hour;
	}
	
	/**
	 * Returns the current minute (game time)
	 * @return
	 */
	public static int getMinuteOfDay() {
		return m_minutes;
	}
	
	/**
	 * Returns true if it is night time
	 * @return
	 */
	public static boolean isNight() {
		return m_hour >= 20 || m_hour < 6;
	}
	
	/**
	 * Returns the id of the weather
	 * @return
	 */
	public static int getWeatherId() {
		switch(m_weather) {
		case NORMAL:
			return 0;
		case RAIN:
			return 1;
		case HAIL:
			return 2;
		case SANDSTORM:
			return 3;
		case FOG:
			return 4;
		default:
			return 0;
		}
	}
}
