package ua.artcode.taxi.utils.geolocation;
/**
 * Helps to get some geolocation info
 *
 * <p></p>
 *
 * @see Location
 * @author Serhii Bilobrov
 * */
public interface GoogleMapsAPI {

    /**
     * find location via goole maps
     *
     * @param unformatted "Kiev, Ushiskogo 3"
     *
     * @return formmated location
     *
     * */
    Location findLocation(String unformatted);

    Location findLocation(String country, String city, String street, String houseNum);

    double getDistance(Location pointA, Location pointB);

}
