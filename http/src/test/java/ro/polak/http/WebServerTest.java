package ro.polak.http;

import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebServerTest {

    @Test
    public void shouldNotStartServerOnTooLessThreadsAvailable() throws IOException {
        ServerSocket serverSocket = mock(ServerSocket.class);

        ServerConfig serverConfig = getDefaultServerConfig();
        when(serverConfig.getMaxServerThreads()).thenReturn(0);

        WebServer webServer = new WebServer(serverSocket, serverConfig);
        assertThat(webServer.startServer(), is(false));
        assertThat(webServer.isRunning(), is(false));
    }

    @Test
    public void shouldNotStartServerOnTempPathNonWritable() throws IOException {
        ServerSocket serverSocket = mock(ServerSocket.class);

        ServerConfig serverConfig = getDefaultServerConfig();
        when(serverConfig.getTempPath()).thenReturn("/proc/someprotectedresource");

        WebServer webServer = new WebServer(serverSocket, serverConfig);
        assertThat(webServer.startServer(), is(false));
        assertThat(webServer.isRunning(), is(false));
    }

    @Test
    public void shouldNotStartServerUnableToBind() throws IOException {
        ServerSocket serverSocket = mock(ServerSocket.class);
        doThrow(new IOException()).when(serverSocket).bind(any(SocketAddress.class));

        ServerConfig serverConfig = getDefaultServerConfig();

        WebServer webServer = new WebServer(serverSocket, serverConfig);
        assertThat(webServer.startServer(), is(false));
        assertThat(webServer.isRunning(), is(false));
    }

    @Test
    public void shouldStartServerIfEverythingIsOk() throws IOException {
        ServerSocket serverSocket = mock(ServerSocket.class);
        ServerConfig serverConfig = getDefaultServerConfig();

        WebServer webServer = new WebServer(serverSocket, serverConfig);
        assertThat(webServer.startServer(), is(true));
        assertThat(webServer.isRunning(), is(true));
        assertThat(webServer.getServerConfig(), is(serverConfig));
        webServer.stopServer();
        assertThat(webServer.isRunning(), is(false));
    }

    private ServerConfig getDefaultServerConfig() {
        ServerConfig serverConfig = mock(ServerConfig.class);
        when(serverConfig.getDocumentRootPath()).thenReturn("/tmp/SomePathThatDoesNotExist");
        when(serverConfig.getTempPath()).thenReturn("/tmp/");
        when(serverConfig.getMaxServerThreads()).thenReturn(99);
        return serverConfig;
    }
}