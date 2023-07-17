package com.coursework;

/**
 * City object holding all data about given city
 *
 * @author Marek Jezinski
 */
public class City {
    private String country;
    private String state;
    private String city;
    private int foundingDate;

    public City() {

    }

    /**
     * Returns country city is in
     * @return country name
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets country name
     * @param country country name
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets state name
     * @return state name
     */
    public String getState() {
        return state;
    }

    /**
     * Sets state name
     * @param state state name
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets city name
     * @return city name
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets city name
     * @param city city name
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets founding date
     * @return founding date
     */
    public int getFoundingDate() {
        return foundingDate;
    }

    /**
     * Sets founding date
     * @param foundingDate founding date
     */
    public void setFoundingDate(int foundingDate) {
        this.foundingDate = foundingDate;
    }
}
