/*
 * StatException.java
 *
 * Created on December 15, 2006, 12:06 PM
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
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, visit the Free Software Foundation, Inc.
 * online at http://gnu.org.
 */

package org.pokenet.server.battle.mechanics;

/**
 * A StatException is throw by some methods if there is a problem with stats.
 * This might occur, for example, if the index of the statistic requested does
 * not exist.
 *
 * @author Colin
 */
@SuppressWarnings("serial")
public class StatException extends IllegalArgumentException {
    public StatException() {
        super("Invalid stat index.");
    }
    public StatException(String reason) {
        super(reason);
    }
}
