package employeeScheduler.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum helping to use specific acceptance Level
 */
public enum AcceptanceLevel {
    VERY_LOW(0.01),
    LOW(0.1),
    MEDIUM(0.4),
    HIGH(0.6),
    VERY_HIGH(1);


    private double value;
    private static Map map = new HashMap<>();

    private AcceptanceLevel(double value) {
        this.value = value;
    }

    static {
        for (AcceptanceLevel acceptanceLevel : AcceptanceLevel.values()) {
            map.put(acceptanceLevel.value, acceptanceLevel);
        }
    }

    public static AcceptanceLevel valueOf(int acceptanceLevel) {
        return (AcceptanceLevel) map.get(acceptanceLevel);
    }

    public double getValue() {
        return value;
    }
}
