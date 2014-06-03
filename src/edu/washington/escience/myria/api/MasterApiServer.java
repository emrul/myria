package edu.washington.escience.myria.api;

import java.io.IOException;
import java.net.URI;

import javax.net.ssl.SSLException;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.ResourceConfig;

import edu.washington.escience.myria.daemon.MasterDaemon;
import edu.washington.escience.myria.parallel.Server;

/**
 * The main class for the Myria API server.
 * 
 * @author dhalperi
 * 
 */
public final class MasterApiServer {

  /** The logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(MasterApiServer.class);

  /** The Jersey web server. */
  private final HttpServer webServer;

  /**
   * Constructor for the Master API Server.
   * 
   * @param server the Myria server that will handle API requests.
   * @param daemon the Myria master daemon.
   * @param port the port the Myria API server will listen on.
   * @throws IOException if the server cannot be created.
   */
  public MasterApiServer(final Server server, final MasterDaemon daemon, final int port) throws IOException {
    URI baseUri = UriBuilder.fromUri("http://0.0.0.0/").port(port).build();
    ResourceConfig masterApplication = new MasterApplication(server, daemon);

    /* If the keystore path and password are both set, use SSL. */
    String keystorePath = server.getConfiguration(MyriaApiConstants.MYRIA_API_SSL_KEYSTORE);
    String keystorePassword = server.getConfiguration(MyriaApiConstants.MYRIA_API_SSL_KEYSTORE_PASSWORD);
    if (keystorePath != null && keystorePassword != null) {
      LOGGER.info("Enabling SSL");
      baseUri = UriBuilder.fromUri(baseUri).scheme("https").build();
      HttpHandler handler = ContainerFactory.createContainer(HttpHandler.class, masterApplication);
      SSLContextConfigurator sslCon = new SSLContextConfigurator();
      sslCon.setKeyStoreFile(keystorePath);
      sslCon.setKeyStorePass(keystorePassword);
      if (!sslCon.validateConfiguration(true)) {
        throw new SSLException(
            "SSL keystore configuration did not validate. Missing or incorrect path to keystore? Wrong password?");
      }
      webServer =
          GrizzlyServerFactory.createHttpServer(baseUri, handler, true, new SSLEngineConfigurator(sslCon, false, false,
              false));
    } else {
      LOGGER.info("Not enabling SSL");
      webServer = GrizzlyServerFactory.createHttpServer(baseUri, masterApplication);
    }
  }

  /**
   * Starts the master Restlet API server.
   * 
   * @throws Exception if there is an error starting the server.
   */
  public void start() throws Exception {
    LOGGER.info("Starting API server");
    webServer.start();
    LOGGER.info("API server started.");
  }

  /**
   * Stops the master Restlet API server.
   * 
   * @throws Exception if there is an error stopping the server.
   */
  public void stop() throws Exception {
    LOGGER.info("Stopping API server");
    webServer.stop();
    LOGGER.info("API server stopped");
  }
}