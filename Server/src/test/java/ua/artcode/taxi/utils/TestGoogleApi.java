package ua.artcode.taxi.utils;

import org.junit.Assert;
import org.junit.Test;
import ua.artcode.taxi.utils.geolocation.GoogleMapsAPI;
import ua.artcode.taxi.utils.geolocation.GoogleMapsAPIImpl;
import ua.artcode.taxi.utils.geolocation.Location;

public class TestGoogleApi {

    @Test
    public void testGoogleApi() {
        GoogleMapsAPI googleMapsAPI = new GoogleMapsAPIImpl();

        Location location = googleMapsAPI.findLocation("Україна Київ Бульва Лесі Українки 5");

        Assert.assertNotNull(location);

        Location location1 = googleMapsAPI.findLocation("Україна", "Київ", "Бульва Лесі Українки", "5");
        Location location2 = googleMapsAPI.findLocation("Україна", "Київ", "Старокиєвська", "10");

        Assert.assertTrue(googleMapsAPI.getDistance(location1, location2) > 0);

    }
}

