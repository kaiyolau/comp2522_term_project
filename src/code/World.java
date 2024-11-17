import java.util.Collection;
import java.util.HashMap;

/**
 * Represents the world containing all countries and their information.
 */
class World {
    private HashMap<String, Country> countries;

    /**
     * Constructs a new World object.
     */
    public World() {
        countries = new HashMap<>();
    }

    /**
     * Adds a country to the world.
     * @param country The country to add
     */
    public void addCountry(Country country) {
        countries.put(country.getName(), country);
    }

    /**
     * Gets a country by its name.
     * @param name The name of the country to retrieve
     * @return The Country object, or null if not found
     */
    public Country getCountry(String name) {
        return countries.get(name);
    }
    /**
     * Gets all countries in the world.
     * @return A Collection of all Country objects
     */
    public Collection<Country> getAllCountries() {
        return countries.values();
    }
}