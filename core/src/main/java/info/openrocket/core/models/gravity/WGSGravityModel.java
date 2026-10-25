package info.openrocket.core.models.gravity;

import info.openrocket.core.util.MathUtil;
import info.openrocket.core.util.ModID;
import info.openrocket.core.util.WorldCoordinate;

/**
 * A gravity model for a spherical body with a configurable surface gravity.
 * Originally based on the WGS84 ellipsoid model by Richard Graham
 * <richard@rdg.cc>; the Earth-specific latitude term has been removed.
 */
public class WGSGravityModel implements GravityModel {

	// Surface gravity (m/s^2) and mean radius (m) of the body.
	// Currently set to the Moon. Examples:
	//   Mars: 3.71 and 3_389_500.0
	//   Earth (no oblateness): 9.80665 and 6_371_000.0
	// Verify these constants against a source you trust before relying on them.
	private static final double G_SURFACE = 1.62;
	private static final double BODY_RADIUS = 1_737_400.0;

	// Cache the previously computed value
	private WorldCoordinate lastWorldCoordinate;
	private double lastg;

	@Override
	public double getGravity(WorldCoordinate wc) {

		// This is a proxy method to calcGravity, to avoid repeated calculation
		if (wc != this.lastWorldCoordinate) {
			this.lastg = calcGravity(wc);
			this.lastWorldCoordinate = wc;
		}

		return this.lastg;

	}

	@Override
	public ModID getModID() {
		// The model is immutable, so it can return a constant mod ID
		return ModID.ZERO;
	}

	private double calcGravity(WorldCoordinate wc) {
		// Inverse-square falloff with altitude, assuming a spherical body.
		// For constant gravity with no altitude effect, return G_SURFACE directly.
		return G_SURFACE * MathUtil.pow2(BODY_RADIUS / (BODY_RADIUS + wc.getAltitude()));
	}

}
