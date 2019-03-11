package application.handler;

import application.SubsetSumCalculator;
import application.bean.SubsetSum;
import application.bean.SubsetSumRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

/**
 * A custom Vertx Handler used to handle all SubsetSum requests coming in to the application's HTTP Server
 */
public class SubsetSumHandler implements Handler<RoutingContext> {
    Logger log = LoggerFactory.getLogger("SubsetSumHandler");

    @Override
    public void handle(RoutingContext rc) {
        //We see if we can successfully extract a SubsetSumRequest from the request body
        ObjectMapper mapper = new ObjectMapper();
        String body = rc.getBodyAsString();
        SubsetSumRequest ssr;
        log.info("Attempting to process JSON request");
        try {
            ssr = mapper.readValue(body, SubsetSumRequest.class);
        } catch (Exception e) {
            log.error("JSON mapping failed!");
            ssr = null;
        }

        HttpServerResponse response = rc.response();

        //If we had to set SSR to null, it wasn't a proper request
        if (null == ssr) {
            response.putHeader("ContentType", "text/plain");
            response.setStatusCode(400);
            response.end("JSON mapping of the request body failed.");
        } else {
            //Otherwise, we extract the pertinent values and feed them into our algorithm
            List<Integer> startList = ssr.getList();
            int target = ssr.getTarget();
            List<SubsetSum> result = SubsetSumCalculator.calculate(startList, target);

            if (null == result) {
                response.putHeader("ContentType", "text/plain");
                response.setStatusCode(200);
                response.end("No subsets within the list sum up to the target value");
            }else {
                response.putHeader("ContentType", "application/json");
                response.setStatusCode(200);
                response.end(Json.encodePrettily(result));
            }
        }

        log.info("Current handler has finished its task");
    }
}
