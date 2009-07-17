/*
 * MoveQueueException.java
 *
 * Created on January 22, 2007, 4:41 PM
 *
 * This file is a part of Shoddy Battle.
 * Copyright (C) 2006  Colin Fitzpatrick
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.pokenet.server.battle.mechanics;

/**
 *
 * @author Colin
 */
@SuppressWarnings("serial")
public class MoveQueueException extends Exception {
    
    /** Creates a new instance of MoveQueueException */
    public MoveQueueException(String description) {
        super(description);
    }
    
}
