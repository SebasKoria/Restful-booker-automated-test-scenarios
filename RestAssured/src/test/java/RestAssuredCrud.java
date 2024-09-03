import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.not;

public class RestAssuredCrud {

    @Test
    public void getBookingsTest(){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        Response response = RestAssured
                .when().get("/booking");
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body("size()", not(0));
        response.then().log().body();
    }
    @Test
    public void getBookingWithValidIdTest(){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        Response response = RestAssured
                .given().pathParam("id", "1")
                .when().get("/booking/{id}");
        response.then().assertThat().statusCode(200);
        response.then().log().body();
        response.then().assertThat().body("firstname", Matchers.equalTo("Mark"));
        response.then().assertThat().body("lastname", Matchers.equalTo("Brown"));
    }

    @Test
    public void getBookingWithInvalidIdTest() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        Response response = RestAssured
                .given().pathParam("id", "100000")
                .when().get("/booking/{id}");
        response.then().assertThat().statusCode(404);
    }

    @Test
    public void postEmployeeWithValidDatesTest() throws JsonProcessingException {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com/booking";

        Booking booking = new Booking();
        booking.setFirstname("Sebastian");
        booking.setLastname("Koria");
        booking.setTotalprice(490);
        booking.setDepositpaid(false);
        BookingDates bookingDates = new BookingDates();
        bookingDates.setCheckin("2004-04-15");
        bookingDates.setCheckout("2005-04-15");
        booking.setBookingdates(bookingDates);
        booking.setAdditionalneeds("A lot of ice cream");

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(booking);
        System.out.println(payload);

        Response response = RestAssured
                .given().contentType(ContentType.JSON).body(payload)
                .when().post();

        response.then().assertThat().statusCode(200);
        response.then().assertThat().body("booking.firstname", Matchers.equalTo(booking.getFirstname()));
        response.then().assertThat().body("booking.lastname", Matchers.equalTo(booking.getLastname()));
        response.then().log().body();
    }

    @Test
    public void postEmployeeWithInvalidDatesTest() throws JsonProcessingException {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com/booking";

        Booking booking = new Booking();
        booking.setFirstname("Sebastian");
        booking.setLastname("Koria");
        booking.setTotalprice(490);
        booking.setDepositpaid(false);
        BookingDates bookingDates = new BookingDates();
        bookingDates.setCheckin("Una fecha invalida :)");
        bookingDates.setCheckout("Otra fecha invalida :0");
        booking.setBookingdates(bookingDates);
        booking.setAdditionalneeds("A lot of ice cream");

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(booking);
        System.out.println(payload);

        Response response = RestAssured
                .given().contentType(ContentType.JSON).body(payload)
                .when().post();

        response.then().assertThat().statusCode(422);
    }
}
