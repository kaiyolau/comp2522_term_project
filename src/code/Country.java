import java.util.HashMap;

/**
 * Represents a country with its name, capital city, and interesting facts.
 */
class Country {
    private String name;
    private String capitalCityName;
    private String[] facts;

    /**
     * Constructs a new Country object.
     * @param name The name of the country
     * @param capitalCityName The name of the capital city
     * @param facts An array of interesting facts about the country
     */
    public Country(String name, String capitalCityName, String[] facts) {
        this.name = name;
        this.capitalCityName = capitalCityName;
        this.facts = facts;
    }

    /**
     * Gets the country name.
     * @return The name of the country
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the capital city name.
     * @return The name of the capital city
     */
    public String getCapitalCityName() {
        return capitalCityName;
    }

    /**
     * Gets the facts about the country.
     * @return An array of facts about the country
     */
    public String[] getFacts() {
        return facts;
    }
}

