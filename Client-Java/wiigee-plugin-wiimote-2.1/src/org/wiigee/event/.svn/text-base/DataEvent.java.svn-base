/*
 * Copyright 2007-2008 Volker Fritzsch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.wiigee.event;

/**
 * Defines the DataEvents.
 * <p>
 * @author <a href="mailto:vfritzsch@users.sourceforge.net">Volker Fritzsch</a>, upgraded by Santiago Hors Fraile
 */
public class DataEvent {

	/**
	 * Represents the BlueTooth address of the device.
	 */
	private byte[] address;
	
	/**
	 * Represnts the PayLoad.
	 */
	private byte[] payload;
	/**
	 * Represents the type of error.
	 */
	private int error;
	
	public DataEvent(byte[] address, byte[] payload, int error) {
		this.address = address;
		this.payload = payload;
		this.error = error;
	}

	/**
	 * Gets the field address.
	 * @return byte[] The current addres.
	 */
	public byte[] getAddress() {
		return address;
	}

	/**
	 * Gets the field payload.
	 * @return byte[] The current payload.
	 */
	public byte[] getPayload() {
		return payload;
	}

	/**
	 * Gets the field error.
	 * @return int The current error.
	 */
	public int getError() {
		return error;
	}
	
}
