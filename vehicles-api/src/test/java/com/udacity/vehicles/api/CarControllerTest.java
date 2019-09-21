package com.udacity.vehicles.api;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @Before
    public void setup() {
        Car car = getCar();
        car.setId(1L);
        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system
     *
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Car car = getCar();
        mvc.perform(
                post(new URI("/cars"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated());
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     *
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {

        MvcResult result = mvc.perform(get(new URI("/cars")))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JsonNode rootNode = mapper.readTree(result.getResponse().getContentAsString());
        JsonNode list = rootNode.findPath("carList");
        List<Car> actual = mapper.readValue(list.toString(), new TypeReference<List<Car>>() {
        });
        Car car = actual.get(0);
        assertThat(car.getDetails(),
                allOf(hasProperty("model", is(getCar().getDetails().getModel())),
                        hasProperty("mileage", is(getCar().getDetails().getMileage())),
                        hasProperty("body", is(getCar().getDetails().getBody())),
                        hasProperty("engine", is(getCar().getDetails().getEngine())),
                        hasProperty("externalColor", is(getCar().getDetails().getExternalColor())),
                        hasProperty("fuelType", is(getCar().getDetails().getFuelType())),
                        hasProperty("modelYear", is(getCar().getDetails().getModelYear())),
                        hasProperty("productionYear",
                                is(getCar().getDetails().getProductionYear())),
                        hasProperty("numberOfDoors",
                                is(getCar().getDetails().getNumberOfDoors()))));

        assertThat(car.getCondition(), is(getCar().getCondition()));
        assertThat(car.getLocation(),
                allOf(hasProperty("lat", is(getCar().getLocation().getLat())), hasProperty("lon",
                        is(getCar().getLocation().getLon()))));
        assertThat(car.getDetails().getManufacturer(),
                allOf(hasProperty("code", is(getCar().getDetails().getManufacturer().getCode())),
                        hasProperty("name",
                                is(getCar().getDetails().getManufacturer().getName()))));
    }


    /**
     * Tests the read operation for a single car by ID.
     *
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void findCar() throws Exception {

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/cars/{id}", "1"))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        Car car = mapper.readValue(result.getResponse().getContentAsString(), Car.class);

        assertThat(car.getDetails(),
                allOf(hasProperty("model", is(getCar().getDetails().getModel())),
                        hasProperty("mileage", is(getCar().getDetails().getMileage())),
                        hasProperty("body", is(getCar().getDetails().getBody())),
                        hasProperty("engine", is(getCar().getDetails().getEngine())),
                        hasProperty("externalColor", is(getCar().getDetails().getExternalColor())),
                        hasProperty("fuelType", is(getCar().getDetails().getFuelType())),
                        hasProperty("modelYear", is(getCar().getDetails().getModelYear())),
                        hasProperty("productionYear",
                                is(getCar().getDetails().getProductionYear())),
                        hasProperty("numberOfDoors",
                                is(getCar().getDetails().getNumberOfDoors()))));

        assertThat(car.getCondition(), is(getCar().getCondition()));
        assertThat(car.getLocation(),
                allOf(hasProperty("lat", is(getCar().getLocation().getLat())), hasProperty("lon",
                        is(getCar().getLocation().getLon()))));
        assertThat(car.getDetails().getManufacturer(),
                allOf(hasProperty("code", is(getCar().getDetails().getManufacturer().getCode())),
                        hasProperty("name",
                                is(getCar().getDetails().getManufacturer().getName()))));


    }

    /**
     * Tests the deletion of a single car by ID.
     *
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        Car car = getCar();
        car.setId(2L);
        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/cars/{id}", "2"))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult delResult = mvc.perform(MockMvcRequestBuilders.delete("/cars/{id}", "2"))
                .andExpect(status().is(204))
                .andReturn();

    }

    /**
     * Tests the update of a single car by ID.
     *
     * @throws Exception if the update operation of a vehicle fails
     */
    @Test
    public void updateCar() throws Exception {
        Car carToSave = getCar();
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.put("/cars/{id}", "1")
                        .content(json.write(carToSave).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
    }


    /**
     * Creates an example Car object for use in testing.
     *
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }
}