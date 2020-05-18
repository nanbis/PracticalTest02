package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class CommunicationThread extends Thread {
    Socket socket;
    ServerThread server_thread;

    public CommunicationThread(Socket socket, ServerThread server_thread) {
        this.socket = socket;
        this.server_thread = server_thread;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = Utilities.getReader(socket);
            PrintWriter writer = Utilities.getWriter(socket);

            String currency = reader.readLine();

            String rate_float;

            Date date= new Date();
            long time = date.getTime();
            Timestamp ts = new Timestamp(time);
            boolean redo = false;
            if (currency.equals(Constants.EUR)) {
                redo = server_thread.shouldUpdateEURRate(ts);
            } else {
                redo = server_thread.shouldUpdateUSDRate(ts);
            }

            if (redo) {
                HttpClient client = new DefaultHttpClient();

                HttpGet request = new HttpGet(Constants.SITE + currency + Constants.ENDING);
                ResponseHandler<String> handler = new BasicResponseHandler();

                String page_source = client.execute(request, handler);

                JSONObject content = new JSONObject(page_source);

                Log.d(Constants.DEBUG, page_source);



                JSONObject bpi = new JSONObject(content.getString("bpi"));
                JSONObject curr = new JSONObject(bpi.getString(currency));
                Log.d(Constants.DEBUG, bpi.getString(currency));

                rate_float = curr.getString("rate");

                if (currency.equals(Constants.EUR)) {
                    server_thread.updateEURRate(rate_float, ts);
                } else {
                    server_thread.updateUSDRate(rate_float, ts);
                }
            } else {
                if (currency.equals(Constants.EUR)) {
                    rate_float = server_thread.getEURRate();
                } else {
                    rate_float = server_thread.getUSDRate();
                }
            }

            writer.println(rate_float);
            writer.flush();

            socket.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
