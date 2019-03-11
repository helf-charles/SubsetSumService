package application;

import application.verticle.HttpServerVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class TestApplication {
    static Logger log = LoggerFactory.getLogger("TestApplication");

    public static void main(String args[]) {
        log.info("Starting main()...");

        Vertx vertx = Vertx.vertx();
        HttpServerVerticle httpVert = new HttpServerVerticle();
        vertx.deployVerticle(httpVert);

    }
}
