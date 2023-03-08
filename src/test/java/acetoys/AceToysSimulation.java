package acetoys;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class AceToysSimulation extends Simulation {

    /*
    HTTP Protocol
     */
    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://acetoys.uk");

    /*
     Transactions
     */
    public static ChainBuilder homepage =
            exec(http("Load Home Page")
                            .get("/")
                            .check(css("#_csrf", "content").saveAs("csrfToken"))
            );

    public static ChainBuilder selectCategory =
            exec(
                    http("Select Category")
                            .get("/category/all")
            );

    public static ChainBuilder viewProduct =
            exec(
                    http("View Product")
                            .get("/product/darts-board")
            );

    public static ChainBuilder addProductToCart =
            exec(
                    http("Add Product to Cart")
                            .get("/cart/add/19")
            );

    public static ChainBuilder checkout =
            exec(
                    http("Checkout")
                            .get(("/cart/view"))
            );

    /*
     User Journeys
     */
    public static ChainBuilder browseUser =
            exec(homepage)
                    .pause(5)
                    .exec(selectCategory)
                    .pause(2)
                    .exec(viewProduct);

    public static ChainBuilder purchaseUser =
            exec(homepage)
                    .pause(3)
                    .exec(selectCategory)
                    .pause(2)
                    .exec(viewProduct)
                    .pause(3)
                    .exec(addProductToCart)
                    .pause(2)
                    .exec(checkout);

    /*
     Load Scenarios
     */
    private ScenarioBuilder browseUsersScenario = scenario("Browse Users")
            .exec(browseUser);

    private ScenarioBuilder purchaseUsersScenario = scenario("Purchase Users")
            .exec(purchaseUser);

    private ScenarioBuilder combinedUsersScenario = scenario("Combined Users")
            .randomSwitch()
                            .on(
                                    Choice.withWeight(50, exec(browseUser)),
                                    Choice.withWeight(50, exec(purchaseUser))
                            );

    /*
     Load Simulation
     */
    {
        setUp(
                browseUsersScenario.injectOpen(atOnceUsers(5)),
                purchaseUsersScenario.injectOpen(atOnceUsers(5))
        ).protocols(httpProtocol);
    }
}
