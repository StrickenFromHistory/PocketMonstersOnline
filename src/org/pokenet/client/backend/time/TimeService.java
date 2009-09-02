package org.pokenet.client.backend.time;

import mdes.slick.sui.Label;

import org.newdawn.slick.Color;
import org.pokenet.client.GameClient;

/**
 * Handles time and rendering of night. Also acts as a label for usage in GUI
 * @author shadowkanji
 *
 */
public class TimeService extends Label implements Runnable {
	private int m_hour, m_minutes, m_daylight, m_targetDaylight;
	private Thread m_thread;
	
	/**
	 * Default constructor
	 */
	public TimeService() {
		super("00:00");
		this.pack();
		this.setLocation(4, 4);
		this.setVisible(true);
		this.setFont(GameClient.getFontLarge());
		this.setForeground(new Color(255, 255, 255));
		m_thread = new Thread(this);
	}
	
	/**
	 * Called by thread.start()
	 */
	public void run() {
		String min;
		String hour;
		while(true) {
			m_minutes = m_minutes == 59 ? 0 : m_minutes + 1;
			if(m_minutes == 0) {
				m_hour = m_hour == 23 ? 0 : m_hour + 1;
				switch(m_hour) {
				case 3:
					m_targetDaylight = 150;
					break;
				case 4:
					m_targetDaylight = 125;
					break;
				case 5:
					m_targetDaylight = 100;
					break;
				case 6:
					m_targetDaylight = 75;
					break;
				case 7:
					m_targetDaylight = 0;
					break;
				case 17:
					m_targetDaylight = 75;
					break;
				case 18:
					m_targetDaylight = 100;
					break;
				case 19:
					m_targetDaylight = 125;
					break;
				case 20:
					m_targetDaylight = 150;
					break;
				case 21:
					m_targetDaylight = 175;
					break;
				default:
					break;
				}
			}
			hour = m_hour < 10 ? "0" + String.valueOf(m_hour) : String.valueOf(m_hour);
			min = m_minutes < 10 ? "0" + String.valueOf(m_minutes) : String.valueOf(m_minutes);
			this.setText(hour + ":" + min);
			this.pack();
			try {
				Thread.sleep(60000);
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Updates daylight
	 */
	public void updateDaylight() {
		if(m_daylight < m_targetDaylight)
			m_daylight++;
		else if(m_daylight > m_targetDaylight)
			m_daylight--;
	}

	/**
	 * Sets the current time and starts the time service
	 * @param hours
	 * @param minutes
	 */
	public void setTime(int hour, int minutes) {
		m_hour = hour;
		m_minutes = minutes;
		
		if(hour == 17) {
			m_daylight = 75;
			m_targetDaylight = 75;
		} else if(hour == 18) {
			m_daylight = 100;
			m_targetDaylight = 100;
		} else if(hour == 19) {
			m_daylight = 125;
			m_targetDaylight = 125;
		} else if(hour == 20) {
			m_daylight = 150;
			m_targetDaylight = 150;
		} else if(hour == 3) {
			m_daylight = 150;
			m_targetDaylight = 150;
		} else if(hour == 4) {
			m_daylight = 125;
			m_targetDaylight = 125;
		} else if(hour == 5) {
			m_daylight = 100;
			m_targetDaylight = 100;
		} else if(hour == 6) {
			m_daylight = 75;
			m_targetDaylight = 75;
		} else if(hour >= 8 && hour <= 17) {
			m_daylight = 0;
			m_targetDaylight = 0;
		} else {
			m_daylight = 175;
			m_targetDaylight = 175;
		}
		this.setText(hour + ":" + minutes);
		
		// Stop was causing this next part not to work for some reason...
		// hopefully this doesn't cause any problems if setTime gets called
		// in the future
		m_thread.start();
	}
	
	/**
	 * Returns true if it is night
	 * @return
	 */
	public boolean isNight() {
		return m_hour >= 18 && m_hour <= 7;
	}
	
	/**
	 * Returns the current daylight
	 * @return
	 */
	public int getDaylight() {
		return m_daylight;
	}
	
	/**
	 * Returns the target daylight
	 * @return
	 */
	public int getTargetDaylight() {
		return m_targetDaylight;
	}
}
