package profusians.zonemanager.action;

import prefuse.action.Action;
import profusians.zonemanager.ZoneManager;

/**
 * Guarding action that checks if all items belonging to a zone are within the
 * borders of the zone.
 * 
 * This action checks this by default every fifth time its run() method is
 * called. This behavior can be changed through the setNumberOfIdleRounds()
 * method or by specifying the number of idle rounds between two checks through
 * the constructor.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */

public class ZoneGuardAction extends Action {

    ZoneManager m_zoneManager;

    int m_round = 0;

    int m_numberOfIdleRounds = 4;

    /**
         * Creates a new ZoneGuard Action
         * 
         * @param zManager
         *                the zone manager who handles the zones to be guarded
         */

    public ZoneGuardAction( ZoneManager zManager) {
	m_zoneManager = zManager;
    }

    /**
         * Creates a new ZoneGuard Action
         * 
         * @param zManager
         *                the zone manager who handles the zones to be guarded
         * @param numberOfIdleRounds
         *                the number of idle rounds between to guarding actions
         *                (default 4)
         */
    public ZoneGuardAction( ZoneManager zManager,
	     int numberOfIdleRounds) {
	m_zoneManager = zManager;
	if (numberOfIdleRounds >= 0) {
	    m_numberOfIdleRounds = numberOfIdleRounds;
	}
    }

    /**
         * Returns the number of idle rounds between two guarding actions
         * 
         * @return the number of idle rounds
         */

    public int getNumberOfIdelRounds() {
	return m_numberOfIdleRounds;
    }

    /**
         * Specifies the number of idle rounds between to guarding action
         * 
         * @param numberOfIdleRounds
         *                the number of idle rounds
         */
    public void setNumberOfIdleRounds( int numberOfIdleRounds) {
	m_numberOfIdleRounds = numberOfIdleRounds;
    }

    public void run( double frac) {

	if ((m_round++) % (m_numberOfIdleRounds + 1) == 0) {
	    m_zoneManager.catchAll();
	}

    }

}
