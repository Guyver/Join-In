/**
 * Copyright 2010 Santiago Hors Fraile and Salvador Jesús Romero

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package NoninPackage;

/**
 * Contains all Nonin data excluding the O2 and pulse data.
 * @author Santiago Hors Fraile
 *
 */
public class NoninData {
	
	/**
	 * Represents the Nonin battery state. It is true when the battery is almost flat, false otherwise.
	 */
	private boolean lowBattery;
	/**
	 * Represents the Nonin artifact condition. It is true when the pulse has artifact condition, false otherwise.
	 */
	private boolean artf;
	/**
	 * Represents the Nonin out of track condition. It is true when there are an absence of consecutive good pulse signals. 
	 */
	private boolean oot;
	/**
	 * Represents the low perfusion condition. Amplitude representation of low/no signal quality (holds for entire duration).
	 */
	private boolean lprf;
	/**
	 * Represents the marginal perfusion condition. Amplitude representation of low/marginal signal quality (holds for entire duration).
	 */
	private boolean mprf;
	/**
	 * Represents the sensor alarm condition. The Nonin is providing unusable data for analysis (set when the finger is removed).
	 */
	private boolean snsa;
	/**
	 * Represents the SmartPoint algorithm condition. High quality SmartPoint measurement.
	 */
	private boolean spa;
	
	/**
	 * This is the class constructor. All fields are set to false unless the artf and oot fields.
	 */
	public NoninData(){
		setLowBattery(false);
		setArtf(true);
		setOot(true);
		setLprf(false);
		setMprf(false);
		setSnsa(false);
		setSpa(false);
		
	}

	/**
	 * Sets the lowBattery field with the given parameter.
	 * @param lowBattery The new battery state.
	 */
	public void setLowBattery(boolean lowBattery) {
		this.lowBattery = lowBattery;
	}
	/**
	 * Gets the battery state.
	 * @return boolean The current battery state.
	 */
	public boolean isLowBattery() {
		return lowBattery;
	}
	/**
	 * Sets the artf field with the given parameter.
	 * @param artf The new artf state.
	 */
	public void setArtf(boolean artf) {
		this.artf = artf;
	}
	/**
	 * Gets the artf  state.
	 * @return boolean The current artf state.
	 */
	public boolean isArtf() {
		return artf;
	}

	/**
	 * Sets the oot field with the given parameter.
	 * @param oot The new oot state.
	 */
	public void setOot(boolean oot) {
		this.oot = oot;
	}
	/**
	 * Gets the oot state.
	 * @return boolean The current oot state.
	 */
	public boolean isOot() {
		return oot;
	}
	/**
	 * Sets the lprf field with the given parameter.
	 * @param lprf The new lprf state.
	 */
	public void setLprf(boolean lprf) {
		this.lprf = lprf;
	}
	/**
	 * Gets the lprf state.
	 * @return boolean The current lprf state.
	 */
	public boolean isLprf() {
		return lprf;
	}
	/**
	 * Sets the mprf field with the given parameter.
	 * @param mprf The new mprf state.
	 */
	public void setMprf(boolean mprf) {
		this.mprf = mprf;
	}
	/**
	 * Gets the mprf state.
	 * @return boolean The current mprf state.
	 */

	public boolean isMprf() {
		return mprf;
	}
	
	/**
	 * Sets the snsa field with the given parameter.
	 * @param snsa The new snsa state.
	 */
	public void setSnsa(boolean snsa) {
		this.snsa = snsa;
	}

	/**
	 * Gets the snsa state.
	 * @return boolean The current snsa state.
	 */
	public boolean isSnsa() {
		return snsa;
	}
	
	/**
	 * Sets the spa field with the given parameter.
	 * @param spa The new spa state.
	 */
	public void setSpa(boolean spa) {
		this.spa = spa;
	}

	/**
	 * Gets the spa state.
	 * @return boolean The current spa state.
	 */
	public boolean isSpa() {
		return spa;
	}


}
