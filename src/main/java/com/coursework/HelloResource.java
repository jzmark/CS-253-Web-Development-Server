package com.coursework;

import jakarta.inject.Singleton;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Server resources and methods
 *
 * @author Marek Jezinski
 */
@Singleton
@Path("/webresource")
public class HelloResource {

    //despite intellij telling me to set it to final, I am keeping it as a variable as cities arraylist keep changing
    private ArrayList<City> cities = new ArrayList<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Returns cities with relevant filtering
     *
     * @param countryName name of the country
     * @param stateName   name of the state
     * @param founded     founding date
     * @return http and JSON response
     */
    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@QueryParam("country") String countryName,
                        @QueryParam("state") String stateName,
                        @QueryParam("founded") Integer founded) { //set to Integer as it supports nulls
        //creating additional arraylist of cities to be returned
        ArrayList<City> newCities = new ArrayList<>();
        lock.readLock().lock();
        for (City value : cities) {
            //filtering results (if any filters are null then those filters are ignored - returns true)
            if (filterString(countryName, value.getCountry())
                    && filterString(stateName, value.getState())
                    && filterInteger(founded, value.getFoundingDate())) {
                newCities.add(value);
            }
        }
        lock.readLock().unlock();
        if (newCities.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("[]").build();
        }
        return Response.status(Response.Status.OK).entity(newCities).build();
    }

    /**
     * Filtering strings so that when any string is not provided (i.e. filter is not set)
     * then true is being returned since we don't really care about not set filters
     *
     * @param queryParam query parameter string (Country or State name)
     * @param string2    second string to be compared with query parameter (provided from local City objects)
     * @return true if strings are equal (or one/both are null) false otherwise
     */
    private boolean filterString(String queryParam, String string2) {
        if (queryParam == null || string2 == null) {
            return true;
        } else {
            return queryParam.equals(string2);
        }
    }

    /**
     * Same as filterString method above with the difference for variable type (Integer) and checking
     * if local Integer value is smaller than query parameter (filter) so that function returns
     * cities with founding date lower than query parameter
     *
     * @param queryParam query parameter Integer
     * @param int2       date to compare
     * @return true if Integers are equal (or one/both are null) false otherwise
     */
    private boolean filterInteger(Integer queryParam, Integer int2) {
        if (queryParam == null || int2 == null) {
            return true;
        } else {
            return queryParam > int2;
        }
    }

    /**
     * Adding a city using POST request
     *
     * @param newCity a JSON provided from POST request in a specified format
     * @return http response
     */
    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("text/plain")
    public Response add(City newCity) {
        lock.writeLock().lock();
        if (addCity(newCity)) {
            lock.writeLock().unlock();
            return Response.status(Response.Status.OK).entity(String.format("Adding: %s was founded in %d "
                            + "and is in %s, which is in %s", newCity.getCity(), newCity.getFoundingDate(),
                    newCity.getState(), newCity.getCountry())).build();
        } else {
            lock.writeLock().unlock();
            return Response.status(Response.Status.CONFLICT).entity("City already exists!").build();
        }
    }

    /**
     * Loop that checks if city does not exist already and adds it to the arraylist
     *
     * @param city city object to be added
     * @return true if added, false if already exists and not added
     */
    private boolean addCity(City city) {
        for (City value : cities) {
            if (isEqual(value, city.getCountry(), city.getState(), city.getCity())) {
                return false;
            }
        }
        cities.add(city);
        return true;
    }

    /**
     * Checks it City object properties are equal to provided Strings
     *
     * @param city     City object
     * @param country  coutry name
     * @param state    state name
     * @param cityName city name
     * @return true if equal, false otherwise
     */
    private boolean isEqual(City city, String country, String state, String cityName) {
        return (city.getCountry().equals(country)
                && city.getState().equals(state)
                && city.getCity().equals(cityName));
    }


    /**
     * Deleting country from the server using DELETE request
     *
     * @param countryName country name to be deleted
     * @param state       state name
     * @param city        city name
     * @return http response
     */
    @DELETE
    @Path("/delete")
    @Produces("text/plain")
    public Response delete(@QueryParam("country") String countryName,
                           @QueryParam("state") String state,
                           @QueryParam("city") String city) {
        lock.writeLock().lock();
        //backward iteration to avoid issues with indexing
        for (int i = cities.size() - 1; i >= 0; i--) {
            if (isEqual(cities.get(i), countryName, state, city)) {
                cities.remove(i);
                lock.writeLock().unlock();
                return Response.status(Response.Status.OK).entity(String.format("Deleting: %s", city)).build();
            }
        }
        lock.writeLock().unlock();
        return Response.status(Response.Status.NOT_FOUND).entity(String.format("Could not delete: %s "
                + "(make sure all parameters are specified)", city)).build();
    }
}
