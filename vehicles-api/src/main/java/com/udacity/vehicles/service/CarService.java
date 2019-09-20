package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import java.util.ArrayList;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Implements the car service create, read, update or delete information about vehicles, as well as
 * gather related location and price data when desired.
 */
@Service
public class CarService {

    Logger logger = LoggerFactory.getLogger(CarService.class);

    private final CarRepository repository;
    private MapsClient mapsClient;
    private PriceClient priceClient;


    public CarService(CarRepository repository, WebClient maps, WebClient pricing) {
        logger.info("Initialize service maps client:" + maps.toString());
        logger.info("Initialize service pricing client:" + pricing.toString());
        this.repository = repository;
        mapsClient = new MapsClient(maps, new ModelMapper());
        priceClient = new PriceClient(pricing);
    }

    /**
     * Gathers a list of all vehicles
     *
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        List<Car> carList = repository.findAll();
        List<Car> modifiedList = new ArrayList<>();
        if (!carList.isEmpty()) {
            for (Car car : carList) {
                logger.info("Finding price for car id" + car.getId());
                String price = priceClient.getPrice(car.getId());
                car.setPrice(price);
                logger.info("Found price" + price);

                Location location = car.getLocation();
                //Set the address
                String address = mapsClient.getAddress(location).getAddress();
                logger.info("Foud address :" + address);
                location.setAddress(address);
                car.setLocation(location);
                modifiedList.add(car);
            }
        }

        return modifiedList;
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     *
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {

        Car car;

        try {

            Optional<Car> optCar = repository.findById(id);
            car = optCar.get();

            logger.info("Finding price for car id" + id);
            String price = priceClient.getPrice(id);
            car.setPrice(price);
            logger.info("Found price" + price);

            Location location = car.getLocation();
            //Set the address
            String address = mapsClient.getAddress(location).getAddress();
            logger.info("Foud address :" + address);
            location.setAddress(address);
            car.setLocation(location);

        } catch (NoSuchElementException noSuchElementException) {
            logger.error("No Car found for the Id  " + id);
            noSuchElementException.printStackTrace();
            throw new CarNotFoundException("No Car found for the Id  " + id);
        }

        return car;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     *
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null && car.getId() > 0) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     *
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        Car car = null;
        try {
            Optional<Car> optCar = repository.findById(id);
            car = optCar.get();
            repository.delete(car);
        } catch (NoSuchElementException noSuchElementException) {
            logger.error("No Car found for the Id  " + id);
            noSuchElementException.printStackTrace();
            throw new CarNotFoundException("No Car found for the Id  " + id);
        }
    }
}
