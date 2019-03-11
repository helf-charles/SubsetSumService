package application.verticle;

import application.handler.SubsetSumHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class HttpServerVerticle extends AbstractVerticle {

    private static Logger log = LoggerFactory.getLogger("HttpServerVerticle");

    public void start() {
        log.info("Starting HttpServerVerticle...");
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        int port = 9001;

        router.route("/test").handler(routingContext -> {
            log.info("Test succeeded");
        });

        router.route("/subsetSum").handler(new SubsetSumHandler());

        server.requestHandler(router).listen(port);
        log.info("HttpServerVerticle successfully started!");
    }
}
