package ro.pub.cs.systems.eim.practicaltest02;

import android.widget.EditText;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class ServerThread extends Thread {
    EditText server_port;
    ServerSocket server_socket;

    private String EUR_RATE;
    private String USD_RATE;
    private Timestamp EUR;
    private Timestamp USD;

    boolean isRunning = false;

    public ServerThread(EditText server_port) {
        this.server_port = server_port;
    }

    public void startServer() {
        isRunning = true;
        Date date= new Date();

        long time = date.getTime() - 6000000;
        Timestamp ts = new Timestamp(time);
        EUR_RATE = "0.0";
        USD_RATE = "0.0";
        EUR = ts;
        USD = ts;
        start();
    }

    public synchronized boolean shouldUpdateEURRate(Timestamp ts) {


        if (ts.getTime() - this.EUR.getTime() >= 60000) {
            this.EUR = ts;
            return true;
        }
        return false;
    }

    public synchronized boolean shouldUpdateUSDRate(Timestamp ts) {
        if (ts.getTime() - this.USD.getTime() >= 60000) {
            this.USD = ts;
            return true;
        }
        return false;
    }

    public synchronized void updateEURRate(String newRate, Timestamp ts) {
            this.EUR = ts;
            this.EUR_RATE = newRate;

    }

    public synchronized void updateUSDRate(String newRate, Timestamp ts) {
            this.USD = ts;
            this.USD_RATE = newRate;

    }

    public String getEURRate() {
        return this.EUR_RATE;
    }

    public String getUSDRate() {
        return this.USD_RATE;
    }

    public void stopServer() throws IOException {
        if (server_socket != null)
            server_socket.close();
    }

    @Override
    public void run() {
        try {
            server_socket = new ServerSocket(Integer.valueOf(server_port.getText().toString()));
            while (isRunning) {
                Socket socket = server_socket.accept();

                if (socket != null) {
                    CommunicationThread comm = new CommunicationThread(socket, this);
                    comm.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
