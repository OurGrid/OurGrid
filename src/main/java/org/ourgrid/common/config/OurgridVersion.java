/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.common.config;

import java.io.Serializable;

public class OurgridVersion implements Serializable {

	public static final String ALPHA = "alpha";

	public static final String BUILD = "-build";

	public static final String BETA = "beta";

	private static final long serialVersionUID = 1L;

	private int major;

	private int minor;

	private int revision;

	private int build;

	private int beta;

	private int alpha;


	public OurgridVersion( int major, int minor ) {

		super();
		this.major = major;
		this.minor = minor;
		this.revision = 0;
		this.build = 0;
		this.beta = 0;
		this.alpha = 0;
	}
	
	public OurgridVersion( int major, int minor, int revision ) {

		super();
		this.major = major;
		this.minor = minor;
		this.revision = revision;
		this.build = 0;
		this.beta = 0;
		this.alpha = 0;
	}


	public OurgridVersion( int major, int minor, int revision, int beta, int alpha, int build ) {

		super();
		this.major = major;
		this.minor = minor;
		this.revision = revision;
		this.beta = beta;
		this.alpha = alpha;
		this.build = build;
	}

	public static OurgridVersion parse(String version) {
		
		if (version == null) {
			throw new IllegalArgumentException(
					"Cannot parse OurGrid version from null string");
		}
		
		String[] versionSplitted = version.split("\\.");
		
		if (versionSplitted.length == 2) {
			return new OurgridVersion(
					Integer.valueOf(versionSplitted[0]), 
					Integer.valueOf(versionSplitted[1]));
		}
		
		if (versionSplitted.length == 3) {
			return new OurgridVersion(
					Integer.valueOf(versionSplitted[0]), 
					Integer.valueOf(versionSplitted[1]),
					Integer.valueOf(versionSplitted[2]));
		}
		
		if (versionSplitted.length == 6) {
			return new OurgridVersion(
					Integer.valueOf(versionSplitted[0]), 
					Integer.valueOf(versionSplitted[1]),
					Integer.valueOf(versionSplitted[2]),
					Integer.valueOf(versionSplitted[3]),
					Integer.valueOf(versionSplitted[4]),
					Integer.valueOf(versionSplitted[5]));
		}
		
		throw new IllegalArgumentException(
				"Cannot parse OurGrid version from string [" + version + "]");
	}

	public int getBeta() {

		return beta;
	}


	public int getBuild() {

		return build;
	}


	public int getMajor() {

		return major;
	}


	public int getMinor() {

		return minor;
	}


	public int getRc() {

		return alpha;
	}


	public int getRevision() {

		return revision;
	}


	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + beta;
		result = PRIME * result + build;
		result = PRIME * result + major;
		result = PRIME * result + minor;
		result = PRIME * result + alpha;
		result = PRIME * result + revision;
		return result;
	}


	@Override
	public boolean equals( Object obj ) {

		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		final OurgridVersion other = (OurgridVersion) obj;
		if ( beta == other.beta && build == other.build && major == other.major && minor == other.minor
				&& alpha == other.alpha && revision == other.revision )
			return true;
		return false;
	}


	@Override
	public String toString() {

		String version = major + "." + minor + "." + revision;

		if ( beta != 0 ) {
			version += BETA + beta;
		} else {
			if ( alpha != 0 ) {
				version += ALPHA + alpha;
			}
		}

		if ( build != 0 ) {
			version += BUILD + build;
		}

		return version;

	}
}
